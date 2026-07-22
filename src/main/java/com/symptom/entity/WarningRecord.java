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
}
