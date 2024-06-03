import {Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {MenuItem, OverlayService} from 'primeng/api';
import { LayoutService } from "./service/app.layout.service";
import { OverlayPanel } from 'primeng/overlaypanel';
import {Router} from "@angular/router";
import {UserControllerService} from "../../open-api";
import {UserService} from "../services/user/user.service";
@Component({
    selector: 'app-topbar',
    templateUrl: './app.topbar.component.html'
})
export class AppTopBarComponent implements OnInit{
  dropdownVisible: boolean = false;

    items!: MenuItem[];

    @ViewChild('menubutton') menuButton!: ElementRef;

    @ViewChild('topbarmenubutton') topbarMenuButton!: ElementRef;

    @ViewChild('topbarmenu') menu!: ElementRef;

    @ViewChild('overlayPanel') overlayPanel: OverlayPanel | undefined;

    constructor(public layoutService: LayoutService ,private  router:Router , private userService:UserControllerService,private userservice :UserService) { }
  toggleDropdown(event: Event) {
    this.dropdownVisible = !this.dropdownVisible;
    event.stopPropagation(); // Prevent event from bubbling up to document click listener
  }

  onDropdownItemClick(event: Event) {
    // Implement click logic for dropdown items here
    this.dropdownVisible = false; // Hide dropdown after item is clicked
    event.stopPropagation(); // Prevent event from bubbling up to document click listener
  }

  // Close dropdown when clicking outside of it
  username: any;
  role: any;
  profileImagePath: string | undefined;
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement; // Explicitly cast event.target to HTMLElement
    if (!target.closest('.topbar')) {
      this.dropdownVisible = false;
    }
  }

    logout() {
        console.log("logout clicked !") ;
        localStorage.clear() ;
        this.router.navigate(['/signIn']) ;
    }

  getUserByEmail() {
   // const email = JSON.parse(localStorage.getItem('connectedUser') || '{}').email;
    const email = this.userservice.getUserDetails()
    if (email) {
      this.userService.getUser(email.email).subscribe(
          (user) => {
           // localStorage.setItem('connectedUser', JSON.stringify(user));

            this.username = user.firstname;
            this.role = user.role;
            this.profileImagePath=user.profileImagePath;
          },
          (error) => {
            console.error("Error fetching user:", error);
          }
      );
    } else {
      console.error("Email is null or not found in localStorage");
    }
  }


  ngOnInit(): void {
    this.getUserByEmail() ;
  }
}
