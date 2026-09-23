// src/main/java/com/lpu/wayfinding/controller/PoiController.java
package com.lpu.wayfinding.controller;

import com.lpu.wayfinding.dto.response.PoiResponse;
import com.lpu.wayfinding.service.MultiStopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poi")
@RequiredArgsConstructor
@Tag(name = "Points of Interest")
public class PoiController {

    private final MultiStopService multiStopService;

    @GetMapping("/nearest")
    @Operation(summary = "Find the nearest POI of a given type")
    public ResponseEntity<PoiResponse> nearest(
            @RequestParam Long fromNode,
            @RequestParam String type,
            @RequestParam(defaultValue = "false") boolean wheelchair,
            @RequestParam(defaultValue = "false") boolean peakHours) {
        PoiResponse resp = multiStopService.nearestPoi(fromNode, type, wheelchair, peakHours);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }
}