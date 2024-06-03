import {Component, OnInit} from '@angular/core';
import {MessageService} from "primeng/api";
import {TableInfo} from "../../model/table-info";
import {TableService} from "../../services/table/table.service";
import {ParamTableComponent} from "../param-table/param-table.component";
import {TablesWithColumns} from "../../model/tables-with-columns";
import {ColumnInfo} from "../../model/column-info";

@Component({
  selector: 'app-list-param-tables',
  templateUrl: './list-param-tables.component.html',
  styleUrls: ['./list-param-tables.component.scss']
})
export class ListParamTablesComponent implements OnInit {
  tablesInfo: TableInfo[] = [];
  currentPage: number = 1;
  numberTables: number = 20;
  limit: number = 5;
  totalPageCount: number = Math.ceil(this.numberTables / this.limit);
  offset: number = 0;
  dataLoaded: boolean = false;
  constructor(private messageService: MessageService, private tableService: TableService, public paramTableComponent: ParamTableComponent) {
  }

  ngOnInit() {
    this.retrieveData();
    this.dataLoaded = true

  }

  retrieveData(): void {
    this.tablesInfo = [];
    this.tableService.retrieveAllTablesAndColumns(this.limit, this.offset).subscribe({
      next: (data: TablesWithColumns) => {
        this.totalPageCount = Math.ceil(data.numberTables / this.limit);
        this.offset = (this.currentPage - 1) * this.limit;
        this.tablesInfo = data.allTablesWithColumns;
        this.tablesInfo.forEach((table: TableInfo) => {
          table.currentPage = 1;
          table.offset = 0;
          table.isExpanded=false;
          table.limit = 5;
          table.newRow = {};
          table.newRows = [];
          this.handleColumnInfo(table);
        });
        this.messageService.add({
          severity: 'success',
          summary: 'Param Tables Loaded',
          detail: `${this.limit} Tables Loaded`
        });
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }
  isTableExpanded(table: TableInfo): boolean {
    return table.isExpanded;
  }

  handleColumnInfo(table: TableInfo) {
    for (let column of table.columns) {
      switch (column.type) {
        case "int8":
        case "int2":
        case "int4":
        case "bigint":
        case "int":
        case "integer":
        case "smallint":
        case "bigserial":
        case "serial":
          column.type = "number";
          break;

        case "real":
        case "decimal":
        case "double":
        case "precision":
          column.type = "float";
          break;
        case "real":
        case "decimal":
        case "double":
        case "precision":
          column.type = "float";
          break;
        case "varchar":
        case "text":
        case "pchar":
          column.type = "string";
          break;
        case "bool":
          column.type = "boolean";
          break;
        case "timestamptz":
        case "date":
          column.type = "date"
          break;
        default:
          column.type = "string";
          break;
      }}

  }

  changeLimit(newLimit: number) {
    this.limit = newLimit;
    this.retrieveData();
  }

  getColumnNames(table: TableInfo): string[] {
    return table.columns.map((column: ColumnInfo) => column.name);
  }

  onModelChange(table: TableInfo) {
    if (this.dataLoaded) {
      this.paramTableComponent.getDataTable(table);
      this.messageService.add({severity: 'success', summary: 'Data Loaded', detail: `Data loaded for ${table.name}`});
    }
  }

  toggleRowExpansion(table: TableInfo) {
    if (!table.selectedColumns || table.selectedColumns.length === 0) {
      table.selectedColumns = this.getColumnNames(table);
    }
    if (!table.isExpanded) {
      this.onModelChange(table);
    }
    table.isExpanded = !table.isExpanded;
  }

  changePage(pageNumber: number) {

    if (pageNumber >= 1 && pageNumber <= this.totalPageCount) {
      this.currentPage = pageNumber;
      this.offset = (this.currentPage - 1) * this.limit;
      this.retrieveData();

    }

  }

}
