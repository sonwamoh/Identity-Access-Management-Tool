package com.example.iag;

import com.example.iag.db.Database;
import com.example.iag.dao.AccessRequestDao;
import com.example.iag.dao.RoleRiskDao;
import com.example.iag.service.AccessService;
import com.example.iag.web.Handlers;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        Database.init(); // create schema + seed data.sql

        AccessRequestDao reqDao = new AccessRequestDao(Database.getUrl(), Database.USER, Database.PASS);
        RoleRiskDao riskDao = new RoleRiskDao(Database.getUrl(), Database.USER, Database.PASS);
        AccessService service = new AccessService(reqDao, riskDao);
        Handlers handlers = new Handlers(service, reqDao, riskDao);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // API endpoints:
        server.createContext("/api/requests", exchange -> handlers.handleRequests(exchange));
        server.createContext("/api/requests/", exchange -> handlers.handleRequestsWithId(exchange));
        server.createContext("/api/risks", exchange -> handlers.handleRisks(exchange));

        // Static file server for index.html and assets
        server.createContext("/", exchange -> {
            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            if (path.equals("/") || path.equals("/index.html")) {
                try (InputStream is = Main.class.getResourceAsStream("/static/index.html")) {
                    byte[] body = is.readAllBytes();
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, body.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(body);
                    }
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        });

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Server started at http://localhost:8080");
    }
}
