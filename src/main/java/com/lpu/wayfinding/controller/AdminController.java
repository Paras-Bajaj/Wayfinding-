// src/main/java/com/lpu/wayfinding/controller/AdminController.java
package com.lpu.wayfinding.controller;

import com.lpu.wayfinding.cache.RouteCache;
import com.lpu.wayfinding.service.GraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final GraphService graphService;
    private final RouteCache cache;

    @PostMapping("/reload-graph")
    @Operation(summary = "Reload the in-memory graph from DB")
    public ResponseEntity<Map<String, String>> reloadGraph() {
        graphService.reload();
        cache.invalidateAll();
        return ResponseEntity.ok(Map.of("status", "success", "message", "Graph reloaded"));
    }

    @PostMapping("/invalidate-cache")
    @Operation(summary = "Clear the route cache")
    public ResponseEntity<Map<String, String>> invalidateCache() {
        cache.invalidateAll();
        return ResponseEntity.ok(Map.of("status", "success", "message", "Cache invalidated"));
    }
}