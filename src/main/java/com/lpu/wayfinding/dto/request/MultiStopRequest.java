// src/main/java/com/lpu/wayfinding/dto/request/MultiStopRequest.java
package com.lpu.wayfinding.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MultiStopRequest {
    @NotNull
    private Long startId;
    @NotEmpty
    private List<Long> stopIds;
    @NotNull
    private Long endId;
    private Boolean wheelchair = false;
    private Boolean peakHours = false;
}