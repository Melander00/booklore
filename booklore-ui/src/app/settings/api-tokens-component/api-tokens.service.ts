import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";

export interface ApiToken {

}

@Injectable({
    providedIn: 'root'
})
export class ApiTokensService {

    private http = inject(HttpClient);

}