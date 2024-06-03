import { Component, Input, OnChanges, SimpleChanges, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import ApexCharts from 'apexcharts';

@Component({
  selector: 'app-stacked-columns',
  templateUrl: './stacked-columns.component.html',
  styleUrls: ['./stacked-columns.component.scss']
})
export class StackedColumnsComponent implements OnChanges, OnDestroy {
  @Input() heatMapdata: { x: string, y: string }[] = [{ x: "string", y: "20" }];
  @ViewChild('chart', { static: true }) chart: ElementRef | undefined;

  apexChart: ApexCharts | undefined;
  chartOptions: any;

  constructor() {
    this.chartOptions = {
      series: [{ data: this.heatMapdata }],
      chart: {
        type: 'treemap'
      },
      title: {
        text: 'Parameter tables updates this year',
        align: 'center'
      },
      colors: [
        '#3B93A5', '#F7B844', '#ADD8C7', '#EC3C65', '#CDD7B6',
        '#C1F666', '#D43F97', '#1E5D8C', '#421243', '#7F94B0',
        '#EF6537', '#C0ADDB'
      ],
      plotOptions: {
        treemap: {
          distributed: true,
          enableShades: false
        }
      }
    };
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['heatMapdata']) {
      this.updateChart();
    }
  }

  updateChart() {
    if (!this.heatMapdata || this.heatMapdata.length === 0) {
      return; // Exit early if heatMapdata is not available or empty
    }

    if (this.apexChart) {
      this.apexChart.updateOptions({ series: [{ data: this.heatMapdata }] }, true); // Second parameter true ensures deep merge
    } else if (this.chart && this.chart.nativeElement) {
      this.apexChart = new ApexCharts(this.chart.nativeElement, this.chartOptions);
      this.apexChart.render();
    }
  }

  ngOnDestroy() {
    if (this.apexChart) {
      this.apexChart.destroy();
    }
  }
}
