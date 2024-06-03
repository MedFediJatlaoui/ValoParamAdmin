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
    companies: string[] = ['Talan , Paris', 'Talan , Marseille', 'Talan , Lyon'];
    roles: string[] = ['ADMIN', 'BUSINESSEXPERT', 'CONSULTANT'];

    constructor(
        private fb: FormBuilder,
        private userService: UserService,
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        public messageService: MessageService
    ) { }

    ngOnInit(): void {
        this.user = this.config.data;
        this.updateUserForm = this.fb.group({
            firstname: [this.user.firstname, Validators.required],
            lastname: [this.user.lastname, Validators.required],
            email: [this.user.email, [Validators.required, Validators.email]],
            phone: [this.user.phone, Validators.required],
            company: [this.user.company, Validators.required],
            role: [this.user.role, Validators.required],
        });
    }

    updateUser(): void {
        if (this.updateUserForm.invalid) {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill in all required fields' });
            return;
        }

        const userId = this.user.id;
        const registerDto = {
            firstname: this.updateUserForm.value.firstname,
            lastname: this.updateUserForm.value.lastname,
            phone: this.updateUserForm.value.phone,
            company: this.updateUserForm.value.company,
            email: this.updateUserForm.value.email,
            role: this.updateUserForm.value.role
        };
        const file = this.file;
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

    onFileChange(event: any): void {
        this.file = event.target.files[0];
    }
}
