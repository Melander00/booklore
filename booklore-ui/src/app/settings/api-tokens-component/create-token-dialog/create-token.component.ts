import { CommonModule } from "@angular/common";
import { Component, inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { MessageService } from "primeng/api";
import { Button } from "primeng/button";
import { Checkbox } from "primeng/checkbox";
import { DynamicDialogRef } from "primeng/dynamicdialog";
import { InputText } from "primeng/inputtext";
import { MultiSelectModule } from "primeng/multiselect";
import { ApiTokenCreationDTO } from "../api-tokens.model";
import { ApiTokensService } from "../api-tokens.service";

@Component({
    selector: "app-create-token-dialog",
    standalone: true,
    imports: [
        InputText,
        Checkbox,
        Button,
        ReactiveFormsModule,
        FormsModule,
        MultiSelectModule,
        CommonModule,
    ],
    templateUrl: "./create-token.component.html",
    styleUrl: "./create-token.component.scss",
})
export class CreateTokenDialogComponent implements OnInit {
    tokenForm!: FormGroup;

    private readonly fb = inject(FormBuilder);
    private readonly messageService = inject(MessageService);
    private readonly ref = inject(DynamicDialogRef);
    private readonly tokenService = inject(ApiTokensService);


    expirationDates: {
        label: string,
        value: number
    }[] = [
        {label: "7 Days", value: 7},
        {label: "14 Days", value: 14},
        {label: "30 Days", value: 30},
        {label: "90 Days", value: 90},
        {label: "180 Days", value: 180},
        {label: "365 Days", value: 365},
    ]

    ngOnInit(): void {
        this.tokenForm = this.fb.group({
            name: ['', [Validators.required]],
            expiresInDays: [this.expirationDates[0].value, [Validators.required]]
        })
    }

    createToken() {
        if (this.tokenForm.invalid) {
            this.messageService.add({
                severity: "warn",
                summary: "Validation Error",
                detail: "Please correct errors before submitting."
            });
            return;
        }

        const tokenData: ApiTokenCreationDTO = {...this.tokenForm.value, permissions: null}

        this.tokenService.generateToken(tokenData).subscribe({
            next: () => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Token Created',
                    detail: 'The API token has been successfully created.'
                });
                this.ref.close(true);
            },
            error: (err) => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'User Creation Failed',
                    detail: err?.error?.message
                        ? `Unable to create user: ${err.error.message}`
                        : 'An unexpected error occurred while creating the user. Please try again later.'
                });
            }
        })
    }
}