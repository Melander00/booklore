import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { API_CONFIG } from "../../config/api-config";
import { ApiToken, ApiTokenCreationDTO, ApiTokenUpdateDTO } from "./api-tokens.model";




@Injectable({
    providedIn: 'root'
})
export class ApiTokensService {
    private readonly baseUrl = `${API_CONFIG.BASE_URL}/api/v1/api-token`;
    private readonly http = inject(HttpClient);

    getTokens(): Observable<ApiToken[]> {
        return this.http.get<ApiToken[]>(`${this.baseUrl}/get`);
    }

    generateToken(token: ApiTokenCreationDTO): Observable<ApiToken> {
        return this.http.post<ApiToken>(`${this.baseUrl}/generate`, {token}); // todo: permissions
    }

    regenerateToken(tokenId: number): Observable<ApiToken> {
        return this.http.put<ApiToken>(`${this.baseUrl}/regenerate`, {tokenId});
    }

    updateToken(token: ApiTokenUpdateDTO): Observable<ApiToken> {
        return this.http.put<ApiToken>(`${this.baseUrl}/update`, {token}); // todo: permissions
    } 

    deleteToken(token: ApiToken): void {
        this.http.delete(`${this.baseUrl}/${token.tokenId}`)
    }
}