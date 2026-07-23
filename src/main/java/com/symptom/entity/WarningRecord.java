package com.symptom.entity;

import java.util.Date;

public class WarningRecord {
    private Integer id;
    private Integer modelId;
    private String syndromeType;
    private String warningLevel;
    private String warningContent;
    private Date warningTime;
    private String status;
    private String handler;
    private String handleResult;
    private Date handleTime;
    private String modelName;
    private String district;
    private String hospital;
    private String venue;
    private Double observedValue;
    private Double baselineValue;
    private Double thresholdValue;
    private String anomalyDegree;
    private String anomalyType;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getModelId() { return modelId; }
    public void setModelId(Integer modelId) { this.modelId = modelId; }
    public String getSyndromeType() { return syndromeType; }
    public void setSyndromeType(String syndromeType) { this.syndromeType = syndromeType; }
    public String getWarningLevel() { return warningLevel; }
    public void setWarningLevel(String warningLevel) { this.warningLevel = warningLevel; }
    public String getWarningContent() { return warningContent; }
    public void setWarningContent(String warningContent) { this.warningContent = warningContent; }
    public Date getWarningTime() { return warningTime; }
    public void setWarningTime(Date warningTime) { this.warningTime = warningTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHandler() { return handler; }
    public void setHandler(String handler) { this.handler = handler; }
    public String getHandleResult() { return handleResult; }
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }
    public Date getHandleTime() { return handleTime; }
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public Double getObservedValue() { return observedValue; }
    public void setObservedValue(Double observedValue) { this.observedValue = observedValue; }
    public Double getBaselineValue() { return baselineValue; }
    public void setBaselineValue(Double baselineValue) { this.baselineValue = baselineValue; }
    public Double getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(Double thresholdValue) { this.thresholdValue = thresholdValue; }
    public String getAnomalyDegree() { return anomalyDegree; }
    public void setAnomalyDegree(String anomalyDegree) { this.anomalyDegree = anomalyDegree; }
    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }
}
