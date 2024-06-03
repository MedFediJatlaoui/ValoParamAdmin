import {Component, OnInit} from '@angular/core';
import {LayoutService} from "../../layout/service/app.layout.service";
import {Subscription} from "rxjs";
import {TableService} from "../../services/table/table.service";
import {RuleService} from "../../../open-api";

@Component({
  selector: 'app-dashbord',
  templateUrl: './dashbord.component.html',
  styleUrls: ['./dashbord.component.scss']
})
export class dashbordComponent implements OnInit {
tablenumberupdates=1;
heatMapdata=[{x:"",y:""}];
  subscription: Subscription;
  public totalRules!: number;
  public totalUsages!: number;
  constructor(public layoutService: LayoutService,public tableService:TableService , public ruleService:RuleService) {
    this.subscription = this.layoutService.configUpdate$.subscribe(config => {

    });
  }
  ngOnInit(): void {
    this.dataforTreeMap() ;
    this.getTotalRules() ;
    this.getTotalRulesUsages() ;
  }
  dataforTreeMap(){
    this.tableService.paramTableTreemap().subscribe({
        next:(response:any)=>
        {
          this.tablenumberupdates=response.numberupdates;
          this.heatMapdata=response.data;
          console.log("response")
        }
      }
    )
  }
  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  getTotalRules(){
    this.ruleService.getTotalRulesCount().subscribe({
      next: data => {
        this.totalRules = data;
      },
      error: error => {
        console.error('Error loading rules count:', error);
      }
    });
  }
  getTotalRulesUsages(){
    this.ruleService.getTotalRuleUsages().subscribe({
      next: data => {
        this.totalUsages = data;
      },
      error: error => {
        console.error('Error loading rules usage count :', error);
      }
    });
  }

}
