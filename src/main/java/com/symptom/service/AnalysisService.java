package com.symptom.service;

import com.symptom.mapper.CaseInfoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class AnalysisService {

    private final CaseInfoMapper caseInfoMapper;

    public AnalysisService(CaseInfoMapper caseInfoMapper) {
        this.caseInfoMapper = caseInfoMapper;
    }

    public Map<String, Object> getTimeDistribution(Map<String, Object> filter, String groupBy) {
        Map<String, Object> params = new HashMap<>(filter);
        params.put("groupBy", groupBy);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = caseInfoMapper.countByDate(params);
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Map<String, Object> item : data) {
            labels.add(String.valueOf(item.get("period")));
            values.add(((Number) item.get("cnt")).intValue());
        }

        Map<String, Integer> allCounts = loadHistoricalCounts(filter, groupBy);
        List<Integer> yoyValues = new ArrayList<>();
        List<Integer> momValues = new ArrayList<>();
        for (String label : labels) {
            yoyValues.add(allCounts.getOrDefault(shiftPeriod(label, groupBy, "yoy"), 0));
            momValues.add(allCounts.getOrDefault(shiftPeriod(label, groupBy, "mom"), 0));
        }

        result.put("labels", labels);
        result.put("values", values);
        result.put("yoy", yoyValues);
        result.put("mom", momValues);
        return result;
    }

    private Map<String, Integer> loadHistoricalCounts(Map<String, Object> filter, String groupBy) {
        Map<String, Object> histParams = new HashMap<>(filter);
        histParams.remove("startDate");
        histParams.remove("endDate");
        histParams.remove("days");
        histParams.put("groupBy", groupBy);
        Map<String, Integer> allCounts = new HashMap<>();
        for (Map<String, Object> item : caseInfoMapper.countByDate(histParams)) {
            allCounts.put(String.valueOf(item.get("period")), ((Number) item.get("cnt")).intValue());
        }
        return allCounts;
    }

    private String shiftPeriod(String period, String groupBy, String mode) {
        if (period == null || period.isEmpty()) {
            return "";
        }
        try {
            if ("month".equals(groupBy)) {
                YearMonth ym = YearMonth.parse(period);
                return "yoy".equals(mode) ? ym.minusYears(1).toString() : ym.minusMonths(1).toString();
            }
            if ("week".equals(groupBy)) {
                int dash = period.indexOf("-W");
                int year = Integer.parseInt(period.substring(0, dash));
                int week = Integer.parseInt(period.substring(dash + 2));
                if ("yoy".equals(mode)) {
                    return String.format("%d-W%02d", year - 1, week);
                }
                week -= 1;
                if (week < 0) {
                    year -= 1;
                    week = 51;
                }
                return String.format("%d-W%02d", year, week);
            }
            LocalDate date = LocalDate.parse(period);
            return "yoy".equals(mode) ? date.minusYears(1).toString() : date.minusDays(1).toString();
        } catch (Exception e) {
            return "";
        }
    }

    public List<Map<String, Object>> getDistrictDistribution(Map<String, Object> filter) {
        return caseInfoMapper.countByDistrict(filter);
    }

    public Map<String, Object> getPopulationDistribution(Map<String, Object> filter) {
        Map<String, Object> result = new HashMap<>();
        result.put("age", caseInfoMapper.countByAgeGroup(filter));
        result.put("gender", caseInfoMapper.countByGender(filter));
        result.put("occupation", caseInfoMapper.countByOccupation(filter));
        return result;
    }

    public Map<String, Object> getSevereDeathStats(Map<String, Object> filter) {
        Map<String, Object> params = new HashMap<>(filter);
        params.put("limit", 5000);
        params.put("offset", 0);
        List<com.symptom.entity.CaseInfo> all = caseInfoMapper.search(params);
        int total = all.size();
        int severe = (int) all.stream().filter(c -> c.getIsSevere() != null && c.getIsSevere() == 1).count();
        int death = (int) all.stream().filter(c -> c.getIsDeath() != null && c.getIsDeath() == 1).count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("severe", severe);
        result.put("death", death);
        result.put("severeRate", total > 0 ? String.format("%.1f", severe * 100.0 / total) : "0.0");
        result.put("deathRate", total > 0 ? String.format("%.1f", death * 100.0 / total) : "0.0");

        Map<String, int[]> monthlyStats = new TreeMap<>();
        for (com.symptom.entity.CaseInfo c : all) {
            if (c.getReportDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(c.getReportDate());
                String month = String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
                monthlyStats.putIfAbsent(month, new int[]{0, 0, 0});
                monthlyStats.get(month)[0]++;
                if (c.getIsSevere() != null && c.getIsSevere() == 1) monthlyStats.get(month)[1]++;
                if (c.getIsDeath() != null && c.getIsDeath() == 1) monthlyStats.get(month)[2]++;
            }
        }

        List<Map<String, Object>> severeTrend = new ArrayList<>();
        List<Map<String, Object>> deathTrend = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : monthlyStats.entrySet()) {
            Map<String, Object> sItem = new HashMap<>();
            sItem.put("month", entry.getKey());
            sItem.put("rate", entry.getValue()[0] > 0 ?
                    String.format("%.1f", entry.getValue()[1] * 100.0 / entry.getValue()[0]) : "0.0");
            severeTrend.add(sItem);

            Map<String, Object> dItem = new HashMap<>();
            dItem.put("month", entry.getKey());
            dItem.put("rate", entry.getValue()[0] > 0 ?
                    String.format("%.1f", entry.getValue()[2] * 100.0 / entry.getValue()[0]) : "0.0");
            deathTrend.add(dItem);
        }
        result.put("severeTrend", severeTrend);
        result.put("deathTrend", deathTrend);
        return result;
    }

    public Map<String, Object> getClinicalFeatures(Map<String, Object> filter) {
        Map<String, Object> params = new HashMap<>(filter);
        params.put("limit", 5000);
        params.put("offset", 0);
        List<com.symptom.entity.CaseInfo> cases = caseInfoMapper.search(params);
        Map<String, Object> result = new HashMap<>();

        int feverCount = 0;
        double totalTemp = 0;
        Map<String, Integer> diagnoses = new HashMap<>();

        for (com.symptom.entity.CaseInfo c : cases) {
            if (c.getFeverTemp() != null && c.getFeverTemp() > 37.3) {
                feverCount++;
                totalTemp += c.getFeverTemp();
            }
            if (c.getDiagnosis() != null) {
                diagnoses.merge(c.getDiagnosis(), 1, Integer::sum);
            }
        }

        result.put("feverRate", cases.size() > 0 ? String.format("%.1f", feverCount * 100.0 / cases.size()) : "0");
        result.put("avgTemp", feverCount > 0 ? String.format("%.1f", totalTemp / feverCount) : "0");
        result.put("totalCases", cases.size());
        result.put("diagnoses", diagnoses);
        return result;
    }

    public Map<String, Object> getRiskStats(Map<String, Object> filter) {
        Map<String, Object> params = new HashMap<>(filter);
        params.put("limit", 5000);
        params.put("offset", 0);
        List<com.symptom.entity.CaseInfo> cases = caseInfoMapper.search(params);
        Map<String, Integer> riskMap = new LinkedHashMap<>();
        riskMap.put("高风险", 0);
        riskMap.put("中风险", 0);
        riskMap.put("低风险", 0);
        riskMap.put("待评估", 0);
        for (com.symptom.entity.CaseInfo c : cases) {
            String level = c.getRiskLevel() != null ? c.getRiskLevel() : "待评估";
            riskMap.merge(level, 1, Integer::sum);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("riskDistribution", riskMap);
        result.put("totalCases", cases.size());
        return result;
    }
}
