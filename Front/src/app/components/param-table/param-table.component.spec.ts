import { ComponentFixture, TestBed } from '@angular/core/testing';
import {ParamTableComponent} from "./param-table.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TableModule} from "primeng/table";
import {ToastModule} from "primeng/toast";
import {DropdownModule} from "primeng/dropdown";
import {DialogModule} from "primeng/dialog";
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";
import {TableInfo} from "../../model/table-info";
import {DialogService} from "primeng/dynamicdialog";
import {HttpClientModule} from "@angular/common/http";
import {of} from "rxjs";
import {DeleteParamComponent} from "../delete-param/delete-param.component";
import {ParamHistoryComponent} from "../param-history/param-history.component";
import any = jasmine.any;
import {DeleteCascadeComponent} from "../delete-cascade/delete-cascade.component";
import {NO_ERRORS_SCHEMA} from "@angular/core";


describe('ParamTableComponent', () => {
  let component: ParamTableComponent;
  let fixture: ComponentFixture<ParamTableComponent>;
  let tableService: jasmine.SpyObj<TableService>;
  let messageService: jasmine.SpyObj<MessageService>;
  let dialogService: jasmine.SpyObj<DialogService>;
  beforeEach(async () => {
    tableService = jasmine.createSpyObj('TableService', [
      'getDataFromTable',
      'addInstance',
      'cancelUpdateInstance',
      'cancelDeletion',
      'updateInstance',
      'dataDeleteInstance',
      'checkreferences',
      'checkunicity'
    ]);

    messageService = jasmine.createSpyObj('MessageService', ['add']);
    dialogService = jasmine.createSpyObj('DialogService', ['open']);

    await TestBed.configureTestingModule({
      declarations: [ ParamTableComponent ],
      imports :[
        BrowserAnimationsModule,
        TableModule,
        ToastModule,
        DialogModule,
        DropdownModule,
        HttpClientModule

      ],
      providers :[
        { provide: TableService, useValue: tableService },
        { provide: MessageService, useValue: messageService },
        { provide: DialogService, useValue: dialogService },
      ],
      schemas:[NO_ERRORS_SCHEMA]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ParamTableComponent);
    component = fixture.componentInstance;
    component.table = new TableInfo();
   component.table.name="_user"
   component.table.limit=5
   component.table.offset=0
   component.table.columns=[{name:"firstname",type:"string",isNullable:"NO",size:"200",isAutoIncrement:"NO"},{name:"lastname",type:"string",isNullable:"NO",size:"200",isAutoIncrement:"NO"},{name:"id",type:"string",isNullable:"NO",size:"200",isAutoIncrement:"NO"}]
    component.table.pk.name ="id"
    component.table.selectedColumns=['id', 'firstname', 'lastname']
component.table.totalPageCount=20
    fixture.detectChanges();
  });


  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should getData from table', async () => {
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };
    component.table.selectedColumns=[];
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    component.getDataTable(component.table)
    await fixture.whenStable()
    expect(tableService.getDataFromTable).toHaveBeenCalled()
    component.table.name = "ramos"
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    component.getDataTable(component.table)
    await fixture.whenStable()
    expect(tableService.getDataFromTable).toHaveBeenCalled()
  });

  it('should cancel update instance request',async () => {
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };
    const response={
      success : "Update of 3 cancelled successfully" };
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));

    tableService.cancelUpdateInstance.and.returnValue((of(response)))
    component.cancelUpdateInstance(component.table,"3");
    await fixture.whenStable()
    expect(tableService.cancelUpdateInstance).toHaveBeenCalled()
  });
  it('should cancel deletion request - success', async () => {
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };

    const successResponse = {
      success: "Deletion request cancelled successfully",
      error: null
    };

    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    tableService.cancelDeletion.and.returnValue(of(successResponse));

    component.canceldeletion(component.table, "3");
    await fixture.whenStable();
    expect(tableService.cancelDeletion).toHaveBeenCalled();
  });

  it('should cancel deletion request - error', async () => {
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };

    const errorResponse = {
      success: null,
      error: "Failed to cancel deletion request"
    };
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    tableService.cancelDeletion.and.returnValue(of(errorResponse));
    component.canceldeletion(component.table, "3");
    await fixture.whenStable();
    expect(tableService.cancelDeletion).toHaveBeenCalled();
  });

  it('should update selected edited Value',  () => {

   const row ={id: "5",
   firstname: "Rivaldo",
   lastname:"DobrasilVoyVoy"}
    component.updateEditedValue(component.table,row,"lastname","Cafu")
    fixture.detectChanges()
    expect(component.table.editedValue["5"]["lastname"]).toEqual("Cafu");
  });
  it('should check if Row is Marked For Update', () => {
    const row ={id: "5",
      firstname: "Rivaldo",
      lastname:"DobrasilVoyVoy"}
    component.table.updateRequests=["2","5"]
   const isMarkedForUpdate = component.isRowMarkedForUpdate(component.table, row);
    expect(isMarkedForUpdate).toBe(true);
  });
  it('should update selected edited Value',  () => {

    const row ={id: "5",
      firstname: "Rivaldo",
      lastname:"DobrasilVoyVoy"}
    component.updateEditedValue(component.table,row,"lastname","Cafu")
    fixture.detectChanges()
    expect(component.table.editedValue["5"]["lastname"]).toEqual("Cafu");
  });
  it('should check if Row is Marked For Deletion', () => {
    const row ={id: "5",
      firstname: "Wirtz",
        lastname:"XabiAlonso"}
    component.table.deleteRequests=["2","5"]
    const isMarkedForDeletion = component.isRowMarkedForDeletion(component.table, row);
    expect(isMarkedForDeletion).toBe(true);
  });

  it('should create Update instance data', () => {
      component.table.editedValue= {
        '5': {
          firstname: 'Rivaldo',
          lastname: 'DobrasilVoyVoy'
        }
      }
    const row = { id: '5', firstname: 'Wirtz',
      lastname: 'XabiAlonso' };
      const instanceData = component.createInstanceDataUpdate(row, component.table);

    expect(instanceData).toEqual({
      id: '5',
      firstname: 'Rivaldo',
      lastname: 'DobrasilVoyVoy'
    });
  });
  it('should Edit Instance',async () => {
    component.table.editedValue= {
      '5': {
        firstname: 'Rivaldo',
        lastname: 'DobrasilVoyVoy'
      }
    }
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };
    const row = { id: '5'};
    let response=""
    tableService.updateInstance.and.returnValue(of(response));
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    component.editValue(component.table,row)
    await fixture.whenStable()
    expect(tableService.updateInstance).toHaveBeenCalled()
    expect(messageService.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'success',
      summary: 'Parameter Updated'
    }));
    const editedrow = { id: '5', firstname: 'Wirtz',
      lastname: 'XabiAlonso' };
     response=""
    tableService.updateInstance.and.returnValue(of(response));
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    component.editValue(component.table,editedrow)
    await fixture.whenStable()
    expect(tableService.updateInstance).toHaveBeenCalled()
    expect(messageService.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'success',
      summary: 'Parameter Updated'
    }));
  });
  it('should toggle Edit Mode', () => {
    const row ={id: "5",
      firstname: "Rivaldo",
      lastname:"DobrasilVoyVoy",
      editMode:false}
    component.toggleEditMode(row)
    fixture.detectChanges()
    expect(row.editMode).toEqual(true)
  });
  it('should toggle Edit Mode when double click', () => {
    const row ={id: "5",
      firstname: "Rivaldo",
      lastname:"DobrasilVoyVoy",
      editMode:false}
    component.dblclickeditmode(row)
    fixture.detectChanges()
    expect(row.editMode).toEqual(true)
  });
  it('should get column type', () => {
    component.table.columns=[{name: "id",type:"number",isNullable:"NO",size:"200",isAutoIncrement:"NO"},{name:"firstname",type:"string",isNullable:"NO",size:"200",isAutoIncrement:"NO"}]
   const type =component.getColumnType("id",component.table)
    fixture.detectChanges()
    expect(type).toEqual("number")
  });
  it('should add new input row', () => {
    const newrows=[{id:'',firstname:'',lastname :''}]
    component.table.selectedColumns=["id","firstname","lastname"]
    component.addNewRow(component.table);
    fixture.detectChanges()
    expect(component.table.newRows).toEqual(newrows)
    expect(component.table.newRows.length).toEqual(1)
    expect(component.table.showNewRow).toEqual(true)
  });
  it('should change table limit', () => {
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    component.changeLimit(20)
    fixture.detectChanges()
    expect(tableService.getDataFromTable).toHaveBeenCalled()
    expect(component.table.limit).toEqual(20)
  });
  it('should open delete instance dialog',async () => {
    tableService.checkreferences.and.returnValue(of([]));
    component.checkReferences("2",component.table)
await fixture.whenStable()
    expect(dialogService.open).toHaveBeenCalledWith(DeleteParamComponent,jasmine.objectContaining({
      header: 'Delete Parameter',
      width: '500px'}));
    expect(tableService.dataDeleteInstance).toEqual({
      table: component.table,
      primaryKeyValue: "2"
    })
  });
  it('should create instance data', () => {

    const row = {
      id: '5', firstname: 'Wirtz',
      lastname: 'XabiAlonso'
    };
    const instanceData = component.createInstanceData(row, component.table);

    expect(instanceData).toEqual({
      id: '5',
      firstname: 'Wirtz',
      lastname: 'XabiAlonso'
    });
  });
    it('should add instance', async () => {
      const dataFromTable = {
        data: [] as { [key: string]: string }[],
        deleteRequests: [],
        updateRequests: []
      };
      const row = { id: '5', firstname: 'Wirtz',
        lastname: 'XabiAlonso' };
      const response=""
      tableService.checkunicity.and.returnValue(of(true));

      tableService.getDataFromTable.and.returnValue(of(dataFromTable));
      tableService.addInstance.and.returnValue(of(response));
      component.addNewInstance(component.table,row)
      await fixture.whenStable()
      expect(tableService.getDataFromTable).toHaveBeenCalled()
      expect(messageService.add).toHaveBeenCalledWith(jasmine.objectContaining({
        severity: 'success',
        summary: 'Parameter Added'
      }));
      expect(component.table.newRows).not.toContain(row)
    });
  it('should handle page change', async () => {
    const dataFromTable = {
      data: [] as { [key: string]: string }[],
      deleteRequests: [],
      updateRequests: []
    };
    tableService.getDataFromTable.and.returnValue(of(dataFromTable));
    component.changePage(component.table,3);
    await fixture.whenStable();
    expect(tableService.getDataFromTable).toHaveBeenCalled();
    expect(component.table.currentPage).toEqual(3);


  });
  it('should open param history', () => {
    component.openparamhistory(component.table.name)
    fixture.detectChanges()
    expect(dialogService.open).toHaveBeenCalledWith(ParamHistoryComponent,jasmine.objectContaining({
      width: '90%',

    }));
    });
    it('should toggle plus mode', () => {
      const newRow: { [anycolumn: string]: any } = {};
      const newRow1: { [anycolumn: string]: any } = {};
      newRow1['id']='1'
        newRow1['firstname']='Sergio'
        newRow1['lastname']='Ramos'
      component.table.newRows.push(newRow1)
      newRow['id']='2'
      newRow['firstname']='Freddy'
      newRow['lastname']='Mercury'
      component.table.newRows.push(newRow)
      console.log('Before toggle:', component.table.newRows);

      component.toggleplusMode(component.table, newRow);
      fixture.detectChanges();

      console.log('After toggle:', component.table.newRows);

      expect(component.table.newRows).toContain(newRow1);
      expect(component.table.newRows).not.toContain(newRow);

      expect(component.table.newRows.length).toEqual(1);

    });
  it('should Check instance References', async () => {
const reference: any[] =["1","123"]
    tableService.checkreferences.and.returnValue(of([]));

    component.checkReferences('1',component.table)
    await fixture.whenStable()
    expect(tableService.checkreferences).toHaveBeenCalled()
    expect(dialogService.open).toHaveBeenCalledWith(DeleteParamComponent,jasmine.objectContaining({
      header: 'Delete Parameter',
      width: '500px'
    }));
    tableService.checkreferences.and.returnValue(of(reference));

    component.checkReferences('1',component.table)
    await fixture.whenStable()
    expect(tableService.checkreferences).toHaveBeenCalled()
    expect(dialogService.open).toHaveBeenCalledWith(DeleteCascadeComponent,jasmine.objectContaining({
      header: 'Delete Parameter Cascade',
      width: '500px'
    }));
  });
it('should get Foreign key options',()=>{
  component.table.foreignKeyoptions=[{column: "lastname",options:["2","3","4","1","5"]}]
  const options= component.getForeignKeyOptionspercolumn("lastname","1")

  expect(options.length).toEqual(6)
});

it('should check Foreign Table Key',()=>{
  component.table.foreignKeys=["lastname"]
let  result= component.checkForeignKey("lastname",component.table)
  expect(result).toBe(true)
  result= component.checkForeignKey("firstname",component.table)
  expect(result).toBe(false)
})
});
