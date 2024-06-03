import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandler, HttpEvent, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InterceptorService } from './interceptor.service';

describe('InterceptorService', () => {
  let service: InterceptorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [InterceptorService]
    });
    service = TestBed.inject(InterceptorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add authorization header', () => {
    const httpRequest = new HttpRequest<any>('GET', '/api/data');
    const next: HttpHandler = {
      handle: (request: HttpRequest<any>): Observable<HttpEvent<any>> => {
        expect(request.headers.get('Authorization')).toEqual(`Bearer ${localStorage.getItem('accessToken')}`);
        return new Observable<HttpEvent<any>>();
      }
    };
    const interceptedRequest = service.intercept(httpRequest, next);
    expect(interceptedRequest).toBeDefined();
  });
});
