package com.symptom.entity;

import java.util.Date;
import java.util.List;

public class CaseInfo {
    private Integer id;
    private String mainIndex;
    private String patientName;
    private String gender;
    private Integer age;
    private String occupation;
    private String caseType;
    private String syndromeType;
    private String address;
    private String district;
    private String discoverType;
    private String diagnosis;
    private String outcome;
    private Integer isSevere;
    private Integer isDeath;
    private String riskLevel;
    private String riskReason;
    private Date reportDate;
    private String hospital;
    private Double feverTemp;
    private String clinicalJson;
    private String labJson;
    private String treatmentJson;
    private Date createdAt;
    private Date updatedAt;
    private List<String> symptoms;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMainIndex() { return mainIndex; }
    public void setMainIndex(String mainIndex) { this.mainIndex = mainIndex; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public String getSyndromeType() { return syndromeType; }
    public void setSyndromeType(String syndromeType) { this.syndromeType = syndromeType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDiscoverType() { return discoverType; }
    public void setDiscoverType(String discoverType) { this.discoverType = discoverType; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public Integer getIsSevere() { return isSevere; }
    public void setIsSevere(Integer isSevere) { this.isSevere = isSevere; }
    public Integer getIsDeath() { return isDeath; }
    public void setIsDeath(Integer isDeath) { this.isDeath = isDeath; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getRiskReason() { return riskReason; }
    public void setRiskReason(String riskReason) { this.riskReason = riskReason; }
    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public Double getFeverTemp() { return feverTemp; }
    public void setFeverTemp(Double feverTemp) { this.feverTemp = feverTemp; }
    public String getClinicalJson() { return clinicalJson; }
    public void setClinicalJson(String clinicalJson) { this.clinicalJson = clinicalJson; }
    public String getLabJson() { return labJson; }
    public void setLabJson(String labJson) { this.labJson = labJson; }
    public String getTreatmentJson() { return treatmentJson; }
    public void setTreatmentJson(String treatmentJson) { this.treatmentJson = treatmentJson; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }
}
