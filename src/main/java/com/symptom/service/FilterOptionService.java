package com.symptom.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilterOptionService {

    private static final List<String> DISTRICTS = Arrays.asList(
            "锦江区", "青羊区", "武侯区", "成华区", "金牛区", "龙泉驿区",
            "德阳市", "绵阳市", "宜宾市"
    );

    private static final Map<String, List<String>> HOSPITALS_BY_DISTRICT = new LinkedHashMap<>();

    static {
        HOSPITALS_BY_DISTRICT.put("锦江区", Arrays.asList("四川省人民医院", "锦江区人民医院"));
        HOSPITALS_BY_DISTRICT.put("青羊区", Arrays.asList("华西医院", "青羊区人民医院"));
        HOSPITALS_BY_DISTRICT.put("武侯区", Arrays.asList("成都市第三人民医院", "武侯区人民医院"));
        HOSPITALS_BY_DISTRICT.put("成华区", Arrays.asList("成都市第六人民医院", "成华区人民医院"));
        HOSPITALS_BY_DISTRICT.put("金牛区", Arrays.asList("成都市金牛区人民医院"));
        HOSPITALS_BY_DISTRICT.put("龙泉驿区", Arrays.asList("龙泉驿区第一人民医院"));
        HOSPITALS_BY_DISTRICT.put("德阳市", Arrays.asList("德阳市人民医院"));
        HOSPITALS_BY_DISTRICT.put("绵阳市", Arrays.asList("绵阳市中心医院"));
        HOSPITALS_BY_DISTRICT.put("宜宾市", Arrays.asList("宜宾市第一人民医院"));
    }

    public List<String> getDistricts() {
        return DISTRICTS;
    }

    public List<String> getHospitals(String district) {
        if (district == null || district.isEmpty()) {
            Set<String> all = new LinkedHashSet<>();
            HOSPITALS_BY_DISTRICT.values().forEach(all::addAll);
            return new ArrayList<>(all);
        }
        return HOSPITALS_BY_DISTRICT.getOrDefault(district, Collections.emptyList());
    }

    public Map<String, List<String>> getHospitalMap() {
        return HOSPITALS_BY_DISTRICT;
    }
}
