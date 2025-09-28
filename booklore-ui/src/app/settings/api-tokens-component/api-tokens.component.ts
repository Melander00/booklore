import { Component, inject, OnDestroy, OnInit } from "@angular/core";
import { MessageService } from "primeng/api";
import { Button } from "primeng/button";
import { Divider } from "primeng/divider";
import { TableModule } from "primeng/table";
import { Subject } from "rxjs";
import { UserService } from "../user-management/user.service";
import { ApiToken } from "./api-tokens.model";
import { ApiTokensService } from "./api-tokens.service";

@Component({
  selector: "app-api-tokens-component",
  imports: [
    Divider,
    TableModule,
    Button
  ],
  templateUrl: "./api-tokens.component.html",
  styleUrl: "./api-tokens.component.scss",
})
export class ApiTokensComponent implements OnInit, OnDestroy {
  private readonly messageService = inject(MessageService);
  private readonly apiTokensService = inject(ApiTokensService);
  private readonly userService = inject(UserService);

  private readonly destroy$ = new Subject<void>();

  apiTokens: ApiToken[] = [];

  ngOnInit() {
    this.loadTokens();
  }

  private loadTokens() {
    // this.apiTokens = [{token: "ABC", tokenId: 1, isEditing: false, name: "My very secret token"}]
    return;

    this.apiTokensService.getTokens().subscribe({
      next: (tokens) => {
        this.apiTokens = tokens;
      },
      error: (err) => {
        if (err.status !== 404) {
          this.messageService.add({
            severity: "error",
            summary: "Load Error",
            detail: "Unable to load API tokens. Please try again.",
          });
        }
      },
    });
  }

  toggleEditToken(token: ApiToken) {
    token.isEditing = !token.isEditing;
  }

  saveToken(token: ApiToken) {
    this.apiTokensService.updateToken(token).subscribe() // todo: finish
  }

  deleteToken(token: ApiToken) {
    if(confirm(`Are you sure you want to delete the token "${token.name}"?`)) {
        this.apiTokensService.deleteToken(token) // todo: finish
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
