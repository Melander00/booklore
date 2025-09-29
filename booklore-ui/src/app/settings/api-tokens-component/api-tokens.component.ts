import { CommonModule } from "@angular/common";
import { Component, inject, OnDestroy, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MessageService } from "primeng/api";
import { Button } from "primeng/button";
import { DialogService, DynamicDialogRef } from "primeng/dynamicdialog";
import { SelectModule } from "primeng/select";
import { TableModule } from "primeng/table";
import { Subject } from "rxjs";
import { UserService } from "../user-management/user.service";
import { ApiToken, ApiTokenUpdateDTO } from "./api-tokens.model";
import { ApiTokensService } from "./api-tokens.service";
import { CreateTokenDialogComponent } from "./create-token-dialog/create-token.component";

@Component({
  selector: "app-api-tokens-component",
  imports: [
    TableModule,
    Button,
    SelectModule,
    FormsModule,
    CommonModule
  ],
  templateUrl: "./api-tokens.component.html",
  styleUrl: "./api-tokens.component.scss",
})
export class ApiTokensComponent implements OnInit, OnDestroy {
  private readonly messageService = inject(MessageService);
  private readonly apiTokensService = inject(ApiTokensService);
  private readonly userService = inject(UserService);

  ref: DynamicDialogRef | undefined;
  private readonly dialogService = inject(DialogService);

  private readonly destroy$ = new Subject<void>();

  apiTokens: ApiToken[] = [];

  expirationDates: {
    label: string,
    value: number
  }[] = [
      { label: "7 Days", value: 7 },
      { label: "14 Days", value: 14 },
      { label: "30 Days", value: 30 },
      { label: "90 Days", value: 90 },
      { label: "180 Days", value: 180 },
      { label: "365 Days", value: 365 },
  ]

  selectedExpirationDate = this.expirationDates[0].value

  ngOnInit() {
    this.loadTokens();
  }

  private loadTokens() {
    // this.apiTokens = [{ name: "My api token", expiryDate: new Date("2026-01-01"), creationDate: new Date(), tokenId: 1, isEditing: false }]
    // return;

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
    const dto: ApiTokenUpdateDTO = {
      name: token.name,
      expiresInDays: this.selectedExpirationDate,
      tokenId: token.tokenId,
    }
    this.apiTokensService.updateToken(dto).subscribe({
      next: () => {
        token.isEditing = false;
        this.messageService.add({
          severity: "success",
          summary: "Success",
          detail: "Token updated successfully."
        })
        this.loadTokens();
      },
      error: (err) => {
        // if (err.status !== 404) {
        this.messageService.add({
          severity: "error",
          summary: "Save Error",
          detail: "Unable to save API token. Please try again.",
        });
        // }
      },
    })
  }

  deleteToken(token: ApiToken) {
    if (confirm(`Are you sure you want to delete the token "${token.name}"?`)) {
      this.apiTokensService.deleteToken(token) // todo: finish
    }
  }

  openCreateTokenDialog(): void {
    this.ref = this.dialogService.open(CreateTokenDialogComponent, {
      header: 'Create new API token',
      modal: true,
      closable: true,
      style: { position: 'absolute', top: '15%' },
    });
    this.ref.onClose.subscribe((result) => {
      if (result) {
        this.loadTokens();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
