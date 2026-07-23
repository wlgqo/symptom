package com.symptom.service;

import com.symptom.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataScopeService {

    public boolean isAdmin(SysUser user) {
        return user != null && "管理员".equals(user.getRole());
    }

    public boolean hasDistrictScope(SysUser user) {
        return user != null && user.getDistrictScope() != null && !user.getDistrictScope().trim().isEmpty();
    }

    public boolean hasHospitalScope(SysUser user) {
        return user != null && user.getHospitalScope() != null && !user.getHospitalScope().trim().isEmpty();
    }

    public boolean isCityWide(SysUser user) {
        return hasDistrictScope(user) && "成都市".equals(user.getDistrictScope().trim());
    }

    /**
     * 业务人员仅能查看所属辖区、机构数据；管理员不限制。
     * district_scope=成都市 表示全市各区县数据。
     */
    public void applyCaseScope(Map<String, Object> params, SysUser user) {
        if (user == null || isAdmin(user)) {
            return;
        }
        if (isCityWide(user)) {
            params.put("districtsIn", FilterOptionService.getChengduDistricts());
            params.remove("district");
            params.remove("districtScope");
        } else if (hasDistrictScope(user)) {
            params.put("districtScope", user.getDistrictScope().trim());
            params.remove("district");
        }
        if (hasHospitalScope(user)) {
            params.put("hospitalScope", user.getHospitalScope().trim());
            params.remove("hospital");
        }
    }

    public void applyWarningScope(Map<String, Object> params, SysUser user) {
        applyCaseScope(params, user);
    }

    public String scopeDistrict(SysUser user) {
        return hasDistrictScope(user) ? user.getDistrictScope().trim() : null;
    }

    public String scopeHospital(SysUser user) {
        return hasHospitalScope(user) ? user.getHospitalScope().trim() : null;
    }

    /**
     * 未传筛选条件时，默认使用用户所属地区、机构。
     * 全市用户不默认锁定单一区县。
     */
    public String resolveDistrict(String requested, SysUser user) {
        if (requested != null && !requested.trim().isEmpty()) {
            return requested.trim();
        }
        if (isCityWide(user)) {
            return null;
        }
        return scopeDistrict(user);
    }

    public String resolveHospital(String requested, SysUser user) {
        if (requested != null && !requested.trim().isEmpty()) {
            return requested.trim();
        }
        return scopeHospital(user);
    }

    public void putResolvedFilters(Map<String, Object> params, SysUser user, String district, String hospital) {
        String resolvedDistrict = resolveDistrict(district, user);
        String resolvedHospital = resolveHospital(hospital, user);
        if (resolvedDistrict != null && !resolvedDistrict.isEmpty()) {
            params.put("district", resolvedDistrict);
        }
        if (resolvedHospital != null && !resolvedHospital.isEmpty()) {
            params.put("hospital", resolvedHospital);
        }
        applyCaseScope(params, user);
    }
}
