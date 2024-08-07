import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AllparamhistoryComponent } from './allparamhistory.component';
import { TableService } from '../../services/table/table.service';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ParamAudit } from '../../model/param-audit';
import { of, throwError } from 'rxjs';

describe('AllparamhistoryComponent', () => {
  let component: AllparamhistoryComponent;
  let fixture: ComponentFixture<AllparamhistoryComponent>;
  let tableServiceSpy: jasmine.SpyObj<TableService>;
  let messageServiceSpy: jasmine.SpyObj<MessageService>;
  let refSpy: jasmine.SpyObj<DynamicDialogRef>;

  beforeEach(async () => {
    tableServiceSpy = jasmine.createSpyObj('TableService', ['allparamHistory']);
    messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    refSpy = jasmine.createSpyObj('DynamicDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [AllparamhistoryComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: { data: { tableName: 'TestTable' } } },
        { provide: TableService, useValue: tableServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AllparamhistoryComponent);
    component = fixture.componentInstance;

    // Mock data for testing
    const testData: ParamAudit[] = [
      {
        id: 1,
        tableName: 'TestTable',
        action: 'DELETED',
        version: 1,
        rowId: '123',
        oldRow: 'Old data',
        newRow: '',
        createdBy: 'User1',
        createdAt: '2022-05-10',
        lastModifiedBy: '',
        lastModifiedAt: ''
      },
      {
        id: 2,
        tableName: 'TestTable',
        action: 'EDITED',
        version: 1,
        rowId: '456',
        oldRow: 'Old data',
        newRow: 'New data',
        createdBy: 'User2',
        createdAt: '2022-05-11',
        lastModifiedBy: 'User2',
        lastModifiedAt: '2022-05-11'
      }
    ];

    tableServiceSpy.allparamHistory.and.returnValue(of(testData));

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with correct data', () => {
    expect(component.paramAuditHistory.length).toBe(2);
    expect(component.paramAuditHistory[0].action).toBe('DELETED');
    expect(component.paramAuditHistory[1].action).toBe('EDITED');
  });

  it('should call paramHistory on ngOnInit', () => {
    spyOn(component, 'paramHistory').and.callThrough();
    component.ngOnInit();
    expect(component.paramHistory).toHaveBeenCalled();
  });

  it('should handle successful data retrieval', waitForAsync(() => {
    component.paramHistory();
    fixture.whenStable().then(() => {
      expect(component.paramAuditHistory.length).toBe(2);
      expect(messageServiceSpy.add).toHaveBeenCalledWith({
        severity: 'success',
        summary: 'History',
        detail: 'History loaded'
      });
    });
  }));

  it('should handle error during data retrieval', waitForAsync(() => {
    tableServiceSpy.allparamHistory.and.returnValue(throwError(() => new Error('Failed to load history')));
    component.paramHistory();
    fixture.whenStable().then(() => {
      expect(messageServiceSpy.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Error loading history',
        detail: 'Failed to load history'
      });
    });
  }));

  it('should return correct class for action', () => {
    expect(component.getClassForAction('DELETED')).toBe('p-badge p-badge-danger');
    expect(component.getClassForAction('EDITED')).toBe('p-badge p-badge-warning');
    expect(component.getClassForAction('ADDED')).toBe('p-badge p-badge-success');
    expect(component.getClassForAction('UNKNOWN')).toBe('');
  });

  it('should call closeDialog and close the dialog', () => {
    component.closeDialog();
    expect(refSpy.close).toHaveBeenCalled();
  });
});
