// src/main/java/com/lpu/wayfinding/entity/Edge.java
package com.lpu.wayfinding.entity;

import com.lpu.wayfinding.util.EdgeType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "edges", indexes = {
    @Index(name = "idx_edge_source", columnList = "source_id"),
    @Index(name = "idx_edge_dest", columnList = "dest_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "dest_id", nullable = false)
    private Long destId;

    @Column(nullable = false)
    private Double distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "edge_type", nullable = false)
    private EdgeType edgeType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accessible = true;

    @Column(name = "open_from")
    private LocalTime openFrom;

    @Column(name = "open_to")
    private LocalTime openTo;

    @Column(name = "congestion_factor", nullable = false)
    @Builder.Default
    private Double congestionFactor = 1.0;
}