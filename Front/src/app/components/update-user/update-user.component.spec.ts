import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UpdateUserComponent } from './update-user.component';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MessageService } from 'primeng/api';
import { UserService } from '../../services/user/user.service';
import {of, throwError} from 'rxjs';
import { UserDto } from "../../../open-api";

describe('UpdateUserComponent', () => {
  let component: UpdateUserComponent;
  let fixture: ComponentFixture<UpdateUserComponent>;
  let mockRef: jasmine.SpyObj<DynamicDialogRef>;
  let mockConfig: DynamicDialogConfig;
  let mockUserService: jasmine.SpyObj<UserService>;
  let mockMessageService: jasmine.SpyObj<MessageService>;
  let formBuilder: FormBuilder;

  beforeEach(waitForAsync(() => {
    mockRef = jasmine.createSpyObj('DynamicDialogRef', ['close']);
    mockConfig = new DynamicDialogConfig();
    mockUserService = jasmine.createSpyObj('UserService', ['updateUser']);
    mockMessageService = jasmine.createSpyObj('MessageService', ['add']);
    TestBed.configureTestingModule({
      declarations: [UpdateUserComponent],
      imports: [FormsModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        FormBuilder,
        { provide: DynamicDialogRef, useValue: mockRef },
        { provide: DynamicDialogConfig, useValue: mockConfig },
        { provide: UserService, useValue: mockUserService },
        { provide: MessageService, useValue: mockMessageService },
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateUserComponent);
    component = fixture.componentInstance;
    formBuilder = TestBed.inject(FormBuilder);
    // Mock user data
    component.config.data = {
      id: 1,
      firstname: 'John',
      lastname: 'Doe',
      email: 'john@example.com',
      phone: '123456789',
      company: 'Example Company',
      role: 'ADMIN'
    };
    component.updateUserForm = formBuilder.group({
      firstname: [''],
      lastname: [''],
      email: [''],
      phone: [''],
      company: [''],
      role: [''],
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update user', () => {
    // Arrange
    const updatedUser: UserDto = {
      id: 1,
      firstname: 'Updated Firstname',
      lastname: 'Updated Lastname',
      email: 'updated@example.com',
      phone: '987654321',
      company: 'Updated Company',
      role: 'ADMIN'
    };
    mockUserService.updateUser.and.returnValue(of(updatedUser));
    const formData = {
      firstname: updatedUser.firstname,
      lastname: updatedUser.lastname,
      email: updatedUser.email,
      phone: updatedUser.phone,
      company: updatedUser.company,
      role: updatedUser.role,
    };

    // Act
    component.updateUserForm.patchValue(formData);
    component.updateUser();

    // Assert
    expect(mockUserService.updateUser).toHaveBeenCalledWith(
      component.user.id,
      formData,
      component.file
    );
    expect(mockRef.close).toHaveBeenCalled();
    expect(mockMessageService.add).toHaveBeenCalledWith({
      severity: 'success',
      summary: 'Success',
      detail: 'User updated successfully'
    });
  });


  it('should handle error when updating user', () => {
    // Arrange
    const error = 'Update user failed';
    spyOn(console, 'error');
    mockUserService.updateUser.and.returnValue(throwError(error));
    component.updateUser();
    expect(console.error).toHaveBeenCalledWith('Error updating user:', error);
    expect(mockMessageService.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error',
      detail: 'Error updating user'
    });
  });
  it('should set the file property when a file is selected', () => {
    const mockFile = new File(['file content'], 'filename.txt');
    const event = {
      target: {
        files: [mockFile]
      }
    };
    component.onFileChange(event);
    expect(component.file).toEqual(mockFile);
  });

});
