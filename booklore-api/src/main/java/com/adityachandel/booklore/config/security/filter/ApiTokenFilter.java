package com.adityachandel.booklore.config.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.adityachandel.booklore.config.security.userdetails.UserAuthenticationDetails;
import com.adityachandel.booklore.mapper.custom.BookLoreUserTransformer;
import com.adityachandel.booklore.model.dto.BookLoreUser;
import com.adityachandel.booklore.model.entity.BookLoreUserEntity;
import com.adityachandel.booklore.model.entity.UserPermissionsEntity;
import com.adityachandel.booklore.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ApiTokenFilter extends OncePerRequestFilter {
    
    // private final ApiTokenService tokenService;
    private final UserRepository userRepository;
    private final BookLoreUserTransformer bookLoreUserTransformer;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return "websocket".equalsIgnoreCase(request.getHeader("Upgrade"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if(token != null && validateToken(token)) {
            Long userId = getUserIdFromToken(token);
            BookLoreUserEntity bookLoreUserEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            BookLoreUser bookLoreUser = bookLoreUserTransformer.toDTO(bookLoreUserEntity);
            List<GrantedAuthority> authorities = getAuthorities(bookLoreUserEntity.getPermissions());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(bookLoreUser, null, authorities);
            authentication.setDetails(new UserAuthenticationDetails(request, bookLoreUser.getId()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("X-API-TOKEN");
        return header;
    }

    private boolean validateToken(String apiToken) {
        // return tokenService.exists(apiToken);
        return false;
    }

    private Long getUserIdFromToken(String apiToken) {
        // return tokenService.userIdFromToken(apiToken);
        return 1L;
    }

    private List<GrantedAuthority> getAuthorities(UserPermissionsEntity permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (permissions.isPermissionUpload()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_UPLOAD"));
        }
        if (permissions.isPermissionDownload()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DOWNLOAD"));
        }
        if (permissions.isPermissionEditMetadata()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EDIT_METADATA"));
        }
        if (permissions.isPermissionManipulateLibrary()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MANIPULATE_LIBRARY"));
        }
        if (permissions.isPermissionAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorities;
    }

}
