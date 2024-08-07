import { Component, OnInit } from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";
import {TableInfo} from "../../model/table-info";
import {ColumnInfo} from "../../model/column-info";

@Component({
  selector: 'app-scheduleddeletion',
  templateUrl: './scheduleddeletion.component.html',
  styleUrls: ['./scheduleddeletion.component.scss']
})
export class ScheduleddeletionComponent implements OnInit {
  table:TableInfo=new TableInfo();
  scheduledDeletion :any[]=[];
  columnNames:any[]=[]
  role=""
  authority=""
  constructor(public ref: DynamicDialogRef, public config: DynamicDialogConfig,private tableService:TableService,private messageService:MessageService) { }
  ngOnInit(): void {
    this.table = this.config.data.table;
    this.role=this.config.data.role;
      this.authority=this.config.data.authority;
    this.funcolumnNames();
    this.paramHistory();
  }
  funcolumnNames()
  {  this.columnNames = this.table.columns.map((column:ColumnInfo) => column.name)}


  paramHistory() {
    this.tableService.getDeleteRequests(this.table.name).subscribe({
      next: (data: any[]) => {
        this.scheduledDeletion = data;

      },
      error: (error) => {
        console.error('Error loading history:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error loading Scheduled params',
          detail: `Scheduled for deletion params loaded for ${this.table.name}`
        });
      }
    });
  }
  canceldeletion(table:TableInfo, primaryKeyValue: string) {
    this.tableService.cancelDeletion(table.name, primaryKeyValue).subscribe({
      next: (response) => {
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Deletion Cancelled',detail: response.success });
          this.paramHistory()}
        else {
          this.messageService.add({ severity: 'warn', summary: 'Cancellation Failed', detail: response.error });
        }},
      error: (error) => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Deletion not Cancelled', detail: error.error });
      }
    });
  }


}
