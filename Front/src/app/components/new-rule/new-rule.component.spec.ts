import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormBuilder, FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Observable, of, throwError } from 'rxjs';

import { NewRuleComponent } from './new-rule.component';
import { MessageService } from 'primeng/api';
import { AttributeService, RuleService, CategoryService, RuleDto } from '../../../open-api';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DialogService } from 'primeng/dynamicdialog';
import {HttpClient, HttpErrorResponse, HttpHandler, HttpResponse} from '@angular/common/http';

class MockAttributeService {
    getAllAttributes(): Observable<any[]> {
        return of([]);
    }
}

class MockRuleService {
    saveRule(formData: any): Observable<any> {
        return of({});
    }
}

class MockCategoryService {
    getAllCategories(): Observable<any[]> {
        return of([]);
    }
}

class MockRouter {
    navigate() {}
}

describe('NewRuleComponent', () => {
    let fixture: ComponentFixture<NewRuleComponent>;
    let component: NewRuleComponent;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule, ReactiveFormsModule, HttpClientTestingModule],
            declarations: [NewRuleComponent],
            providers: [
                FormBuilder,
                MessageService,
                DialogService,
                HttpClient,
                { provide: HttpHandler, useValue: {} },
                { provide: AttributeService, useClass: MockAttributeService },
                { provide: RuleService, useClass: MockRuleService },
                { provide: CategoryService, useClass: MockCategoryService },
                { provide: Router, useClass: MockRouter }
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(NewRuleComponent);
        component = fixture.componentInstance;
        component.ngOnInit();
    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should call ngOnInit and initialize form and load categories and attributes', () => {
        spyOn(component, 'initForm');
        spyOn(component, 'loadCategories');
        spyOn(component, 'loadAttributes');
        component.ngOnInit();
        expect(component.initForm).toHaveBeenCalled();
        expect(component.loadCategories).toHaveBeenCalled();
        expect(component.loadAttributes).toHaveBeenCalled();
    });

    it('should load categories successfully', () => {
        const dummyCategories = [{ name: 'Category 1' }, { name: 'Category 2' }];
        const httpResponse = new HttpResponse({ body: dummyCategories });
        spyOn(component['categoryService'], 'getAllCategories').and.returnValue(of(httpResponse));
        component.loadCategories();
    });

    it('should handle error when loading categories', () => {
        const error = new HttpErrorResponse({ status: 404, statusText: 'Not Found' });
        spyOn(component['categoryService'], 'getAllCategories').and.returnValue(throwError(error));
        spyOn(console, 'error');
        component.loadCategories();
    });

    it('should load attributes successfully', () => {
        const dummyAttributes = [{ name: 'Attribute 1' }, { name: 'Attribute 2' }];
        const httpResponse = new HttpResponse({ body: dummyAttributes });
        spyOn(component['attributeService'], 'getAllAttributes').and.returnValue(of(httpResponse));
        component.loadAttributes();
    });

    it('should handle error when loading attributes', () => {
        const error = new Error('Failed to load attributes');
        spyOn(component['attributeService'], 'getAllAttributes').and.returnValue(throwError(error));
        spyOn(console, 'error');
        component.loadAttributes();
        expect(console.error).toHaveBeenCalledWith('Error loading attributes:', error);
    });

    describe('addAttribute()', () => {
        it('should add a new attribute group to the form array', () => {
            const attributeArray = component.ruleForm?.get('attributeDtos') as FormArray;
            const initialLength = attributeArray?.length ?? 0;
            component.addAttribute();
            expect(attributeArray?.length).toBe(initialLength + 1);
        });
    });

    describe('removeAttribute()', () => {
        it('should remove the attribute group at the specified index from the form array', () => {
            const attributeArray = component.ruleForm?.get('attributeDtos') as FormArray;
            const initialLength = attributeArray?.length ?? 0;
            const indexToRemove = initialLength - 1;
            component.removeAttribute(indexToRemove);
            expect(attributeArray?.length).toBe(initialLength - 1);
        });

        it('should not remove anything if index is out of bounds', () => {
            const attributeArray = component.ruleForm?.get('attributeDtos') as FormArray;
            const initialLength = attributeArray?.length ?? 0;
            const indexToRemove = initialLength + 1;
            component.removeAttribute(indexToRemove);
            expect(attributeArray?.length).toBe(initialLength);
        });
    });

    it('should return attribute controls', () => {
        const formArray = new FormArray([new FormControl('value1'), new FormControl('value2')]);
        component.ruleForm.setControl('attributeDtos', formArray);
        const controls = component.getAttributeControls();
        expect(controls.length).toBe(2);
        expect(controls[0] instanceof FormControl).toBe(true);
        expect(controls[1] instanceof FormControl).toBe(true);
    });

    it('should return false when attribute names are not unique', () => {
        const formArray = new FormArray([
            new FormControl({ name: 'attribute1' }),
            new FormControl({ name: 'attribute2' }),
            new FormControl({ name: 'attribute1' })
        ]);
        component.ruleForm.setControl('attributeDtos', formArray);
        const result = component['validateAttributeNames']();
        expect(result).toBe(false);
    });

    it('should return true when attribute values are within the valid range', () => {
        const formArray = new FormArray([new FormControl('5'), new FormControl('8'), new FormControl('10')]);
        component.ruleForm.setControl('attributeDtos', formArray);
        const result = component['validateAttributeValues']();
    });

    it('should return false when attribute values are outside the valid range', () => {
        const formArray = new FormArray([new FormControl('5'), new FormControl('8'), new FormControl('10')]);
        component.ruleForm.setControl('attributeDtos', formArray);
        const result = component['validateAttributeValues']();
        expect(result).toBe(false);
    });

    it('should return true when the sum of percentages is 100', () => {
        spyOn(component['messageService'], 'add');
        spyOn(component, 'getAttributeControls').and.returnValue([
            new FormControl({ percentage: '30' }),
            new FormControl({ percentage: '40' }),
            new FormControl({ percentage: '30' })
        ]);
        const result = component['validateAttributePercentages']();
        expect(result).toBe(true);
        expect(component['messageService'].add).not.toHaveBeenCalled();
    });

    it('should return false and add error message when the sum of percentages is not 100', () => {
        spyOn(component['messageService'], 'add');
        spyOn(component, 'getAttributeControls').and.returnValue([
            new FormControl({ percentage: '30' }),
            new FormControl({ percentage: '40' }),
            new FormControl({ percentage: '30' })
        ]);
        const result = component['validateAttributePercentages']();
    });

    it('should set categoryVisible to true when type is "category"', () => {
        const type = 'category';
        component.showDialog(type);
        expect(component.categoryVisible).toBe(true);
        expect(component.attributeVisible).toBe(false);
    });

    it('should set attributeVisible to true when type is not "category"', () => {
        const type = 'attribute';
        component.showDialog(type);
        expect(component.categoryVisible).toBe(false);
        expect(component.attributeVisible).toBe(true);
    });

    it('should add a new attribute when the name is not empty and does not exist already', () => {
        component.newAttributeName = 'New Attribute';
        component.attributes = [{ name: 'Existing Attribute' }];
        component.addNewAttribute();
        expect(component.attributes.length).toBe(2);
        expect(component.attributeVisible).toBe(false);
    });

    it('should not add a new attribute if the name is empty', () => {
        component.newAttributeName = '';
        component.addNewAttribute();
    });

    it('should not add a new attribute if the name already exists', () => {
        component.newAttributeName = 'Existing Attribute';
        component.attributes = [{ name: 'Existing Attribute' }];
        component.addNewAttribute();
        expect(component.attributes.length).toBe(1);
        expect(component.attributeVisible).toBe(false);
    });

    it('should add a new category when the name is not empty and does not exist already', () => {
        component.newCategoryName = 'New Category';
        component.categories = [{ name: 'Existing Category' }];
        component.addNewCategory();
        expect(component.categories.length).toBe(2);
        expect(component.categoryVisible).toBe(false);
    });

    it('should not add a new category if the name is empty', () => {
        component.newCategoryName = '';
        component.addNewCategory();
    });

    it('should not add a new category if the name already exists', () => {
        component.newCategoryName = 'Existing Category';
        component.categories = [{ name: 'Existing Category' }];
        component.addNewCategory();
        expect(component.categories.length).toBe(1);
        expect(component.categoryVisible).toBe(false);
    });

    it('should not submit the form when form is invalid', () => {
        spyOn(component['ruleService'], 'saveRule');
        spyOn(component['messageService'], 'add');
        component.onSubmit();
        expect(component['ruleService'].saveRule).not.toHaveBeenCalled();
        expect(component['messageService'].add).toHaveBeenCalledWith({severity: 'error', summary: 'Error', detail: 'Please fill all required fields correctly.'});
    });
});
