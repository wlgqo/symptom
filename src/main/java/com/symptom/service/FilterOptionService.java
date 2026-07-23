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

    private static final List<String> PREFECTURE_CITIES = Collections.unmodifiableList(Arrays.asList(
            "绵阳市", "德阳市", "宜宾市", "乐山市", "南充市", "泸州市",
            "自贡市", "攀枝花市", "广元市", "遂宁市", "内江市", "眉山市",
            "广安市", "达州市", "雅安市", "巴中市", "资阳市",
            "阿坝州", "甘孜州", "凉山州"
    ));

    private static final List<String> PROVINCIAL_DISTRICTS;

    static {
        List<String> all = new ArrayList<>(CHENGDU_DISTRICTS);
        all.addAll(PREFECTURE_CITIES);
        PROVINCIAL_DISTRICTS = Collections.unmodifiableList(all);
    }

    private static final Map<String, String> CITY_ADCODE = new LinkedHashMap<>();

    static {
        CITY_ADCODE.put("成都市", "510100");
        CITY_ADCODE.put("自贡市", "510300");
        CITY_ADCODE.put("攀枝花市", "510400");
        CITY_ADCODE.put("泸州市", "510500");
        CITY_ADCODE.put("德阳市", "510600");
        CITY_ADCODE.put("绵阳市", "510700");
        CITY_ADCODE.put("广元市", "510800");
        CITY_ADCODE.put("遂宁市", "510900");
        CITY_ADCODE.put("内江市", "511000");
        CITY_ADCODE.put("乐山市", "511100");
        CITY_ADCODE.put("南充市", "511300");
        CITY_ADCODE.put("眉山市", "511400");
        CITY_ADCODE.put("宜宾市", "511500");
        CITY_ADCODE.put("广安市", "511600");
        CITY_ADCODE.put("达州市", "511700");
        CITY_ADCODE.put("雅安市", "511800");
        CITY_ADCODE.put("巴中市", "511900");
        CITY_ADCODE.put("资阳市", "512000");
        CITY_ADCODE.put("阿坝州", "513200");
        CITY_ADCODE.put("甘孜州", "513300");
        CITY_ADCODE.put("凉山州", "513400");
    }

    /** 数据库区县名称 → 省级地图 GeoJSON 名称 */
    private static final Map<String, String> DISTRICT_TO_GEO_NAME = new LinkedHashMap<>();

    static {
        DISTRICT_TO_GEO_NAME.put("阿坝州", "阿坝藏族羌族自治州");
        DISTRICT_TO_GEO_NAME.put("甘孜州", "甘孜藏族自治州");
        DISTRICT_TO_GEO_NAME.put("凉山州", "凉山彝族自治州");
        for (String city : PREFECTURE_CITIES) {
            DISTRICT_TO_GEO_NAME.putIfAbsent(city, city);
        }
    }

    /** 地市下钻地图时，将市级汇总数据映射到中心城区 */
    private static final Map<String, String> CITY_CENTRAL_DISTRICT = new LinkedHashMap<>();

    static {
        CITY_CENTRAL_DISTRICT.put("绵阳市", "涪城区");
        CITY_CENTRAL_DISTRICT.put("德阳市", "旌阳区");
        CITY_CENTRAL_DISTRICT.put("宜宾市", "翠屏区");
        CITY_CENTRAL_DISTRICT.put("乐山市", "市中区");
        CITY_CENTRAL_DISTRICT.put("南充市", "顺庆区");
        CITY_CENTRAL_DISTRICT.put("泸州市", "江阳区");
        CITY_CENTRAL_DISTRICT.put("自贡市", "自流井区");
        CITY_CENTRAL_DISTRICT.put("攀枝花市", "东区");
        CITY_CENTRAL_DISTRICT.put("广元市", "利州区");
        CITY_CENTRAL_DISTRICT.put("遂宁市", "船山区");
        CITY_CENTRAL_DISTRICT.put("内江市", "市中区");
        CITY_CENTRAL_DISTRICT.put("眉山市", "东坡区");
        CITY_CENTRAL_DISTRICT.put("广安市", "广安区");
        CITY_CENTRAL_DISTRICT.put("达州市", "通川区");
        CITY_CENTRAL_DISTRICT.put("雅安市", "雨城区");
        CITY_CENTRAL_DISTRICT.put("巴中市", "巴州区");
        CITY_CENTRAL_DISTRICT.put("资阳市", "雁江区");
        CITY_CENTRAL_DISTRICT.put("阿坝州", "马尔康市");
        CITY_CENTRAL_DISTRICT.put("甘孜州", "康定市");
        CITY_CENTRAL_DISTRICT.put("凉山州", "西昌市");
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
        HOSPITALS_BY_DISTRICT.put("自贡市", Arrays.asList("自贡市第一人民医院"));
        HOSPITALS_BY_DISTRICT.put("攀枝花市", Arrays.asList("攀枝花市中心医院"));
        HOSPITALS_BY_DISTRICT.put("广元市", Arrays.asList("广元市中心医院"));
        HOSPITALS_BY_DISTRICT.put("遂宁市", Arrays.asList("遂宁市中心医院"));
        HOSPITALS_BY_DISTRICT.put("内江市", Arrays.asList("内江市第一人民医院"));
        HOSPITALS_BY_DISTRICT.put("眉山市", Arrays.asList("眉山市人民医院"));
        HOSPITALS_BY_DISTRICT.put("广安市", Arrays.asList("广安市人民医院"));
        HOSPITALS_BY_DISTRICT.put("达州市", Arrays.asList("达州市中心医院"));
        HOSPITALS_BY_DISTRICT.put("雅安市", Arrays.asList("雅安市人民医院"));
        HOSPITALS_BY_DISTRICT.put("巴中市", Arrays.asList("巴中市中心医院"));
        HOSPITALS_BY_DISTRICT.put("资阳市", Arrays.asList("资阳市人民医院"));
        HOSPITALS_BY_DISTRICT.put("阿坝州", Arrays.asList("阿坝州人民医院"));
        HOSPITALS_BY_DISTRICT.put("甘孜州", Arrays.asList("甘孜州人民医院"));
        HOSPITALS_BY_DISTRICT.put("凉山州", Arrays.asList("凉山州第一人民医院"));
    }

    public static final List<String> WARNING_REGIONS = Collections.unmodifiableList(Arrays.asList(
            "四川省", "成都市", "锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区",
            "绵阳市", "德阳市", "宜宾市", "乐山市", "南充市", "泸州市", "自贡市", "攀枝花市",
            "广元市", "遂宁市", "内江市", "眉山市", "广安市", "达州市", "雅安市", "巴中市", "资阳市"
    ));

    public static final List<String> WARNING_TYPES = Collections.unmodifiableList(Arrays.asList(
            "病例数异常", "趋势异常", "同比异常", "环比异常", "聚集性异常", "重症异常", "死亡异常"
    ));

    public static List<String> getChengduDistricts() {
        return CHENGDU_DISTRICTS;
    }

    public static List<String> getPrefectureCities() {
        return PREFECTURE_CITIES;
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

    public static Map<String, String> getDistrictGeoNameMap() {
        return DISTRICT_TO_GEO_NAME;
    }

    public static Map<String, String> getCityCentralDistrictMap() {
        return CITY_CENTRAL_DISTRICT;
    }

    public static String toGeoName(String district) {
        if (district == null) {
            return null;
        }
        return DISTRICT_TO_GEO_NAME.getOrDefault(district.trim(), district.trim());
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
