import { Component, OnInit } from '@angular/core';
import { UserService } from "../../services/user/user.service";
import { Router } from "@angular/router";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MessageService } from 'primeng/api';
import { UserDto } from "../../../open-api";

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss'],
  providers: [MessageService]
})
export class AddUserComponent implements OnInit {

  errorFound: boolean = false;
  formSave!: FormGroup;
  file!: File;
  isExpertSelected: boolean = false;
  roles: string[] = ['ADMIN', 'EXPERT', 'CONSULTANT'];
  companies: string[] = ['Talan , Paris', 'Talan , Marseille', 'Talan , Lyon', 'Talan , Tunisie', 'Talan , Suisse', 'Talan , Canada'];

  constructor(
    private userService: UserService,
    private router: Router,
    private fb: FormBuilder,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.formSave = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      company: ['Talan , Tunisie', Validators.required],
      role: [UserDto.RoleEnum.Admin, Validators.required],
      canCancel: [{ value: false, disabled: !this.isExpertSelected }],
      password: ['', Validators.required],
      password2: ['', Validators.required],
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const password2 = formGroup.get('password2')?.value;
    if (password !== password2) {
      formGroup.get('password2')?.setErrors({ passwordMismatch: true });
    } else {
      formGroup.get('password2')?.setErrors(null);
    }
  }

  addUser() {
    if (this.formSave.invalid) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill in all required fields' });
      return;
    }

    const formValue = this.formSave.value;
    console.log('Form Value:', formValue);

    let auth = "";
    if (formValue.canCancel) {
      auth = 'CAN_CANCEL';
    }

    this.userService.addUser({ ...formValue, auth: auth }, this.file).subscribe({
      next: data => {
        this.router.navigate(['users']);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User successfully added' });
      },
      error: error => {
        this.errorFound = true;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to add user' });
      }
    });
  }

  onRoleChange(event: any): void {
    this.isExpertSelected = event.target.value === 'EXPERT';
    const canCancelControl = this.formSave.get('canCancel');
    if (this.isExpertSelected && canCancelControl) {
      canCancelControl.enable();
    } else if (canCancelControl) {
      canCancelControl.disable();
      canCancelControl.setValue(false);
    }
  }

  onFileChange(event: any) {
    this.file = event.target.files[0];
  }

  protected readonly console = console;
}
