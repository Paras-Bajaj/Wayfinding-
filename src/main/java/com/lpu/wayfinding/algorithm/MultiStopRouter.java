// src/main/java/com/lpu/wayfinding/algorithm/MultiStopRouter.java
package com.lpu.wayfinding.algorithm;

import com.lpu.wayfinding.entity.Poi;
import com.lpu.wayfinding.exception.NoRouteFoundException;
import com.lpu.wayfinding.repository.PoiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.lpu.wayfinding.dto.response.PoiResponse;
import java.time.LocalTime;
import java.util.*;

/**
 * Multi-stop routing (TSP-flavoured).
 * - n <= 7 : brute force permutations (optimal)
 * - n  > 7 : nearest-neighbour heuristic
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MultiStopRouter {

    private final DijkstraRouter router;
    private final PoiRepository poiRepository;

    private static final int BRUTE_FORCE_LIMIT = 7;

    public record MultiResult(List<Long> path, double distance) {}

    public MultiResult planRoute(
            Long startId,
            List<Long> stopIds,
            Long endId,
            boolean wheelchair,
            boolean peakHours) {

        LocalTime now = LocalTime.now();

        if (stopIds == null || stopIds.isEmpty()) {
            var r = router.shortestPath(startId, endId, wheelchair, now, peakHours);
            if (!r.isReachable()) throw new NoRouteFoundException(startId, endId, "No path found");
            return new MultiResult(r.path(), r.distance());
        }

        // Precompute pairwise distances
        List<Long> allPoints = new ArrayList<>();
        allPoints.add(startId);
        allPoints.addAll(stopIds);
        allPoints.add(endId);

        Map<String, DijkstraRouter.Result> pairResult = new HashMap<>();
        for (Long a : allPoints) {
            for (Long b : allPoints) {
                if (!a.equals(b)) {
                    pairResult.put(key(a, b),
                            router.shortestPath(a, b, wheelchair, now, peakHours));
                }
            }
        }

        List<Long> orderedStops = (stopIds.size() <= BRUTE_FORCE_LIMIT)
                ? bruteForceOrder(startId, stopIds, endId, pairResult)
                : nearestNeighbourOrder(startId, stopIds, endId, pairResult);

        // Stitch the full path
        LinkedList<Long> full = new LinkedList<>();
        double total = 0.0;
        List<Long> sequence = new ArrayList<>();
        sequence.add(startId);
        sequence.addAll(orderedStops);
        sequence.add(endId);

        for (int i = 0; i < sequence.size() - 1; i++) {
            var r = pairResult.get(key(sequence.get(i), sequence.get(i + 1)));
            if (r == null || !r.isReachable()) {
                throw new NoRouteFoundException(sequence.get(i), sequence.get(i + 1), "Segment unreachable");
            }
            total += r.distance();
            if (i == 0) {
                full.addAll(r.path());
            } else {
                // avoid duplicate junction node
                full.addAll(r.path().subList(1, r.path().size()));
            }
        }
        return new MultiResult(full, total);
    }

    private List<Long> bruteForceOrder(Long start, List<Long> stops, Long end,
                                       Map<String, DijkstraRouter.Result> pair) {
        List<Long> best = null;
        double bestCost = Double.POSITIVE_INFINITY;
        for (List<Long> perm : permutations(stops)) {
            double cost = costOf(start, perm, end, pair);
            if (cost < bestCost) {
                bestCost = cost;
                best = new ArrayList<>(perm);
            }
        }
        return best != null ? best : new ArrayList<>(stops);
    }

    private double costOf(Long start, List<Long> perm, Long end,
                          Map<String, DijkstraRouter.Result> pair) {
        double c = 0;
        Long prev = start;
        for (Long s : perm) {
            var r = pair.get(key(prev, s));
            if (r == null || !r.isReachable()) return Double.POSITIVE_INFINITY;
            c += r.distance();
            prev = s;
        }
        var r = pair.get(key(prev, end));
        if (r == null || !r.isReachable()) return Double.POSITIVE_INFINITY;
        return c + r.distance();
    }

    private List<Long> nearestNeighbourOrder(Long start, List<Long> stops, Long end,
                                             Map<String, DijkstraRouter.Result> pair) {
        Set<Long> remaining = new LinkedHashSet<>(stops);
        List<Long> order = new ArrayList<>();
        Long current = start;
        while (!remaining.isEmpty()) {
            Long best = null;
            double bestD = Double.POSITIVE_INFINITY;
            for (Long s : remaining) {
                var r = pair.get(key(current, s));
                double d = (r == null || !r.isReachable()) ? Double.POSITIVE_INFINITY : r.distance();
                if (d < bestD) {
                    bestD = d;
                    best = s;
                }
            }
            if (best == null) break;
            order.add(best);
            remaining.remove(best);
            current = best;
        }
        // Append any remaining (unreachable ones) at the end
        order.addAll(remaining);
        return order;
    }

    private <T> List<List<T>> permutations(List<T> list) {
        List<List<T>> out = new ArrayList<>();
        permute(new ArrayList<>(list), 0, out);
        return out;
    }

    private <T> void permute(List<T> arr, int k, List<List<T>> out) {
        if (k == arr.size()) {
            out.add(new ArrayList<>(arr));
            return;
        }
        for (int i = k; i < arr.size(); i++) {
            Collections.swap(arr, i, k);
            permute(arr, k + 1, out);
            Collections.swap(arr, i, k);
        }
    }

    public PoiResponse findNearestPoi(Long fromNode, String poiType,
                                      boolean wheelchair, boolean peakHours) {
        List<Poi> pois = poiRepository.findByPoiType(poiType.toUpperCase());
        LocalTime now = LocalTime.now();
        PoiResponse best = null;

        for (Poi p : pois) {
            var r = router.shortestPath(fromNode, p.getNodeId(), wheelchair, now, peakHours);
            if (r.isReachable()
                    && (best == null || r.distance() < best.getDistance())) {
                best = PoiResponse.builder()
                        .poiId(p.getId())
                        .nodeId(p.getNodeId())
                        .name(p.getName())
                        .type(p.getPoiType())
                        .distance(r.distance())
                        .path(r.path())
                        .build();
            }
        }
        return best;
    }

    private String key(Long a, Long b) {
        return a + ":" + b;
    }
}