// src/main/java/com/lpu/wayfinding/entity/Node.java
package com.lpu.wayfinding.entity;

import com.lpu.wayfinding.util.NodeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nodes", indexes = {
    @Index(name = "idx_node_floor", columnList = "floor_id"),
    @Index(name = "idx_node_type", columnList = "node_type")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false)
    private NodeType nodeType;

    @Column(nullable = false)
    private Double xCoord;

    @Column(nullable = false)
    private Double yCoord;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accessible = true;
}