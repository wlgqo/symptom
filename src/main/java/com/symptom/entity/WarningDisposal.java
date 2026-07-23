package com.symptom.entity;

import java.util.Date;

public class WarningDisposal {
    private Integer id;
    private Integer warningId;
    private String operator;
    private Date actionTime;
    private String actionType;
    private String actionComment;
    private String attachment;
    private String remark;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getWarningId() { return warningId; }
    public void setWarningId(Integer warningId) { this.warningId = warningId; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getActionTime() { return actionTime; }
    public void setActionTime(Date actionTime) { this.actionTime = actionTime; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionComment() { return actionComment; }
    public void setActionComment(String actionComment) { this.actionComment = actionComment; }
    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
