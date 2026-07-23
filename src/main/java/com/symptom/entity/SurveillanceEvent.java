package com.symptom.entity;

import java.util.Date;

public class SurveillanceEvent {
    private Integer id;
    private String eventName;
    private String eventType;
    private String syndromeType;
    private String district;
    private String venue;
    private String relatedCases;
    private String relatedWarnings;
    private String status;
    private String responsiblePerson;
    private Date discoveryTime;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSyndromeType() { return syndromeType; }
    public void setSyndromeType(String syndromeType) { this.syndromeType = syndromeType; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getRelatedCases() { return relatedCases; }
    public void setRelatedCases(String relatedCases) { this.relatedCases = relatedCases; }
    public String getRelatedWarnings() { return relatedWarnings; }
    public void setRelatedWarnings(String relatedWarnings) { this.relatedWarnings = relatedWarnings; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }
    public Date getDiscoveryTime() { return discoveryTime; }
    public void setDiscoveryTime(Date discoveryTime) { this.discoveryTime = discoveryTime; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
