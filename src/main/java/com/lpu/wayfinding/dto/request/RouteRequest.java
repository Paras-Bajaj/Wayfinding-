// src/main/java/com/lpu/wayfinding/dto/request/RouteRequest.java
package com.lpu.wayfinding.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RouteRequest {
    @NotNull
    private Long startId;
    @NotNull
    private Long endId;
    private Boolean wheelchair = false;
    private Boolean peakHours = false;
}