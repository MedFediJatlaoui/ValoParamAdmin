import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { AuthenticationService, AuthenticationRequest, UserControllerService, UserDto, RegisterDto } from '../../../open-api';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import jwtDecode from 'jwt-decode';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [UserService, AuthenticationService, UserControllerService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add user', () => {
    const dto = {} as RegisterDto;
    const file = new File([''], 'mockFile.txt', { type: 'text/plain' });

    service.addUser(dto, file).subscribe(() => {});

    const req = httpMock.expectOne('http://localhost:8090/api/users');
    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBeTruthy();
    expect(req.request.body.get('file')).toEqual(file);
    req.flush({});
  });

  it('should handle error when adding user', () => {
    const dto = {} as RegisterDto;
    const file = new File([''], 'mockFile.txt', { type: 'text/plain' });

    service.addUser(dto, file).subscribe(
      () => fail('should have failed with 500 status'),
      (error) => expect(error.status).toBe(500)
    );

    const req = httpMock.expectOne('http://localhost:8090/api/users');
    req.flush('Error', { status: 500, statusText: 'Server Error' });
  });

  it('should login', () => {
    const authRequest: AuthenticationRequest = { email: 'test', password: 'password' };

    service.login(authRequest).subscribe(() => {});

    const req = httpMock.expectOne('http://localhost:8090/api/auth/authenticate');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(authRequest);
    req.flush({});
  });

  it('should handle login error', () => {
    const authRequest: AuthenticationRequest = { email: 'test', password: 'password' };

    service.login(authRequest).subscribe(
      () => fail('should have failed with 401 status'),
      (error) => expect(error.status).toBe(401)
    );

    const req = httpMock.expectOne('http://localhost:8090/api/auth/authenticate');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
  });

  it('should get all users', () => {
    service.getAllUsers().subscribe(() => {});

    const req = httpMock.expectOne('http://localhost:8090/api/users');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should check if user is logged in and access token is valid', () => {
    spyOn(service, 'isValidAccessToken').and.returnValue(true);
    localStorage.setItem('accessToken', 'validToken');

    expect(service.isUserLoggedAndAccessTokenValid()).toBeTruthy();
    expect(service.isValidAccessToken).toHaveBeenCalled();
  });

  it('should redirect to sign in if access token is invalid', () => {
    const spy = spyOn(router, 'navigate');
    spyOn(service, 'isValidAccessToken').and.returnValue(false);
    localStorage.setItem('accessToken', 'invalidToken');

    expect(service.isUserLoggedAndAccessTokenValid()).toBeFalsy();
    expect(localStorage.getItem('accessToken')).toBeNull();
    expect(spy).toHaveBeenCalledWith(['/signIn']);
  });


  it('should handle invalid token format in isValidAccessToken', () => {
    const invalidToken = 'invalidToken';

    expect(service.isValidAccessToken(invalidToken)).toBe(false);
  });

  it('should set token', () => {
    const tokenData = { access_token: 'newToken' };
    service.setToken(tokenData);
    expect(localStorage.getItem('accessToken')).toBe('newToken');
  });



  it('should update user', () => {
    const id = 1;
    const registerDto: RegisterDto = {}; // Provide valid RegisterDto
    const file = new File([''], 'mockFile.txt', { type: 'text/plain' });

    service.updateUser(id, registerDto, file).subscribe(() => {});

    const req = httpMock.expectOne(`http://localhost:8090/api/users/${id}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body instanceof FormData).toBeTruthy();
    expect(req.request.body.get('file')).toEqual(file);
    req.flush({});
  });

  it('should handle error when updating user', () => {
    const id = 1;
    const registerDto: RegisterDto = {}; // Provide valid RegisterDto
    const file = new File([''], 'mockFile.txt', { type: 'text/plain' });

    service.updateUser(id, registerDto, file).subscribe(
      () => fail('should have failed with 500 status'),
      (error) => expect(error.status).toBe(500)
    );

    const req = httpMock.expectOne(`http://localhost:8090/api/users/${id}`);
    req.flush('Error', { status: 500, statusText: 'Server Error' });
  });
});
