import { Component, OnInit } from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {ParamAudit} from "../../model/param-audit";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-param-history',
  templateUrl: './param-history.component.html',
  styleUrls: ['./param-history.component.scss']
})
export class ParamHistoryComponent implements OnInit {
  tableName: string ="";
paramAuditHistory:ParamAudit[]=[];
  constructor(public ref: DynamicDialogRef, public config: DynamicDialogConfig,private tableService:TableService,private messageService:MessageService) { }
  logClass(action: string) {
    if (action === 'DELETED') {
      console.log('p-badge p-badge-danger');
    } else if (action === 'EDITED') {
      console.log('p-badge p-badge-warning');
    } else if (action === 'ADDED') {
      console.log('p-badge p-badge-success');
    }
  }
  getClassForAction(action: string): string {
    switch (action.toUpperCase()) {
      case 'DELETED':
        return 'p-badge p-badge-danger';
      case 'EDITED':
        return 'p-badge p-badge-warning';
      case 'ADDED':
        return 'p-badge p-badge-success';
      default:
        return '';
    }
  }

  ngOnInit(): void {
    this.tableName = this.config.data.tableName;
    this.paramHistory();
  }

  closeDialog() {
    this.ref.close()
  }


  paramHistory() {
    this.tableService.paramHistory(this.tableName).subscribe({
      next: (data: ParamAudit[]) => {
        this.paramAuditHistory = data;
        this.messageService.add({
          severity: 'success',
          summary: 'history',
          detail: `History loaded for ${this.tableName}`
        });
      },
      error: (error) => {
        console.error('Error loading history:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error loading history',
          detail: `Failed to load history for ${this.tableName}`
        });
      }
    });
  }


}
