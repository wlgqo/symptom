package com.symptom.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public final class ConfigDisplayHelper {

    private static final ObjectMapper JSON = new ObjectMapper();

    private ConfigDisplayHelper() {
    }

    public static String formatLevelThreshold(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "—";
        }
        try {
            Map<String, Object> root = JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> levels = (List<Map<String, Object>>) root.get("levels");
            if (levels == null || levels.isEmpty()) {
                return "—";
            }
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> lv : levels) {
                if (sb.length() > 0) {
                    sb.append("；");
                }
                String name = String.valueOf(lv.getOrDefault("level", ""));
                Object caseCount = lv.get("caseCount");
                Object rise = lv.get("risePercent");
                sb.append(name);
                if (caseCount != null) {
                    sb.append("≥").append(caseCount).append("例");
                }
                if (rise != null) {
                    sb.append("/").append(rise).append("%");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "已配置";
        }
    }

    public static String formatModelConfig(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "暂无参数";
        }
        try {
            Map<String, Object> data = JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> e : data.entrySet()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(e.getKey()).append("：").append(e.getValue());
            }
            return sb.toString();
        } catch (Exception e) {
            return "已配置";
        }
    }

    public static String formatSyndromeRules(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "暂无规则";
        }
        try {
            Map<String, Object> data = JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
            StringBuilder sb = new StringBuilder();
            sb.append("组合方式：").append(data.getOrDefault("logic", "AND")).append("\n");
            Map<String, Object> symptoms = (Map<String, Object>) data.get("symptoms");
            if (symptoms != null) {
                List<String> required = (List<String>) symptoms.get("required");
                if (required != null && !required.isEmpty()) {
                    sb.append("必备症状：").append(String.join("、", required)).append("\n");
                }
                List<String> anyOf = (List<String>) symptoms.get("anyOf");
                if (anyOf != null && !anyOf.isEmpty()) {
                    sb.append("可选症状（其一）：").append(String.join("、", anyOf)).append("\n");
                }
            }
            Map<String, Object> signs = (Map<String, Object>) data.get("signs");
            if (signs != null) {
                List<String> anyOf = (List<String>) signs.get("anyOf");
                if (anyOf != null && !anyOf.isEmpty()) {
                    sb.append("体征条件：").append(String.join("、", anyOf)).append("\n");
                }
            }
            Map<String, Object> lab = (Map<String, Object>) data.get("lab");
            if (lab != null) {
                List<String> anyOf = (List<String>) lab.get("anyOf");
                if (anyOf != null && !anyOf.isEmpty()) {
                    sb.append("检验条件：").append(String.join("、", anyOf)).append("\n");
                }
            }
            List<String> exclude = (List<String>) data.get("exclude");
            if (exclude != null && !exclude.isEmpty()) {
                sb.append("排除：").append(String.join("、", exclude));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "规则已配置";
        }
    }

    public static String formatRiskRules(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "暂无规则";
        }
        try {
            Map<String, Object> data = JSON.readValue(json, new TypeReference<Map<String, Object>>() {});
            StringBuilder sb = new StringBuilder();
            List<String> high = (List<String>) data.get("highRisk");
            if (high != null && !high.isEmpty()) {
                sb.append("高风险：").append(String.join("、", high)).append("\n");
            }
            List<String> medium = (List<String>) data.get("mediumRisk");
            if (medium != null && !medium.isEmpty()) {
                sb.append("中风险：").append(String.join("、", medium));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "规则已配置";
        }
    }
}
