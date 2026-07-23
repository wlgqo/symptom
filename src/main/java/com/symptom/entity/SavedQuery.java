package com.symptom.entity;

import java.util.Date;

public class SavedQuery {
    private Integer id;
    private String queryName;
    private String syndromeType;
    private String conditionJson;
    private String createdBy;
    private Date createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getQueryName() { return queryName; }
    public void setQueryName(String queryName) { this.queryName = queryName; }
    public String getSyndromeType() { return syndromeType; }
    public void setSyndromeType(String syndromeType) { this.syndromeType = syndromeType; }
    public String getConditionJson() { return conditionJson; }
    public void setConditionJson(String conditionJson) { this.conditionJson = conditionJson; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
