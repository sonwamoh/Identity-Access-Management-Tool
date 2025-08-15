package com.example.iag.dao;

import com.example.iag.model.RoleRisk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRiskDao {
    private final String url;
    private final String user;
    private final String pass;

    public RoleRiskDao(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
    }

    public Optional<RoleRisk> findByRoleNameIgnoreCase(String roleName) {
        String sql = "SELECT id, role_name, risk_level FROM role_risk WHERE LOWER(role_name)=LOWER(?)";
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RoleRisk r = new RoleRisk();
                    r.setId(rs.getLong(1));
                    r.setRoleName(rs.getString(2));
                    r.setRiskLevel(rs.getString(3));
                    return Optional.of(r);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public List<RoleRisk> findAll() {
        List<RoleRisk> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, user, pass);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, role_name, risk_level FROM role_risk")) {
            while (rs.next()) {
                RoleRisk r = new RoleRisk();
                r.setId(rs.getLong(1));
                r.setRoleName(rs.getString(2));
                r.setRiskLevel(rs.getString(3));
                out.add(r);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
