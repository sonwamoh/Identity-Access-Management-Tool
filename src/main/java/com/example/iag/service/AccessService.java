package com.example.iag.service;

import com.example.iag.dao.AccessRequestDao;
import com.example.iag.dao.RoleRiskDao;
import com.example.iag.model.AccessRequest;
import com.example.iag.model.RoleRisk;

import java.time.Instant;

public class AccessService {
    private final AccessRequestDao reqDao;
    private final RoleRiskDao riskDao;

    public AccessService(AccessRequestDao reqDao, RoleRiskDao riskDao) {
        this.reqDao = reqDao;
        this.riskDao = riskDao;
    }

    public AccessRequest submit(AccessRequest req) {
        String role = req.getRequestedRole() == null ? "" : req.getRequestedRole().trim();
        RoleRisk risk = riskDao.findByRoleNameIgnoreCase(role).orElse(null);
        if (risk != null) {
            req.setHighRisk("HIGH".equalsIgnoreCase(risk.getRiskLevel()));
            req.setRiskLevel(risk.getRiskLevel());
        } else {
            req.setHighRisk(false);
            req.setRiskLevel(null);
        }
        req.setStatus(AccessRequest.Status.PENDING);
        req.setCreatedAt(Instant.now());
        return reqDao.save(req);
    }

    public AccessRequest approve(Long id, String note) {
        AccessRequest r = reqDao.findById(id).orElseThrow();
        r.setStatus(AccessRequest.Status.PROVISIONED);
        r.setDecidedAt(Instant.now());
        r.setProvisionedAt(Instant.now());
        r.setDecisionNote(note);
        return reqDao.save(r);
    }

    public AccessRequest reject(Long id, String reason) {
        AccessRequest r = reqDao.findById(id).orElseThrow();
        r.setStatus(AccessRequest.Status.REJECTED);
        r.setDecidedAt(Instant.now());
        r.setDecisionNote(reason);
        return reqDao.save(r);
    }
}
