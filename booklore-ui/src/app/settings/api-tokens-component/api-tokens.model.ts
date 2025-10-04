export interface ApiToken {
  id: number;
  name: string;
  permissions: ApiTokenPermissions;
  expiresAt: Date;
  createdAt: Date;
  revoked: boolean;
  isEditing: boolean;
  token?: string;
}

export interface ApiTokenPermissions {
  example: boolean;
}

export type ApiTokenPermissionLabelMap = {
  [K in keyof ApiTokenPermissions]: string;
};

// Centralized placement for ease of modifying permissions.
export function GenerateApiTokenPermissionLabels(): ApiTokenPermissionLabelMap {
  return {
    example: "Example",
  };
}



export interface ApiTokenCreationDTO {
  name: string;
  expiresInDays: number;
  permissions: ApiTokenPermissions;
}

export interface ApiTokenUpdateDTO {
  name: string;
  expiresInDays: number;
  permissions: ApiTokenPermissions;
}
