package com.adityachandel.booklore.model.dto.request;

import com.adityachandel.booklore.model.ApiTokenPermissions;

import lombok.Data;

@Data
public class UpdateApiTokenRequest {
    private String name;
    private ApiTokenPermissions permissions;
    private Long expiresInDays;
}
