// src/main/java/com/lpu/wayfinding/entity/Poi.java
package com.lpu.wayfinding.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pois")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Poi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id", nullable = false)
    private Long nodeId;

    @Column(name = "poi_type", nullable = false)
    private String poiType; // WASHROOM, WATER, EXIT, COFFEE

    @Column(nullable = false)
    private String name;
}