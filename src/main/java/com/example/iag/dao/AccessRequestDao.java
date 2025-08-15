package com.example.iag.dao;

import com.example.iag.model.AccessRequest;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccessRequestDao {
    private final String url;
    private final String user;
    private final String pass;

    public AccessRequestDao(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
    }

    public AccessRequest save(AccessRequest r) {
        if (r.getId() == null) return insert(r);
        else return update(r);
    }

    private AccessRequest insert(AccessRequest r) {
        String sql = "INSERT INTO access_request (username, system_name, requested_role, reason, status, high_risk, risk_level, created_at, decision_note) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getUsername());
            ps.setString(2, r.getSystemName());
            ps.setString(3, r.getRequestedRole());
            ps.setString(4, r.getReason());
            ps.setString(5, r.getStatus().name());
            ps.setBoolean(6, r.getHighRisk() != null && r.getHighRisk());
            ps.setString(7, r.getRiskLevel());
            ps.setTimestamp(8, r.getCreatedAt() == null ? Timestamp.from(Instant.now()) : Timestamp.from(r.getCreatedAt()));
            ps.setString(9, r.getDecisionNote());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getLong(1));
            }
            return r;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private AccessRequest update(AccessRequest r) {
        String sql = "UPDATE access_request SET username=?, system_name=?, requested_role=?, reason=?, status=?, high_risk=?, risk_level=?, decided_at=?, provisioned_at=?, decision_note=? WHERE id=?";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getUsername());
            ps.setString(2, r.getSystemName());
            ps.setString(3, r.getRequestedRole());
            ps.setString(4, r.getReason());
            ps.setString(5, r.getStatus().name());
            ps.setBoolean(6, r.getHighRisk() != null && r.getHighRisk());
            ps.setString(7, r.getRiskLevel());
            ps.setTimestamp(8, r.getDecidedAt() == null ? null : Timestamp.from(r.getDecidedAt()));
            ps.setTimestamp(9, r.getProvisionedAt() == null ? null : Timestamp.from(r.getProvisionedAt()));
            ps.setString(10, r.getDecisionNote());
            ps.setLong(11, r.getId());
            ps.executeUpdate();
            return r;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<AccessRequest> findById(Long id) {
        String sql = "SELECT id, username, system_name, requested_role, reason, status, high_risk, risk_level, created_at, decided_at, provisioned_at, decision_note FROM access_request WHERE id=?";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public List<AccessRequest> findAll() {
        List<AccessRequest> out = new ArrayList<>();
        String sql = "SELECT id, username, system_name, requested_role, reason, status, high_risk, risk_level, created_at, decided_at, provisioned_at, decision_note FROM access_request ORDER BY id DESC";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    public List<AccessRequest> findByStatus(AccessRequest.Status status) {
        List<AccessRequest> out = new ArrayList<>();
        String sql = "SELECT * FROM access_request WHERE status=?";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    public List<AccessRequest> findByUsernameIgnoreCase(String username) {
        List<AccessRequest> out = new ArrayList<>();
        String sql = "SELECT * FROM access_request WHERE LOWER(username)=LOWER(?)";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private AccessRequest map(ResultSet rs) throws SQLException {
        AccessRequest r = new AccessRequest();
        r.setId(rs.getLong("id"));
        r.setUsername(rs.getString("username"));
        r.setSystemName(rs.getString("system_name"));
        r.setRequestedRole(rs.getString("requested_role"));
        r.setReason(rs.getString("reason"));
        r.setStatus(AccessRequest.Status.valueOf(rs.getString("status")));
        r.setHighRisk(rs.getBoolean("high_risk"));
        r.setRiskLevel(rs.getString("risk_level"));
        Timestamp t = rs.getTimestamp("created_at");
        if (t != null) r.setCreatedAt(t.toInstant());
        t = rs.getTimestamp("decided_at");
        if (t != null) r.setDecidedAt(t.toInstant());
        t = rs.getTimestamp("provisioned_at");
        if (t != null) r.setProvisionedAt(t.toInstant());
        r.setDecisionNote(rs.getString("decision_note"));
        return r;
    }
}
