package com.adityachandel.booklore.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adityachandel.booklore.model.dto.request.CreateApiTokenRequest;
import com.adityachandel.booklore.model.dto.request.UpdateApiTokenRequest;
import com.adityachandel.booklore.model.dto.response.ApiTokenResponse;
import com.adityachandel.booklore.model.entity.ApiTokenEntity;
import com.adityachandel.booklore.service.security.ApiTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/api-token")
@RequiredArgsConstructor
public class ApiTokenController {

    private final ApiTokenService tokenService;

    @PostMapping("/create")
    public ResponseEntity<ApiTokenResponse> createToken(
            @RequestBody CreateApiTokenRequest request
    ) {
        ApiTokenEntity token = tokenService.createToken(request);

        return ResponseEntity.ok(toResponse(token, true));
    }

    @GetMapping("/my-tokens")
    public ResponseEntity<List<ApiTokenResponse>> getMyTokens() {
        List<ApiTokenResponse> tokens = tokenService.getUserTokens()
                .stream()
                .map(token -> toResponse(token, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/{id}/regenerate")
    public ResponseEntity<ApiTokenResponse> regenerateToken(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(toResponse(tokenService.regenerateToken(id), true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiTokenResponse> updateToken(
            @PathVariable Long id,
            @RequestBody UpdateApiTokenRequest request
    ) {
        return ResponseEntity.ok(toResponse(tokenService.updateToken(id, request), false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToken(
            @PathVariable Long id
    ) {
        boolean success = tokenService.deleteToken(id);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private ApiTokenResponse toResponse(ApiTokenEntity token, boolean includeSecret) {
        ApiTokenResponse.ApiTokenResponseBuilder builder = ApiTokenResponse.builder()
            .id(token.getId())
            .name(token.getName())
            .permissions(token.getPermissions())
            .expiresAt(token.getExpiresAt())
            .createdAt(token.getCreatedAt())
            .revoked(token.isRevoked());

        if(includeSecret) {
            builder.token(token.getToken());
        }

        return builder.build();

        // return new ApiTokenResponse(
        //         token.getId(),
        //         token.getName(),
        //         token.getPermissions(),
        //         token.getExpiresAt(),
        //         token.getCreatedAt(),
        //         token.isRevoked(),
        //         includeSecret ? token.getToken() : null
        // );
    }
}
