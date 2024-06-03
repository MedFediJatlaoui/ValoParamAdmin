import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {
  AttributeDataDto,
  AttributeDto,
  AttributeService,
  CategoryDto,
  CategoryService,
  RuleDto, RuleService
} from "../../../open-api";
import {MessageService} from "primeng/api";
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {UserService} from "../../services/user/user.service";
import Swal from 'sweetalert2' ;
import {error} from "@angular/compiler-cli/src/transformers/util";
@Component({
  selector: 'app-use-rule',
  templateUrl: './use-rule.component.html',
  styleUrls: ['./use-rule.component.scss']
})
export class UseRuleComponent implements OnInit {
  ruleForm!: FormGroup;
  attributes!: AttributeDto[];
  categories!: CategoryDto[];
  selectedCategory!: CategoryDto;
  selectedAttributes: AttributeDto[] = [];
  existingAttributes!: AttributeDataDto[];
  rule!: RuleDto;
  username: any;
  imageUrl: string | undefined;

  constructor(
      public fb: FormBuilder,
      public messageService: MessageService,
      public attributeService: AttributeService,
      public categoryService: CategoryService,
      public ref: DynamicDialogRef,
      public config: DynamicDialogConfig,
      public userService: UserService,
      public ruleService:RuleService
  ) {
  }

  ngOnInit(): void {
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
      imageUrl: [this.imageUrl]
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
      value: [attr.value, Validators.required]
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
  getAttributeControls() {
    return (this.ruleForm.get('attributeDtos') as FormArray).controls;
  }
  public validateAttributeValues(): boolean {
    const attributeControls = this.getAttributeControls();
    for (const control of attributeControls) {
      const value = parseInt(control.value.value);
      if (isNaN(value) || value < 1 || value > 10) {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Attribute value must be between 1 and 10'
        });
        return false;
      }
    }
    return true;
  }

  onSubmit() {
      if (!this.validateAttributeValues()) {
        return;
      }
      else {
        let note = 0.0;
        const attributeControls = this.getAttributeControls();
        for (const control of attributeControls) {
          const percentage = parseFloat(control.get('percentage')!.value); // Parse as float
          const value = parseFloat(control.get('value')!.value); // Parse as float
          note += percentage / 100 * value;
        }
        const formattedNote = note.toFixed(2); // Format note to two decimal places
        if (this.rule.id != null) {
          this.ruleService.createRuleUsage(this.rule.id).subscribe({
            next: data => {
              console.log("rule used !");
              this.displaySuccessMessage(formattedNote); // Display success message after rule is used
            },
            error: error => {
              console.error('Error using rule:', error);
            }
          });
        } else {
          this.displaySuccessMessage(formattedNote); // Display success message if rule id is null
        }
      }
      }

  displaySuccessMessage(formattedNote: string) {
    Swal.fire({
      title: "Note",
      text: formattedNote,
      icon: "success"
    });
    this.ref.close(true); // Close dialog after displaying success message
  }





}


