import { CommonModule } from "@angular/common";
import { Component, inject, Input } from "@angular/core";
import { Button } from "primeng/button";
import { InputText } from "primeng/inputtext";
import { MessageService } from "primeng/api";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";

@Component({
  selector: "app-show-token-dialog",
  standalone: true,
  imports: [CommonModule, Button, InputText],
  templateUrl: "./show-token-dialog.component.html",
})
export class ShowTokenDialogComponent {
  @Input() tokenValue!: string;

  private readonly ref = inject(DynamicDialogRef);
  private readonly messageService = inject(MessageService);
  private readonly config = inject(DynamicDialogConfig); // <-- inject the config

  ngOnInit() {
    this.tokenValue = this.config.data?.tokenValue ?? '';
  }

  copyToken() {
    if (this.tokenValue) {
      navigator.clipboard.writeText(this.tokenValue).then(() => {
        this.messageService.add({
          severity: "info",
          summary: "Copied",
          detail: "Token copied to clipboard",
        });
      });
    }
  }

  closeDialog() {
    this.ref.close();
  }
}
