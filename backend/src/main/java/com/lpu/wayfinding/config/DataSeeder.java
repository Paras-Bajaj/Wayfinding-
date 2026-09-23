// backend/src/main/java/com/lpu/wayfinding/config/DataSeeder.java
package com.lpu.wayfinding.config;

import com.lpu.wayfinding.entity.*;
import com.lpu.wayfinding.repository.*;
import com.lpu.wayfinding.service.GraphService;
import com.lpu.wayfinding.util.EdgeType;
import com.lpu.wayfinding.util.NodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

/**
 * Seeds a small demo campus on first startup, only if the DB is empty.
 * Idempotent: re-running won't duplicate data.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final PoiRepository poiRepository;
    private final GraphService graphService;

    @Bean
    CommandLineRunner seedDatabase() {
        return args -> seed();
    }

    @Transactional
    public void seed() {
        if (nodeRepository.count() > 0) {
            log.info("Seed data already present — skipping ({} nodes)", nodeRepository.count());
            return;
        }

        log.info("Seeding demo campus data...");

        Building block = Building.builder()
                .name("LPU Block 32")
                .address("Lovely Professional University, Phagwara")
                .build();
        buildingRepository.save(block);

        Floor ground = Floor.builder()
                .building(block)
                .level(0)
                .name("Ground Floor")
                .build();
        floorRepository.save(ground);

        // ---- Nodes ----
        Node reception = nodeRepository.save(Node.builder()
                .floor(ground).name("Reception").nodeType(NodeType.ENTRANCE)
                .xCoord(0.0).yCoord(0.0).accessible(true).build());

        Node corridorA = nodeRepository.save(Node.builder()
                .floor(ground).name("Corridor A").nodeType(NodeType.CORRIDOR)
                .xCoord(10.0).yCoord(0.0).accessible(true).build());

        Node corridorB = nodeRepository.save(Node.builder()
                .floor(ground).name("Corridor B").nodeType(NodeType.CORRIDOR)
                .xCoord(20.0).yCoord(0.0).accessible(true).build());

        Node meeting4B = nodeRepository.save(Node.builder()
                .floor(ground).name("Meeting Room 4B").nodeType(NodeType.ROOM)
                .xCoord(30.0).yCoord(0.0).accessible(true).build());

        Node stairs = nodeRepository.save(Node.builder()
                .floor(ground).name("Main Stairs").nodeType(NodeType.STAIRS)
                .xCoord(15.0).yCoord(5.0).accessible(false).build());

        Node lift = nodeRepository.save(Node.builder()
                .floor(ground).name("Main Lift").nodeType(NodeType.LIFT)
                .xCoord(15.0).yCoord(-5.0).accessible(true).build());

        Node washroom = nodeRepository.save(Node.builder()
                .floor(ground).name("Washroom (Corridor B)").nodeType(NodeType.ROOM)
                .xCoord(22.0).yCoord(3.0).accessible(true).build());

        // ---- Edges ----
        edgeRepository.save(Edge.builder()
                .sourceId(reception.getId()).destId(corridorA.getId())
                .distance(10.0).edgeType(EdgeType.WALK)
                .accessible(true).congestionFactor(1.5).build());

        edgeRepository.save(Edge.builder()
                .sourceId(corridorA.getId()).destId(corridorB.getId())
                .distance(10.0).edgeType(EdgeType.WALK)
                .accessible(true).congestionFactor(1.5).build());

        edgeRepository.save(Edge.builder()
                .sourceId(corridorB.getId()).destId(meeting4B.getId())
                .distance(10.0).edgeType(EdgeType.WALK)
                .accessible(true).congestionFactor(1.0).build());

        edgeRepository.save(Edge.builder()
                .sourceId(corridorA.getId()).destId(stairs.getId())
                .distance(5.0).edgeType(EdgeType.STAIRS)
                .accessible(false)
                .openFrom(LocalTime.of(7, 0))
                .openTo(LocalTime.of(20, 0))
                .congestionFactor(1.0).build());

        edgeRepository.save(Edge.builder()
                .sourceId(corridorA.getId()).destId(lift.getId())
                .distance(6.0).edgeType(EdgeType.LIFT)
                .accessible(true).congestionFactor(1.0).build());

        edgeRepository.save(Edge.builder()
                .sourceId(corridorB.getId()).destId(washroom.getId())
                .distance(3.0).edgeType(EdgeType.WALK)
                .accessible(true).congestionFactor(1.0).build());

        // ---- POI ----
        poiRepository.save(Poi.builder()
                .nodeId(washroom.getId())
                .poiType("WASHROOM")
                .name("Washroom near Corridor B")
                .build());

        log.info("Seeded: 1 building, 1 floor, {} nodes, {} edges, 1 POI",
                nodeRepository.count(), edgeRepository.count());

        // Reload the in-memory graph so routing works immediately
        graphService.reload();
    }
}