import {Component, OnInit} from '@angular/core';
import {PageRuleDto, RuleDto, RuleService} from "../../../open-api";
import {DialogService} from "primeng/dynamicdialog";
import {DisableRuleComponent} from "../disable-rule/disable-rule.component";
import {UpdateRuleComponent} from "../update-rule/update-rule.component";
import {RuleHistoryComponent} from "../rule-history/rule-history.component";
import {UseRuleComponent} from "../use-rule/use-rule.component";
import {UserService} from "../../services/user/user.service";

interface PageEvent {
  first: number;
  rows: number;
  page: number;
  pageCount: number;
}

@Component({
  selector: 'app-all-rules',
  templateUrl: './all-rules.component.html',
  styleUrls: ['./all-rules.component.scss']
})
export class AllRulesComponent implements OnInit {
  first: number = 0;
  rows: number = 10;
  searchQuery: string = '';
  rules: Array<RuleDto> | undefined = [];
  totalRecords: number | undefined = 0;
  public selectedRule!: RuleDto;

  constructor(public ruleService: RuleService,public userService:UserService  , public dialogService: DialogService) {}
getuserRole():string{
return this.userService.getTokenRole()
}
  onPageChange(event: PageEvent) {
    this.first = event.first/10;
    this.rows = event.rows;
    this.loadRules();
  }
  ngOnInit(): void {
    this.loadRules();
  }
  search() {
    this.first = 0;
    this.searchRules();
  }
  loadRules() {
    this.ruleService.findAllRules(this.first, this.rows).subscribe({
      next: (response: PageRuleDto) => {
        this.rules = response.content;
        this.totalRecords = response.totalElements;
      },
      error: error => {
        console.error('Error fetching rules:', error);
      }
    });
  }
  searchRules() {
    this.ruleService.searchRules(this.first, this.rows, this.searchQuery).subscribe({
      next: (response: PageRuleDto) => {
        this.rules = response.content;
        this.totalRecords = response.totalElements;
      },
      error: error => {
        console.error('Error fetching rules:', error);
      }
    });
  }
  disableRule(rule: RuleDto) {
    this.selectedRule = rule;
    const ref = this.dialogService.open(DisableRuleComponent, {
      header: 'Disable rule',
      width: '500px',
      contentStyle: {"background-color": "var(--color-white)","color": "var(--color-dark)"},
      data: this.selectedRule
    });
    ref.onClose.subscribe((result: any) => {
      if (result==true) {
        this.loadRules();
      }
    });
  }

  updateRule(rule: RuleDto) {
    this.selectedRule = rule;
    const headerText = rule.status == 'Enabled' ? 'Update Rule' : 'Consult rule';
    const ref = this.dialogService.open(UpdateRuleComponent, {
      header: headerText,
      width: '900px',
      height: '600px',
      contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"},
      data: this.selectedRule
    });
    ref.onClose.subscribe((result: any) => {
      if (result==true) {
        this.loadRules();
      }
    });
  }


  openRuleHistory(rule:RuleDto) {
    this.selectedRule = rule;
    const ref = this.dialogService.open(RuleHistoryComponent, {
      header: 'Rule history',
      width: '900px',
      height: '600px',
      contentStyle: {"background-color": "var(--color-white)","color": "var(--color-dark)"},
      data: this.selectedRule
    });

  }

  useRule(rule: RuleDto) {
    this.selectedRule = rule;
    const headerText = rule.status == 'Enabled' ? 'Use Rule' : 'Rule';
    const ref = this.dialogService.open(UseRuleComponent, {
      header: headerText,
      width: '900px',
      height: '600px',
      contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"},
      data: this.selectedRule
    });


  }
}
