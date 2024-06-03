import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MessageService} from 'primeng/api';
import {of} from 'rxjs';
import {TableModule} from 'primeng/table';
import {ToastModule} from 'primeng/toast';
import {DropdownModule} from 'primeng/dropdown';
import {MultiSelectModule} from 'primeng/multiselect';
import {ListParamTablesComponent} from "./list-param-tables.component";
import {TableService} from "../../services/table/table.service";
import {ParamTableComponent} from "../param-table/param-table.component";
import {TableInfo} from "../../model/table-info";

describe('ListParamTablesComponent', () => {
  let component: ListParamTablesComponent;
  let fixture: ComponentFixture<ListParamTablesComponent>;
  let tableService: jasmine.SpyObj<TableService>;
  let messageService: jasmine.SpyObj<MessageService>;
  let paramTableComponent: jasmine.SpyObj<ParamTableComponent>;
  const table: TableInfo = new TableInfo();
  table.columns = [
    { name: 'Column1', type: 'string' ,isNullable:"NO",size:"200",isAutoIncrement:"NO"},
    { name: 'Column2', type: 'number',isNullable :"NO",size:"200",isAutoIncrement:"NO"},
    { name: 'Column3', type: 'boolean',isNullable :"NO",size:"200",isAutoIncrement:"NO"},
  ];
  beforeEach(async () => {
    tableService = jasmine.createSpyObj('TableService', ['retrieveAllTablesAndColumns']);
    messageService = jasmine.createSpyObj('MessageService', ['add']);
    paramTableComponent = jasmine.createSpyObj('ParamTableComponent', ['getDataTable']);

    await TestBed.configureTestingModule({
      imports: [
        TableModule,
        ToastModule,
        DropdownModule,
        MultiSelectModule,
      ],
      declarations: [ListParamTablesComponent],
      providers: [
        { provide: TableService, useValue: tableService },
        { provide: MessageService, useValue: messageService },
        { provide: ParamTableComponent, useValue: paramTableComponent }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListParamTablesComponent);
    component = fixture.componentInstance;
    tableService.retrieveAllTablesAndColumns.and.returnValue(of({
      numberTables: 10,
      allTablesWithColumns: [{
        name: 'Table1',
        type: null,
        pk: { name: "", type: "",isNullable:"",size:"200",isAutoIncrement:"NO" },
        totalRows: 0,
        columns: [{ name: "Column1", type: "string",isNullable:"NO",size:"200",isAutoIncrement:"NO" },{ name: "Column2", type: "int2",isNullable:"NO",size:"200",isAutoIncrement:"NO" },{ name: "Column3", type: "varchar",isNullable:"NO" ,size:"200",isAutoIncrement:"NO"}
          ,{ name: "Column4", type: "bool",isNullable:"NO",size:"200",isAutoIncrement:"NO" } ,{ name: "Column5", type: "bool",isNullable:"NO" ,size:"200",isAutoIncrement:"NO"},{ name: "Column5", type: "timestamptz",isNullable:"NO" ,size:"200",isAutoIncrement:"NO"}],
        selectedColumns: [],
        sortByColumn: "",
        sortOrder: "",
        limit: 5,
        data: [],
        showNewRow: false,
        editedValue: {},
        isExpanded: false,
        currentPage: 1,
        newRow: {},
        newRows: [],
        totalPageCount: 1,
        offset: 0,
        search: "",
        deleteRequests: [],
        updateRequests: [],
        foreignKeys:[],
        foreignKeyoptions:[]
      }]
    }));

    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load data on init', async () => {

    component.ngOnInit();
    await fixture.whenStable();
    expect(tableService.retrieveAllTablesAndColumns).toHaveBeenCalled();
    expect(component.tablesInfo.length).toBeGreaterThan(0);
    expect(component.dataLoaded).toBeTrue();
    expect(messageService.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'success',
      summary: 'Param Tables Loaded'
    }));
  });

  it('should handle page change', async () => {
    component.changePage(3);
    await fixture.whenStable();
    expect(component.currentPage).toEqual(3);
    expect(tableService.retrieveAllTablesAndColumns).toHaveBeenCalled();

  });

  it('should change limit', () => {
    component.changeLimit(10);
    expect(component.limit).toEqual(10);
    expect(tableService.retrieveAllTablesAndColumns).toHaveBeenCalled();

  });
  it('should have columns names', async () => {
let columnNames : String[] ;
    columnNames =  component.getColumnNames(table);
    await fixture.whenStable();
    expect(columnNames.length).toBe(3);
    expect(columnNames[0]).toBe('Column1');
    expect(columnNames[1]).toBe('Column2');
    expect(columnNames[2]).toBe('Column3');
  });
  it('should toggle Expansion', async () => {
    table.isExpanded= false;
    component.toggleRowExpansion(table)
await fixture.whenStable();
   expect(table.selectedColumns).toEqual(component.getColumnNames(table)) ;
    expect(table.isExpanded).toBe(true)

  });
  it('should on change of any attribute fetch datatable', async() => {

    component.dataLoaded=true

    component.onModelChange(table)
    await fixture.whenStable()
expect(paramTableComponent.getDataTable).toHaveBeenCalled();
  });

});
