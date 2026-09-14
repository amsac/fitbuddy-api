package com.fitbuddy.backend.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {
    // Liveness only: does not query the database or external services.
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("status", "UP"));
    }
}
