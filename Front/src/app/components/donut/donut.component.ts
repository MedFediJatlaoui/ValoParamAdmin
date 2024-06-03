import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartComponent} from "ng-apexcharts";
import {CategoryService} from "../../../open-api";
export type ChartOptions = {
  series: any;
  chart: any;
  responsive: any[];
  labels: any;
  dataLabels: any;
};

@Component({
  selector: 'app-donut',
  templateUrl: './donut.component.html',
  styleUrls: ['./donut.component.scss']
})
export class DonutComponent implements OnInit {
  @ViewChild("chart") chart!: ChartComponent;
  public chartOptions: Partial<ChartOptions>;
  catLabels!: string[] ;
  catCounts!: number[];

  constructor(private categoryService: CategoryService) {
    this.loadCategories();
    this.chartOptions = {
      series: this.catCounts,
      chart: {
        type: "pie"
      },
      dataLabels: {
        enabled: false
      },
      labels: this.catLabels,
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            dataLabels: {
              enabled: false
            },
            legend: {
              show: false
            }
          }
        }
      ]
    };
  }

  ngOnInit(): void {
  }

  loadCategories() {
    this.categoryService.getTopUsedCategories().subscribe({
      next: data => {
        this.catLabels = data.map(category => category.name ?? '');
        this.catCounts = data.map(category => category.ruleCount ?? 0);

        this.updateChartOptions();
      },
      error: error => {
        console.error('Error loading categories:', error);
      }
    });
  }

  updateChartOptions() {
    this.chartOptions = {
      ...this.chartOptions,
      series : this.catCounts ,
      labels : this.catLabels
    };
  }
}
