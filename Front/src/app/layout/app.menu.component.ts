import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';
import {UserService} from "../services/user/user.service";

@Component({
  selector: 'app-menu',
  templateUrl: './app.menu.component.html',
})
export class AppMenuComponent implements OnInit {
  model: any[] = [];

  constructor(public layoutService: LayoutService,public userService :UserService) {}

  ngOnInit() {
   this.modelRole()
  }
  getuserRole(): string {
    const tokenData = this.userService.getTokenRoleAndAuthorities();
    if (typeof tokenData === 'string') {
      return "err";
    } else {
      return tokenData.role;
    }
  }

  modelRole(){
    const role =this.getuserRole()
    this.model = [
      {
        items: [ {label: 'dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/']},
          {label: 'Rules', icon: 'pi pi-fw pi-list', routerLink: ['/rules']} ,
          {label: 'Configuration table ', icon: 'pi pi-fw pi-table', routerLink: ['/configtable']}
        ]
      } ]
    if (role==="ADMIN"){
      this.model[0].items.splice(2, 0, { label: 'Users', icon: 'pi pi-fw pi-user', routerLink: ['/users'] });
     this.model[0].items.splice(3, 0,{label: 'Add user ', icon: 'pi pi-fw pi-user-plus', routerLink: ['/adduser']})
      this.model[0].items.push({label: 'Add rule', icon: 'pi pi-fw pi-plus', routerLink: ['/addrule']})
    }else if (role==="EXPERT"){
      this.model[0].items.push({label: 'Add rule', icon: 'pi pi-fw pi-plus', routerLink: ['/addrule']})}
  }
}
