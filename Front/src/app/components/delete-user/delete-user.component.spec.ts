// @ts-nocheck
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, Directive, Input, Pipe, PipeTransform } from '@angular/core';
import { of as observableOf } from 'rxjs';

import { DeleteUserComponent } from './delete-user.component';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { MessageService } from 'primeng/api';
import { UserControllerService } from '../../../open-api';
import { Router } from '@angular/router';

class MockUserControllerService {
  _delete() { return observableOf({}); }
}

class MockRouter {
  navigate() {};
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

describe('DeleteUserComponent', () => {
  let fixture: ComponentFixture<DeleteUserComponent>;
  let component: DeleteUserComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule],
      declarations: [
        DeleteUserComponent,
        MockTranslatePipe,
        MockPhoneNumberPipe,
        MockSafeHtmlPipe,
        MockMyCustomDirective
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
      providers: [
        DynamicDialogRef,
        DynamicDialogConfig,
        MessageService,
        { provide: UserControllerService, useClass: MockUserControllerService },
        { provide: Router, useClass: MockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteUserComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set config data on ngOnInit', () => {
    component.config = { data: 'data' };
    component.ngOnInit();
    expect(component.config.data).toEqual('data');
  });

  it('should close dialog', () => {
    spyOn(component.ref, 'close');
    component.closeDialog();
    expect(component.ref.close).toHaveBeenCalled();
  });

  it('should delete user', () => {
    component.config = { data: { id: {} } };
    spyOn(component.userService, '_delete').and.callThrough();
    spyOn(component.ref, 'close');
    spyOn(component.messageService, 'add');
    component.deleteUser();
    expect(component.userService._delete).toHaveBeenCalled();
    expect(component.ref.close).toHaveBeenCalled();
    expect(component.messageService.add).toHaveBeenCalled();
  });
});
