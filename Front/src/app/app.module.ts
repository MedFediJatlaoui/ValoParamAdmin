import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AppLayoutModule} from './layout/app.layout.module';
import {MatInputModule} from '@angular/material/input';
import {MatStepperModule} from '@angular/material/stepper';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {ToastModule} from 'primeng/toast';
import {StepsModule} from 'primeng/steps';
import {MessageService} from 'primeng/api';
import {AllUsersComponent} from './components/all-users/all-users.component';
import {CheckboxModule} from 'primeng/checkbox';
import {PasswordModule} from 'primeng/password';
import {CommonModule} from "@angular/common";
import {TableModule} from "primeng/table";
import {FileUploadModule} from "primeng/fileupload";
import {ButtonModule} from "primeng/button";
import {ToolbarModule} from "primeng/toolbar";
import {RippleModule} from "primeng/ripple";
import {RatingModule} from "primeng/rating";
import {InputTextModule} from "primeng/inputtext";
import {InputTextareaModule} from "primeng/inputtextarea";
import {DropdownModule} from "primeng/dropdown";
import {NgApexchartsModule} from "ng-apexcharts";

import {RadioButtonModule} from "primeng/radiobutton";
import {InputNumberModule} from "primeng/inputnumber";
import {DialogModule} from "primeng/dialog";
import {OverlayModule} from "primeng/overlay";
import {AllRulesComponent} from './components/all-rules/all-rules.component';
import {NewRuleComponent} from './components/new-rule/new-rule.component';
import {MultiSelectModule} from "primeng/multiselect";
import {SliderModule} from "primeng/slider";
import {ToggleButtonModule} from "primeng/togglebutton";
import {LoginComponent} from './pages/login/login.component';
import {ParamTableComponent} from './components/param-table/param-table.component';
import {ChartModule} from "primeng/chart";
import {AddUserComponent} from './components/add-user/add-user.component';
import {UpdateUserComponent} from './components/update-user/update-user.component';
import {LockUserComponent} from './components/lock-user/lock-user.component';
import {DeleteUserComponent} from './components/delete-user/delete-user.component';
import {AvatarModule} from "primeng/avatar";
import {InterceptorService} from "./services/interceptor/interceptor.service";
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from "@angular/common/http";
import {DialogService} from "primeng/dynamicdialog";
import {MatDialogModule} from "@angular/material/dialog";
import {PaginatorModule} from "primeng/paginator";
import {DisableRuleComponent} from './components/disable-rule/disable-rule.component';
import {UpdateRuleComponent} from './components/update-rule/update-rule.component';
import {RuleHistoryComponent} from './components/rule-history/rule-history.component';
import {HeatmapComponent} from './components/heatmap/heatmap.component';
import {DonutComponent} from './components/donut/donut.component';
import {StackedColumnsComponent} from './components/stacked-columns/stacked-columns.component';
import {ParamHistoryComponent} from "./components/param-history/param-history.component";
import {ListParamTablesComponent} from "./components/list-param-tables/list-param-tables.component";
import {dashbordComponent} from './components/dashbord/dashbord.component';
import {NotfoundComponent} from './components/notfound/notfound.component';
import {DeleteParamComponent} from "./components/delete-param/delete-param.component";
import {DeleteCascadeComponent} from './components/delete-cascade/delete-cascade.component';
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {CalendarModule} from "primeng/calendar";
import {UseRuleComponent} from "./components/use-rule/use-rule.component";
import * as Sweetalert2 from "sweetalert2";

@NgModule({
    declarations: [AppComponent, AllUsersComponent, AllRulesComponent, NewRuleComponent, LoginComponent, ParamTableComponent, AddUserComponent, UpdateUserComponent, LockUserComponent, DeleteUserComponent, ListParamTablesComponent, ParamHistoryComponent, DisableRuleComponent, UpdateRuleComponent, RuleHistoryComponent,UseRuleComponent, HeatmapComponent, DonutComponent, StackedColumnsComponent, dashbordComponent, NotfoundComponent, DeleteParamComponent, DeleteCascadeComponent],
    imports: [
        BrowserModule,
        AppRoutingModule,
        AppLayoutModule,
        MatInputModule,
        MatStepperModule,
        ReactiveFormsModule,
        MatButtonModule,
        ToastModule,
        StepsModule,
        CheckboxModule,
        PasswordModule,
        FormsModule,

        CommonModule,
        FileUploadModule,
        ButtonModule,
        ToolbarModule,
        RippleModule,
        RatingModule,
        InputTextModule,
        InputTextareaModule,
        DropdownModule,
        RadioButtonModule,
        InputNumberModule,
        DialogModule,
        OverlayModule,
        MultiSelectModule,
        SliderModule,
        ToggleButtonModule,
        ChartModule,
        ToastModule,
        DialogModule,
        AvatarModule,
        MatDialogModule,
        HttpClientModule,
        PaginatorModule,
        NgApexchartsModule,
        TableModule,
        ProgressSpinnerModule,
        CalendarModule,
    ],

    providers: [MessageService, DialogService, ParamTableComponent, HttpClient, PaginatorModule, TableModule, dashbordComponent, {
        provide: HTTP_INTERCEPTORS,
        useClass: InterceptorService,
        multi: true,
    },],
    bootstrap: [AppComponent],
})

export class AppModule {
}
