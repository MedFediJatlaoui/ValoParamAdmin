import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../../open-api';
import { MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { CategoryDto } from '../../../open-api/model/categoryDto';
import { DeletecategoryComponent } from '../deletecategory/deletecategory.component';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {
  categories: CategoryDto[] = [];

  constructor(
    private categoryService: CategoryService,
    private messageService: MessageService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories()
      .subscribe(
        categories => this.categories = categories,
        error => {
          console.error('Error loading categories', error);
          this.categories = []; // Handle error by setting categories to empty array
        }
      );
  }

  checkref(category: CategoryDto): void {
    this.categoryService.refCategory(category.id!)
      .subscribe(
        response => {
          if (response === 0) {
            this.calldeletecomponent(category);
          } else {
            this.messageService.add({
              severity: 'error',
              summary: 'Category not deleted',
              detail: `category ${category.name} is used by ${response} active rules`
            });
          }
        },
        error => {
          console.log('Error checking reference', error);
          // Handle error as needed
        }
      );
  }

  calldeletecomponent(category: CategoryDto): void {
    const ref = this.dialogService.open(DeletecategoryComponent, {
      header: 'Delete category',
      width: '500px',
      contentStyle: {"background-color": "var(--color-white)","color": "var(--color-dark)"},
      data:  category
    });

    ref.onClose.subscribe(result => {
      if (result) {
        this.loadCategories()
      }
    });
  }

  updateCategory(id: number, categoryDto: CategoryDto): void {
    this.categoryService.updateCategory(id, categoryDto)
      .subscribe(
        () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Category updated successfully'
          });
          categoryDto.editmode=!categoryDto.editmode
        },
        error => {
          this.messageService.add({
            severity: 'error',
            summary: 'Category not updated',
            detail: 'Error updating category'
          });
          console.error('Error updating category', error);
        }
      );
  }

  toggleEditMode(category: any): void {
    category.editmode = !category.editmode;
  }
}
