// src/main/java/com/lpu/wayfinding/dto/response/PoiResponse.java
package com.lpu.wayfinding.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PoiResponse {
    private Long poiId;
    private Long nodeId;
    private String name;
    private String type;
    private Double distance;
    private List<Long> path;
}