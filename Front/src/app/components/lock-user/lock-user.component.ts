import { Component, OnInit } from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {MessageService} from "primeng/api";
import {UserControllerService} from "../../../open-api";

@Component({
  selector: 'app-lock-user',
  templateUrl: './lock-user.component.html',
  styleUrls: ['./lock-user.component.scss']
})
export class LockUserComponent implements OnInit {

  user: any;

  constructor(public ref: DynamicDialogRef ,public config: DynamicDialogConfig, public messageService:MessageService  , private userService:UserControllerService) { }

  ngOnInit(): void {
    this.user = this.config.data ;
  }

  closeDialog() {
    this.ref.close() ;
  }

  lockUser() {
  //   this.userService._delete(this.user.id) ;
  //   this.ref.close();
  //   this.messageService.add({severity:'success', summary:'User locked !', detail:'User locked successfully'});
  //
    }
}
