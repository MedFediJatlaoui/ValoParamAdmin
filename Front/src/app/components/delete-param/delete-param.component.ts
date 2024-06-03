import {Component, OnInit} from '@angular/core';
import {DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {ParamTableComponent} from "../param-table/param-table.component";
import {TableInfo} from "../../model/table-info";

@Component({
  selector: 'app-delete-param',
  templateUrl: './delete-param.component.html',
  styleUrls: ['./delete-param.component.scss'],
})
export class DeleteParamComponent implements OnInit {

 table:TableInfo = new TableInfo();
primaryKeyValue: string="";
  constructor( private ref: DynamicDialogRef,private tableService:TableService,private messageService:MessageService,private router:Router,private paramTableComponent:ParamTableComponent) { }

  ngOnInit(): void {
    this.table = this.tableService.dataDeleteInstance.table;
    this.primaryKeyValue = this.tableService.dataDeleteInstance.primaryKeyValue;
  }
  closeDialog() {
this.ref.close()
  }
  deleteRecord(): void {
    this.tableService.deleteRecord(this.table.name, this.primaryKeyValue).subscribe({
      next: (response: any) => {
        if (response.success) {
          console.log(response);
          this.messageService.add({
            severity: 'success',
            summary: 'Parameter scheduled to be deleted',
            detail: `${response.success}`
          });
          this.paramTableComponent.getDataTable(this.table);
          this.ref.close();
        }
      },
      error: (error: any) => {
        console.log(error);
      }
    });
  }


}
