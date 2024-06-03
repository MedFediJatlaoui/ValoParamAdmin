import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../services/user/user.service";
import {UserDto} from "../../../open-api";
import {UpdateUserComponent} from "../update-user/update-user.component";
import {DialogService} from "primeng/dynamicdialog";
import {LockUserComponent} from "../lock-user/lock-user.component";
import {DeleteUserComponent} from "../delete-user/delete-user.component";
import {map} from "rxjs";
@Component({
  selector: 'app-all-users',
  templateUrl: './all-users.component.html',
  styleUrls: ['./all-users.component.scss'], providers: [DialogService]
})
export class AllUsersComponent implements OnInit {
    selectedUser: any;

    userList!:UserDto[] ;

  constructor(private http: HttpClient , private userService:UserService ,private dialogService: DialogService) { }

  ngOnInit(): void {
    this.getAllUsers();
  }

    getAllUsers() {
        this.userService.getAllUsers()
            .pipe(
                map(users => users.filter(user => user.active === true))
            )
            .subscribe(
                activeUsers => {
                    console.log(activeUsers);
                    this.userList = activeUsers;
                },
                error => {
                    console.error("Error fetching users:", error);
                }
            );
    }

    updateUser(user: any) {
        this.selectedUser = user;
        const ref = this.dialogService.open(UpdateUserComponent, {
        header: 'Update User',
        width: '900px',
        height: '600px',
        contentStyle: {"background-color": "var(--color-white)","color": "var(--color-dark)"},
        data: this.selectedUser
        });
        ref.onClose.subscribe((result: any) => {
            if (result==true) {
                this.getAllUsers();
            }
        });
    }

  deleteUser(user: any) {
      this.selectedUser = user;
      const ref = this.dialogService.open(DeleteUserComponent, {
      header: 'Delete User',
      width: '500px',
      contentStyle: {"background-color": "var(--color-white)","color": "var(--color-dark)"},
        data: this.selectedUser
    });
      ref.onClose.subscribe((result: any) => {
          if (result==true) {
              this.getAllUsers();
          }
      });
  }

  lockUser(user: any) {
    this.selectedUser = user;
    const ref = this.dialogService.open(LockUserComponent, {
      header: 'Lock User',
      width: '500px',
      contentStyle: {"background-color": "var(--color-white)","color": "var(--color-dark)"},
      data: this.selectedUser

    });
  }
}
