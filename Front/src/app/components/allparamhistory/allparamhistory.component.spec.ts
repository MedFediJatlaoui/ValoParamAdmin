import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllparamhistoryComponent } from './allparamhistory.component';
import {TableService} from "../../services/table/table.service";
import {MessageService} from "primeng/api";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {ParamAudit} from "../../model/param-audit";
import {of} from "rxjs";

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
      declarations: [ AllparamhistoryComponent ],
      providers: [
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: { data: { tableName: 'TestTable' } } },
        { provide: TableService, useValue: tableServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy }
      ]
    })
    .compileComponents();
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

    // Call ngOnInit to trigger the paramHistory method
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
