package com.adityachandel.booklore.controller;

import com.adityachandel.booklore.model.entity.ApiTokenEntity;
import com.adityachandel.booklore.model.entity.BookLoreUserEntity;
import com.adityachandel.booklore.service.security.ApiTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/api-tokens") 
public class ApiTokenController {

    private final ApiTokenService tokenService;

    // --- GET /api/v1/api-tokens ---
    // Corresponds to: ApiTokenController.getTokens() and ApiTokenService.getTokens()
    @GetMapping
    public ResponseEntity<List<ApiTokenEntity>> getAllTokensForUser(
        @AuthenticationPrincipal BookLoreUserEntity currentUser
    ) {
        List<ApiTokenEntity> tokens = tokenService.getTokens(currentUser);
        // Note: You might want to map this to a DTO to hide the raw token string
        return ResponseEntity.ok(tokens);
    }

    // --- POST /api/v1/api-tokens/generate (or just /create) ---
    // Corresponds to: ApiTokenController.createToken() and ApiTokenService.createToken()
    @PostMapping("/generate")
    public ResponseEntity<String> createToken(
        @AuthenticationPrincipal BookLoreUserEntity currentUser,
        // Using @RequestBody is more typical for complex objects than @RequestParam
        // but sticking to your current use for simplicity.
        @RequestParam(required = false) String permissions,
        @RequestParam(required = false) Long expiresInSeconds
    ) {
        Instant expiresAt = expiresInSeconds != null ? Instant.now().plusSeconds(expiresInSeconds) : null;
        ApiTokenEntity token = tokenService.createToken(currentUser, permissions, expiresAt);
        // Returns the raw token string, often returned inside a small DTO or JSON object
        return ResponseEntity.ok(token.getToken());
    }

    // --- PUT /api/v1/api-tokens/{tokenId}/regenerate ---
    // Corresponds to: ApiTokenController.regenerate() and ApiTokenService.regenerateToken()
    @PutMapping("/{tokenId}/regenerate")
    public ResponseEntity<String> regenerateToken(
        @AuthenticationPrincipal BookLoreUserEntity currentUser,
        @PathVariable Long tokenId
    ) {
        String newToken = tokenService.regenerateToken(currentUser, tokenId);
        return ResponseEntity.ok(newToken);
    }

    // --- PUT /api/v1/api-tokens/{tokenId} (or /update) ---
    // Corresponds to: ApiTokenController.update() and ApiTokenService.updateToken()
    @PutMapping("/{tokenId}")
    public ResponseEntity<ApiTokenEntity> updateToken(
        @AuthenticationPrincipal BookLoreUserEntity currentUser,
        @PathVariable Long tokenId,
        @RequestParam(required = false) String newPermissions,
        @RequestParam(required = false) Long expiresInSeconds // Optional: allow updating expiry
    ) {
        Instant newExpiresAt = expiresInSeconds != null ? Instant.now().plusSeconds(expiresInSeconds) : null;
        ApiTokenEntity updatedToken = tokenService.updateToken(currentUser, tokenId, newPermissions, newExpiresAt);
        
        // Handle case where token might not be found or user doesn't own it
        if (updatedToken == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedToken);
    }

    // --- DELETE /api/v1/api-tokens/{tokenId} ---
    // Corresponds to: ApiTokenController.delete() and ApiTokenService.deleteToken()
    @DeleteMapping("/{tokenId}")
    public ResponseEntity<Void> deleteToken(
        @AuthenticationPrincipal BookLoreUserEntity currentUser,
        @PathVariable Long tokenId
    ) {
        boolean deleted = tokenService.deleteToken(currentUser, tokenId);
        
        if (deleted) {
            // 204 No Content is standard for a successful DELETE
            return ResponseEntity.noContent().build(); 
        } else {
            // Could be 404 Not Found or 403 Forbidden, depending on implementation detail
            return ResponseEntity.notFound().build(); 
        }
    }
}
