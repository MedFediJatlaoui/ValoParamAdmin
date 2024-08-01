import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ScheduledUpdateComponent } from './scheduled-update.component';
import { TableService } from "../../services/table/table.service";
import { MessageService } from "primeng/api";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { of, throwError } from 'rxjs';
import { TableInfo } from "../../model/table-info";

describe('ScheduledUpdateComponent', () => {
  let component: ScheduledUpdateComponent;
  let fixture: ComponentFixture<ScheduledUpdateComponent>;
  let tableServiceSpy: jasmine.SpyObj<TableService>;
  let messageServiceSpy: jasmine.SpyObj<MessageService>;
  let refSpy: jasmine.SpyObj<DynamicDialogRef>;

  const mockTableInfo: TableInfo = {
    name: 'TestTable',
    type: null,
    pk: { name: '', type: '', isNullable: '', size: '', isAutoIncrement: '' },
    totalRows: 0,
    columns: [
      { name: 'column1', type: '', isNullable: '', size: '', isAutoIncrement: '' },
      { name: 'column2', type: '', isNullable: '', size: '', isAutoIncrement: '' }
    ],
    selectedColumns: [],
    sortByColumn: '',
    sortOrder: '',
    limit: 0,
    data: [],
    showNewRow: false,
    editedValue: {},
    isExpanded: false,
    currentPage: 1,
    newRow: {},
    newRows: [],
    totalPageCount: 0,
    offset: 0,
    search: '',
    deleteRequests: [],
    updateRequests: [],
    foreignKeys: [],
    foreignKeyoptions: []
  };

  const mockUpdateRequests = [
    {
      instanceData: {
        actor_id: "1",
        first_name: "PENELOPE",
        last_name: "BATISTUTA",
        last_update: "2024-05-30T10:45:17.552+00:00"
      },
      tableName: "actor",
      username: "jatlaouimedfedi@gmail.com"
    }
  ];

  beforeEach(async () => {
    tableServiceSpy = jasmine.createSpyObj('TableService', ['getupdaterequests', 'cancelUpdateInstance']);
    messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    refSpy = jasmine.createSpyObj('DynamicDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ScheduledUpdateComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: { data: { table: mockTableInfo } } },
        { provide: TableService, useValue: tableServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ScheduledUpdateComponent);
    component = fixture.componentInstance;
    component.table = mockTableInfo;

    tableServiceSpy.getupdaterequests.and.returnValue(of(mockUpdateRequests));

    // Call ngOnInit to trigger the data loading
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate columnNames correctly', () => {
    expect(component.columnNames).toEqual(['column1', 'column2', 'modifiedBy']);
  });

  it('should call loadscheduledupdate and set scheduledUpdate', () => {
    fixture.detectChanges();
    expect(component.scheduledUpdate).toEqual(mockUpdateRequests);
    expect(tableServiceSpy.getupdaterequests).toHaveBeenCalledWith('TestTable');
  });

  it('should call cancelUpdateInstance and handle success response', () => {
    const primaryKeyValue = '1';
    const response = { success: 'Update cancelled successfully' };

    tableServiceSpy.cancelUpdateInstance.and.returnValue(of(response));

    component.cancelupdate(mockTableInfo, primaryKeyValue);

    expect(tableServiceSpy.cancelUpdateInstance).toHaveBeenCalledWith(mockTableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith({ severity: 'success', summary: 'Update Cancelled', detail: response.success });
    expect(tableServiceSpy.getupdaterequests).toHaveBeenCalled();
  });

  it('should call cancelUpdateInstance and handle error response', () => {
    const primaryKeyValue = '1';
    const errorResponse = { error: 'Cancellation failed' };

    tableServiceSpy.cancelUpdateInstance.and.returnValue(throwError(errorResponse));

    component.cancelupdate(mockTableInfo, primaryKeyValue);

    expect(tableServiceSpy.cancelUpdateInstance).toHaveBeenCalledWith(mockTableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith({ severity: 'error', summary: 'Update not Cancelled', detail: errorResponse.error });
  });
  it('should call cancelUpdateInstance and handle error response', () => {
    const primaryKeyValue = '1';
    const errorResponse = { error: 'Cancellation failed' };

    tableServiceSpy.cancelUpdateInstance.and.returnValue(throwError(errorResponse));

    component.cancelupdate(mockTableInfo, primaryKeyValue);

    expect(tableServiceSpy.cancelUpdateInstance).toHaveBeenCalledWith(mockTableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith({ severity: 'error', summary: 'Update not Cancelled', detail: errorResponse.error });
  });

  it('should handle error response in loadscheduledupdate', () => {
    const errorResponse = { error: 'Error loading scheduled for update' };
    tableServiceSpy.getupdaterequests.and.returnValue(throwError(errorResponse));

    component.loadscheduledupdate();

    expect(tableServiceSpy.getupdaterequests).toHaveBeenCalledWith('TestTable');
    expect(messageServiceSpy.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error loading Scheduled params',
      detail: `Scheduled for update params loaded for TestTable`
    });
  });


});
