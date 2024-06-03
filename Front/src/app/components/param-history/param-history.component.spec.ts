import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ParamHistoryComponent } from './param-history.component';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { TableService } from '../../services/table/table.service';
import { MessageService } from 'primeng/api';
import { of, throwError } from 'rxjs';
import { ParamAudit } from '../../model/param-audit';

describe('ParamHistoryComponent', () => {
  let component: ParamHistoryComponent;
  let fixture: ComponentFixture<ParamHistoryComponent>;
  let tableServiceSpy: jasmine.SpyObj<TableService>;
  let messageServiceSpy: jasmine.SpyObj<MessageService>;
  let refSpy: jasmine.SpyObj<DynamicDialogRef>;

  beforeEach(async () => {
    tableServiceSpy = jasmine.createSpyObj('TableService', ['paramHistory']);
    messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    refSpy = jasmine.createSpyObj('DynamicDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ParamHistoryComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: { data: { tableName: 'TestTable' } } },
        { provide: TableService, useValue: tableServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ParamHistoryComponent);
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

    // Mock the paramHistory method to return an observable with test data
    tableServiceSpy.paramHistory.and.returnValue(of(testData));

    // Call ngOnInit to trigger the paramHistory method
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load param history on init', fakeAsync(() => {
    fixture.whenStable().then(() => {
      expect(component.tableName).toBe('TestTable');
      expect(tableServiceSpy.paramHistory).toHaveBeenCalledWith('TestTable');
      expect(component.paramAuditHistory).toEqual([
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
      ]);
      expect(messageServiceSpy.add).toHaveBeenCalledWith(jasmine.objectContaining({
        severity: 'success',
        summary: 'history',
        detail: 'History loaded for TestTable'
      }));
    });
  }));

  it('should handle error when loading param history', fakeAsync(() => {
    tableServiceSpy.paramHistory.and.returnValue(throwError('Error loading history'));

    component.paramHistory();
    tick();

    expect(messageServiceSpy.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'error',
      summary: 'Error loading history',
      detail: 'Failed to load history for TestTable'
    }));
  }));

  it('should close the dialog', () => {
    component.closeDialog();
    expect(refSpy.close).toHaveBeenCalled();
  });

  it('should return the correct class for actions', () => {
    expect(component.getClassForAction('DELETED')).toBe('p-badge p-badge-danger');
    expect(component.getClassForAction('EDITED')).toBe('p-badge p-badge-warning');
    expect(component.getClassForAction('ADDED')).toBe('p-badge p-badge-success');
    expect(component.getClassForAction('UNKNOWN')).toBe('');
  });

  it('should log the correct class for actions', () => {
    spyOn(console, 'log');
    component.logClass('DELETED');
    expect(console.log).toHaveBeenCalledWith('p-badge p-badge-danger');
    component.logClass('EDITED');
    expect(console.log).toHaveBeenCalledWith('p-badge p-badge-warning');
    component.logClass('ADDED');
    expect(console.log).toHaveBeenCalledWith('p-badge p-badge-success');

  });
});
