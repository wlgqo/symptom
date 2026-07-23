package com.symptom.service;

import com.symptom.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilterOptionService {

    private static final List<String> CHENGDU_DISTRICTS = Collections.unmodifiableList(Arrays.asList(
            "锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区",
            "青白江区", "新都区", "温江区", "双流区", "郫都区", "新津区"
    ));

    private static final List<String> PROVINCIAL_DISTRICTS = Collections.unmodifiableList(Arrays.asList(
            "锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区",
            "青白江区", "新都区", "温江区", "双流区", "郫都区", "新津区",
            "绵阳市", "德阳市", "宜宾市", "乐山市", "南充市", "泸州市"
    ));

    private static final Map<String, String> CITY_ADCODE = new LinkedHashMap<>();

    static {
        CITY_ADCODE.put("成都市", "510100");
        CITY_ADCODE.put("绵阳市", "510700");
        CITY_ADCODE.put("德阳市", "510600");
        CITY_ADCODE.put("宜宾市", "511500");
        CITY_ADCODE.put("乐山市", "511100");
        CITY_ADCODE.put("南充市", "511300");
        CITY_ADCODE.put("泸州市", "510500");
    }

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
        HOSPITALS_BY_DISTRICT.put("绵阳市", Arrays.asList("绵阳市中心医院"));
        HOSPITALS_BY_DISTRICT.put("德阳市", Arrays.asList("德阳市人民医院"));
        HOSPITALS_BY_DISTRICT.put("宜宾市", Arrays.asList("宜宾市第一人民医院"));
        HOSPITALS_BY_DISTRICT.put("乐山市", Arrays.asList("乐山市人民医院"));
        HOSPITALS_BY_DISTRICT.put("南充市", Arrays.asList("南充市中心医院"));
        HOSPITALS_BY_DISTRICT.put("泸州市", Arrays.asList("泸州市人民医院"));
    }

    public static final List<String> WARNING_REGIONS = Collections.unmodifiableList(Arrays.asList(
            "四川省", "成都市", "锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区",
            "绵阳市", "德阳市", "宜宾市"
    ));

    public static final List<String> WARNING_TYPES = Collections.unmodifiableList(Arrays.asList(
            "病例数异常", "趋势异常", "同比异常", "环比异常", "聚集性异常", "重症异常", "死亡异常"
    ));

    public static List<String> getChengduDistricts() {
        return CHENGDU_DISTRICTS;
    }

    public static List<String> getProvincialDistricts() {
        return PROVINCIAL_DISTRICTS;
    }

    public static boolean isPrefectureCity(String name) {
        return name != null && CITY_ADCODE.containsKey(name.trim());
    }

    public static String getCityAdcode(String city) {
        return CITY_ADCODE.get(city);
    }

    public static Map<String, String> getCityAdcodeMap() {
        return CITY_ADCODE;
    }

    public List<String> getDistricts() {
        return CHENGDU_DISTRICTS;
    }

    public List<String> getDistrictsForUser(SysUser user, DataScopeService dataScopeService) {
        if (user == null || dataScopeService.isAdmin(user) || dataScopeService.isProvincial(user)) {
            return PROVINCIAL_DISTRICTS;
        }
        if (dataScopeService.isCityWide(user)) {
            return CHENGDU_DISTRICTS;
        }
        if (dataScopeService.hasDistrictScope(user)) {
            return Collections.singletonList(user.getDistrictScope().trim());
        }
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
