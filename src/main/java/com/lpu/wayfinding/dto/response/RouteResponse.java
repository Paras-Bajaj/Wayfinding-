// src/main/java/com/lpu/wayfinding/dto/response/RouteResponse.java
package com.lpu.wayfinding.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteResponse {
    private String status;
    private List<Long> path;
    private Double distance;
    private List<DirectionStep> directions;
    private Boolean wheelchairAccessible;
    private Boolean cached;
}