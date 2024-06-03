import { Component, OnInit } from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {MessageService} from "primeng/api";
import {RuleDto, RuleService, UserControllerService} from "../../../open-api";
import {UserService} from "../../services/user/user.service";

@Component({
  selector: 'app-disable-rule',
  templateUrl: './disable-rule.component.html',
  styleUrls: ['./disable-rule.component.scss']
})
export class DisableRuleComponent implements OnInit {
  public rule!: RuleDto;
  public username: any;
  public imageUrl: any;

  constructor(public ref: DynamicDialogRef , public config: DynamicDialogConfig ,
              public ruleService:RuleService , public messageService:MessageService ,
              public userService:UserService , public userControllerService:UserControllerService) { }

  ngOnInit(): void {
    this.retrieveUserFromLocalStorage() ;
    this.rule = this.config.data;
  }

  closeDialog() {
    this.ref.close() ;
  }

  disable() {
    if (this.rule.id != null) {
      this.ruleService.deleteRule(this.rule.id , {modifiedBy: this.username,imageUrl: this.imageUrl})
        .subscribe(
          (response) => {
            this.messageService.add({severity:'success', summary:'Scheduled delete', detail:'Rule deletion scheduled'});
            this.ref.close(true);
          },
          (error) => {
            console.log("Error occured !")
          }
        );
    }
  }

  retrieveUserFromLocalStorage() {
    const userDetailsJSON = localStorage.getItem('userDetails');
    if (userDetailsJSON) {
      const userDetails = JSON.parse(userDetailsJSON);
      this.username = userDetails.username;
      this.imageUrl = userDetails.profileImagePath;
    } else {
      console.error("User details not found in local storage.");
    }
  }
}
