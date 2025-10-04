package com.adityachandel.booklore.model.dto.response;

import java.time.Instant;

import com.adityachandel.booklore.model.ApiTokenPermissions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiTokenResponse {
    private Long id;
    private String name;
    private ApiTokenPermissions permissions;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean revoked;
    private String token; // only on create/regenerate
}

