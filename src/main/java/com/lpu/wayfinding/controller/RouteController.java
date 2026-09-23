// src/main/java/com/lpu/wayfinding/controller/RouteController.java
package com.lpu.wayfinding.controller;

import com.lpu.wayfinding.dto.request.MultiStopRequest;
import com.lpu.wayfinding.dto.request.RouteRequest;
import com.lpu.wayfinding.dto.response.RouteResponse;
import com.lpu.wayfinding.service.MultiStopService;
import com.lpu.wayfinding.service.WayfindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
@Tag(name = "Routing")
public class RouteController {

    private final WayfindingService wayfindingService;
    private final MultiStopService multiStopService;

    @PostMapping
    @Operation(summary = "Shortest path between two nodes")
    public ResponseEntity<RouteResponse> getRoute(@Valid @RequestBody RouteRequest req) {
        return ResponseEntity.ok(wayfindingService.getRoute(req));
    }

    @PostMapping("/multi-stop")
    @Operation(summary = "Route that visits multiple POIs in best order")
    public ResponseEntity<RouteResponse> multiStop(@Valid @RequestBody MultiStopRequest req) {
        return ResponseEntity.ok(multiStopService.planMultiStop(req));
    }
}