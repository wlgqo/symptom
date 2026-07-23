package com.symptom.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilterOptionService {

    private static final List<String> CHENGDU_DISTRICTS = Collections.unmodifiableList(Arrays.asList(
            "锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区",
            "青白江区", "新都区", "温江区", "双流区", "郫都区", "新津区"
    ));

    private static final Map<String, List<String>> HOSPITALS_BY_DISTRICT = new LinkedHashMap<>();

    static {
        HOSPITALS_BY_DISTRICT.put("锦江区", Arrays.asList("四川省人民医院", "锦江区人民医院"));
        HOSPITALS_BY_DISTRICT.put("青羊区", Arrays.asList("华西医院", "青羊区人民医院"));
        HOSPITALS_BY_DISTRICT.put("武侯区", Arrays.asList("成都市第三人民医院", "武侯区人民医院"));
        HOSPITALS_BY_DISTRICT.put("成华区", Arrays.asList("成都市第六人民医院", "成华区人民医院"));
        HOSPITALS_BY_DISTRICT.put("金牛区", Arrays.asList("成都市金牛区人民医院"));
        HOSPITALS_BY_DISTRICT.put("龙泉驿区", Arrays.asList("龙泉驿区第一人民医院"));
        HOSPITALS_BY_DISTRICT.put("青白江区", Arrays.asList("青白江区人民医院"));
        HOSPITALS_BY_DISTRICT.put("新都区", Arrays.asList("新都区人民医院"));
        HOSPITALS_BY_DISTRICT.put("温江区", Arrays.asList("温江区人民医院"));
        HOSPITALS_BY_DISTRICT.put("双流区", Arrays.asList("双流区第一人民医院"));
        HOSPITALS_BY_DISTRICT.put("郫都区", Arrays.asList("郫都区人民医院"));
        HOSPITALS_BY_DISTRICT.put("新津区", Arrays.asList("新津区人民医院"));
    }

    public static List<String> getChengduDistricts() {
        return CHENGDU_DISTRICTS;
    }

    public List<String> getDistricts() {
        return CHENGDU_DISTRICTS;
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
