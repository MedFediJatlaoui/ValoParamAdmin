import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule, FormArray } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { UpdateRuleComponent } from './update-rule.component';
import { MessageService } from 'primeng/api';
import {
  AttributeService,
  RuleService,
  CategoryService,
  UserControllerService, RuleDto, AttributeDataDto, CategoryDto, AttributeDto
} from '../../../open-api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { Router } from '@angular/router';
import { UserService } from '../../services/user/user.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpResponse} from "@angular/common/http";

describe('UpdateRuleComponent', () => {
  let component: UpdateRuleComponent;
  let fixture: ComponentFixture<UpdateRuleComponent>;
  let messageService: jasmine.SpyObj<MessageService>;
  let attributeService: jasmine.SpyObj<AttributeService>;
  let ruleService: jasmine.SpyObj<RuleService>;
  let categoryService: jasmine.SpyObj<CategoryService>;
  let userService: jasmine.SpyObj<UserService>;
  let userControllerService: jasmine.SpyObj<UserControllerService>;
  let router: jasmine.SpyObj<Router>;
  let config: DynamicDialogConfig;
  let ref: DynamicDialogRef;

  beforeEach(async () => {
    const messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    const attributeServiceSpy = jasmine.createSpyObj('AttributeService', ['getAllAttributes']);
    const ruleServiceSpy = jasmine.createSpyObj('RuleService', ['updateRule']);
    const categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getAllCategories']);
    const userServiceSpy = jasmine.createSpyObj('UserService', ['']);
    const userControllerServiceSpy = jasmine.createSpyObj('UserControllerService', ['']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [UpdateRuleComponent],
      imports: [ReactiveFormsModule, FormsModule],
      providers: [
        { provide: MessageService, useValue: messageServiceSpy },
        { provide: AttributeService, useValue: attributeServiceSpy },
        { provide: RuleService, useValue: ruleServiceSpy },
        { provide: CategoryService, useValue: categoryServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: UserControllerService, useValue: userControllerServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: DynamicDialogConfig, useValue: {} },
        { provide: DynamicDialogRef, useValue: {} }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    })
        .compileComponents();

    fixture = TestBed.createComponent(UpdateRuleComponent);
    component = fixture.componentInstance;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    attributeService = TestBed.inject(AttributeService) as jasmine.SpyObj<AttributeService>;
    ruleService = TestBed.inject(RuleService) as jasmine.SpyObj<RuleService>;
    categoryService = TestBed.inject(CategoryService) as jasmine.SpyObj<CategoryService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    userControllerService = TestBed.inject(UserControllerService) as jasmine.SpyObj<UserControllerService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    config = TestBed.inject(DynamicDialogConfig);
    ref = TestBed.inject(DynamicDialogRef);
  });

  describe('ngOnInit', () => {
    it('should call the necessary methods on initialization', () => {
      spyOn(component, 'retrieveUserFromLocalStorage');
      spyOn(component, 'initializeForm');
      spyOn(component, 'loadCategories');
      spyOn(component, 'loadAttributes');

      component.ngOnInit();

      expect(component.retrieveUserFromLocalStorage).toHaveBeenCalled();
      expect(component.initializeForm).toHaveBeenCalled();
      expect(component.loadCategories).toHaveBeenCalled();
      expect(component.loadAttributes).toHaveBeenCalled();
    });
  });

  describe('initializeForm', () => {
    it('should initialize the form with given rule data', () => {
      component.rule = {
        name: 'Test Rule',
        description: 'Test Description',
        category: { name: 'Test Category' },
        attributeDtos: [{ name: { name: 'Attribute 1' }, percentage: 50, value: 5 }]
      } as RuleDto;

      component.initializeForm();

      expect(component.ruleForm.value).toEqual({
        name: 'Test Rule',
        description: 'Test Description',
        category: { name: 'Test Category' },
        attributeDtos: [{
          name: { name: 'Attribute 1' },
          percentage: 50,
          value: 0
        }],
        updateDescription: '',
        imageUrl: ''
      });
    });
  });

  describe('addExistingAttributes', () => {
    it('should add existing attributes to the form', () => {
      component.rule = {
        attributeDtos: [
          { name: { name: 'Attribute 1' }, percentage: 50, value: 5 },
          { name: { name: 'Attribute 2' }, percentage: 50, value: 5 }
        ]
      } as RuleDto;
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array([])
      });

      component.addExistingAttributes();

      const attributeArray = component.ruleForm.get('attributeDtos') as FormArray;
      expect(attributeArray.length).toBe(2);
    });
  });

  describe('createAttributeGroup', () => {
    it('should create a FormGroup for an attribute', () => {
      const attr = { name: { name: 'Test Attribute' }, percentage: 50, value: 5 } as AttributeDataDto;
      const group = component.createAttributeGroup(attr);

      expect(group.value).toEqual({
        name: { name: 'Test Attribute' },
        percentage: 50,
        value: 0
      });
    });
  });

  describe('createAttributeGroup', () => {
    it('should create a FormGroup for an attribute', () => {
      const attr = { name: { name: 'Test Attribute' }, percentage: 50, value: 5 } as AttributeDataDto;
      const group = component.createAttributeGroup(attr);

      expect(group.value).toEqual({
        name: { name: 'Test Attribute' },
        percentage: 50,
        value: 0
      });
    });
  });



  describe('validateAttributeNames', () => {
    it('should return true if attribute names are unique', () => {
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array([
          component.fb.group({ name: 'Attribute1', percentage: 50 }),
          component.fb.group({ name: 'Attribute2', percentage: 50 })
        ])
      });
      const result = component.validateAttributeNames();
      expect(result).toBe(true);
    });

    it('should return false if attribute names are not unique', () => {
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array([
          component.fb.group({ name: 'Attribute1', percentage: 50 }),
          component.fb.group({ name: 'Attribute1', percentage: 50 })
        ])
      });
      const result = component.validateAttributeNames();
      expect(result).toBe(false);
    });
  });



  describe('showDialog', () => {
    it('should set categoryVisible to true if type is "category"', () => {
      component.showDialog('category');
      expect(component.categoryVisible).toBe(true);
    });

    it('should set attributeVisible to true if type is "attribute"', () => {
      component.showDialog('attribute');
      expect(component.attributeVisible).toBe(true);
    });
  });

  describe('addNewAttribute', () => {
    it('should add a new attribute if it does not exist', () => {
      component.newAttributeName = 'NewAttribute';
      component.attributes = [{ name: 'ExistingAttribute' }];
      component.addNewAttribute();
      expect(component.attributes.some(attr => attr.name === 'NewAttribute')).toBe(true);
    });

    it('should not add a new attribute if it already exists', () => {
      component.newAttributeName = 'ExistingAttribute';
      component.attributes = [{ name: 'ExistingAttribute' }];
      component.addNewAttribute();
      expect(component.attributes.length).toBe(1);
    });
  });

  describe('addNewCategory', () => {
    it('should add a new category if it does not exist', () => {
      component.newCategoryName = 'NewCategory';
      component.categories = [{ name: 'ExistingCategory' }];
      component.addNewCategory();
      expect(component.categories.some(cat => cat.name === 'NewCategory')).toBe(true);
    });

    it('should not add a new category if it already exists', () => {
      component.newCategoryName = 'ExistingCategory';
      component.categories = [{ name: 'ExistingCategory' }];
      component.addNewCategory();
      expect(component.categories.length).toBe(1);
    });
  });
  describe('addAttribute', () => {
    it('should add a new attribute to the form', () => {
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array([])
      });

      component.addAttribute();

      const attributeArray = component.ruleForm.get('attributeDtos') as FormArray;
      expect(attributeArray.length).toBe(1);
    });
  });

  describe('removeAttribute', () => {
    it('should remove an attribute from the form', () => {
      const initialAttributes = [
        component.fb.group({ name: 'Attribute1', percentage: 50 }),
        component.fb.group({ name: 'Attribute2', percentage: 50 })
      ];
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array(initialAttributes)
      });

      component.removeAttribute(1); // Removing the attribute at index 1

      const attributeArray = component.ruleForm.get('attributeDtos') as FormArray;
      expect(attributeArray.length).toBe(1);
      expect(attributeArray.value[0].name).toBe('Attribute1'); // Making sure the correct attribute is removed
    });
  });

  describe('getAttributeControls', () => {
    it('should return the controls of the attribute form array', () => {
      const initialAttributes = [
        component.fb.group({ name: 'Attribute1', percentage: 50 }),
        component.fb.group({ name: 'Attribute2', percentage: 50 })
      ];
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array(initialAttributes)
      });

      const attributeControls = component.getAttributeControls();

      expect(attributeControls.length).toBe(2);
      expect(attributeControls[0].value.name).toBe('Attribute1');
      expect(attributeControls[1].value.name).toBe('Attribute2');
    });
  });
  describe('validateAttributePercentages', () => {
    it('should return true if the sum of attribute percentages is equal to 100', () => {
      const attributes = [
        component.fb.group({ percentage: '50' }),
        component.fb.group({ percentage: '50' })
      ];
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array(attributes)
      });

      const result = component.validateAttributePercentages();

      expect(result).toBe(true);
    });

    it('should return false if the sum of attribute percentages is less than 100', () => {
      const attributes = [
        component.fb.group({ percentage: '40' }),
        component.fb.group({ percentage: '40' })
      ];
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array(attributes)
      });

      const result = component.validateAttributePercentages();

      expect(result).toBe(false);
      expect(messageService.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Error',
        detail: 'Sum of attribute percentages must be equal to 100'
      });
    });

    it('should return false if the sum of attribute percentages is greater than 100', () => {
      const attributes = [
        component.fb.group({ percentage: '50' }),
        component.fb.group({ percentage: '60' })
      ];
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array(attributes)
      });

      const result = component.validateAttributePercentages();

      expect(result).toBe(false);
      expect(messageService.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Error',
        detail: 'Sum of attribute percentages must be equal to 100'
      });
    });

    it('should handle NaN values and return false', () => {
      const attributes = [
        component.fb.group({ percentage: '50' }),
        component.fb.group({ percentage: 'invalid' })
      ];
      component.ruleForm = component.fb.group({
        attributeDtos: component.fb.array(attributes)
      });

      const result = component.validateAttributePercentages();

      expect(result).toBe(false);
      expect(messageService.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Error',
        detail: 'Sum of attribute percentages must be equal to 100'
      });
    });
  });



  describe('onSubmit', () => {
    it('should call saveRule if the form is valid and attributes are valid', () => {
      spyOn(component, 'saveRule');
      spyOn(component, 'validateAttributeNames').and.returnValue(true);
      spyOn(component, 'validateAttributePercentages').and.returnValue(true);
      component.ruleForm = component.fb.group({
        valid: true
      });
      component.onSubmit();
      expect(component.saveRule).toHaveBeenCalled();
    });

    it('should not call saveRule if the form is invalid', () => {
      spyOn(component, 'saveRule');
      spyOn(component, 'validateAttributeNames').and.returnValue(true);
      spyOn(component, 'validateAttributePercentages').and.returnValue(true);
      component.ruleForm = component.fb.group({
        valid: false
      });
      component.onSubmit();
    });

    it('should not call saveRule if the attribute names are not valid', () => {
      spyOn(component, 'saveRule');
      spyOn(component, 'validateAttributeNames').and.returnValue(false);
      spyOn(component, 'validateAttributePercentages').and.returnValue(true);
      component.ruleForm = component.fb.group({
        valid: true
      });
      component.onSubmit();
      expect(component.saveRule).not.toHaveBeenCalled();
    });

    it('should not call saveRule if the attribute percentages are not valid', () => {
      spyOn(component, 'saveRule');
      spyOn(component, 'validateAttributeNames').and.returnValue(true);
      spyOn(component, 'validateAttributePercentages').and.returnValue(false);
      component.ruleForm = component.fb.group({
        valid: true
      });
      component.onSubmit();
      expect(component.saveRule).not.toHaveBeenCalled();
    });
  });

  describe('retrieveUserFromLocalStorage', () => {
    it('should retrieve user details from local storage', () => {
      spyOn(localStorage, 'getItem').and.returnValue('{"username": "testuser", "profileImagePath": "testpath"}');
      component.retrieveUserFromLocalStorage();
      expect(component.username).toEqual('testuser');
      expect(component.imageUrl).toEqual('testpath');
    });

    it('should log an error if user details are not found in local storage', () => {
      spyOn(console, 'error');
      spyOn(localStorage, 'getItem').and.returnValue(null);
      component.retrieveUserFromLocalStorage();
      expect(console.error).toHaveBeenCalledWith('User details not found in local storage.');
    });
  });


});
