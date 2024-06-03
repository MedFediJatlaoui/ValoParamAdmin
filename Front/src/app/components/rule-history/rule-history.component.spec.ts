import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RuleHistoryComponent } from './rule-history.component';
import { DynamicDialogConfig } from 'primeng/dynamicdialog';
import { RuleService, RuleModificationDto } from '../../../open-api';
import { of } from 'rxjs';
import {HttpResponse} from "@angular/common/http";

describe('RuleHistoryComponent', () => {
  let component: RuleHistoryComponent;
  let fixture: ComponentFixture<RuleHistoryComponent>;
  let ruleServiceSpy: jasmine.SpyObj<RuleService>;

  beforeEach(async () => {
    const ruleServiceSpyObj = jasmine.createSpyObj('RuleService', ['getModificationsByRuleId']);
    await TestBed.configureTestingModule({
      declarations: [RuleHistoryComponent],
      providers: [
        { provide: DynamicDialogConfig, useValue: { data: { id: 1 } } },
        { provide: RuleService, useValue: ruleServiceSpyObj }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RuleHistoryComponent);
    component = fixture.componentInstance;
    ruleServiceSpy = TestBed.inject(RuleService) as jasmine.SpyObj<RuleService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load rule modifications on initialization', () => {
    const mockModifications: RuleModificationDto[] = [{ /* Mock modification data */ }];
    const httpResponse = new HttpResponse({ body: mockModifications });
    ruleServiceSpy.getModificationsByRuleId.and.returnValue(of(httpResponse));

    component.ngOnInit();

    expect(ruleServiceSpy.getModificationsByRuleId).toHaveBeenCalledOnceWith(component.ruleId);
  });

  it('should handle error when loading rule modifications', () => {
    ruleServiceSpy.getModificationsByRuleId.and.returnValue(of(new HttpResponse({ body: [] }))); // Simulate error response

    component.ngOnInit();

    expect(ruleServiceSpy.getModificationsByRuleId).toHaveBeenCalledOnceWith(component.ruleId);
    expect(component.ruleModifications).toBeDefined();
  });
});
