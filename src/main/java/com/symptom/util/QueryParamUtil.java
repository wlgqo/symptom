package com.symptom.util;

import java.util.HashMap;
import java.util.Map;

public final class QueryParamUtil {

    private QueryParamUtil() {
    }

    public static Map<String, Object> baseFilter(String syndromeType, String district,
                                                  String startDate, String endDate, Integer days) {
        Map<String, Object> params = new HashMap<>();
        if (syndromeType != null && !syndromeType.isEmpty()) {
            params.put("syndromeType", syndromeType);
        }
        if (district != null && !district.isEmpty()) {
            params.put("district", district);
        }
        if (startDate != null && !startDate.isEmpty()) {
            params.put("startDate", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.put("endDate", endDate);
        }
        if (days != null && days > 0 && (startDate == null || startDate.isEmpty())) {
            params.put("days", days);
        }
        return params;
    }

    public static void applyPagination(Map<String, Object> params, Integer page, Integer pageSize) {
        int p = page == null || page < 1 ? 1 : page;
        int size = pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 100);
        params.put("page", p);
        params.put("pageSize", size);
        params.put("offset", (p - 1) * size);
        params.put("limit", size);
    }
}
