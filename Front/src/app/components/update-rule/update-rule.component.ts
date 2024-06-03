import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MessageService} from 'primeng/api';
import {
  AttributeDataDto,
  AttributeDto,
  AttributeService,
  CategoryDto,
  CategoryService,
  RuleDto,
  RuleService,
  UserControllerService
} from "../../../open-api";
import {Router} from "@angular/router";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {UserService} from "../../services/user/user.service";

@Component({
  selector: 'app-update-rule',
  templateUrl: './update-rule.component.html',
  styleUrls: ['./update-rule.component.scss']
})
export class UpdateRuleComponent implements OnInit {

  ruleForm!: FormGroup;
  attributes!: AttributeDto[];
  categories!: CategoryDto[];
  selectedCategory!: CategoryDto;
  selectedAttributes: AttributeDto[] = [];
  categoryVisible: boolean = false;
  attributeVisible: boolean = false;
  newCategoryName: string = '';
  newAttributeName: string = '';
  existingAttributes!: AttributeDataDto[];
  rule!: RuleDto;
  username: any;
  imageUrl: string | undefined;

  constructor(
    public fb: FormBuilder,
    public messageService: MessageService,
    public attributeService: AttributeService,
    public ruleService: RuleService,
    public categoryService: CategoryService,
    public router: Router,
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    public userService: UserService,
    public userControllerService: UserControllerService
  ) {
  }

  ngOnInit(): void {
    this.retrieveUserFromLocalStorage() ;
    this.rule = this.config.data;
    this.initializeForm();
    this.loadCategories();
    this.loadAttributes();
    console.log(this.imageUrl)
  }

  initializeForm() {
    if (this.rule.category != null) this.selectedCategory = this.rule.category;
    this.ruleForm = this.fb.group({
      name: [this.rule.name, Validators.required],
      description: [this.rule.description, Validators.required],
      category: [this.rule.category, Validators.required],
      attributeDtos: this.fb.array([]),
      updateDescription: [''] ,
      imageUrl: [this.imageUrl || '']
    });
    this.addExistingAttributes();
  }

  addExistingAttributes() {
    if (this.rule.attributeDtos != null) {
      const attributeArray = this.ruleForm.get('attributeDtos') as FormArray;
      this.existingAttributes = this.rule.attributeDtos;
      this.existingAttributes.forEach(attr => {
        if (attr.name != null) this.selectedAttributes.push(attr.name);
        attributeArray.push(this.createAttributeGroup(attr));
      });
    }
  }

  createAttributeGroup(attr: AttributeDataDto) {
    return this.fb.group({
      name: [attr.name, Validators.required],
      percentage: [attr.percentage, Validators.required],
      value: [0]
    });
  }

  loadCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: data => {
        this.categories = data;
      },
      error: error => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadAttributes() {
    this.attributeService.getAllAttributes().subscribe({
      next: data => {
        this.attributes = data;
      },
      error: error => {
        console.error('Error loading attributes:', error);
      }
    });
  }

  createEmptyAttributeGroup() {
    return this.fb.group({
      name: ['', Validators.required],
      percentage: ['', Validators.required],
      value: [0]
    });
  }

  addAttribute() {
    const attributeArray = this.ruleForm.get('attributeDtos') as FormArray;
    attributeArray.push(this.createEmptyAttributeGroup());
  }


  removeAttribute(index: number) {
    const attributeArray = this.ruleForm.get('attributeDtos') as FormArray;
    attributeArray.removeAt(index);
  }

  getAttributeControls() {
    return (this.ruleForm.get('attributeDtos') as FormArray).controls;
  }

  public validateAttributeNames(): boolean {
    const attributeNames = new Set<string>();
    const attributeControls = this.getAttributeControls();
    for (const control of attributeControls) {
      const name = control.value.name;
      if (attributeNames.has(name)) {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Attribute names must be unique'
        });
        return false;
      }
      attributeNames.add(name);
    }
    return true;
  }

  public validateAttributePercentages(): boolean {
    let sum = 0;
    for (const control of this.getAttributeControls()) {
      const percentage = parseInt(control.value.percentage);
      if (!isNaN(percentage)) {
        sum += percentage;
      }
    }

    if (sum !== 100) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Sum of attribute percentages must be equal to 100'
      });
      return false;
    }
    return true;
  }

  showDialog(type: string) {
    if (type == 'category') {
      this.categoryVisible = true;
    } else {
      this.attributeVisible = true;
    }
  }

  addNewAttribute() {
    if (this.newAttributeName.trim() !== '') {
      const attributeExists = this.attributes.some(attr => attr.name === this.newAttributeName);
      if (!attributeExists) {
        const newAttribute: AttributeDto = {name: this.newAttributeName};
        this.attributes.push(newAttribute);
        this.attributeVisible = false;
        this.messageService.add({severity: 'success', summary: 'success', detail: 'Attribute added'});
      } else {
        this.messageService.add({severity: 'error', summary: 'error', detail: 'Attribute exists '});
      }
    }
  }

  addNewCategory() {
    if (this.newCategoryName.trim() !== '') {
      const categoryExists = this.categories.some(cat => cat.name === this.newCategoryName);
      if (!categoryExists) {
        const newCategory: CategoryDto = {name: this.newCategoryName};
        this.categories.push(newCategory);
        this.categoryVisible = false;
        this.messageService.add({severity: 'success', summary: 'success', detail: 'Category added'});
      } else {
        this.messageService.add({severity: 'error', summary: 'error', detail: 'Category exists '});
      }
    }
  }

  onSubmit() {
    const formData = this.ruleForm.value;
    console.log(formData);
    if (this.ruleForm.valid) {
      if (!this.validateAttributeNames() || !this.validateAttributePercentages()) {
        return;
      }
      this.saveRule(formData);
    } else {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'Please fill all required fields correctly.'});
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

  saveRule(formData: any) {
    let ruleId = this.rule.id;
    if (ruleId != null) {
      this.ruleService.updateRule(this.username, ruleId, formData.updateDescription , formData).subscribe({
        next: response => {
          this.messageService.add({severity: 'success', summary: 'Update scheduled', detail: 'Rule update scheduled'});
          this.ref.close(true);
        },
        error: error => {
          console.error('Error saving rule:', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to update rule. Please try again.'});
        }
      });
    }
  }
}
