import { Component, OnInit } from '@angular/core';
import {ParamAudit} from "../../model/param-audit";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-allparamhistory',
  templateUrl: './allparamhistory.component.html',
  styleUrls: ['./allparamhistory.component.scss']
})
export class AllparamhistoryComponent implements OnInit {
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
    this.paramHistory();
  }

  closeDialog() {
    this.ref.close()
  }


  paramHistory() {
    this.tableService.allparamHistory().subscribe({
      next: (data: ParamAudit[]) => {
        this.paramAuditHistory = data;
        this.messageService.add({
          severity: 'success',
          summary: 'History',
          detail: `History loaded`
        });
      },
      error: (error) => {
        console.error('Error loading history:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error loading history',
          detail: `Failed to load history`
        });
      }
    });
  }

}
