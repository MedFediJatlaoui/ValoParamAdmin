import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DisableRuleComponent } from './disable-rule.component';
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { MessageService } from "primeng/api";
import { RuleService, UserControllerService } from "../../../open-api";
import { UserService } from "../../services/user/user.service";
import { of, throwError } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

describe('DisableRuleComponent', () => {
  let component: DisableRuleComponent;
  let fixture: ComponentFixture<DisableRuleComponent>;
  let ruleService: jasmine.SpyObj<RuleService>;
  let messageService: jasmine.SpyObj<MessageService>;
  let userService: jasmine.SpyObj<UserService>;
  let userControllerService: jasmine.SpyObj<UserControllerService>;
  let ref: jasmine.SpyObj<DynamicDialogRef>;
  let config: DynamicDialogConfig;

  beforeEach(async () => {
    const ruleServiceSpy = jasmine.createSpyObj('RuleService', ['deleteRule']);
    const messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    const userServiceSpy = jasmine.createSpyObj('UserService', ['']);
    const userControllerServiceSpy = jasmine.createSpyObj('UserControllerService', ['']);
    const refSpy = jasmine.createSpyObj('DynamicDialogRef', ['close']);

    config = { data: { id: 1 } } as DynamicDialogConfig;

    await TestBed.configureTestingModule({
      declarations: [ DisableRuleComponent ],
      providers: [
        { provide: RuleService, useValue: ruleServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: UserControllerService, useValue: userControllerServiceSpy },
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: config }
      ]
    })
        .compileComponents();

    ruleService = TestBed.inject(RuleService) as jasmine.SpyObj<RuleService>;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    userControllerService = TestBed.inject(UserControllerService) as jasmine.SpyObj<UserControllerService>;
    ref = TestBed.inject(DynamicDialogRef) as jasmine.SpyObj<DynamicDialogRef>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DisableRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should retrieve user details from local storage', () => {
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify({ username: 'testUser', profileImagePath: 'testImage.jpg' }));
    component.retrieveUserFromLocalStorage();
    expect(component.username).toBe('testUser');
    expect(component.imageUrl).toBe('testImage.jpg');
  });

  it('should handle missing user details in local storage', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);
    spyOn(console, 'error');
    component.retrieveUserFromLocalStorage();
    expect(console.error).toHaveBeenCalledWith('User details not found in local storage.');
  });

  it('should close the dialog', () => {
    component.closeDialog();
    expect(ref.close).toHaveBeenCalled();
  });

  it('should disable the rule successfully', () => {
    component.username = 'testUser';
    component.imageUrl = 'testImage.jpg';
    ruleService.deleteRule.and.returnValue(of(new HttpResponse({ status: 200 })));

    component.disable();

    expect(ruleService.deleteRule).toHaveBeenCalledWith(1, { modifiedBy: 'testUser', imageUrl: 'testImage.jpg' });
    expect(messageService.add).toHaveBeenCalledWith({ severity: 'success', summary: 'Scheduled delete', detail: 'Rule deletion scheduled' });
    expect(ref.close).toHaveBeenCalledWith(true);
  });

  it('should handle error during rule disable', () => {
    component.username = 'testUser';
    component.imageUrl = 'testImage.jpg';
    ruleService.deleteRule.and.returnValue(throwError('Error'));

    spyOn(console, 'log');
    component.disable();

    expect(ruleService.deleteRule).toHaveBeenCalledWith(1, { modifiedBy: 'testUser', imageUrl: 'testImage.jpg' });
    expect(console.log).toHaveBeenCalledWith('Error occured !');
  });
});
