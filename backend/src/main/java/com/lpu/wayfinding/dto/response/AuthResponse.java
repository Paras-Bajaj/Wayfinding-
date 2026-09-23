// src/main/java/com/lpu/wayfinding/dto/response/AuthResponse.java
package com.lpu.wayfinding.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private String username;
    private Long expiresIn;
}