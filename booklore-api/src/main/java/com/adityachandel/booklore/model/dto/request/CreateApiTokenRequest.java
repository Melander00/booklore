package com.adityachandel.booklore.model.dto.request;

import lombok.Data;

@Data
public class CreateApiTokenRequest {
    private String name;
    private Permissions permissions;
    private Long expiresInDays;

    @Data
    public static class Permissions {
        // future: add more fine-grained flags as needed
    }
}
