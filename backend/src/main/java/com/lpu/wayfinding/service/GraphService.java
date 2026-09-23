// src/main/java/com/lpu/wayfinding/service/GraphService.java
package com.lpu.wayfinding.service;

import com.lpu.wayfinding.algorithm.CampusGraph;
import com.lpu.wayfinding.entity.Edge;
import com.lpu.wayfinding.entity.Node;
import com.lpu.wayfinding.repository.EdgeRepository;
import com.lpu.wayfinding.repository.NodeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Loads (and reloads) the campus graph from the DB into memory.
 *
 * <p>Uses a synchronized rebuild: the underlying {@link CampusGraph} is
 * cleared and repopulated from the current database snapshot. All read
 * operations on the graph are lock-free because the graph bean itself
 * is only mutated during reload, which is guarded by the monitor on
 * this service instance.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GraphService {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final CampusGraph campusGraph;

    /**
     * Loads the graph at application startup.
     */
    @PostConstruct
    public void init() {
        reload();
    }

    /**
     * Rebuilds the in-memory graph from the current database state.
     *
     * <p>Clears the existing graph and repopulates it from the repository.
     * Because {@link CampusGraph#addEdge(Edge)} automatically adds the
     * reverse edge, we must only iterate the persisted edges once — do
     * NOT iterate the adjacency map, or every edge will be duplicated.</p>
     */
    @Transactional(readOnly = true)
    public synchronized void reload() {
        log.info("Reloading campus graph...");

        List<Node> nodes = nodeRepository.findAll();
        List<Edge> edges = edgeRepository.findAll();

        campusGraph.clear();

        nodes.forEach(campusGraph::addNode);
        edges.forEach(campusGraph::addEdge);

        log.info("Graph reloaded: {} nodes, {} edges", nodes.size(), edges.size());
    }
}