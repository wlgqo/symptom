package com.symptom.entity;

import java.util.Date;

public class CaseModifyLog {
    private Integer id;
    private Integer caseId;
    private String operator;
    private Date modifyTime;
    private String snapshot;
    private String changeDesc;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCaseId() { return caseId; }
    public void setCaseId(Integer caseId) { this.caseId = caseId; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getModifyTime() { return modifyTime; }
    public void setModifyTime(Date modifyTime) { this.modifyTime = modifyTime; }
    public String getSnapshot() { return snapshot; }
    public void setSnapshot(String snapshot) { this.snapshot = snapshot; }
    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }
}
