package com.symptom.entity;

import java.util.Date;

public class WarningNotification {
    private Integer id;
    private Integer warningId;
    private String notifyTarget;
    private String notifyMethod;
    private Date notifyTime;
    private String notifyStatus;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getWarningId() { return warningId; }
    public void setWarningId(Integer warningId) { this.warningId = warningId; }
    public String getNotifyTarget() { return notifyTarget; }
    public void setNotifyTarget(String notifyTarget) { this.notifyTarget = notifyTarget; }
    public String getNotifyMethod() { return notifyMethod; }
    public void setNotifyMethod(String notifyMethod) { this.notifyMethod = notifyMethod; }
    public Date getNotifyTime() { return notifyTime; }
    public void setNotifyTime(Date notifyTime) { this.notifyTime = notifyTime; }
    public String getNotifyStatus() { return notifyStatus; }
    public void setNotifyStatus(String notifyStatus) { this.notifyStatus = notifyStatus; }
}
