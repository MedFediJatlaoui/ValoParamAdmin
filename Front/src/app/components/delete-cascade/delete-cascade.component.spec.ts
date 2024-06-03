import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteCascadeComponent } from './delete-cascade.component';
import {DynamicDialogRef} from "primeng/dynamicdialog";
import {TableService} from "../../services/table/table.service";
import {NO_ERRORS_SCHEMA} from "@angular/core";
import {ToastModule} from "primeng/toast";
import {MessageService} from "primeng/api";
import {ParamTableComponent} from "../param-table/param-table.component";
import {TableInfo} from "../../model/table-info";
import {of, throwError} from "rxjs";
describe('DeleteCascadeComponent', () => {
  let component: DeleteCascadeComponent;
  let fixture: ComponentFixture<DeleteCascadeComponent>;
  let tableServiceSpy: jasmine.SpyObj<TableService>;
  let messageServiceSpy: jasmine.SpyObj<MessageService>;
  let refSpy: jasmine.SpyObj<DynamicDialogRef>;
  let paramTableComponentSpy: jasmine.SpyObj<ParamTableComponent>;
  beforeEach(async () => {
    const refSpyObj = jasmine.createSpyObj('DynamicDialogRef', ['close']);
    const tableServiceSpyObj = jasmine.createSpyObj('TableService', ['deleteCascade']);
    const messageServiceSpyObj = jasmine.createSpyObj('MessageService', ['add']);
    const paramTableComponentSpyObj = jasmine.createSpyObj('ParamTableComponent', ['getDataTable']);
    tableServiceSpyObj.dataDeleteInstance = {
      table: new TableInfo(),
      primaryKeyValue: 'mockPrimaryKeyValue',
      references: []
    };
    await TestBed.configureTestingModule({
      imports:[ToastModule],
      declarations: [ DeleteCascadeComponent ],
      providers:[  { provide: DynamicDialogRef, useValue: refSpyObj },
        { provide: TableService, useValue: tableServiceSpyObj },
        { provide: MessageService, useValue: messageServiceSpyObj },
        { provide: ParamTableComponent, useValue: paramTableComponentSpyObj }
      ],
      schemas :[NO_ERRORS_SCHEMA]
    })
      .compileComponents();
    refSpy = TestBed.inject(DynamicDialogRef) as jasmine.SpyObj<DynamicDialogRef>;
    paramTableComponentSpy = TestBed.inject(ParamTableComponent) as jasmine.SpyObj<ParamTableComponent>;
    tableServiceSpy = TestBed.inject(TableService) as jasmine.SpyObj<TableService>;
    messageServiceSpy = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    fixture = TestBed.createComponent(DeleteCascadeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.table).toEqual(tableServiceSpy.dataDeleteInstance.table);
    expect(component.primaryKeyValue).toEqual(tableServiceSpy.dataDeleteInstance.primaryKeyValue);
    expect(component.references).toEqual(tableServiceSpy.dataDeleteInstance.references);
  });
  it('should call delete cascade Record and close dialog upon successful deletion',async () => {
    const tableInfo = new TableInfo();
    tableInfo.name = 'TestTable';
    const primaryKeyValue = '123';
    const response={success:'dns'};
    tableServiceSpy.deleteCascade.and.returnValue(of({response}));
    component.table = tableInfo;
    component.primaryKeyValue = primaryKeyValue;
    await  component.deletecascadeRecord(tableInfo.name,primaryKeyValue);
    await fixture.whenStable()
    expect(tableServiceSpy.deleteCascade).toHaveBeenCalledWith(tableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'error',
      summary: 'Parameter not deleted '
    }));
  });
  it('should call delete cascade record and close dialog upon successful deletion', async () => {
    const tableInfo = new TableInfo();
    tableInfo.name = 'TestTable';
    const primaryKeyValue = '123';
    const response = { success: 'success' };
    tableServiceSpy.deleteCascade.and.returnValue(of(response));
    component.table = tableInfo;
    component.primaryKeyValue = primaryKeyValue;
    await component.deletecascadeRecord(tableInfo.name, primaryKeyValue);
    expect(tableServiceSpy.deleteCascade).toHaveBeenCalledWith(tableInfo.name, primaryKeyValue);
    expect(paramTableComponentSpy.getDataTable).toHaveBeenCalledWith(tableInfo);
    expect(refSpy.close).toHaveBeenCalled();
  });
  it('should handle error and close dialog on deletion failure', async () => {
    const tableInfo = new TableInfo();
    tableInfo.name = 'TestTable';
    const primaryKeyValue = '123';
    const errorResponse = { message: 'Error occurred' };
    tableServiceSpy.deleteCascade.and.returnValue(throwError(errorResponse));
    component.table = tableInfo;
    component.primaryKeyValue = primaryKeyValue;
    spyOn(console, 'error');
    await component.deletecascadeRecord(tableInfo.name, primaryKeyValue);
    expect(tableServiceSpy.deleteCascade).toHaveBeenCalledWith(tableInfo.name, primaryKeyValue);
    expect(console.error).toHaveBeenCalledWith(errorResponse);
    expect(refSpy.close).toHaveBeenCalled();
  });
});
