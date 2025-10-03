package com.adityachandel.booklore.service.security;

import com.adityachandel.booklore.model.entity.ApiTokenEntity;
import com.adityachandel.booklore.model.entity.BookLoreUserEntity;
import com.adityachandel.booklore.repository.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiTokenService {

    private final ApiTokenRepository tokenRepository;

    public ApiTokenEntity createToken(BookLoreUserEntity user, String name,
                                  CreateApiTokenRequest.Permissions permissions, Instant expiresAt) {
    String tokenValue = generateSecureToken();
    ApiTokenEntity token = ApiTokenEntity.builder()
            .user(user)
            .name(name)
            .token(tokenValue)
            .permissions(permissions) // now an object, not a string
            .expiresAt(expiresAt)
            .revoked(false)
            .createdAt(Instant.now())
            .build();
    return tokenRepository.save(token);
    }

    public List<ApiTokenEntity> getUserTokens(BookLoreUserEntity user) {
        return tokenRepository.findAll()
                .stream()
                .filter(token -> token.getUser().getId().equals(user.getId()))
                .toList();
    }

    public Optional<ApiTokenEntity> regenerateToken(Long tokenId, BookLoreUserEntity user) {
        return tokenRepository.findById(tokenId)
                .filter(token -> token.getUser().getId().equals(user.getId()))
                .map(token -> {
                    token.setToken(generateSecureToken());
                    token.setRevoked(false);
                    return tokenRepository.save(token);
                });
    }

    public Optional<ApiTokenEntity> updateToken(Long tokenId, BookLoreUserEntity user,
                                            String newName,
                                            UpdateApiTokenRequest.Permissions newPermissions,
                                            Instant newExpiry) {
    return tokenRepository.findById(tokenId)
            .filter(token -> token.getUser().getId().equals(user.getId()))
            .map(token -> {
                if (newName != null) token.setName(newName);
                if (newPermissions != null) token.setPermissions(newPermissions);
                if (newExpiry != null) token.setExpiresAt(newExpiry);
                return tokenRepository.save(token);
            });
    }

    public boolean revokeToken(Long tokenId, BookLoreUserEntity user) {
        return tokenRepository.findById(tokenId)
                .filter(token -> token.getUser().getId().equals(user.getId()))
                .map(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                    return true;
                })
                .orElse(false);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

