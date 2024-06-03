// @ts-nocheck
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Pipe, PipeTransform, Injectable, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, Directive, Input, Output } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { Observable, of as observableOf, throwError } from 'rxjs';

import { Component } from '@angular/core';
import { LockUserComponent } from './lock-user.component';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { MessageService } from 'primeng/api';
import { UserControllerService } from '../../../open-api';

@Injectable()
class MockUserControllerService {}

@Directive({ selector: '[myCustom]' })
class MyCustomDirective {
  @Input() myCustom;
}

@Pipe({name: 'translate'})
class TranslatePipe implements PipeTransform {
  transform(value) { return value; }
}

@Pipe({name: 'phoneNumber'})
class PhoneNumberPipe implements PipeTransform {
  transform(value) { return value; }
}

@Pipe({name: 'safeHtml'})
class SafeHtmlPipe implements PipeTransform {
  transform(value) { return value; }
}

describe('LockUserComponent', () => {
  let fixture;
  let component;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule, ReactiveFormsModule ],
      declarations: [
        LockUserComponent,
        TranslatePipe, PhoneNumberPipe, SafeHtmlPipe,
        MyCustomDirective
      ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ],
      providers: [
        DynamicDialogRef,
        DynamicDialogConfig,
        MessageService,
        { provide: UserControllerService, useClass: MockUserControllerService }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(LockUserComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  it('should set config data on ngOnInit', async () => {
    component.config = { data: 'data' };
    component.ngOnInit();
    expect(component.config.data).toEqual('data');
  });

  it('should close dialog', async () => {
    spyOn(component.ref, 'close');
    component.closeDialog();
    expect(component.ref.close).toHaveBeenCalled();
  });

  xit('should lock user successfully', () => {
    const userId = 1; // Example user ID
    component.user = { id: userId }; // Assuming user object is provided to the component

    expect(userService._delete).toHaveBeenCalled(userId);
    expect(messageService.add).toHaveBeenCalledWith({ severity: 'success', summary: 'User locked !', detail: 'User locked successfully' });
  });

});
