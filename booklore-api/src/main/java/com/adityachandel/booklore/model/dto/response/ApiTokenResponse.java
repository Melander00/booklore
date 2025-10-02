package com.adityachandel.booklore.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ApiTokenResponse {
    private Long id;
    private String name;
    private CreateApiTokenRequest.Permissions permissions;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean revoked;
    private String token; // only on create/regenerate
}

