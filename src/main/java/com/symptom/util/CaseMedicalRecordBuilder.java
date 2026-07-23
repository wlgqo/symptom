package com.symptom.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.symptom.entity.CaseInfo;

import java.text.SimpleDateFormat;
import java.util.*;

public final class CaseMedicalRecordBuilder {

    private CaseMedicalRecordBuilder() {
    }

    public static String effectiveProfileJson(CaseInfo caseInfo) {
        if (caseInfo.getProfileJson() != null && !caseInfo.getProfileJson().trim().isEmpty()) {
            return caseInfo.getProfileJson();
        }
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("bloodType", randomBloodType(caseInfo.getId()));
        profile.put("maritalStatus", caseInfo.getAge() != null && caseInfo.getAge() < 22 ? "未婚" : "已婚");
        profile.put("chronicDiseases", caseInfo.getAge() != null && caseInfo.getAge() > 60 ? Collections.singletonList("高血压") : Collections.emptyList());
        profile.put("allergies", Collections.emptyList());
        profile.put("vaccination", Collections.singletonList("流感疫苗(2025-10)"));
        profile.put("tags", buildTags(caseInfo));
        profile.put("portrait", buildPortrait(caseInfo));
        return JSON.toJSONString(profile);
    }

    public static String effectiveMedicalRecordJson(CaseInfo caseInfo) {
        JSONObject medical;
        if (caseInfo.getMedicalRecordJson() != null && !caseInfo.getMedicalRecordJson().trim().isEmpty()) {
            try {
                medical = JSON.parseObject(caseInfo.getMedicalRecordJson());
            } catch (Exception e) {
                medical = new JSONObject();
            }
        } else {
            medical = JSON.parseObject(buildDefaultMedicalRecord(caseInfo));
        }
        enrichMedicalRecord(medical, caseInfo);
        return medical.toJSONString();
    }

    private static String buildDefaultMedicalRecord(CaseInfo caseInfo) {
        String symptoms = caseInfo.getSymptoms() != null ? String.join("、", caseInfo.getSymptoms()) : "发热";
        String reportDate = formatDate(caseInfo.getReportDate());
        String admissionDate = formatDate(caseInfo.getAdmissionDate() != null ? caseInfo.getAdmissionDate() : caseInfo.getReportDate());

        Map<String, Object> medical = new LinkedHashMap<>();
        medical.put("department", inferDepartment(caseInfo.getSyndromeType()));
        medical.put("bedNo", (caseInfo.getIsSevere() != null && caseInfo.getIsSevere() == 1) ? "重症病区" : "普通病房");
        medical.put("chiefComplaint", buildChiefComplaint(caseInfo, symptoms));
        medical.put("presentIllness", buildPresentIllness(caseInfo, symptoms));
        medical.put("physicalExam", buildPhysicalExam(caseInfo));
        medical.put("treatmentPlan", parseTreatment(caseInfo.getTreatmentJson()));

        List<Map<String, Object>> visits = new ArrayList<>();
        Map<String, Object> visit = new LinkedHashMap<>();
        visit.put("date", admissionDate);
        visit.put("type", caseInfo.getDiscoverType() != null ? caseInfo.getDiscoverType() : "门诊");
        visit.put("dept", inferDepartment(caseInfo.getSyndromeType()));
        visit.put("doctor", caseInfo.getHospital() != null ? caseInfo.getHospital() + "医师" : "接诊医师");
        visits.add(visit);
        medical.put("visits", visits);

        medical.put("examinations", buildExaminations(caseInfo, reportDate));
        medical.put("labTests", buildLabTests(caseInfo, reportDate));

        if (caseInfo.getIsDeath() != null && caseInfo.getIsDeath() == 1) {
            Map<String, Object> death = new LinkedHashMap<>();
            death.put("deathDate", formatDate(caseInfo.getDeathDate() != null ? caseInfo.getDeathDate() : caseInfo.getReportDate()));
            death.put("deathCause", caseInfo.getDiagnosis() != null ? caseInfo.getDiagnosis() + "相关并发症" : "待明确");
            death.put("deathPlace", caseInfo.getHospital());
            medical.put("deathInfo", death);
        }

        return JSON.toJSONString(medical);
    }

    private static void enrichMedicalRecord(JSONObject medical, CaseInfo caseInfo) {
        if (isBlank(medical.getString("westernDiagnosis"))) {
            medical.put("westernDiagnosis", inferWesternDiagnosis(caseInfo));
        }
        if (isBlank(medical.getString("tcmDiagnosis"))) {
            medical.put("tcmDiagnosis", inferTcmDiagnosis(caseInfo));
        }
        if (isBlank(medical.getString("infectiousDiagnosis"))) {
            medical.put("infectiousDiagnosis", inferInfectiousDiagnosis(caseInfo));
        }
        if (caseInfo.getIdCard() != null && !caseInfo.getIdCard().trim().isEmpty()) {
            medical.put("idCard", caseInfo.getIdCard().trim());
        }
        if (caseInfo.getPhone() != null && !caseInfo.getPhone().trim().isEmpty()) {
            medical.put("phone", caseInfo.getPhone().trim());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String inferWesternDiagnosis(CaseInfo c) {
        if (c.getDiagnosis() != null && !c.getDiagnosis().trim().isEmpty()) {
            return c.getDiagnosis().trim();
        }
        return "待查";
    }

    private static String inferTcmDiagnosis(CaseInfo c) {
        String syndrome = c.getSyndromeType();
        if (syndrome == null) {
            return "发热待辨";
        }
        if (syndrome.contains("呼吸道")) {
            return "风热袭肺";
        }
        if (syndrome.contains("腹泻")) {
            return "湿热下注";
        }
        if (syndrome.contains("出血")) {
            return "血热妄行";
        }
        if (syndrome.contains("出疹")) {
            return "风热犯表";
        }
        if (syndrome.contains("脑炎")) {
            return "热入营血";
        }
        return "邪伏膜原";
    }

    private static String inferInfectiousDiagnosis(CaseInfo c) {
        String diag = c.getDiagnosis();
        if (diag == null || diag.trim().isEmpty()) {
            return "待排除传染病";
        }
        String d = diag.trim();
        if (d.contains("流感")) {
            return "流行性感冒";
        }
        if (d.contains("肺炎") && !d.contains("非传染")) {
            return "社区获得性肺炎";
        }
        if (d.contains("诺如")) {
            return "诺如病毒感染";
        }
        if (d.contains("痢疾")) {
            return "细菌性痢疾";
        }
        if (d.contains("出血热")) {
            return "肾综合征出血热（疑似）";
        }
        if (d.contains("麻疹") || d.contains("风疹") || d.contains("猩红热")) {
            return d;
        }
        if (d.contains("脑炎") || d.contains("脑膜炎")) {
            return d;
        }
        if (d.contains("上呼吸道") || d.contains("支气管炎") || d.contains("肠胃炎") || d.contains("待查") || d.contains("不明")) {
            return "非传染病";
        }
        return "待排除传染病";
    }

    private static String buildChiefComplaint(CaseInfo c, String symptoms) {
        String temp = c.getFeverTemp() != null ? "体温" + c.getFeverTemp() + "℃" : "发热";
        return temp + "伴" + (symptoms.contains("、") ? symptoms.substring(symptoms.indexOf("、") + 1) : symptoms.replace("发热", "不适"));
    }

    private static String buildPresentIllness(CaseInfo c, String symptoms) {
        StringBuilder sb = new StringBuilder();
        sb.append("患者因").append(symptoms).append("于");
        sb.append(formatDate(c.getReportDate())).append("就诊，");
        sb.append("诊断为").append(c.getDiagnosis() != null ? c.getDiagnosis() : "待查");
        sb.append("，症候群类型：").append(c.getSyndromeType()).append("。");
        if (c.getRiskReason() != null) {
            sb.append("风险因素：").append(c.getRiskReason()).append("。");
        }
        return sb.toString();
    }

    private static String buildPhysicalExam(CaseInfo c) {
        StringBuilder sb = new StringBuilder();
        if (c.getFeverTemp() != null) {
            sb.append("T ").append(c.getFeverTemp()).append("℃，");
        }
        sb.append("神志清楚，精神尚可。");
        if (c.getSyndromeType() != null && c.getSyndromeType().contains("呼吸道")) {
            sb.append("咽部充血，双肺呼吸音粗。");
        } else if (c.getSyndromeType() != null && c.getSyndromeType().contains("腹泻")) {
            sb.append("腹软，肠鸣音活跃。");
        } else if (c.getSyndromeType() != null && c.getSyndromeType().contains("出血")) {
            sb.append("皮肤黏膜未见明显出血点。");
        }
        return sb.toString();
    }

    private static String buildPortrait(CaseInfo c) {
        return String.format("%d岁%s%s，%s，因%s就诊于%s，匹配%s识别规则。",
                c.getAge() != null ? c.getAge() : 0,
                c.getGender() != null ? c.getGender() : "",
                c.getOccupation() != null ? c.getOccupation() : "",
                c.getDistrict() != null ? c.getDistrict() : "",
                c.getDiagnosis() != null ? c.getDiagnosis() : "相关症状",
                c.getHospital() != null ? c.getHospital() : "医疗机构",
                c.getSyndromeType() != null ? c.getSyndromeType() : "症候群");
    }

    private static List<String> buildTags(CaseInfo c) {
        List<String> tags = new ArrayList<>();
        tags.add(c.getSyndromeType() != null ? c.getSyndromeType().replace("症候群", "") : "监测病例");
        if (c.getRiskLevel() != null) tags.add(c.getRiskLevel());
        if (c.getIsSevere() != null && c.getIsSevere() == 1) tags.add("重症");
        if (c.getIsDeath() != null && c.getIsDeath() == 1) tags.add("死亡");
        return tags;
    }

    private static String inferDepartment(String syndromeType) {
        if (syndromeType == null) return "内科";
        if (syndromeType.contains("呼吸道")) return "呼吸内科";
        if (syndromeType.contains("腹泻")) return "消化内科";
        if (syndromeType.contains("出血")) return "感染科";
        if (syndromeType.contains("脑炎")) return "神经内科";
        if (syndromeType.contains("出疹")) return "儿科";
        return "发热门诊";
    }

    private static List<Map<String, Object>> buildExaminations(CaseInfo c, String date) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("date", date);
        if (c.getSyndromeType() != null && c.getSyndromeType().contains("呼吸道")) {
            item.put("item", "胸部X线");
            item.put("result", "双肺纹理增粗");
        } else if (c.getSyndromeType() != null && c.getSyndromeType().contains("脑炎")) {
            item.put("item", "头颅CT");
            item.put("result", "未见明显异常");
        } else {
            item.put("item", "体格检查");
            item.put("result", "见病历记录");
        }
        list.add(item);
        return list;
    }

    private static List<Map<String, Object>> buildLabTests(CaseInfo c, String date) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("date", date);
        item.put("item", "血常规");
        if (c.getLabJson() != null && !c.getLabJson().isEmpty()) {
            try {
                JSONObject lab = JSON.parseObject(c.getLabJson());
                StringBuilder result = new StringBuilder();
                if (lab.containsKey("wbc")) result.append("WBC ").append(lab.get("wbc")).append("×10⁹/L");
                if (lab.containsKey("crp")) {
                    if (result.length() > 0) result.append("，");
                    result.append("CRP ").append(lab.get("crp")).append("mg/L");
                }
                if (lab.containsKey("platelet")) {
                    if (result.length() > 0) result.append("，");
                    result.append("PLT ").append(lab.get("platelet")).append("×10⁹/L");
                }
                item.put("result", result.length() > 0 ? result.toString() : "详见检验单");
            } catch (Exception e) {
                item.put("result", "详见检验单");
            }
        } else {
            item.put("result", "WBC 6.5×10⁹/L，CRP 15mg/L");
        }
        list.add(item);
        return list;
    }

    private static String parseTreatment(String treatmentJson) {
        if (treatmentJson == null || treatmentJson.isEmpty()) {
            return "对症支持治疗，密切观察病情变化。";
        }
        try {
            JSONObject t = JSON.parseObject(treatmentJson);
            if (t.containsKey("medication")) {
                return "药物治疗：" + String.join("、", t.getJSONArray("medication").toJavaList(String.class));
            }
        } catch (Exception ignored) {
        }
        return "对症支持治疗。";
    }

    private static String formatDate(Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private static String randomBloodType(Integer id) {
        String[] types = {"A型", "B型", "O型", "AB型"};
        return types[(id != null ? id : 0) % types.length];
    }
}
