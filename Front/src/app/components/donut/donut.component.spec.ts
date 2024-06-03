import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { DonutComponent } from './donut.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CategoryDto, CategoryService } from '../../../open-api';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

describe('DonutComponent', () => {
  let component: DonutComponent;
  let fixture: ComponentFixture<DonutComponent>;
  let categoryService: CategoryService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DonutComponent],
      imports: [HttpClientTestingModule],
        providers: [CategoryService],
    }).compileComponents();

    fixture = TestBed.createComponent(DonutComponent);
    component = fixture.componentInstance;
    categoryService = TestBed.inject(CategoryService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  xit('should load categories and update chart options on success', fakeAsync(() => {
    const dummyCategories: CategoryDto[] = [
      { name: 'Category 1', ruleCount: 10 },
      { name: 'Category 2', ruleCount: 20 }
    ];
    const httpResponse = new HttpResponse<CategoryDto[]>({ body: dummyCategories, status: 200 });

    spyOn(categoryService, 'getTopUsedCategories').and.returnValue(of(httpResponse));

    spyOn(component, 'updateChartOptions');

    component.loadCategories();
    tick();

    expect(categoryService.getTopUsedCategories).toHaveBeenCalled();
    expect(component.catLabels).toEqual(['Category 1', 'Category 2']);
    expect(component.catCounts).toEqual([10, 20]);
    expect(component.updateChartOptions).toHaveBeenCalled();
  }));

  it('should handle error when loading categories', fakeAsync(() => {
    const error = new HttpErrorResponse({ status: 404, statusText: 'Not Found' });
    spyOn(categoryService, 'getTopUsedCategories').and.returnValue(throwError(error));
    spyOn(console, 'error');

    component.loadCategories();
    tick();

    expect(categoryService.getTopUsedCategories).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledWith('Error loading categories:', error);
  }));

  it('should update chart options', () => {
    component.catLabels = ['Label 1', 'Label 2'];
    component.catCounts = [10, 20];

    component.updateChartOptions();

    expect(component.chartOptions.series).toEqual([10, 20]);
    expect(component.chartOptions.labels).toEqual(['Label 1', 'Label 2']);
  });
});
