package com.example.iag.db;

import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

public class Database {
    public static final String URL = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    public static final String USER = "sa";
    public static final String PASS = "";

    public static void init() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // Create tables
            try (Statement s = conn.createStatement()) {
                s.execute("""
                    CREATE TABLE IF NOT EXISTS role_risk (
                        id BIGSERIAL PRIMARY KEY,
                        role_name VARCHAR(255) UNIQUE NOT NULL,
                        risk_level VARCHAR(50)
                    );
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS access_request (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(255),
                        system_name VARCHAR(255),
                        requested_role VARCHAR(255),
                        reason VARCHAR(1000),
                        status VARCHAR(50),
                        high_risk BOOLEAN,
                        risk_level VARCHAR(50),
                        created_at TIMESTAMP,
                        decided_at TIMESTAMP,
                        provisioned_at TIMESTAMP,
                        decision_note VARCHAR(1000)
                    );
                """);
            }

            // Run data.sql from resources (if present)
            InputStream is = Database.class.getResourceAsStream("/data.sql");
            if (is != null) {
                String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                for (String stmt : sql.split(";")) {
                    String t = stmt.trim();
                    if (!t.isEmpty()) {
                        try (Statement s = conn.createStatement()) {
                            s.execute(t);
                        } catch (SQLException e) {
                            // Ignore individual errors
                        }
                    }
                }
            }
        }
    }

    public static String getUrl() {
        return URL;
    }
}
