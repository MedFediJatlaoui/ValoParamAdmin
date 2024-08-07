import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { UserService } from "../../services/user/user.service";
import { UserDto } from "../../../open-api";
import { MessageService } from "primeng/api";

@Component({
  selector: 'app-update-user',
  templateUrl: './update-user.component.html',
  styleUrls: ['./update-user.component.scss']
})
export class UpdateUserComponent implements OnInit {
  updateUserForm!: FormGroup;
  user: any;
  file!: File;
  companies: string[] =  [ 'Talan , Paris', 'Talan , Marseille', 'Talan , Lyon','Talan , Tunisie','Talan , Suisse','Talan , Canada'];
  roles: string[] = ['ADMIN', 'EXPERT', 'CONSULTANT'];
  isExpertSelected: boolean = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    public messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.user = this.config.data;
    this.isExpertSelected = this.user.role === 'EXPERT';

    // Determine the initial value of canCancel
    const canCancel = this.user.authorities?.includes('CAN_CANCEL') || false;
    this.updateUserForm = this.fb.group({
      firstname: [this.user.firstname, Validators.required],
      lastname: [this.user.lastname, Validators.required],
      email: [this.user.email, [Validators.required, Validators.email]],
      phone: [this.user.phone, Validators.required],
      company: [this.user.company, Validators.required],
      role: [this.user.role, Validators.required],
      canCancel: [{ value: canCancel, disabled: !this.isExpertSelected }],
      password: [this.user.password],
    });

  }

  updateUser(): void {
    if (this.updateUserForm.invalid) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill in all required fields' });
      return;
    }

    // Get the current value of canCancel from the form
    const formValue = this.updateUserForm.value;

    const userId = this.user.id;
    const registerDto: any = {
      firstname: formValue.firstname,
      lastname: formValue.lastname,
      phone: formValue.phone,
      company: formValue.company,
      email: formValue.email,
      role: formValue.role,
      auth: formValue.canCancel ? 'CAN_CANCEL' : "",
    };

    // Include password only if it has a value
    if (formValue.password) {
      registerDto.password = formValue.password;
    }

    this.userService.updateUser(userId, registerDto, this.file)
      .subscribe({
        next: (updatedUser: UserDto) => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User updated successfully' });
          this.ref.close(true);
        },
        error: (err: any) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error updating user' });
          console.error('Error updating user:', err);
        }
      });
  }

  onRoleChange(event: any): void {
    this.isExpertSelected = event.target.value === 'EXPERT';
    const canCancelControl = this.updateUserForm.get('canCancel');
    if (this.isExpertSelected && canCancelControl) {
      canCancelControl.enable();
    } else if (canCancelControl) {
      canCancelControl.disable();
      canCancelControl.setValue(false);
    }
  }

  onFileChange(event: any): void {
    this.file = event.target.files[0];
  }
}
