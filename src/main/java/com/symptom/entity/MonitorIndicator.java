package com.symptom.entity;

public class MonitorIndicator {
    private Integer id;
    private String indicatorCode;
    private String indicatorName;
    private String category;
    private String syndromeType;
    private String description;
    private String formula;
    private String unit;
    private String thresholdJson;
    private String dataSource;
    private String status;
    private Integer sortOrder;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getIndicatorCode() { return indicatorCode; }
    public void setIndicatorCode(String indicatorCode) { this.indicatorCode = indicatorCode; }
    public String getIndicatorName() { return indicatorName; }
    public void setIndicatorName(String indicatorName) { this.indicatorName = indicatorName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSyndromeType() { return syndromeType; }
    public void setSyndromeType(String syndromeType) { this.syndromeType = syndromeType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getThresholdJson() { return thresholdJson; }
    public void setThresholdJson(String thresholdJson) { this.thresholdJson = thresholdJson; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
