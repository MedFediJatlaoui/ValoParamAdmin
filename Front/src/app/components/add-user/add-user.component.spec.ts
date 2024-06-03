/*
import { ComponentFixture, TestBed, tick } from '@angular/core/testing';
import { AddUserComponent } from './add-user.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { UserService } from '../../services/user/user.service';
import { UserDto } from '../../../open-api';

describe('AddUserComponent', () => {
  let component: AddUserComponent;
  let fixture: ComponentFixture<AddUserComponent>;
  let userService: jasmine.SpyObj<UserService>;
  let messageService: MessageService;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['addUser']);
    const messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);

    await TestBed.configureTestingModule({
      declarations: [ AddUserComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule ],
      providers: [
        FormBuilder,
        { provide: UserService, useValue: userServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddUserComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    messageService = TestBed.inject(MessageService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


    it('should add user successfully', () => {
      const userDto: UserDto = { /!* mock user data *!/ };
      const file: File = new File([], 'test-file');
      const addUserSpy = userService.addUser.and.returnValue(of(userDto));
      component.formSave.controls['firstname'].setValue('John');
      component.formSave.controls['lastname'].setValue('Doe');
      component.formSave.controls['email'].setValue('john.doe@example.com');
      component.formSave.controls['phone'].setValue('1234567890');
      component.formSave.controls['company'].setValue('Example Company');
      component.formSave.controls['role'].setValue(UserDto.RoleEnum.Admin);
      component.formSave.controls['password'].setValue('password');

      component.file = file;
      component.addUser();

      expect(addUserSpy).toHaveBeenCalledWith(component.formSave.value, file);

      fixture.detectChanges();
      tick();

      expect(messageService.add).toHaveBeenCalledWith({
        severity: 'success',
        summary: 'Success',
        detail: 'User successfully added'
      });
    });


  it('should handle error when adding user', () => {
    const error = 'Failed to add user';
    userService.addUser.and.throwError(error);

    component.addUser();

    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error',
      detail: error
    });
  });

  it('should handle invalid form', () => {
    component.formSave.setValue({}); // Set empty form data

    component.addUser();

    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error',
      detail: 'Please fill in all required fields'
    });
  });

  it('should set role to Admin by default', () => {
    expect(component.formSave.get('role')!.value).toBe(UserDto.RoleEnum.Admin);
  });

  it('should set role based on valueChanges', () => {
    component.formSave.get('role')!.setValue(UserDto.RoleEnum.Businessexpert);
    expect(component.formSave.get('role')!.value).toBe(UserDto.RoleEnum.Businessexpert);
  });

  it('should update file on file change', () => {
    const file: File = new File([], 'test-file');
    const event = { target: { files: [file] } };
    component.onFileChange(event);
    expect(component.file).toBe(file);
  });
});
*/
import { ComponentFixture, TestBed, tick } from '@angular/core/testing';
import { AddUserComponent } from './add-user.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { UserService } from '../../services/user/user.service';
import { UserDto } from '../../../open-api';
import {AllUsersComponent} from "../all-users/all-users.component";

describe('AddUserComponent', () => {
  let component: AddUserComponent;
  let fixture: ComponentFixture<AddUserComponent>;
  let userService: jasmine.SpyObj<UserService>;
  let messageService:  jasmine.SpyObj<MessageService>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['addUser']);
    const messageService = jasmine.createSpyObj('MessageService', ['add']);

    TestBed.configureTestingModule({
      declarations: [ AddUserComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule.withRoutes([{ path: 'users', component: AllUsersComponent }]) ],
      providers: [
        FormBuilder,
        { provide: UserService, useValue: userServiceSpy },
        { provide: MessageService, useValue: messageService }
      ]
    });

    fixture = TestBed.createComponent(AddUserComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add user successfully', async () => {
    const userDto: UserDto = { /* mock user data */ };
    const file: File = new File([], 'test-file');
    const addUserSpy = userService.addUser.and.returnValue(of(userDto));

    component.formSave.controls['firstname'].setValue('John');
    component.formSave.controls['lastname'].setValue('Doe');
    component.formSave.controls['email'].setValue('john.doe@example.com');
    component.formSave.controls['phone'].setValue('1234567890');
    component.formSave.controls['company'].setValue('Example Company');
    component.formSave.controls['role'].setValue(UserDto.RoleEnum.Admin);
    component.formSave.controls['password'].setValue('password');

    component.file = file;
    component.addUser();
    await fixture.whenStable()
    expect(addUserSpy).toHaveBeenCalledWith(component.formSave.value, file);

  });
/*  it('should handle error when adding user',async () => {
    const error = 'Failed to add user';
    userService.addUser.and.throwError(error);

    component.addUser();
await  fixture.whenStable()
    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error',
      detail: error
    });
  });

  it('should handle invalid form', async () => {
    component.formSave.controls['firstname'].setValue('John');

    component.addUser();
await fixture.whenStable()
    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error',
      detail: 'Please fill in all required fields'
    });
  });*/

  it('should set role to Admin by default', () => {
    expect(component.formSave.get('role')!.value).toBe(UserDto.RoleEnum.Admin);
  });

  it('should set role based on valueChanges', () => {
    component.formSave.get('role')!.setValue(UserDto.RoleEnum.Businessexpert);
    expect(component.formSave.get('role')!.value).toBe(UserDto.RoleEnum.Businessexpert);
  });

  it('should update file on file change', () => {
    const file: File = new File([], 'test-file');
    const event = { target: { files: [file] } };
    component.onFileChange(event);
    expect(component.file).toBe(file);
  });
});
