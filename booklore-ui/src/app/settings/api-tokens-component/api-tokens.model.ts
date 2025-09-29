export interface ApiToken {
    name: string,
    tokenId: number,
    creationDate: Date,
    expiryDate: Date,
    // permissions: any[],
    isEditing: boolean,
}

export interface ApiTokenCreationDTO {
    name: string,
    expiresInDays: number,
}

export interface ApiTokenUpdateDTO {
    name: string,
    expiresInDays: number,
    tokenId: number,
}