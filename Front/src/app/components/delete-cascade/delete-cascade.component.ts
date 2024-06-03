import { Component, OnInit } from '@angular/core';
import {TableInfo} from "../../model/table-info";
import {DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {ParamTableComponent} from "../param-table/param-table.component";
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-delete-cascade',
  templateUrl: './delete-cascade.component.html',
  styleUrls: ['./delete-cascade.component.scss']
})
export class DeleteCascadeComponent implements OnInit {
  table:TableInfo = new TableInfo();
  primaryKeyValue: string="";
  references:any[]=[]
  constructor(private ref: DynamicDialogRef,private tableService:TableService,private messageService:MessageService,private router:Router,private paramTableComponent:ParamTableComponent) { }
  isLoading:boolean=false

  ngOnInit(): void {
    console.log(this.tableService.dataDeleteInstance)
    this.table = this.tableService.dataDeleteInstance.table;
    this.primaryKeyValue = this.tableService.dataDeleteInstance.primaryKeyValue;
    this.references = this.tableService.dataDeleteInstance.references;
  }
  closeDialog() {
    this.ref.close()
  }


  async deletecascadeRecord(tableName:string,primaryKeyValue:string){
try {
  this.isLoading=true
  const response: any = await lastValueFrom(this.tableService.deleteCascade(tableName, primaryKeyValue))
  this.tableService.deleteCascade(tableName,primaryKeyValue).subscribe({
    next : (response : any) =>{
      if (response.success) {
        this.isLoading=false
        this.messageService.add({
          severity: 'success',
          summary: 'Cascade Deletion scheduled',
          detail: `${response.success}`
        });
        this.paramTableComponent.getDataTable(this.table);
        this.ref.close();
      }else {
        this.messageService.add({
          severity: 'error',
          summary: 'Parameter not deleted ',
          detail: `${response.success}`
        });
      }
    },
    error:(error:any) =>{
      console.error(error)
      this.ref.close()
    }
  })
}catch (error) {
  console.error(error)
  this.ref.close()
}
}

}
