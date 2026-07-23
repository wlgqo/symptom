package com.symptom.entity;

public class CaseSymptom {
    private Integer id;
    private Integer caseId;
    private String symptomName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCaseId() { return caseId; }
    public void setCaseId(Integer caseId) { this.caseId = caseId; }
    public String getSymptomName() { return symptomName; }
    public void setSymptomName(String symptomName) { this.symptomName = symptomName; }
}
