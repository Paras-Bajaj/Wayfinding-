// src/main/java/com/lpu/wayfinding/dto/request/LoginRequest.java
package com.lpu.wayfinding.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}