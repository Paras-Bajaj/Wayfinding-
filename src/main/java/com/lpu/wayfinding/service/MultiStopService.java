// src/main/java/com/lpu/wayfinding/service/MultiStopService.java
package com.lpu.wayfinding.service;

import com.lpu.wayfinding.algorithm.MultiStopRouter;
import com.lpu.wayfinding.dto.request.MultiStopRequest;
import com.lpu.wayfinding.dto.response.PoiResponse;
import com.lpu.wayfinding.dto.response.RouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MultiStopService {

    private final MultiStopRouter multiStopRouter;
    private final WayfindingService wayfindingService;

    public RouteResponse planMultiStop(MultiStopRequest req) {
        var result = multiStopRouter.planRoute(
                req.getStartId(),
                req.getStopIds(),
                req.getEndId(),
                Boolean.TRUE.equals(req.getWheelchair()),
                Boolean.TRUE.equals(req.getPeakHours()));

        // Reuse the direction builder
        RouteResponse dummy = RouteResponse.builder().path(result.path()).build();
        var directions = wayfindingService.getRoute(
                // Avoid double-call: build directions manually via helper
                buildRouteRequest(req)).getDirections();

        return RouteResponse.builder()
                .status("success")
                .path(result.path())
                .distance(Math.round(result.distance() * 100.0) / 100.0)
                .directions(directions)
                .wheelchairAccessible(Boolean.TRUE.equals(req.getWheelchair()))
                .cached(false)
                .build();
    }

    public PoiResponse nearestPoi(Long fromNode, String type, boolean wheelchair, boolean peak) {
        return multiStopRouter.findNearestPoi(fromNode, type, wheelchair, peak);
    }

    private com.lpu.wayfinding.dto.request.RouteRequest buildRouteRequest(MultiStopRequest req) {
        var r = new com.lpu.wayfinding.dto.request.RouteRequest();
        r.setStartId(req.getStartId());
        r.setEndId(req.getEndId());
        r.setWheelchair(req.getWheelchair());
        r.setPeakHours(req.getPeakHours());
        return r;
    }
}