import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ScheduleddeletionComponent } from './scheduleddeletion.component';
import { TableService } from "../../services/table/table.service";
import { MessageService } from "primeng/api";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { TableInfo } from "../../model/table-info";
import { of, throwError } from "rxjs";

describe('ScheduleddeletionComponent', () => {
  let component: ScheduleddeletionComponent;
  let fixture: ComponentFixture<ScheduleddeletionComponent>;
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
      actor_id: "1",
      first_name: "PENELOPE",
      last_name: "BATISTUTA",
      last_update: "2024-05-30T10:45:17.552+00:00"
    }
  ];

  beforeEach(async () => {
    tableServiceSpy = jasmine.createSpyObj('TableService', ['getDeleteRequests', 'cancelDeletion']);
    messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    refSpy = jasmine.createSpyObj('DynamicDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ScheduleddeletionComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: { data: { table: mockTableInfo } } },
        { provide: TableService, useValue: tableServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ScheduleddeletionComponent);
    component = fixture.componentInstance;
    component.table = mockTableInfo;

    tableServiceSpy.getDeleteRequests.and.returnValue(of(mockUpdateRequests));

    // Call ngOnInit to trigger the data loading
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call cancelDeletion and handle success response', () => {
    const primaryKeyValue = '1';
    const response = { success: 'Deletion cancelled successfully' };

    tableServiceSpy.cancelDeletion.and.returnValue(of(response));

    spyOn(component, 'paramHistory'); // Spying on the paramHistory method

    component.canceldeletion(mockTableInfo, primaryKeyValue);

    expect(tableServiceSpy.cancelDeletion).toHaveBeenCalledWith(mockTableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith({ severity: 'success', summary: 'Deletion Cancelled', detail: response.success });
    expect(component.paramHistory).toHaveBeenCalled();
  });

  it('should call cancelDeletion and handle error response', () => {
    const primaryKeyValue = '1';
    const errorResponse = { error: 'Cancellation failed' };

    tableServiceSpy.cancelDeletion.and.returnValue(throwError(errorResponse));

    component.canceldeletion(mockTableInfo, primaryKeyValue);

    expect(tableServiceSpy.cancelDeletion).toHaveBeenCalledWith(mockTableInfo.name, primaryKeyValue);
    expect(messageServiceSpy.add).toHaveBeenCalledWith({ severity: 'error', summary: 'Deletion not Cancelled', detail: errorResponse.error });
  });

});
