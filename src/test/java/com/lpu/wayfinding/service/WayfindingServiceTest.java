// src/test/java/com/lpu/wayfinding/service/WayfindingServiceTest.java
package com.lpu.wayfinding.service;

import com.lpu.wayfinding.algorithm.CampusGraph;
import com.lpu.wayfinding.algorithm.DijkstraRouter;
import com.lpu.wayfinding.cache.RouteCache;
import com.lpu.wayfinding.dto.request.RouteRequest;
import com.lpu.wayfinding.entity.Edge;
import com.lpu.wayfinding.entity.Node;
import com.lpu.wayfinding.exception.NoRouteFoundException;
import com.lpu.wayfinding.util.EdgeType;
import com.lpu.wayfinding.util.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WayfindingServiceTest {

    private WayfindingService service;
    private CampusGraph graph;

    @BeforeEach
    void setup() {
        graph = new CampusGraph();
        var router = new DijkstraRouter(graph);
        var cache = new RouteCache();
        var monitoring = mock(MonitoringService.class);

        service = new WayfindingService(router, graph, cache, monitoring);

        graph.addNode(Node.builder().id(1L).name("A").nodeType(NodeType.ROOM)
                .xCoord(0.0).yCoord(0.0).accessible(true).build());
        graph.addNode(Node.builder().id(2L).name("B").nodeType(NodeType.ROOM)
                .xCoord(1.0).yCoord(1.0).accessible(true).build());
        graph.addEdge(Edge.builder().id(1L).sourceId(1L).destId(2L).distance(5.0)
                .edgeType(EdgeType.WALK).accessible(true).congestionFactor(1.0).build());
    }

    @Test
    void getRoute_success() {
        var req = new RouteRequest();
        req.setStartId(1L);
        req.setEndId(2L);
        var resp = service.getRoute(req);
        assertEquals("success", resp.getStatus());
        assertEquals(2, resp.getPath().size());
        assertEquals(5.0, resp.getDistance(), 0.001);
    }

    @Test
    void getRoute_throwsWhenUnreachable() {
        graph.clear();
        graph.addNode(Node.builder().id(1L).name("A").nodeType(NodeType.ROOM)
                .xCoord(0.0).yCoord(0.0).accessible(true).build());
        graph.addNode(Node.builder().id(2L).name("B").nodeType(NodeType.ROOM)
                .xCoord(1.0).yCoord(1.0).accessible(true).build());

        var req = new RouteRequest();
        req.setStartId(1L);
        req.setEndId(2L);
        assertThrows(NoRouteFoundException.class, () -> service.getRoute(req));
    }
}