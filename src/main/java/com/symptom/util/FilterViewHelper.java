package com.symptom.util;

import com.symptom.entity.SysUser;
import com.symptom.service.DataScopeService;
import com.symptom.service.FilterOptionService;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

public final class FilterViewHelper {

    private FilterViewHelper() {
    }

    public static Map<String, Object> buildScopedFilter(DataScopeService dataScopeService,
                                                        SysUser user,
                                                        String syndromeType,
                                                        String district,
                                                        String hospital,
                                                        String startDate,
                                                        String endDate,
                                                        Integer days) {
        Map<String, Object> params = QueryParamUtil.baseFilter(syndromeType,
                dataScopeService.resolveDistrict(district, user),
                dataScopeService.resolveHospital(hospital, user),
                startDate, endDate, days);
        dataScopeService.applyCaseScope(params, user);
        return params;
    }

    public static void addRegionHospitalModel(Model model, FilterOptionService filterOptionService,
                                              DataScopeService dataScopeService, SysUser user,
                                              String district, String hospital) {
        String resolvedDistrict = dataScopeService.resolveDistrict(district, user);
        String resolvedHospital = dataScopeService.resolveHospital(hospital, user);
        model.addAttribute("filterDistrict", resolvedDistrict);
        model.addAttribute("filterHospital", resolvedHospital);
        model.addAttribute("districtOptions", filterOptionService.getDistrictsForUser(user, dataScopeService));
        model.addAttribute("hospitalOptions", filterOptionService.getHospitals(resolvedDistrict));
        model.addAttribute("scopeDistrict", dataScopeService.scopeDistrict(user));
        model.addAttribute("scopeHospital", dataScopeService.scopeHospital(user));
        model.addAttribute("hospitalMap", filterOptionService.getHospitalMap());
    }
}
