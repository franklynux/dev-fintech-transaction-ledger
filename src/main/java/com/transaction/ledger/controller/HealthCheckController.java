package com.transaction.ledger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/database")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection
            String dbVersion = jdbcTemplate.queryForObject("SELECT version()", String.class);
            Integer testQuery = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            response.put("status", "UP");
            response.put("database", "PostgreSQL");
            response.put("connected", true);
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("message", "Database connection successful");
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("database", "PostgreSQL");
            response.put("connected", false);
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    @GetMapping("/service")
    public Map<String, Object> checkService() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("service", "Transaction Ledger");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("environment", System.getenv("SPRING_PROFILES_ACTIVE"));
        
        // Add some system info
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("availableMemory", Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
        
        return response;
    }
    
    @GetMapping("/full")
    public Map<String, Object> fullHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> dbStatus = checkDatabase();
        Map<String, Object> serviceStatus = checkService();
        
        response.put("service", serviceStatus);
        response.put("database", dbStatus);
        response.put("overallStatus", 
                     "DOWN".equals(dbStatus.get("status")) ? "DOWN" : "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return response;
    }
}
