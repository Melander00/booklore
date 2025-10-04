package com.adityachandel.booklore.service.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adityachandel.booklore.config.security.service.AuthenticationService;
import com.adityachandel.booklore.exception.ApiError;
import com.adityachandel.booklore.model.dto.BookLoreUser;
import com.adityachandel.booklore.model.dto.request.CreateApiTokenRequest;
import com.adityachandel.booklore.model.dto.request.UpdateApiTokenRequest;
import com.adityachandel.booklore.model.entity.ApiTokenEntity;
import com.adityachandel.booklore.model.entity.BookLoreUserEntity;
import com.adityachandel.booklore.repository.ApiTokenRepository;
import com.adityachandel.booklore.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiTokenService {

    private final ApiTokenRepository tokenRepository;
    private final UserRepository userRepository;
    
    private final AuthenticationService authenticationService;

    private BookLoreUserEntity getUser() {
        BookLoreUser currentUser = authenticationService.getAuthenticatedUser();
        BookLoreUserEntity userEntity = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> ApiError.USER_NOT_FOUND.createException(currentUser.getId()));
        return userEntity;
    }

    public ApiTokenEntity createToken(CreateApiTokenRequest body) {
        BookLoreUserEntity userEntity = getUser();

        Instant expiresAt = body.getExpiresInDays() != null
                        ? Instant.now().plusSeconds(body.getExpiresInDays() * 24 * 60 * 60)
                        : null;

        ApiTokenEntity token = ApiTokenEntity.builder()
                .user(userEntity)
                .name(body.getName())
                .token(generateSecureToken())
                .permissions(body.getPermissions())
                .expiresAt(expiresAt)
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        return tokenRepository.save(token);
    }


    public List<ApiTokenEntity> getUserTokens() {
        BookLoreUserEntity userEntity = getUser();

        return tokenRepository.findByUser(userEntity);
    }

    public ApiTokenEntity regenerateToken(Long tokenId) {
        BookLoreUserEntity userEntity = getUser();

        ApiTokenEntity token = tokenRepository.findById(tokenId).orElseThrow();
        if(token.getUser().getId() != userEntity.getId()) {
            throw ApiError.GENERIC_UNAUTHORIZED.createException("You do not have permission to update API tokens you do not own.");
        }
        token.setToken(generateSecureToken());
        token.setRevoked(false);
        return tokenRepository.save(token);
    }

    public ApiTokenEntity updateToken(Long id, UpdateApiTokenRequest body) {
        BookLoreUserEntity userEntity = getUser();

        ApiTokenEntity token = tokenRepository.findById(id).orElseThrow();
        if(token.getUser().getId() != userEntity.getId()) {
            throw ApiError.GENERIC_UNAUTHORIZED.createException("You do not have permission to update API tokens you do not own.");
        }

        Instant expiresAt = body.getExpiresInDays() != null
                        ? Instant.now().plusSeconds(body.getExpiresInDays() * 24 * 60 * 60)
                        : null;

        if(body.getName() != null) token.setName(body.getName());
        if(body.getPermissions() != null) token.setPermissions(body.getPermissions());
        if(expiresAt != null) token.setExpiresAt(expiresAt);
        
        return tokenRepository.save(token);
    }

    public boolean revokeToken(Long tokenId) {
        BookLoreUserEntity userEntity = getUser();

        ApiTokenEntity token = tokenRepository.findById(tokenId).orElse(null);

        if(token == null) return false;

        if(token.getUser().getId() != userEntity.getId()) {
            throw ApiError.GENERIC_UNAUTHORIZED.createException("You do not have permission to update API tokens you do not own.");
        }
        token.setRevoked(true);
        tokenRepository.save(token);
        return true;
    }

    public boolean deleteToken(Long tokenId) {
        BookLoreUserEntity userEntity = getUser();

        tokenRepository.deleteByIdAndUser(tokenId, userEntity);

        return true;
    }

    // public ApiTokenEntity createToken(BookLoreUserEntity user, String name,
    //                               CreateApiTokenRequest.Permissions permissions, Instant expiresAt) {
    // String tokenValue = generateSecureToken();

    //                                 System.out.println("[DEBUG] SERVICE NAME: " + user.getName());

    // ApiTokenEntity token = ApiTokenEntity.builder()
    //         .user(user)
    //         .name(name)
    //         .token(tokenValue)
    //         .permissions("{}") // todo: to json
    //         .expiresAt(expiresAt)
    //         .revoked(false)
    //         .createdAt(Instant.now())
    //         .build();
    // return tokenRepository.save(token);
    // }

    // public List<ApiTokenEntity> getUserTokens(BookLoreUserEntity user) {
    //     return tokenRepository.findAll()
    //             .stream()
    //             .filter(token -> token.getUser().getId().equals(user.getId()))
    //             .toList();
    // }

    // public Optional<ApiTokenEntity> regenerateToken(Long tokenId, BookLoreUserEntity user) {
    //     return tokenRepository.findById(tokenId)
    //             .filter(token -> token.getUser().getId().equals(user.getId()))
    //             .map(token -> {
    //                 token.setToken(generateSecureToken());
    //                 token.setRevoked(false);
    //                 return tokenRepository.save(token);
    //             });
    // }

    // public Optional<ApiTokenEntity> updateToken(Long tokenId, BookLoreUserEntity user,
    //                                         String newName,
    //                                         UpdateApiTokenRequest.Permissions newPermissions,
    //                                         Instant newExpiry) {
    // return tokenRepository.findById(tokenId)
    //         .filter(token -> token.getUser().getId().equals(user.getId()))
    //         .map(token -> {
    //             if (newName != null) token.setName(newName);
    //             if (newPermissions != null) token.setPermissions("{}"); // todo: to json
    //             if (newExpiry != null) token.setExpiresAt(newExpiry);
    //             return tokenRepository.save(token);
    //         });
    // }

    // public boolean revokeToken(Long tokenId, BookLoreUserEntity user) {
    //     return tokenRepository.findById(tokenId)
    //             .filter(token -> token.getUser().getId().equals(user.getId()))
    //             .map(token -> {
    //                 token.setRevoked(true);
    //                 tokenRepository.save(token);
    //                 return true;
    //             })
    //             .orElse(false);
    // }


    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

