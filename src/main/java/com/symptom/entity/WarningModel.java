package com.symptom.entity;

public class WarningModel {
    private Integer id;
    private String modelName;
    private String modelType;
    private String syndromeType;
    private String regionScope;
    private String warningType;
    private String levelThresholdJson;
    private String configJson;
    private String description;
    private Integer enabled;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    public String getSyndromeType() { return syndromeType; }
    public void setSyndromeType(String syndromeType) { this.syndromeType = syndromeType; }
    public String getRegionScope() { return regionScope; }
    public void setRegionScope(String regionScope) { this.regionScope = regionScope; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public String getLevelThresholdJson() { return levelThresholdJson; }
    public void setLevelThresholdJson(String levelThresholdJson) { this.levelThresholdJson = levelThresholdJson; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
