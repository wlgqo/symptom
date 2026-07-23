package com.symptom.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptom.entity.MonitorIndicator;
import com.symptom.mapper.CaseInfoMapper;
import com.symptom.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class AnalysisService {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final CaseInfoMapper caseInfoMapper;
    private final MonitorIndicatorService monitorIndicatorService;
    private final WarningRecordMapper warningRecordMapper;
    private final MapScopeService mapScopeService;

    public AnalysisService(CaseInfoMapper caseInfoMapper,
                           MonitorIndicatorService monitorIndicatorService,
                           WarningRecordMapper warningRecordMapper,
                           MapScopeService mapScopeService) {
        this.caseInfoMapper = caseInfoMapper;
        this.monitorIndicatorService = monitorIndicatorService;
        this.warningRecordMapper = warningRecordMapper;
        this.mapScopeService = mapScopeService;
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

    public List<Map<String, Object>> getMapDistrictDistribution(Map<String, Object> filter, String mapLevel) {
        List<Map<String, Object>> raw = caseInfoMapper.countByDistrict(filter);
        return mapScopeService.aggregateMapData(raw, mapLevel);
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

    /**
     * 症候群历史至今按月发病趋势，标注异常等级与颜色（预警核心指标）。
     */
    public Map<String, Object> getHistoricalTrend(Map<String, Object> filter, String levelThresholdJson) {
        Map<String, Object> histParams = new HashMap<>(filter);
        histParams.remove("startDate");
        histParams.remove("endDate");
        histParams.remove("days");
        histParams.put("groupBy", "month");

        List<Map<String, Object>> monthly = caseInfoMapper.countByDate(histParams);
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        List<Double> risePercents = new ArrayList<>();
        List<String> levels = new ArrayList<>();
        List<String> colors = new ArrayList<>();

        int lowCase = 5, midCase = 10, highCase = 20;
        double lowRise = 30, midRise = 60, highRise = 100;
        Map<String, Object> parsed = parseLevelThresholds(levelThresholdJson);
        lowCase = (int) parsed.get("lowCase");
        midCase = (int) parsed.get("midCase");
        highCase = (int) parsed.get("highCase");
        lowRise = (double) parsed.get("lowRise");
        midRise = (double) parsed.get("midRise");
        highRise = (double) parsed.get("highRise");

        Integer prev = null;
        for (Map<String, Object> item : monthly) {
            int cnt = ((Number) item.get("cnt")).intValue();
            labels.add(String.valueOf(item.get("period")));
            values.add(cnt);

            double rise = 0;
            if (prev != null && prev > 0) {
                rise = (cnt - prev) * 100.0 / prev;
            }
            risePercents.add(Math.round(rise * 10) / 10.0);
            prev = cnt;

            String level = "正常";
            String color = "#1677ff";
            if (cnt >= highCase || rise >= highRise) {
                level = "高风险";
                color = "#ff4d4f";
            } else if (cnt >= midCase || rise >= midRise) {
                level = "中风险";
                color = "#faad14";
            } else if (cnt >= lowCase || rise >= lowRise) {
                level = "低风险";
                color = "#52c41a";
            }
            levels.add(level);
            colors.add(color);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        result.put("risePercents", risePercents);
        result.put("levels", levels);
        result.put("colors", colors);
        result.put("total", values.stream().mapToInt(Integer::intValue).sum());
        result.put("latestLevel", levels.isEmpty() ? "正常" : levels.get(levels.size() - 1));
        result.put("latestRise", risePercents.isEmpty() ? 0 : risePercents.get(risePercents.size() - 1));
        return result;
    }

    /**
     * 计算监测指标当前值并与阈值比对，返回异常高亮信息。
     */
    public List<Map<String, Object>> evaluateIndicators(Map<String, Object> filter, String syndromeType) {
        List<MonitorIndicator> indicators = monitorIndicatorService.findBySyndromeType(syndromeType);
        if (indicators.isEmpty()) {
            indicators = monitorIndicatorService.findAll();
        }

        Map<String, Object> timeMonth = getTimeDistribution(filter, "month");
        List<String> monthLabels = (List<String>) timeMonth.get("labels");
        List<Integer> monthValues = (List<Integer>) timeMonth.get("values");
        int latestCases = monthValues.isEmpty() ? 0 : monthValues.get(monthValues.size() - 1);
        int prevCases = monthValues.size() > 1 ? monthValues.get(monthValues.size() - 2) : 0;
        double mom = prevCases > 0 ? (latestCases - prevCases) * 100.0 / prevCases : 0;

        int yoyCases = 0;
        if (!monthLabels.isEmpty()) {
            String latestLabel = monthLabels.get(monthLabels.size() - 1);
            Map<String, Integer> allCounts = loadHistoricalCounts(filter, "month");
            yoyCases = allCounts.getOrDefault(shiftPeriod(latestLabel, "month", "yoy"), 0);
        }
        double yoy = yoyCases > 0 ? (latestCases - yoyCases) * 100.0 / yoyCases : 0;

        Map<String, Object> severe = getSevereDeathStats(filter);
        Map<String, Object> risk = getRiskStats(filter);
        Map<String, Object> warnScope = new HashMap<>(filter);
        int warnCnt = warningRecordMapper.countScoped(warnScope);

        List<Map<String, Object>> result = new ArrayList<>();
        for (MonitorIndicator ind : indicators) {
            if (!"启用".equals(ind.getStatus())) {
                continue;
            }
            String code = ind.getIndicatorCode();
            Object rawValue = null;
            switch (code) {
                case "CASE_DAILY":
                case "CASE_NEW_7D":
                    rawValue = latestCases;
                    break;
                case "CASE_TOTAL":
                    rawValue = risk.get("totalCases");
                    break;
                case "TREND_YOY":
                    rawValue = Math.round(yoy * 10) / 10.0;
                    break;
                case "TREND_MOM":
                    rawValue = Math.round(mom * 10) / 10.0;
                    break;
                case "SEVERE_RATE":
                    rawValue = Double.parseDouble(String.valueOf(severe.get("severeRate")));
                    break;
                case "DEATH_RATE":
                    rawValue = Double.parseDouble(String.valueOf(severe.get("deathRate")));
                    break;
                case "WARN_CNT":
                    rawValue = warnCnt;
                    break;
                case "RISK_HIGH_CNT":
                    Map<String, Integer> riskDist = (Map<String, Integer>) risk.get("riskDistribution");
                    rawValue = riskDist != null ? riskDist.getOrDefault("高风险", 0) : 0;
                    break;
                default:
                    continue;
            }
            Map<String, Object> row = buildIndicatorRow(ind, rawValue);
            result.add(row);
        }
        return result;
    }

    private Map<String, Object> buildIndicatorRow(MonitorIndicator ind, Object rawValue) {
        Map<String, Object> row = new HashMap<>();
        row.put("code", ind.getIndicatorCode());
        row.put("name", ind.getIndicatorName());
        row.put("category", ind.getCategory());
        row.put("value", rawValue);
        row.put("unit", ind.getUnit());

        String status = "正常";
        String color = "#4a5568";
        String bgColor = "transparent";
        Map<String, Object> thresholds = parseThresholdJson(ind.getThresholdJson());
        double val = rawValue instanceof Number ? ((Number) rawValue).doubleValue() : 0;
        if (thresholds.containsKey("alert") && val >= toDouble(thresholds.get("alert"))) {
            status = "严重异常";
            color = "#cf1322";
            bgColor = "#fff2f0";
        } else if (thresholds.containsKey("warning") && val >= toDouble(thresholds.get("warning"))) {
            status = "异常";
            color = "#d46b08";
            bgColor = "#fff7e6";
        }
        row.put("status", status);
        row.put("color", color);
        row.put("bgColor", bgColor);
        row.put("abnormal", !"正常".equals(status));
        return row;
    }

    private Map<String, Object> parseThresholdJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private double toDouble(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(o));
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    private Map<String, Object> parseLevelThresholds(String json) {
        int lowCase = 5, midCase = 10, highCase = 20;
        double lowRise = 30, midRise = 60, highRise = 100;
        if (json != null && !json.trim().isEmpty()) {
            try {
                Map<String, Object> root = JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
                List<Map<String, Object>> levels = (List<Map<String, Object>>) root.get("levels");
                if (levels != null) {
                    for (Map<String, Object> lv : levels) {
                        String name = String.valueOf(lv.get("level"));
                        int caseCount = lv.get("caseCount") != null ? ((Number) lv.get("caseCount")).intValue() : 0;
                        double rise = lv.get("risePercent") != null ? ((Number) lv.get("risePercent")).doubleValue() : 0;
                        if (name.contains("低")) {
                            lowCase = caseCount;
                            lowRise = rise;
                        } else if (name.contains("中")) {
                            midCase = caseCount;
                            midRise = rise;
                        } else if (name.contains("高")) {
                            highCase = caseCount;
                            highRise = rise;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("lowCase", lowCase);
        result.put("midCase", midCase);
        result.put("highCase", highCase);
        result.put("lowRise", lowRise);
        result.put("midRise", midRise);
        result.put("highRise", highRise);
        return result;
    }
}
