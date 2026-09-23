// src/main/java/com/lpu/wayfinding/algorithm/CampusGraph.java
package com.lpu.wayfinding.algorithm;

import com.lpu.wayfinding.entity.Edge;
import com.lpu.wayfinding.entity.Node;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adjacency-list graph representation of the campus.
 * Thread-safe rebuild via snapshot swap.
 */
@Component
@Getter
public class CampusGraph {

    private final Map<Long, Node> nodes = new ConcurrentHashMap<>();
    private final Map<Long, List<Edge>> adjacency = new ConcurrentHashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacency.computeIfAbsent(node.getId(), k -> new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        adjacency.computeIfAbsent(edge.getSourceId(), k -> new ArrayList<>()).add(edge);
        // Undirected: add reverse
        Edge reverse = Edge.builder()
                .id(-edge.getId())
                .sourceId(edge.getDestId())
                .destId(edge.getSourceId())
                .distance(edge.getDistance())
                .edgeType(edge.getEdgeType())
                .accessible(edge.getAccessible())
                .openFrom(edge.getOpenFrom())
                .openTo(edge.getOpenTo())
                .congestionFactor(edge.getCongestionFactor())
                .build();
        adjacency.computeIfAbsent(edge.getDestId(), k -> new ArrayList<>()).add(reverse);
    }

    public void clear() {
        nodes.clear();
        adjacency.clear();
    }

    public List<Edge> getNeighbors(Long nodeId) {
        return adjacency.getOrDefault(nodeId, Collections.emptyList());
    }

    public Node getNode(Long id) {
        return nodes.get(id);
    }
}