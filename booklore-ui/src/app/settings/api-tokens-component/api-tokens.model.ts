export interface ApiToken {
    id: number,
    name: string,
    permissions: null,
    expiresAt: Date,
    createdAt: Date,
    revoked: boolean,
    isEditing: boolean,
    token?: string;
}

export interface ApiTokenCreationDTO {
    name: string,
    expiresInDays: number,
    permissions: null
}

export interface ApiTokenUpdateDTO {
    name: string,
    expiresInDays: number,
    permissions: null
}