import { Component, OnInit } from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {CategoryDto, CategoryService, RuleService, UserControllerService} from "../../../open-api";
import {MessageService} from "primeng/api";
import {UserService} from "../../services/user/user.service";

@Component({
  selector: 'app-deletecategory',
  templateUrl: './deletecategory.component.html',
  styleUrls: ['./deletecategory.component.scss']
})
export class DeletecategoryComponent implements OnInit {
 category : CategoryDto={ } ;
 username="";
  constructor(public ref: DynamicDialogRef , public config: DynamicDialogConfig ,
              public categoryService:CategoryService , public messageService:MessageService ,
              public userService:UserService , public userControllerService:UserControllerService) { }

  ngOnInit(): void {
    this.category = this.config.data;
  }

  closeDialog() {
    this.ref.close() ;
  }

  disable() {
    if (this.category.id != null) {
      console.log(`Attempting to delete category with id: ${this.category.id}`);
      this.categoryService.deleteCategory(this.category.id)
        .subscribe(
          {
            next: () => {
              console.log('Category deleted successfully');
              this.messageService.add({
                severity: 'success',
                summary: 'Category deleted',
                detail: `Category ${this.category.name} Deleted`
              });
              this.ref.close(true);
            },
            error: (err) => {
              console.log('Error occurred!', err);
            }
          }
        );
    }
  }

}
