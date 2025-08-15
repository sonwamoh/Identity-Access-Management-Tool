package com.example.iag.model;

import java.time.Instant;

public class AccessRequest {
    public enum Status { PENDING, APPROVED, REJECTED, PROVISIONED }

    private Long id;
    private String username;
    private String systemName;
    private String requestedRole;
    private String reason;
    private Status status = Status.PENDING;
    private Boolean highRisk;
    private String riskLevel;
    private Instant createdAt;
    private Instant decidedAt;
    private Instant provisionedAt;
    private String decisionNote;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public String getRequestedRole() { return requestedRole; }
    public void setRequestedRole(String requestedRole) { this.requestedRole = requestedRole; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Boolean getHighRisk() { return highRisk; }
    public void setHighRisk(Boolean highRisk) { this.highRisk = highRisk; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }
    public Instant getProvisionedAt() { return provisionedAt; }
    public void setProvisionedAt(Instant provisionedAt) { this.provisionedAt = provisionedAt; }
    public String getDecisionNote() { return decisionNote; }
    public void setDecisionNote(String decisionNote) { this.decisionNote = decisionNote; }
}
