// src/main/java/com/lpu/wayfinding/service/WayfindingService.java
package com.lpu.wayfinding.service;

import com.lpu.wayfinding.algorithm.CampusGraph;
import com.lpu.wayfinding.algorithm.DijkstraRouter;
import com.lpu.wayfinding.cache.RouteCache;
import com.lpu.wayfinding.dto.request.RouteRequest;
import com.lpu.wayfinding.dto.response.DirectionStep;
import com.lpu.wayfinding.dto.response.RouteResponse;
import com.lpu.wayfinding.entity.Node;
import com.lpu.wayfinding.exception.NoRouteFoundException;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WayfindingService {

    private final DijkstraRouter router;
    private final CampusGraph graph;
    private final RouteCache cache;
    private final MonitoringService monitoring;

    @Timed(value = "wayfinding.route", description = "Time taken to compute a route")
    public RouteResponse getRoute(RouteRequest req) {
        long start = System.currentTimeMillis();

        boolean wheelchair = Boolean.TRUE.equals(req.getWheelchair());
        boolean peakHours = Boolean.TRUE.equals(req.getPeakHours());
        int hourBucket = LocalTime.now().getHour();

        String cacheKey = cache.key(req.getStartId(), req.getEndId(), wheelchair, hourBucket);
        RouteResponse cached = cache.get(cacheKey);
        if (cached != null) {
            monitoring.recordCacheHit();
            return RouteResponse.builder()
                    .status("success")
                    .path(cached.getPath())
                    .distance(cached.getDistance())
                    .directions(cached.getDirections())
                    .wheelchairAccessible(cached.getWheelchairAccessible())
                    .cached(true)
                    .build();
        }
        monitoring.recordCacheMiss();

        var result = router.shortestPath(
                req.getStartId(), req.getEndId(), wheelchair, LocalTime.now(), peakHours);

        if (!result.isReachable()) {
            String reason = wheelchair
                    ? "No wheelchair-accessible route available"
                    : "Destination may be unreachable or closed at this time";
            monitoring.recordRouteFailure();
            throw new NoRouteFoundException(req.getStartId(), req.getEndId(), reason);
        }

        List<DirectionStep> directions = buildDirections(result.path());

        RouteResponse response = RouteResponse.builder()
                .status("success")
                .path(result.path())
                .distance(Math.round(result.distance() * 100.0) / 100.0)
                .directions(directions)
                .wheelchairAccessible(wheelchair)
                .cached(false)
                .build();

        cache.put(cacheKey, response);
        monitoring.recordRouteSuccess(System.currentTimeMillis() - start);
        return response;
    }

    private List<DirectionStep> buildDirections(List<Long> path) {
        List<DirectionStep> out = new ArrayList<>(path.size());
        for (int i = 0; i < path.size(); i++) {
            Long id = path.get(i);
            Node node = graph.getNode(id);
            if (node == null) continue;

            String instruction;
            if (i == 0) instruction = "Start at " + node.getName();
            else if (i == path.size() - 1) instruction = "Arrive at " + node.getName();
            else instruction = "Continue to " + node.getName();

            out.add(DirectionStep.builder()
                    .step(i + 1)
                    .nodeId(id)
                    .name(node.getName())
                    .type(node.getNodeType().name())
                    .instruction(instruction)
                    .build());
        }
        return out;
    }
}