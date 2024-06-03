import { ComponentFixture, TestBed } from '@angular/core/testing';
import { dashbordComponent } from './dashbord.component';
import { NO_ERRORS_SCHEMA } from "@angular/core";
import { TableService } from "../../services/table/table.service";
import { of } from 'rxjs';
import {HttpClientModule} from "@angular/common/http";

describe('dashbordComponent', () => {
    let component: dashbordComponent;
    let fixture: ComponentFixture<dashbordComponent>;
    let tableService: jasmine.SpyObj<TableService>;

    beforeEach(async () => {
        tableService = jasmine.createSpyObj('TableService', ['paramTableTreemap']);

        await TestBed.configureTestingModule({
            declarations: [dashbordComponent],
            providers: [{ provide: TableService, useValue: tableService }],
            imports: [ HttpClientModule ] ,
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(dashbordComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should call dataforTreeMap on initialization', () => {
        spyOn(component, 'dataforTreeMap');
        fixture.detectChanges();
        expect(component.dataforTreeMap).toHaveBeenCalled();
    });

    it('should fetch data for tree map', () => {
        const response = {
            numberupdates: 2,
            data: [{x:'data1x', y:'data1y'},{x:'data2x',y:'data2y'}]
        };
        tableService.paramTableTreemap.and.returnValue(of(response));

        component.dataforTreeMap();

        expect(component.tablenumberupdates).toBe(response.numberupdates);
    });

    it('should unsubscribe on component destruction', () => {
        spyOn(component.subscription, 'unsubscribe');
        component.ngOnDestroy();
        expect(component.subscription.unsubscribe).toHaveBeenCalled();
    });
});

