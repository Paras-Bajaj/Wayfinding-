// src/test/java/com/lpu/wayfinding/algorithm/DijkstraRouterTest.java
package com.lpu.wayfinding.algorithm;

import com.lpu.wayfinding.entity.Edge;
import com.lpu.wayfinding.entity.Node;
import com.lpu.wayfinding.exception.NodeNotFoundException;
import com.lpu.wayfinding.util.EdgeType;
import com.lpu.wayfinding.util.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraRouterTest {

    private CampusGraph graph;
    private DijkstraRouter router;

    @BeforeEach
    void setup() {
        graph = new CampusGraph();
        router = new DijkstraRouter(graph);

        addNode(1L, "Reception", NodeType.ENTRANCE);
        addNode(2L, "Corridor A", NodeType.CORRIDOR);
        addNode(3L, "Meeting Room 4B", NodeType.ROOM);
        addNode(4L, "Stairs", NodeType.STAIRS);
        addNode(5L, "Lift", NodeType.LIFT);

        addEdge(1L, 1L, 2L, 10.0, EdgeType.WALK, true);
        addEdge(2L, 2L, 3L, 10.0, EdgeType.WALK, true);
        addEdge(3L, 2L, 4L, 5.0, EdgeType.STAIRS, false);
        addEdge(4L, 2L, 5L, 5.0, EdgeType.LIFT, true);
        addEdge(5L, 4L, 3L, 5.0, EdgeType.WALK, true);
        addEdge(6L, 5L, 3L, 5.0, EdgeType.WALK, true);
    }

    private void addNode(Long id, String name, NodeType type) {
        graph.addNode(Node.builder().id(id).name(name).nodeType(type)
                .xCoord(0.0).yCoord(0.0).accessible(true).build());
    }

    private void addEdge(Long id, Long src, Long dst, double dist, EdgeType t, boolean acc) {
        graph.addEdge(Edge.builder().id(id).sourceId(src).destId(dst)
                .distance(dist).edgeType(t).accessible(acc).congestionFactor(1.0).build());
    }

    @Test
    void shortestPath_normal() {
        var r = router.shortestPath(1L, 3L, false, null, false);
        assertTrue(r.isReachable());
        assertEquals(20.0, r.distance(), 0.001);
        assertEquals(3, r.path().size());
    }

    @Test
    void wheelchair_avoidsStairs() {
        var r = router.shortestPath(1L, 3L, true, null, false);
        assertTrue(r.isReachable());
        assertFalse(r.path().contains(4L), "Wheelchair route must not include stairs node");
    }

    @Test
    void sameStartEnd() {
        var r = router.shortestPath(1L, 1L, false, null, false);
        assertTrue(r.isReachable());
        assertEquals(0.0, r.distance(), 0.001);
    }

    @Test
    void unreachableDestination() {
        CampusGraph isolated = new CampusGraph();
        isolated.addNode(Node.builder().id(1L).name("A").nodeType(NodeType.ROOM)
                .xCoord(0.0).yCoord(0.0).accessible(true).build());
        isolated.addNode(Node.builder().id(2L).name("B").nodeType(NodeType.ROOM)
                .xCoord(1.0).yCoord(1.0).accessible(true).build());
        var isolatedRouter = new DijkstraRouter(isolated);
        var r = isolatedRouter.shortestPath(1L, 2L, false, null, false);
        assertFalse(r.isReachable());
    }

    @Test
    void invalidStartNodeThrows() {
        assertThrows(NodeNotFoundException.class,
                () -> router.shortestPath(999L, 1L, false, null, false));
    }
}