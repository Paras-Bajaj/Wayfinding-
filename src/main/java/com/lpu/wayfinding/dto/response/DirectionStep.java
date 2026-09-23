// src/main/java/com/lpu/wayfinding/dto/response/DirectionStep.java
package com.lpu.wayfinding.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DirectionStep {
    private int step;
    private Long nodeId;
    private String name;
    private String type;
    private String instruction;
}