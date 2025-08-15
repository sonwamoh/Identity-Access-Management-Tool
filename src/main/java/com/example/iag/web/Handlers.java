package com.example.iag.web;

import com.example.iag.model.AccessRequest;
import com.example.iag.model.RoleRisk;
import com.example.iag.dao.AccessRequestDao;
import com.example.iag.dao.RoleRiskDao;
import com.example.iag.service.AccessService;
import com.fasterxml.jackson.core.type.TypeReference;

import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Handlers {
    private final AccessService service;
    private final AccessRequestDao reqDao;
    private final RoleRiskDao riskDao;

    public Handlers(AccessService service, AccessRequestDao reqDao, RoleRiskDao riskDao) {
        this.service = service;
        this.reqDao = reqDao;
        this.riskDao = riskDao;
    }

    public void handleRequests(HttpExchange ex) throws IOException {
        try {
            String method = ex.getRequestMethod();
            if ("POST".equalsIgnoreCase(method)) {
                AccessRequest r = JsonUtils.MAPPER.readValue(ex.getRequestBody(), AccessRequest.class);
                AccessRequest saved = service.submit(r);
                sendJson(ex, 200, saved);
                return;
            } else if ("GET".equalsIgnoreCase(method)) {
                URI u = ex.getRequestURI();
                String query = u.getQuery();
                if (query != null && query.contains("status=")) {
                    String status = getQueryParam(query, "status");
                    List<AccessRequest> list = reqDao.findByStatus(AccessRequest.Status.valueOf(status));
                    sendJson(ex, 200, list);
                    return;
                }
                if (query != null && query.contains("user=")) {
                    String user = getQueryParam(query, "user");
                    List<AccessRequest> list = reqDao.findByUsernameIgnoreCase(user);
                    sendJson(ex, 200, list);
                    return;
                }
                sendJson(ex, 200, reqDao.findAll());
                return;
            }
            ex.sendResponseHeaders(405, -1);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, e.getMessage());
        }
    }

    public void handleRequestsWithId(HttpExchange ex) throws IOException {
        // path forms: /api/requests/{id}/approve or /api/requests/{id}/reject
        try {
            String path = ex.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) { ex.sendResponseHeaders(404, -1); return; }
            Long id = Long.valueOf(parts[3]);
            if (parts.length >= 5) {
                String action = parts[4];
                if ("approve".equalsIgnoreCase(action) && "POST".equalsIgnoreCase(ex.getRequestMethod())) {
                    Map<String, String> body = JsonUtils.MAPPER.readValue(ex.getRequestBody(), new TypeReference<>() {});
                    String note = body != null ? body.get("note") : null;
                    AccessRequest updated = service.approve(id, note);
                    sendJson(ex, 200, updated);
                    return;
                } else if ("reject".equalsIgnoreCase(action) && "POST".equalsIgnoreCase(ex.getRequestMethod())) {
                    Map<String, String> body = JsonUtils.MAPPER.readValue(ex.getRequestBody(), new TypeReference<>() {});
                    String reason = body != null ? body.get("reason") : null;
                    AccessRequest updated = service.reject(id, reason);
                    sendJson(ex, 200, updated);
                    return;
                }
            }
            ex.sendResponseHeaders(404, -1);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, e.getMessage());
        }
    }

    public void handleRisks(HttpExchange ex) throws IOException {
        try {
            if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
                List<RoleRisk> list = riskDao.findAll();
                sendJson(ex, 200, list);
                return;
            }
            ex.sendResponseHeaders(405, -1);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, e.getMessage());
        }
    }

    private void sendJson(HttpExchange ex, int code, Object obj) throws IOException {
        byte[] bytes = JsonUtils.MAPPER.writeValueAsBytes(obj);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private void sendError(HttpExchange ex, int code, String msg) throws IOException {
        sendJson(ex, code, Map.of("error", msg));
    }

    private String getQueryParam(String q, String key) {
        for (String part : q.split("&")) {
            if (part.startsWith(key + "=")) return part.substring((key + "=").length());
        }
        return null;
    }
}
