import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../../services/user/user.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import {UserControllerService, UserDto} from "../../../open-api";
import {jwtDecode} from "jwt-decode";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  providers: [MessageService]
})
export class LoginComponent implements OnInit {

  formLogin!: FormGroup;
  errorFound: boolean = false;
  private username: string | undefined;
  private role: UserDto.RoleEnum | undefined;
  private profileImagePath: string | undefined;

  constructor(
    private userService: UserService,
    private router: Router,
    private fb: FormBuilder,
    private messageService: MessageService , private userControllerService:UserControllerService
  ) { }

  ngOnInit(): void {
    this.formLogin = this.fb.group({
      email: this.fb.control(''),
      password: this.fb.control('')
    });
  }

  handleLogin() {
    this.userService.login(this.formLogin.value).subscribe({
      next: data => {
        this.errorFound = false;
        console.log(this.errorFound);
        console.log(data);
        this.userService.setToken(data);
        this.connectUser(data);
      }, error: error => {
        this.errorFound = true;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Wrong email or password' });
      }
    });
  }

  getUserDetails() {
    const token = localStorage.getItem('accessToken');
    if (token) {
      const decodedJwt: any = jwtDecode(token);
      const email = decodedJwt.sub;
      return {
        email: email
      };
    } else {
      return {
        email: "wael.arfaoui@talan.com"
      } ;
    }
  }
  connectUser(data: any) {
    this.router.navigate(['/']);
    const email = this.getUserDetails().email;
    if (email) {
      this.userControllerService.getUser(email).subscribe(
        (user) => {
          this.username = user.firstname + " " + user.lastname ;
          this.role = user.role;
          this.profileImagePath = user.profileImagePath;
          const userDetailsJSON = JSON.stringify({
            username: this.username,
            role: this.role,
            profileImagePath: this.profileImagePath
          });
          localStorage.setItem('userDetails', userDetailsJSON);
        },
        (error) => {
          console.error("Error fetching user:", error);
        }
      );
    } else {
      console.error("Email is null or not found in localStorage");
    }
  }
}
