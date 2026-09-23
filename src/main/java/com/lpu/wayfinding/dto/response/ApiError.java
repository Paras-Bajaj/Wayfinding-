// src/main/java/com/lpu/wayfinding/dto/response/ApiError.java
package com.lpu.wayfinding.dto.response;

import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiError {
    private String status;
    private String errorType;
    private String message;
    private Instant timestamp;
}