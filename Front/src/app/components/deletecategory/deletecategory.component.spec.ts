import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MessageService } from 'primeng/api';
import { CategoryService } from '../../../open-api'; // Adjust import path as needed
import { DeletecategoryComponent } from '../deletecategory/deletecategory.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

describe('DeletecategoryComponent', () => {
  let component: DeletecategoryComponent;
  let fixture: ComponentFixture<DeletecategoryComponent>;
  let categoryService: jasmine.SpyObj<CategoryService>;
  let messageService: jasmine.SpyObj<MessageService>;
  let ref: jasmine.SpyObj<DynamicDialogRef>;
  let config: DynamicDialogConfig;

  beforeEach(async () => {
    // Create spy objects with method names
    const categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['deleteCategory']);
    const messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    const refSpy = jasmine.createSpyObj('DynamicDialogRef', ['close']);

    // Set up configuration for the dialog
    config = { data: { id: 1, name: 'Test Category' } } as DynamicDialogConfig;

    await TestBed.configureTestingModule({
      declarations: [DeletecategoryComponent],
      imports: [HttpClientTestingModule], // Import HttpClientTestingModule
      providers: [
        { provide: CategoryService, useValue: categoryServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy },
        { provide: DynamicDialogRef, useValue: refSpy },
        { provide: DynamicDialogConfig, useValue: config }
      ]
    }).compileComponents();

    // Inject the spies into the test
    categoryService = TestBed.inject(CategoryService) as jasmine.SpyObj<CategoryService>;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    ref = TestBed.inject(DynamicDialogRef) as jasmine.SpyObj<DynamicDialogRef>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeletecategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close the dialog', () => {
    component.closeDialog();
    expect(ref.close).toHaveBeenCalled();
  });

  it('should delete the category successfully', () => {
    const httpResponse = new HttpResponse({ status: 200 }); // Mock HttpResponse
    categoryService.deleteCategory.and.returnValue(of(httpResponse)); // Return HttpResponse

    component.disable();

    expect(categoryService.deleteCategory).toHaveBeenCalledWith(1);
    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'success',
      summary: 'Category deleted',
      detail: 'Category Test Category Deleted'
    });
    expect(ref.close).toHaveBeenCalledWith(true);
  });

  it('should handle error during category deletion', () => {
    const errorResponse = new Error('Error deleting category');
    categoryService.deleteCategory.and.returnValue(throwError(() => errorResponse));

    spyOn(console, 'log');
    component.disable();

    expect(categoryService.deleteCategory).toHaveBeenCalledWith(1);
    expect(messageService.add).not.toHaveBeenCalled();
    expect(ref.close).not.toHaveBeenCalled();
    expect(console.log).toHaveBeenCalledWith('Error occurred!', errorResponse);
  });
});
