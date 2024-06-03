import { TestBed } from '@angular/core/testing';
import { GuardService } from './guard.service';
import { HttpClientModule } from "@angular/common/http";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { UserService } from '../user/user.service';
import { of } from 'rxjs';

describe('GuardService', () => {
  let service: GuardService;
  let userService: jasmine.SpyObj<UserService>;

  beforeEach(() => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['isUserLoggedAndAccessTokenValid']);

    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        GuardService,
        { provide: UserService, useValue: userServiceSpy }
      ]
    });
    service = TestBed.inject(GuardService);
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return true if user is logged in and access token is valid', () => {
    // Arrange
    userService.isUserLoggedAndAccessTokenValid.and.returnValue(true);

    // Act
    const canActivate = service.canActivate(new ActivatedRouteSnapshot(), {} as RouterStateSnapshot);

    // Assert
    expect(canActivate).toBeTrue();
  });

  it('should return false if user is not logged in or access token is not valid', () => {
    // Arrange
    userService.isUserLoggedAndAccessTokenValid.and.returnValue(false);

    // Act
    const canActivate = service.canActivate(new ActivatedRouteSnapshot(), {} as RouterStateSnapshot);

    // Assert
    expect(canActivate).toBeFalse();
  });
});
