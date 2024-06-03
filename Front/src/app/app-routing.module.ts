
import { NgModule } from '@angular/core';
import { RouterModule, Routes} from '@angular/router';
import { AppLayoutComponent } from './layout/app.layout.component';
import {AllUsersComponent} from "./components/all-users/all-users.component";
import {NewRuleComponent} from "./components/new-rule/new-rule.component";
import {LoginComponent} from "./pages/login/login.component";
import {AllRulesComponent} from "./components/all-rules/all-rules.component";
import {AddUserComponent} from "./components/add-user/add-user.component";
import {GuardService} from "./services/gurad/guard.service";
import {dashbordComponent} from "./components/dashbord/dashbord.component";
import {ListParamTablesComponent} from "./components/list-param-tables/list-param-tables.component";
import {NotfoundComponent} from "./components/notfound/notfound.component";

const routes: Routes = [
  {
    path: 'signIn',
    component: LoginComponent
  },
  {
    path: '',
    component: AppLayoutComponent, canActivate: [GuardService],
    children: [
      {path: '', component: dashbordComponent},
      {path: 'all', component: AllUsersComponent},
      {path: 'users', component: AllUsersComponent},
      {path: 'configtable', component: ListParamTablesComponent},
      {path: 'addrule', component: NewRuleComponent},
      {path: 'rules', component: AllRulesComponent},
      {path: 'adduser', component: AddUserComponent},

    ]
  },
  {
    path: '**',
   component:NotfoundComponent
  }


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
