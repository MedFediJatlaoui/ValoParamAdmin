// @ts-nocheck
import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CUSTOM_ELEMENTS_SCHEMA, Directive, Input, NO_ERRORS_SCHEMA, Pipe, PipeTransform} from '@angular/core';
import {of, throwError} from 'rxjs';
import {AllRulesComponent} from './all-rules.component';
import {RuleDto, RuleService} from '../../../open-api';
import {DialogService, DynamicDialogRef} from 'primeng/dynamicdialog';
import {UseRuleComponent} from '../use-rule/use-rule.component';
import {DisableRuleComponent} from '../disable-rule/disable-rule.component';
import {HttpClientModule} from '@angular/common/http';
import {UserService} from '../../services/user/user.service';

class MockRuleService {
  findAllRules() {
    return of({
      content: [],
      totalElements: 0
    });
  }

  searchRules() {
    return of({
      content: [],
      totalElements: 0
    });
  }
}

@Directive({ selector: '[myCustom]' })
class MockMyCustomDirective {
  @Input() myCustom;
}

@Pipe({ name: 'translate' })
class MockTranslatePipe implements PipeTransform {
  transform(value) { return value; }
}

@Pipe({ name: 'phoneNumber' })
class MockPhoneNumberPipe implements PipeTransform {
  transform(value) { return value; }
}

@Pipe({ name: 'safeHtml' })
class MockSafeHtmlPipe implements PipeTransform {
  transform(value) { return value; }
}

class MockUserService {
  getTokenRole() {
    return 'user';
  }
}

describe('AllRulesComponent', () => {
  let fixture: ComponentFixture<AllRulesComponent>;
  let component: AllRulesComponent;
  let mockDialogRef: jasmine.SpyObj<DynamicDialogRef>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, HttpClientModule],
      declarations: [
        AllRulesComponent,
        MockTranslatePipe,
        MockPhoneNumberPipe,
        MockSafeHtmlPipe,
        MockMyCustomDirective
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
      providers: [
        { provide: RuleService, useClass: MockRuleService },
        { provide: UserService, useClass: MockUserService },
        DialogService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AllRulesComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadRules on onPageChange', () => {
    spyOn(component, 'loadRules');
    component.onPageChange({ first: 0, rows: 10, page: 1, pageCount: 1 });
    expect(component.loadRules).toHaveBeenCalled();
  });

  it('should call loadRules on ngOnInit', () => {
    spyOn(component, 'loadRules');
    component.ngOnInit();
    expect(component.loadRules).toHaveBeenCalled();
  });

  it('should call searchRules on search', () => {
    spyOn(component, 'searchRules');
    component.search();
    expect(component.searchRules).toHaveBeenCalled();
  });

  it('should call findAllRules on loadRules', () => {
    spyOn(component.ruleService, 'findAllRules').and.callThrough();
    component.loadRules();
    expect(component.ruleService.findAllRules).toHaveBeenCalled();
  });

  it('should handle error on loadRules', () => {
    spyOn(component.ruleService, 'findAllRules').and.returnValue(throwError('Error fetching rules'));
    spyOn(console, 'error');
    component.loadRules();
    expect(console.error).toHaveBeenCalledWith('Error fetching rules:', 'Error fetching rules');
  });

  it('should call searchRules on searchRules', () => {
    spyOn(component.ruleService, 'searchRules').and.callThrough();
    component.searchRules();
    expect(component.ruleService.searchRules).toHaveBeenCalled();
  });

  it('should handle error on searchRules', () => {
    spyOn(component.ruleService, 'searchRules').and.returnValue(throwError('Error fetching rules'));
    spyOn(console, 'error');
    component.searchRules();
    expect(console.error).toHaveBeenCalledWith('Error fetching rules:', 'Error fetching rules');
  });

  it('should call dialogService.open on updateRule', () => {
    const mockRef = { onClose: of(true) };
    spyOn(component.dialogService, 'open').and.returnValue(mockRef);
    component.updateRule({ id: 1, status: 'Enabled' });
    expect(component.dialogService.open).toHaveBeenCalled();
  });

  it('should call dialogService.open on openRuleHistory', () => {
    const mockRef = { onClose: of(true) };
    spyOn(component.dialogService, 'open').and.returnValue(mockRef);
    component.openRuleHistory({ id: 1, status: 'Enabled' });
    expect(component.dialogService.open).toHaveBeenCalled();
  });

  it('should use rule', fakeAsync(() => {
    const mockRule: RuleDto = { id: 1, status: 'Enabled' };
    const mockRef = { onClose: of(true) };
    spyOn(component.dialogService, 'open').and.returnValue(mockRef);
    spyOn(component, 'loadRules');
    component.useRule(mockRule);
    tick();
    expect(component.selectedRule).toEqual(mockRule);
    expect(component.dialogService.open).toHaveBeenCalledWith(UseRuleComponent, jasmine.objectContaining({
      header: 'Use Rule',
      width: '900px',
      height: '600px',
      contentStyle: { "background-color": "var(--color-white)", "color": "var(--color-dark)" },
      data: mockRule
    }));
  }));

  it('should disable rule', fakeAsync(() => {
    const mockRule: RuleDto = { id: 1, status: 'Enabled' };
    const mockRef = { onClose: of(true) };
    spyOn(component.dialogService, 'open').and.returnValue(mockRef);
    spyOn(component, 'loadRules');
    component.disableRule(mockRule);
    tick();
    expect(component.selectedRule).toEqual(mockRule);
    expect(component.dialogService.open).toHaveBeenCalledWith(DisableRuleComponent, jasmine.objectContaining({
      header: 'Disable rule',
      width: '500px',
      contentStyle: { "background-color": "var(--color-white)", "color": "var(--color-dark)" },
      data: mockRule
    }));
  }));

  it('should return user role', () => {
    const userRole = component.getuserRole();
    expect(userRole).toBe('user');
  });


});
