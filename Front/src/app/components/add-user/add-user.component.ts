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

    const selectedRole = this.formSave.get('role')!.value;
    this.userService.addUser({ ...this.formSave.value, role: selectedRole }, this.file).subscribe({
      next: data => {
        this.router.navigate(['users']);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User successfully added' });
      },
      error: error => {
        this.errorFound = true;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Email exists !' });
      }
    });
  }

  onFileChange(event: any) {
    this.file = event.target.files[0];
  }
}
