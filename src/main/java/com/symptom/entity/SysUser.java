package com.symptom.entity;

import java.util.Date;

public class SysUser {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String realName;
    private String districtScope;
    private Date createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getDistrictScope() { return districtScope; }
    public void setDistrictScope(String districtScope) { this.districtScope = districtScope; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
