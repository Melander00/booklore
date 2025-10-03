package com.adityachandel.booklore.model.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UpdateApiTokenRequest {
    private String name;
    private Permissions permissions;
    private Long expiresInDays;

    @Data
    public static class Permissions {
        private boolean canRead;
        private boolean canWrite;
        private boolean canDelete;
    }
}
