package com.symptom.entity;

import java.util.Date;

public class ReportCard {
    private Integer id;
    private Integer caseId;
    private String cardNo;
    private String reportType;
    private Date reportDate;
    private String reporter;
    private String status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCaseId() { return caseId; }
    public void setCaseId(Integer caseId) { this.caseId = caseId; }
    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }
    public String getReporter() { return reporter; }
    public void setReporter(String reporter) { this.reporter = reporter; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
