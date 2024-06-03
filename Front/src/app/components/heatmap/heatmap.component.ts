import { Component, ViewChild, OnInit } from "@angular/core";
import { ChartComponent } from "ng-apexcharts";
import { RuleService, RuleUsageDTO } from "../../../open-api";
export type ChartOptions = {
  series: any;
  chart: any;
  dataLabels: any;
  fill: any;
  colors: any;
  title: any;
  xaxis: any;
  grid: any;
  plotOptions: any;
};
@Component({
  selector: 'app-heatmap',
  templateUrl: './heatmap.component.html',
  styleUrls: ['./heatmap.component.scss']
})
export class HeatmapComponent implements OnInit {
  @ViewChild("chart") chart!: ChartComponent;
  public chartOptions: Partial<ChartOptions>;
  constructor(private ruleService: RuleService) {
    this.chartOptions = {
      series: [],
      chart: {
        height: 200,
        type: "heatmap",
        toolbar: {
          show: false,
        }
      },
      dataLabels: {
        enabled: false
      },
      colors: ["#0275d8"],
      xaxis: {
        type: 'category',
        labels: {
          rotate: -45,
          rotateAlways: true,
        },
        tickPlacement: 'on'
      }
    };
  }
  ngOnInit() {
    this.fetchChartData();
  }
  fetchChartData() {
    this.ruleService.getTop5UsedRulesForLast18Days().subscribe(
      (data: RuleUsageDTO[]) => {
        const dataSeries = data.map(rule => ({
          name: rule.ruleName,
          data: (rule.dayUsages?.map(dayUsage => ({
            x: dayUsage.date,
            y: dayUsage.usageCount
          })) || []).reverse()
        }));
        this.updateChartOptions(dataSeries);
      },
      (error) => {
        console.error("Error fetching chart data:", error);
      }
    );
  }
  updateChartOptions(dataSeries: any[]) {
    this.chartOptions = {
      ...this.chartOptions,
      series: dataSeries
    };
  }
}

