package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康檢查控制器
 * 為Kubernetes提供健康檢查端點
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * 存活探針 - 確認應用程序是否在運行
     * 用於Kubernetes的livenessProbe
     */
    @GetMapping("/liveness")
    public ResponseEntity<Map<String, String>> livenessCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }

    /**
     * 就緒探針 - 確認應用程序是否準備好處理請求
     * 用於Kubernetes的readinessProbe
     */
    @GetMapping("/readiness")
    public ResponseEntity<Map<String, String>> readinessCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "READY");
        return ResponseEntity.ok(response);
    }
} 