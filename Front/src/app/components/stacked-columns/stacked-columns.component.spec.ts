import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { StackedColumnsComponent } from './stacked-columns.component';
import { ElementRef } from '@angular/core';
import ApexCharts from 'apexcharts';

describe('StackedColumnsComponent', () => {
  let component: StackedColumnsComponent;
  let fixture: ComponentFixture<StackedColumnsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StackedColumnsComponent],
      providers: [
        { provide: ElementRef, useValue: { nativeElement: document.createElement('div') } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StackedColumnsComponent);
    component = fixture.componentInstance;

    // Set the heatMapdata after the component is created
    component.heatMapdata = [
      { x: 'data1x', y: 'data1y' },
      { x: 'data2x', y: 'data2y' }
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  xit('should update chart options when heatMapdata changes', fakeAsync(() => {
    const newHeatMapdata = [
      { x: 'data1x', y: 'data1y' },
      { x: 'data2x', y: 'data2y' }
    ];

    spyOn(component, 'updateChart').and.callThrough();
    spyOn(ApexCharts.prototype, 'updateOptions').and.callThrough();

    component.heatMapdata = newHeatMapdata;
    fixture.detectChanges();

    tick();

    expect(component.updateChart).toHaveBeenCalled();
    expect(ApexCharts.prototype.updateOptions).toHaveBeenCalledWith({
      series: [{ data: newHeatMapdata }],
      legend: { show: false },
      chart: { type: 'treemap' },
      title: {
        text: 'Parameter tables updates this year',
        align: 'center'
      },
      colors: jasmine.any(Array),
      plotOptions: {
        treemap: {
          distributed: true,
          enableShades: false
        }
      }
    });
  }));

  it('should not update chart options if heatMapdata is unchanged', fakeAsync(() => {
    const initialHeatMapdata = [
      { x: 'data1x', y: 'data1y' }
    ];

    component.heatMapdata = initialHeatMapdata;
    fixture.detectChanges();

    spyOn(component, 'updateChart').and.callThrough();

    component.heatMapdata = initialHeatMapdata;
    component.chartOptions
    fixture.detectChanges();

    tick();

    expect(component.updateChart).toHaveBeenCalledTimes(0); // it will be called initially
  }));
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update chart when heatMapdata changes', () => {
    const spy = spyOn(component, 'updateChart').and.callThrough();

    // Simulate changes to heatMapdata
    component.heatMapdata = [{ x: 'New String', y: '30' }];
    component.ngOnChanges({ heatMapdata: { currentValue: component.heatMapdata, previousValue: null, firstChange: true, isFirstChange: () => true } });

    expect(spy).toHaveBeenCalled();
  });

});
