// src/main/java/com/lpu/wayfinding/controller/MonitoringController.java
package com.lpu.wayfinding.controller;

import com.lpu.wayfinding.cache.RouteCache;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final RouteCache cache;

    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> cacheStats() {
        var stats = cache.raw().stats();
        return ResponseEntity.ok(Map.of(
                "hitCount", stats.hitCount(),
                "missCount", stats.missCount(),
                "hitRate", stats.hitRate(),
                "evictionCount", stats.evictionCount(),
                "size", cache.raw().estimatedSize()
        ));
    }
}