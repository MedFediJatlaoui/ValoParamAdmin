
import {Component, Input, OnInit} from '@angular/core';
import { MessageService } from 'primeng/api';
import { HttpClient } from '@angular/common/http';
import {DeleteParamComponent} from "../delete-param/delete-param.component";
import {DialogService} from "primeng/dynamicdialog";
import {TableInfo} from "../../model/table-info";
import {TableService} from "../../services/table/table.service";
import {ParamHistoryComponent} from "../param-history/param-history.component";
import {DeleteCascadeComponent} from "../delete-cascade/delete-cascade.component";
import { map, Observable} from "rxjs";
import {ColumnInfo} from "../../model/column-info";

@Component({
  selector: 'app-param-table',
  templateUrl: './param-table.component.html',
  styleUrls: ['./param-table.component.scss'],
})
export class ParamTableComponent implements OnInit{

  @Input() table: TableInfo=new TableInfo();
isLoading:boolean=false
  testtest:string=""
  async ngOnInit() {
    await this.getForeignKeyOptions(this.table.name);
  }

  async getForeignKeyOptions(tableName: string) {
    try {
      const response = await this.tableService.fkoptions(tableName).toPromise();
      this.table.foreignKeyoptions = response || [];
    } catch (error) {
      console.error(error);
    }
  }

  getInvalidColumns(row: any, table: any): string[] {
  const columnNames = table.columns.map((column:ColumnInfo) => column.name)
    const invalidColumns: string[] = [];
  const wrongsize:String[]=[];
    columnNames.forEach((column: any) => {
      if (this.checkNullable(column, table)&&(!row[column] || row[column]==='' )&& column.name!==table.pk.name) {
        invalidColumns.push(column);
      }

    });
    return invalidColumns;
  }
  checksizes(row:any,table:any){
  const wrongsize:string[]=[];
    table.columns.forEach((column:ColumnInfo)=> {
      const columnSize = parseInt(column.size, 10);
  if (row[column.name]&& column.type !="boolean" && row[column.name].length>columnSize){
    wrongsize.push(column.name)
   }
   });

  return wrongsize;
  };


  constructor(private messageService: MessageService, private tableService: TableService, private http: HttpClient,private dialogService:DialogService) {}
  getDataTable(table: TableInfo) {
    table.data=[];
    table.totalPageCount = Math.ceil(table.totalRows / table.limit);
    table.offset = (table.currentPage - 1) * table.limit;
    if (table.selectedColumns === []) {
      table.selectedColumns = table.columns.map(column => column.name);
    }

    this.tableService.getDataFromTable(table).subscribe({
      next:(DataFromTable)=>{table.data = DataFromTable.data;
        table.deleteRequests = DataFromTable.deleteRequests;
        table.updateRequests = DataFromTable.updateRequests;
       },
      error:()=>{this.messageService.add({ severity: 'error', summary: 'error data', detail: `Data loaded for ${table.name}` });},
    })

  }

  cancelUpdateInstance(table:TableInfo,primaryKeyValue :string){
    this.tableService.cancelUpdateInstance(table.name,primaryKeyValue).subscribe({
      next: (response) => {
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Update Cancelled',detail: response.success });
          this.getDataTable(table)
        } else {
          this.messageService.add({ severity: 'error', summary: 'Update not Cancelled',detail: response.error });
        }
      },
      error: (error) => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Update not Cancelled', detail: error});

      }
    });
  }




  canceldeletion(table:TableInfo, primaryKeyValue: string) {
  this.isLoading=true
    this.tableService.cancelDeletion(table.name, primaryKeyValue).subscribe({
      next: (response) => {
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Deletion Cancelled',detail: response.success });
          this.isLoading=false
           this.getDataTable(table)}
        else {
          this.messageService.add({ severity: 'warn', summary: 'Cancellation Failed', detail: response.error });
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Deletion not Cancelled', detail: error.error });
        this.isLoading = false;
      }
    });
  }

  checkForeignKey(column:string,table:TableInfo):boolean {
    let isFk : boolean = false

    for (let fk of table.foreignKeys){
      if (fk ===column){
        isFk=true
        break;
      }
    }
    return isFk
  }
  checkNullable(columnName: string, table: TableInfo): boolean {
    let isNotNullable: boolean = false;
    for (let column of table.columns) {

      if (column.name === columnName && column.isNullable === "NO" && column.name !== table.pk.name&& !this.checkForeignKey(columnName,table)&& column.type!=="date" ) {
        isNotNullable = true;
        break;
      }
    }

    return isNotNullable;
  }
  getForeignKeyOptionspercolumn(column: string, instance: string): string[] {
    let options: string[] = [];
    for (const option of this.table.foreignKeyoptions) {
      if (option.column === column) {
        options = [instance, ...option.options];
        break;
      }
    }
    return options;
  }



  updateEditedValue(table: TableInfo, row: any, column: string, newValue: any) {
    const rowId = row[table.pk.name];
    if (!table.editedValue) {
      table.editedValue = {};
    }
    if (!table.editedValue[rowId]) {
      table.editedValue[rowId] = {};
    }
    table.editedValue[rowId][column] = newValue;
  }

  isRowMarkedForUpdate(table: TableInfo, row: any): boolean {
    const rowId = row[table.pk.name];
    let rowMarkedForUpdate = false;
    for (const request of table.updateRequests) {

      if (request=== rowId.toString()) {
        rowMarkedForUpdate = true;
        break;
      }
    }
    return rowMarkedForUpdate;
  }
  isRowMarkedForDeletion(table: TableInfo, row: any): boolean {


    const rowId = row[table.pk.name];
    let rowMarkedForDeletion = false;

    for (const request of table.deleteRequests) {
      if (request === rowId.toString()) {

        rowMarkedForDeletion = true;
        break;
      }
    }
    return rowMarkedForDeletion;
  }


  createInstanceDataUpdate(row: any, table: TableInfo) {
    const instanceData: { [column: string]: any } = {};
    const rowId = row[table.pk.name];
    instanceData[table.pk.name] = rowId;
    for (const column of table.selectedColumns) {
      if (column === table.pk.name) {
        continue;
      }
        if (table.editedValue && table.editedValue[rowId] && table.editedValue[rowId][column] !== undefined) {
          instanceData[column] = table.editedValue[rowId][column];
        }
        else if (row[column]===null || row[column] === undefined){
          instanceData[column]=""
        }else {
            instanceData[column] = row[column]
          }

      }

    return instanceData;
  }

editValue (table: TableInfo, row: any) {

    const instanceData = this.createInstanceDataUpdate(row, table);
  const invalidColumns = this.getInvalidColumns(instanceData, table);
  if (invalidColumns.length > 0) {
    this.messageService.add({
      severity: 'warn',
      summary: 'Validation Error',
      detail: `Please fill the required fields: ${invalidColumns.join(', ')}`
    });

  }else {
    this.tableService.updateInstance(instanceData, table.name).subscribe({
      next: (response: any) => {
        this.getDataTable(table);
        this.messageService.add({
          severity: 'success',
          summary: 'Parameter Updated',
          detail: `Parameter of ${table.name} will be updated at 8 AM`
        });
      },
      error:

        (error: any) => {
          console.error('Error adding instance:', error);
        }
    });
  }
}


 async toggleEditMode(row: any) {
    row.editMode = !row.editMode;
  }
  dblclickeditmode(row: any) {
    if (!row.editMode) {
      row.editMode = true;
    }
  }
  getColumnType(column: string,table:TableInfo): string  {
    if (!this.checkForeignKey(column,table)) {
      for (let col of this.table.columns) {
        if (col.name === column) {
          return col.type;
        }
      }
    }
    return "fk";
  }

  addNewRow(table: TableInfo) {
    const newRow: { [anycolumn: string]: any } = {};
    for (let column of table.selectedColumns) {
      newRow[column] = '';
    }
    table.newRows.push(newRow);
    table.showNewRow = true;
  }
  changeLimit(newLimit: number) {
    this.table.limit = newLimit;
    this.getDataTable(this.table);
  }
  toggleplusMode(table: TableInfo, newRow: any) {
    const index = table.newRows.indexOf(newRow);
    if (index !== -1) {
      table.newRows.splice(index, 1);
    }
  }
  checkReferences(primaryKeyValue: string , table:TableInfo){
  this.tableService.checkreferences(table.name,primaryKeyValue).subscribe({
    next: (value : any[]) =>{

      if (value.length===0){
        this.dialogService.open(DeleteParamComponent, {
          header: 'Delete Parameter',
          width: '500px',
          contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"}
        });
        this.tableService.dataDeleteInstance = {
          table: table,
          primaryKeyValue: primaryKeyValue
        };
      }
      else {
        this.dialogService.open(DeleteCascadeComponent, {
          header: 'Delete Parameter Cascade',
          width: '500px',
          contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"}
        });
        this.tableService.dataDeleteInstance = {
          table: table,
          primaryKeyValue: primaryKeyValue,
          references:value
        };
      }
    }
  })
  }

  createInstanceData(newRow: { [column: string]: string }, table: TableInfo) {
    const instanceData: { [column: string]: string } = {};
    for (let column of table.selectedColumns) {
      if (newRow[column]===null || newRow[column]==="undefined" ){

        instanceData[column] =""
      }
      if (this.getColumnType(column,table)===("number"||"real")&&(newRow[column]===""||instanceData[column]==="")){
        instanceData[column]="0"
      }
      instanceData[column] = newRow[column];
    }
    return instanceData;
  }

  addNewInstance(table: TableInfo, newRow: any) {
    const instanceData = this.createInstanceData(newRow, table);
    const invalidColumns = this.getInvalidColumns(instanceData, table);
 const wrongsize =this.checksizes(instanceData,table);
    if (table.pk.isAutoIncrement !== "YES" && instanceData[table.pk.name] !== "") {
      this.tableService.checkunicity(instanceData[table.pk.name], table.name)
        .subscribe({
          next: (response: boolean) => {
            if (!response) {
              this.messageService.add({
                severity: 'warn',
                summary: 'Validation Error',
                detail: `Primary Key value: ${newRow[table.pk.name]} must be unique`
              });
            } else {
              this.executeAddInstance(instanceData, table, newRow, invalidColumns,wrongsize);
            }
          },
          error: (error: any) => {
            console.error('Error checking unicity:', error);
          }
        });
    } else {
      this.executeAddInstance(instanceData, table, newRow, invalidColumns,wrongsize);
    }
  }

  executeAddInstance(instanceData: any, table: TableInfo, newRow: any, invalidColumns: string[],wrongsize:string[]) {
    if (invalidColumns.length > 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Validation Error',
        detail: `Please fill the required fields: ${invalidColumns.join(', ')}`
      });
    } else if (wrongsize.length>0){
      this.messageService.add({
        severity: 'warn',
        summary: 'Validation Error',
        detail: `Wrong size of : ${wrongsize.join(', ')  }`

      });
    }else {
      this.tableService.addInstance(instanceData, table.name)
        .subscribe({
          next: (response: any) => {
            this.getDataTable(table);
            this.messageService.add({
              severity: 'success',
              summary: 'Parameter Added',
              detail: `Parameter added to ${table.name}`
            });
            const index = table.newRows.indexOf(newRow);
            if (index !== -1) {
              table.newRows.splice(index, 1);
            }
          },
          error: (error: any) => {
            console.error('Error adding instance:', error);
          }
        });
    }
  }

  changePage(table: TableInfo, pageNumber: number) {
    if (pageNumber >= 1 && pageNumber <= table.totalPageCount) {
      table.currentPage = pageNumber;

      table.offset = (table.currentPage - 1) * table.limit;

      this.getDataTable(table);
    }
  }
  iscolumndisabled(columnName:string,table:any):boolean{
    let auto: boolean = false;
    for (let column of table.columns) {
      if (column.name === columnName && column.isAutoIncrement ==="YES") {
        auto = true;
        break;
      }
    }

    return auto;
  }
  openparamhistory(tableName: string) {
    this.dialogService.open(ParamHistoryComponent, {
      header: `History ${tableName}`,
      width: '90%',
      contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"},
      data: {
        tableName: tableName
      }
    });
  }

}
