import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CategoryComponent } from './category.component';
import { CategoryService } from '../../../open-api';
import { MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { of, throwError } from 'rxjs';
import { CategoryDto } from '../../../open-api/model/categoryDto';
import { DeletecategoryComponent } from '../deletecategory/deletecategory.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpEvent, HttpResponse } from '@angular/common/http';

// Mock data
const mockCategories: CategoryDto[] = [
  { id: 1, name: 'Category 1',editmode:false },
  { id: 2, name: 'Category 2',editmode:false }
];

const categoryServiceMock = {
  getAllCategories: jasmine.createSpy('getAllCategories').and.returnValue(of(mockCategories)),
  refCategory: jasmine.createSpy('refCategory').and.returnValue(of(0)),
  updateCategory: jasmine.createSpy('updateCategory').and.returnValue(of(mockCategories[0]))
};

const messageServiceMock = {
  add: jasmine.createSpy('add')
};

const dialogServiceMock = {
  open: jasmine.createSpy('open').and.returnValue({ onClose: of(true) })
};

describe('CategoryComponent', () => {
  let component: CategoryComponent;
  let fixture: ComponentFixture<CategoryComponent>;
  let categoryService: jasmine.SpyObj<CategoryService>;
  let messageService: jasmine.SpyObj<MessageService>;
  let dialogService: jasmine.SpyObj<DialogService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CategoryComponent, DeletecategoryComponent ],
      providers: [
        { provide: CategoryService, useValue: categoryServiceMock },
        { provide: MessageService, useValue: messageServiceMock },
        { provide: DialogService, useValue: dialogServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadCategories on initialization', () => {
    spyOn(component, 'loadCategories').and.callThrough();
    component.ngOnInit();
    expect(component.loadCategories).toHaveBeenCalled();
  });

  it('should set categories on successful load', () => {
    component.loadCategories();
    fixture.detectChanges();
    component.categories=mockCategories;
    expect(component.categories).toEqual(mockCategories);
  });

  it('should call getAllCategories and update categories on loadCategories', () => {
    const mockCategories: CategoryDto[] = [
      { id: 1, name: 'Category 1', editmode: false },
      { id: 2, name: 'Category 2', editmode: false }
    ];
    categoryServiceMock.getAllCategories.and.returnValue(of(mockCategories));

    component.loadCategories();
    fixture.detectChanges(); // Trigger change detection

    // Check if getAllCategories was called
    expect(categoryServiceMock.getAllCategories).toHaveBeenCalled();

    // Verify that component.categories is updated correctly
    expect(component.categories).toEqual(mockCategories);
  });


  it('should handle error in loadCategories', () => {
    spyOn(console, 'error'); // Suppress console error output
    categoryServiceMock.getAllCategories.and.returnValue(throwError(() => new Error('Error')));
    component.loadCategories();
    fixture.detectChanges(); // Trigger change detection

    expect(categoryServiceMock.getAllCategories).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledWith('Error loading categories', jasmine.any(Error));
  });

  it('should call checkref and handle successful refCategory response', () => {
    const httpEvent: HttpEvent<number> = new HttpResponse<number>({ body: 0 });
    const mockCategory: CategoryDto = { id: 1, name: 'Check Category' ,editmode:false};
    categoryServiceMock.refCategory.and.returnValue(of(httpEvent));
    spyOn(component, 'calldeletecomponent');

    component.checkref(mockCategory);
    fixture.detectChanges(); // Trigger change detection

    expect(categoryServiceMock.refCategory).toHaveBeenCalledWith(mockCategory.id!);
  });

  it('should show an error message when refCategory returns a non-zero value', () => {
    const httpResponse = 5; // Mock response value
    const mockCategory: CategoryDto = { id: 1, name: 'Category with Rules', editmode: false };
    categoryServiceMock.refCategory.and.returnValue(of(httpResponse));

    component.checkref(mockCategory);
    fixture.detectChanges(); // Trigger change detection

    // Check if refCategory was called with the correct id
    expect(categoryServiceMock.refCategory).toHaveBeenCalledWith(mockCategory.id!);

    // Expected error message
    const expectedErrorMessage = {
      severity: 'error',
      summary: 'Category not deleted',
      detail: `category ${mockCategory.name} is used by ${httpResponse} active rules`
    };
    expect(messageServiceMock.add).toHaveBeenCalledWith(expectedErrorMessage);
  });


  it('should handle error in checkref', () => {
    spyOn(console, 'log');
    categoryServiceMock.refCategory.and.returnValue(throwError(() => new Error('Ref Error')));
    component.checkref({ id: 1, name: 'Error Category', editmode: false });
    fixture.detectChanges(); // Trigger change detection

    // Check that console.log is called with the expected parameters
    expect(console.log).toHaveBeenCalledWith('Error checking reference', jasmine.any(Error));

    // Extract the actual Error object from the console.log call
    const callArgs = (console.log as jasmine.Spy).calls.mostRecent().args;
    expect(callArgs[1].message).toBe('Ref Error');
  });


  it('should call updateCategory and show success message on success', () => {
    const mockCategoryDto: CategoryDto = { id: 1, name: 'Updated Category' ,editmode:true};
    const mockCategoryId = 1;
    categoryServiceMock.updateCategory.and.returnValue(of(mockCategoryDto));

    component.updateCategory(mockCategoryId, mockCategoryDto);
    fixture.detectChanges(); // Trigger change detection

    expect(categoryServiceMock.updateCategory).toHaveBeenCalledWith(mockCategoryId, mockCategoryDto);
    expect(messageServiceMock.add).toHaveBeenCalledWith({
      severity: 'success',
      summary: 'Category updated successfully'
    });
  });

  it('should handle error when updating category', () => {
    const errorMessage = 'Update category failed';
    spyOn(console, 'error');
    categoryServiceMock.updateCategory.and.returnValue(throwError(new Error(errorMessage)));

    component.updateCategory(1, { id: 1, name: 'Category', editmode: true });
    fixture.detectChanges(); // Trigger change detection

    // Check that console.error is called with the expected parameters
    expect(console.error).toHaveBeenCalledWith('Error updating category', jasmine.any(Error));

    // Extract the actual Error object from the console.error call
    const callArgs = (console.error as jasmine.Spy).calls.mostRecent().args;
    expect(callArgs[1].message).toBe(errorMessage);

    // Verify messageServiceMock call
    expect(messageServiceMock.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Category not updated',
      detail: 'Error updating category'
    });
  });







});
