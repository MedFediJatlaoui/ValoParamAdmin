import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteParamComponent } from './delete-param.component';
import { DynamicDialogRef } from "primeng/dynamicdialog";
import { MessageService } from "primeng/api";
import { ParamTableComponent } from "../param-table/param-table.component";
import { HttpClientModule } from "@angular/common/http";
import { TableInfo } from "../../model/table-info";
import {TableService} from "../../services/table/table.service";
import {of} from "rxjs";

describe('DeleteParamComponent', () => {
  let component: DeleteParamComponent;
  let fixture: ComponentFixture<DeleteParamComponent>;
  let refSpy: jasmine.SpyObj<DynamicDialogRef>;
  let tableServiceSpy: jasmine.SpyObj<TableService>;
  let messageServiceSpy: jasmine.SpyObj<MessageService>;
  let paramTableComponentSpy: jasmine.SpyObj<ParamTableComponent>;

  beforeEach(async () => {
    const refSpyObj = jasmine.createSpyObj('DynamicDialogRef', ['close']);
    const tableServiceSpyObj = jasmine.createSpyObj('TableService', ['deleteRecord']);
    const messageServiceSpyObj = jasmine.createSpyObj('MessageService', ['add']);
    const paramTableComponentSpyObj = jasmine.createSpyObj('ParamTableComponent', ['getDataTable']);
    tableServiceSpyObj.dataDeleteInstance = {
      table: new TableInfo(),
      primaryKeyValue: 'mockPrimaryKeyValue'
    };
    await TestBed.configureTestingModule({
      declarations: [DeleteParamComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: refSpyObj },
        { provide: TableService, useValue: tableServiceSpyObj },
        { provide: MessageService, useValue: messageServiceSpyObj },
        { provide: ParamTableComponent, useValue: paramTableComponentSpyObj }
      ],
      imports: [HttpClientModule]
    }).compileComponents();

    refSpy = TestBed.inject(DynamicDialogRef) as jasmine.SpyObj<DynamicDialogRef>;
    tableServiceSpy = TestBed.inject(TableService) as jasmine.SpyObj<TableService>;
    messageServiceSpy = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    paramTableComponentSpy = TestBed.inject(ParamTableComponent) as jasmine.SpyObj<ParamTableComponent>;

    fixture = TestBed.createComponent(DeleteParamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.table).toEqual(tableServiceSpy.dataDeleteInstance.table);
  });
  it('should call deleteRecord and close dialog upon successful deletion', () => {
    const tableInfo = new TableInfo();
    tableInfo.name = 'TestTable';
    const primaryKeyValue = '123';

    tableServiceSpy.deleteRecord.and.returnValue(of({ success: true }));
    component.table = tableInfo;
    component.primaryKeyValue = primaryKeyValue;

    component.deleteRecord();

    expect(tableServiceSpy.deleteRecord).toHaveBeenCalledWith(tableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'success',
      summary: 'Parameter scheduled to be deleted'
    }));
    expect(paramTableComponentSpy.getDataTable).toHaveBeenCalledWith(tableInfo);
    expect(refSpy.close).toHaveBeenCalled();
  });

  it('should log error when deletion fails', () => {
    const tableInfo = new TableInfo();
    tableInfo.name = 'TestTable';
    const primaryKeyValue = '123';
    const errorMessage = 'Deletion failed';

    tableServiceSpy.deleteRecord.and.returnValue(of({ success: false }));
    spyOn(console, 'log');
    component.table = tableInfo;
    component.primaryKeyValue = primaryKeyValue;
    component.deleteRecord();
    expect(tableServiceSpy.deleteRecord).toHaveBeenCalledWith(tableInfo.name, primaryKeyValue);
  });
  it('should call close method of ref', () => {
    component.closeDialog();

    expect(refSpy.close).toHaveBeenCalled();
  });
});
