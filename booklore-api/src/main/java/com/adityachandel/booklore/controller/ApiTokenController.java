package com.adityachandel.booklore.controller;

import com.adityachandel.booklore.model.dto.request.CreateApiTokenRequest;
import com.adityachandel.booklore.model.dto.request.UpdateApiTokenRequest;
import com.adityachandel.booklore.model.dto.response.ApiTokenResponse;
import com.adityachandel.booklore.model.entity.ApiTokenEntity;
import com.adityachandel.booklore.model.entity.BookLoreUserEntity;
import com.adityachandel.booklore.service.security.ApiTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/api-token")
@RequiredArgsConstructor
public class ApiTokenController {

    private final ApiTokenService tokenService;

    @PostMapping("/create")
    public ResponseEntity<ApiTokenResponse> createToken(
            @AuthenticationPrincipal BookLoreUserEntity currentUser,
            @RequestBody CreateApiTokenRequest request
    ) {
        Instant expiresAt = request.getExpiresInDays() != null
                ? Instant.now().plusSeconds(request.getExpiresInDays() * 24 * 60 * 60)
                : null;

        ApiTokenEntity token = tokenService.createToken(
                currentUser,
                request.getName(),
                request.getPermissions(),
                expiresAt
        );

        return ResponseEntity.ok(toResponse(token, true));
    }

    @GetMapping("/my-tokens")
    public ResponseEntity<List<ApiTokenResponse>> getMyTokens(
            @AuthenticationPrincipal BookLoreUserEntity currentUser
    ) {
        List<ApiTokenResponse> tokens = tokenService.getUserTokens(currentUser)
                .stream()
                .map(token -> toResponse(token, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/{id}/regenerate")
    public ResponseEntity<ApiTokenResponse> regenerateToken(
            @AuthenticationPrincipal BookLoreUserEntity currentUser,
            @PathVariable Long id
    ) {
        return tokenService.regenerateToken(id, currentUser)
                .map(token -> ResponseEntity.ok(toResponse(token, true)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiTokenResponse> updateToken(
            @AuthenticationPrincipal BookLoreUserEntity currentUser,
            @PathVariable Long id,
            @RequestBody UpdateApiTokenRequest request
    ) {
        Instant newExpiry = request.getExpiresInDays() != null
                ? Instant.now().plusSeconds(request.getExpiresInDays() * 24 * 60 * 60)
                : null;

        return tokenService.updateToken(id, currentUser, request.getName(), request.getPermissions(), newExpiry)
                .map(token -> ResponseEntity.ok(toResponse(token, false)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToken(
            @AuthenticationPrincipal BookLoreUserEntity currentUser,
            @PathVariable Long id
    ) {
        boolean success = tokenService.revokeToken(id, currentUser);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private ApiTokenResponse toResponse(ApiTokenEntity token, boolean includeSecret) {
        return new ApiTokenResponse(
                token.getId(),
                token.getName(),
                token.getPermissions(),
                token.getExpiresAt(),
                token.getCreatedAt(),
                token.isRevoked(),
                includeSecret ? token.getToken() : null
        );
    }
}
