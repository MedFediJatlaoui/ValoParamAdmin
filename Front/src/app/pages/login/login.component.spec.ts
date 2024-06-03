import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../services/user/user.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { UserControllerService } from "../../../open-api";
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let userService: jasmine.SpyObj<UserService>;
  let router: jasmine.SpyObj<Router>;
  let messageService: jasmine.SpyObj<MessageService>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['login', 'setToken']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [HttpClientTestingModule, ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MessageService, useValue: messageServiceSpy },
        UserControllerService
      ]
    }).compileComponents();

    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle login successfully', () => {
    const loginData = { email: 'test@example.com', password: 'password' };
    userService.login.and.returnValue(of({ /* mock response data for successful login */ }));

    component.formLogin.setValue(loginData);
    component.handleLogin();

    expect(userService.login).toHaveBeenCalledWith(loginData);
    expect(userService.setToken).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalled();
    expect(messageService.add).not.toHaveBeenCalled();
  });

  it('should handle login error', () => {
    const loginData = { email: 'test@example.com', password: 'password' };
    userService.login.and.returnValue(throwError('error'));

    component.formLogin.setValue(loginData);
    component.handleLogin();

    expect(userService.login).toHaveBeenCalledWith(loginData);
    expect(userService.setToken).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
