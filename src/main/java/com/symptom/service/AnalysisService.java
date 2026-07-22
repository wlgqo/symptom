package com.symptom.service;

import com.symptom.mapper.CaseInfoMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalysisService {

    private final CaseInfoMapper caseInfoMapper;

    public AnalysisService(CaseInfoMapper caseInfoMapper) {
        this.caseInfoMapper = caseInfoMapper;
    }

    public Map<String, Object> getTimeDistribution(String syndromeType, String groupBy) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = caseInfoMapper.countByDate(syndromeType, groupBy);
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Map<String, Object> item : data) {
            labels.add(String.valueOf(item.get("period")));
            values.add(((Number) item.get("cnt")).intValue());
        }
        result.put("labels", labels);
        result.put("values", values);
        return result;
    }

    public List<Map<String, Object>> getDistrictDistribution(String syndromeType) {
        return caseInfoMapper.countByDistrict(syndromeType);
    }

    public Map<String, Object> getPopulationDistribution(String syndromeType) {
        Map<String, Object> result = new HashMap<>();
        result.put("age", caseInfoMapper.countByAgeGroup(syndromeType));
        result.put("gender", caseInfoMapper.countByGender(syndromeType));
        result.put("occupation", caseInfoMapper.countByOccupation(syndromeType));
        return result;
    }

    public Map<String, Object> getSevereDeathStats(String syndromeType) {
        Map<String, Object> result = new HashMap<>();
        List<com.symptom.entity.CaseInfo> all = caseInfoMapper.findBySyndromeType(syndromeType);
        int total = all.size();
        int severe = (int) all.stream().filter(c -> c.getIsSevere() != null && c.getIsSevere() == 1).count();
        int death = (int) all.stream().filter(c -> c.getIsDeath() != null && c.getIsDeath() == 1).count();

        result.put("total", total);
        result.put("severe", severe);
        result.put("death", death);
        result.put("severeRate", total > 0 ? String.format("%.1f", severe * 100.0 / total) : "0.0");
        result.put("deathRate", total > 0 ? String.format("%.1f", death * 100.0 / total) : "0.0");

        List<Map<String, Object>> severeTrend = new ArrayList<>();
        List<Map<String, Object>> deathTrend = new ArrayList<>();
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

    public Map<String, Object> getClinicalFeatures(String syndromeType) {
        List<com.symptom.entity.CaseInfo> cases = caseInfoMapper.findBySyndromeType(syndromeType);
        Map<String, Object> result = new HashMap<>();

        int feverCount = 0;
        double totalTemp = 0;
        Map<String, Integer> respiratorySymptoms = new HashMap<>();
        Map<String, Integer> accompanySymptoms = new HashMap<>();
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
}
