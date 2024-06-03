import {ComponentFixture, TestBed} from '@angular/core/testing';
import {AllUsersComponent} from './all-users.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {UserService} from '../../services/user/user.service';
import {DialogService} from 'primeng/dynamicdialog';
import {UserDto} from '../../../open-api';
import {of, throwError} from 'rxjs';
import {UpdateUserComponent} from "../update-user/update-user.component";
import {LockUserComponent} from "../lock-user/lock-user.component";

describe('AllUsersComponent', () => {
    let component: AllUsersComponent;
    let fixture: ComponentFixture<AllUsersComponent>;
    let userService: jasmine.SpyObj<UserService>;
    let dialogService: jasmine.SpyObj<DialogService>;

    beforeEach(async () => {
        const userServiceSpy = jasmine.createSpyObj('UserService', ['getAllUsers']);
        const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['open']);

        await TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            declarations: [AllUsersComponent],
            providers: [
                {provide: UserService, useValue: userServiceSpy},
                {provide: DialogService, useValue: dialogServiceSpy}
            ]
        }).compileComponents();

        userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
        dialogService = TestBed.inject(DialogService) as jasmine.SpyObj<DialogService>;
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(AllUsersComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should fetch all active users', () => {
        const mockUserList: UserDto[] = [
            {id: 1, firstname: 'John', lastname: 'Doe', email: 'john@example.com', active: true},
            {id: 2, firstname: 'Jane', lastname: 'Doe', email: 'jane@example.com', active: true}
        ];

        userService.getAllUsers.and.returnValue(of(mockUserList));

        component.ngOnInit();

        expect(userService.getAllUsers).toHaveBeenCalled();
        expect(component.userList).toEqual(mockUserList);
    });

    it('should open update user dialog with selected user data', () => {
        const mockUser: UserDto = {
            id: 1,
            firstname: 'John',
            lastname: 'Doe',
            email: 'john.doe@example.com',
            active: true,
            nonExpired: true,
            profileImagePath: '/path/to/profile/image.jpg',
            phone: '123456789',
            company: 'Example Company',
            role: 'ADMIN',
            error: ''
        };
        component.updateUser(mockUser);
        expect(component.selectedUser).toEqual(mockUser);
    });

    it('should open delete user dialog and handle closure', () => {
        const mockUser = { id: 1, name: 'John Doe' };

        // Mock the dialogService.open method
        const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['afterClosed']);
        dialogService.open.and.returnValue(dialogRefSpy);

        // Simulate dialog closure
        dialogRefSpy.afterClosed.and.returnValue(of(true));

        // Call the deleteUser method
        component.deleteUser(mockUser);
    });
    it('should open lock user dialog', () => {
        const mockUser = { id: 1, name: 'John Doe' };

        // Mock the dialogService.open method
        const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['afterClosed']);
        dialogService.open.and.returnValue(dialogRefSpy);

        // Call the lockUser method
        component.lockUser(mockUser);

    });
});
