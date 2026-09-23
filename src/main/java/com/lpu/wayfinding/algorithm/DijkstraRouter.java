// src/main/java/com/lpu/wayfinding/algorithm/DijkstraRouter.java
package com.lpu.wayfinding.algorithm;

import com.lpu.wayfinding.entity.Edge;
import com.lpu.wayfinding.entity.Node;
import com.lpu.wayfinding.exception.NodeNotFoundException;
import com.lpu.wayfinding.util.EdgeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;

/**
 * Dijkstra shortest path with real-world constraints.
 *
 * Time Complexity : O((V + E) log V)
 * Space Complexity: O(V + E)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DijkstraRouter {

    private final CampusGraph graph;

    public record Result(List<Long> path, double distance) {
        public static Result unreachable() {
            return new Result(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        public boolean isReachable() {
            return !path.isEmpty();
        }
    }

    public Result shortestPath(
            Long startId,
            Long endId,
            boolean wheelchair,
            LocalTime currentTime,
            boolean peakHours) {

        Node startNode = graph.getNode(startId);
        Node endNode = graph.getNode(endId);

        if (startNode == null) throw new NodeNotFoundException(startId);
        if (endNode == null) throw new NodeNotFoundException(endId);

        if (startId.equals(endId)) {
            return new Result(List.of(startId), 0.0);
        }

        LocalTime now = (currentTime != null) ? currentTime : LocalTime.now();

        Map<Long, Double> dist = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        Set<Long> visited = new HashSet<>();

        // Priority queue ordered by distance
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> Double.longBitsToDouble(a[1])));

        dist.put(startId, 0.0);
        pq.offer(new long[]{startId, Double.doubleToLongBits(0.0)});

        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            long u = top[0];
            double d = Double.longBitsToDouble(top[1]);

            if (visited.contains(u)) continue;
            visited.add(u);

            if (u == endId) break;

            for (Edge edge : graph.getNeighbors(u)) {
                if (!isEdgeAvailable(edge, wheelchair, now)) continue;

                long v = edge.getDestId();
                double weight = effectiveWeight(edge, peakHours);
                double newDist = d + weight;

                if (newDist < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    pq.offer(new long[]{v, Double.doubleToLongBits(newDist)});
                }
            }
        }

        if (!dist.containsKey(endId)) {
            log.debug("No route from {} to {} (wheelchair={})", startId, endId, wheelchair);
            return Result.unreachable();
        }

        // Reconstruct path
        LinkedList<Long> path = new LinkedList<>();
        Long cur = endId;
        while (cur != null) {
            path.addFirst(cur);
            cur = prev.get(cur);
        }

        return new Result(path, dist.get(endId));
    }

    private boolean isEdgeAvailable(Edge edge, boolean wheelchair, LocalTime now) {
        if (wheelchair) {
            if (Boolean.FALSE.equals(edge.getAccessible())) return false;
            if (edge.getEdgeType() == EdgeType.STAIRS) return false;
        }

        if (edge.getOpenFrom() != null && edge.getOpenTo() != null) {
            LocalTime from = edge.getOpenFrom();
            LocalTime to = edge.getOpenTo();
            if (from.isBefore(to) || from.equals(to)) {
                if (now.isBefore(from) || now.isAfter(to)) return false;
            } else {
                // crosses midnight
                if (now.isBefore(from) && now.isAfter(to)) return false;
            }
        }
        return true;
    }

    private double effectiveWeight(Edge edge, boolean peakHours) {
        if (peakHours && edge.getCongestionFactor() != null) {
            return edge.getDistance() * edge.getCongestionFactor();
        }
        return edge.getDistance();
    }
}