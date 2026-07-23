package com.symptom.service;

import com.symptom.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MapScopeService {

    public static class MapViewContext {
        private final String level;
        private final String adcode;
        private final String mapKey;
        private final String city;
        private final String title;
        private final String breadcrumbRoot;
        private final boolean allowProvinceDrill;

        public MapViewContext(String level, String adcode, String mapKey, String city,
                              String title, String breadcrumbRoot, boolean allowProvinceDrill) {
            this.level = level;
            this.adcode = adcode;
            this.mapKey = mapKey;
            this.city = city;
            this.title = title;
            this.breadcrumbRoot = breadcrumbRoot;
            this.allowProvinceDrill = allowProvinceDrill;
        }

        public String getLevel() { return level; }
        public String getAdcode() { return adcode; }
        public String getMapKey() { return mapKey; }
        public String getCity() { return city; }
        public String getTitle() { return title; }
        public String getBreadcrumbRoot() { return breadcrumbRoot; }
        public boolean isAllowProvinceDrill() { return allowProvinceDrill; }
        public String getGeoUrl() {
            return "https://geo.datav.aliyun.com/areas_v3/bound/" + adcode + "_full.json";
        }
    }

    public MapViewContext resolve(SysUser user, DataScopeService dataScopeService, String district) {
        String resolved = dataScopeService.resolveDistrict(district, user);
        boolean provincial = dataScopeService.isProvincial(user) || dataScopeService.isAdmin(user);

        if (provincial && (resolved == null || resolved.isEmpty())) {
            return new MapViewContext("province", "510000", "sichuan", null,
                    "四川省", "全省", true);
        }

        if ("成都市".equals(resolved)) {
            return new MapViewContext("city", "510100", "chengdu", "成都市",
                    "成都市", provincial ? "全省" : "全市", provincial);
        }

        if (resolved != null && FilterOptionService.isPrefectureCity(resolved)) {
            String adcode = FilterOptionService.getCityAdcode(resolved);
            return new MapViewContext("city", adcode, "city_" + adcode, resolved, resolved,
                    provincial ? "全省" : "全市", provincial);
        }

        if (resolved != null && FilterOptionService.getChengduDistricts().contains(resolved)) {
            return new MapViewContext("city", "510100", "chengdu", "成都市", resolved,
                    provincial ? "全省" : "全市", provincial);
        }

        if (dataScopeService.isCityWide(user) && (resolved == null || resolved.isEmpty())) {
            return new MapViewContext("city", "510100", "chengdu", "成都市",
                    "成都市", "全市", false);
        }

        return new MapViewContext("city", "510100", "chengdu", "成都市",
                resolved != null ? resolved : "成都市",
                provincial ? "全省" : "全市", provincial);
    }

    public static void expandCityDistrictParam(Map<String, Object> params) {
        Object district = params.get("district");
        if ("成都市".equals(district)) {
            params.put("districtsIn", FilterOptionService.getChengduDistricts());
            params.remove("district");
        }
    }

    public List<Map<String, Object>> aggregateMapData(List<Map<String, Object>> raw, String mapLevel) {
        if (!"province".equals(mapLevel)) {
            return raw;
        }
        int chengduSum = 0;
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> chengdu = new HashSet<>(FilterOptionService.getChengduDistricts());
        for (Map<String, Object> row : raw) {
            String name = String.valueOf(row.get("name"));
            int value = ((Number) row.get("value")).intValue();
            if (chengdu.contains(name)) {
                chengduSum += value;
            } else {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", name);
                item.put("value", value);
                result.add(item);
            }
        }
        Map<String, Object> chengduItem = new LinkedHashMap<>();
        chengduItem.put("name", "成都市");
        chengduItem.put("value", chengduSum);
        result.add(chengduItem);
        result.sort((a, b) -> Integer.compare(
                ((Number) b.get("value")).intValue(),
                ((Number) a.get("value")).intValue()));
        return result;
    }
}
