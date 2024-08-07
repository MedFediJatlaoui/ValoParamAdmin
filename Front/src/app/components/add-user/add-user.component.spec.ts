import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AddUserComponent } from './add-user.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { UserService } from '../../services/user/user.service';
import { UserDto } from '../../../open-api';
import { AllUsersComponent } from '../all-users/all-users.component';
import { ToastModule } from 'primeng/toast';
import { InputSwitchModule } from 'primeng/inputswitch';

describe('AddUserComponent', () => {
  let component: AddUserComponent;
  let fixture: ComponentFixture<AddUserComponent>;
  let userService: jasmine.SpyObj<UserService>;
  let messageService: jasmine.SpyObj<MessageService>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['addUser']);
    const messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);

    await TestBed.configureTestingModule({
      declarations: [AddUserComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([{path: 'users', component: AllUsersComponent}]),
        InputSwitchModule,
        ToastModule
      ],
      providers: [
        FormBuilder,
        {provide: UserService, useValue: userServiceSpy},
        {provide: MessageService, useValue: messageServiceSpy}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddUserComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add user successfully', waitForAsync(() => {
    const userDto: UserDto = { /* mock user data */ };
    const file: File = new File([], 'test-file');
    userService.addUser.and.returnValue(of(userDto));

    component.formSave.controls['firstname'].setValue('John');
    component.formSave.controls['lastname'].setValue('Doe');
    component.formSave.controls['email'].setValue('john.doe@example.com');
    component.formSave.controls['phone'].setValue('1234567890');
    component.formSave.controls['company'].setValue('Example Company');
    component.formSave.controls['role'].setValue(UserDto.RoleEnum.Admin);
    component.formSave.controls['password'].setValue('password');
    component.formSave.controls['password2'].setValue('password');
    component.formSave.controls['canCancel'].setValue(false);

    component.file = file;
    component.addUser();

    fixture.whenStable().then(() => {
      expect(userService.addUser).toHaveBeenCalledWith({
        firstname: 'John',
        lastname: 'Doe',
        email: 'john.doe@example.com',
        phone: '1234567890',
        company: 'Example Company',
        role: 'ADMIN',
        password: 'password',
        password2: 'password',
        auth: ''
      }, file);
    });
  }));

  it('should handle error when adding user', waitForAsync(() => {
    userService.addUser.and.returnValue(throwError(() => new Error('Failed to add user')));

    component.formSave.controls['firstname'].setValue('John');
    component.formSave.controls['lastname'].setValue('Doe');
    component.formSave.controls['email'].setValue('john.doe@example.com');
    component.formSave.controls['phone'].setValue('1234567890');
    component.formSave.controls['company'].setValue('Example Company');
    component.formSave.controls['role'].setValue(UserDto.RoleEnum.Admin);
    component.formSave.controls['password'].setValue('password');
    component.formSave.controls['password2'].setValue('password');
    component.formSave.controls['canCancel'].setValue(false);

    component.addUser();


  }));

  it('should handle invalid form', waitForAsync(() => {
    component.formSave.controls['firstname'].setValue('John');
    component.formSave.controls['lastname'].setValue('');
    // Set incomplete form data to trigger validation error

    component.addUser();


  }));

  it('should set role to Admin by default', () => {
    expect(component.formSave.get('role')!.value).toBe(UserDto.RoleEnum.Admin);
  });

  it('should set role based on valueChanges', () => {
    component.formSave.get('role')!.setValue(UserDto.RoleEnum.EXPERT);
    expect(component.formSave.get('role')!.value).toBe(UserDto.RoleEnum.EXPERT);
  });

  it('should update file on file change', () => {
    const file: File = new File([], 'test-file');
    const event = {target: {files: [file]}};
    component.onFileChange(event);
    expect(component.file).toBe(file);
  });

  describe('onRoleChange', () => {
    it('should enable canCancel control if role is EXPERT', () => {
      component.formSave.get('canCancel')!.disable();
      component.onRoleChange({ target: { value: 'EXPERT' } });
      expect(component.formSave.get('canCancel')!.enabled).toBe(true);
    });

    it('should disable canCancel control if role is not EXPERT', () => {
      component.formSave.get('canCancel')!.enable();
      component.onRoleChange({ target: { value: 'USER' } });
      expect(component.formSave.get('canCancel')!.disabled).toBe(true);
      expect(component.formSave.get('canCancel')!.value).toBe(false);
    });
  });
});
