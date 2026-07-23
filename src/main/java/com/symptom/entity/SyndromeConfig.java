package com.symptom.entity;

public class SyndromeConfig {
    private Integer id;
    private String syndromeName;
    private String syndromeCode;
    private String definition;
    private String symptomRulesJson;
    private String riskRulesJson;
    private String monitorModelJson;
    private String status;
    private String description;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSyndromeName() { return syndromeName; }
    public void setSyndromeName(String syndromeName) { this.syndromeName = syndromeName; }
    public String getSyndromeCode() { return syndromeCode; }
    public void setSyndromeCode(String syndromeCode) { this.syndromeCode = syndromeCode; }
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
    public String getSymptomRulesJson() { return symptomRulesJson; }
    public void setSymptomRulesJson(String symptomRulesJson) { this.symptomRulesJson = symptomRulesJson; }
    public String getRiskRulesJson() { return riskRulesJson; }
    public void setRiskRulesJson(String riskRulesJson) { this.riskRulesJson = riskRulesJson; }
    public String getMonitorModelJson() { return monitorModelJson; }
    public void setMonitorModelJson(String monitorModelJson) { this.monitorModelJson = monitorModelJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
