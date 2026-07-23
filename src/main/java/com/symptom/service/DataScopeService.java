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

    /**
     * 业务人员仅能查看所属辖区数据；管理员不限制。
     */
    public void applyCaseScope(Map<String, Object> params, SysUser user) {
        if (user == null || isAdmin(user)) {
            return;
        }
        if (hasDistrictScope(user)) {
            params.put("districtScope", user.getDistrictScope().trim());
            params.remove("district");
        }
    }

    public void applyWarningScope(Map<String, Object> params, SysUser user) {
        applyCaseScope(params, user);
    }

    public String scopeDistrict(SysUser user) {
        return hasDistrictScope(user) ? user.getDistrictScope().trim() : null;
    }
}
