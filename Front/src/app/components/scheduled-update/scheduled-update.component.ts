import { Component, OnInit } from '@angular/core';
import {TableInfo} from "../../model/table-info";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";
import {ColumnInfo} from "../../model/column-info";

@Component({
  selector: 'app-scheduled-update',
  templateUrl: './scheduled-update.component.html',
  styleUrls: ['./scheduled-update.component.scss']
})
export class ScheduledUpdateComponent implements OnInit {
  table: TableInfo = new TableInfo();
  scheduledUpdate: any[] = [];
  columnNames: any[] = [];

  constructor(public ref: DynamicDialogRef, public config: DynamicDialogConfig, private tableService: TableService, private messageService: MessageService) {}

  ngOnInit(): void {
    this.table = this.config.data.table;
    this.funcolumnNames();
    this.loadscheduledupdate();
  }

  funcolumnNames() {
    if (this.table && this.table.columns) {
      // Add the new column 'modifiedBy'
      this.columnNames = [...this.table.columns.map((column: ColumnInfo) => column.name), 'modifiedBy'];
    } else {
      console.error('Table or columns not defined');
    }
  }




  loadscheduledupdate() {
    this.tableService.getupdaterequests(this.table.name).subscribe({
      next: (data: any[]) => {
        this.scheduledUpdate = data;

      },
      error: (error) => {
        console.error('Error loading scheduled for update:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error loading Scheduled params',
          detail: `Scheduled for update params loaded for ${this.table.name}`
        });
      }
    });
  }

  cancelupdate(table: TableInfo, primaryKeyValue: string) {
    this.tableService.cancelUpdateInstance(table.name, primaryKeyValue).subscribe({
      next: (response) => {
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Update Cancelled', detail: response.success });
          this.loadscheduledupdate();
        } else {
          this.messageService.add({ severity: 'warn', summary: 'Cancellation Failed', detail: response.error });
        }
      },
      error: (error) => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Update not Cancelled', detail: error.error });
      }
    });
  }
}
