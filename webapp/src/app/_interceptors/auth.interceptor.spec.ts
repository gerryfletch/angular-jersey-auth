import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {getTestBed, TestBed} from '@angular/core/testing';
import {AuthInterceptor} from './auth.interceptor';
import {AuthenticationService} from '../_services/authentication.service';
import {TokenService} from '../_services/token.service';
import {Message} from '../_models/message.model';
import {HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
class MockService {

  constructor(private http: HttpClient) {
  }

  getRequest(url: string): Observable<any> {
    return this.http.get(url);
  }
}

describe('AuthInterceptor', () => {

  let authService: AuthenticationService;
  let tokenService: TokenService;
  let dataService: MockService;

  let httpMock: HttpTestingController;

  const mockMessage = {
    message: 'Test'
  } as Message;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthenticationService,
        {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
      ]
    });

    const injector = getTestBed();

    authService = injector.get(AuthenticationService);
    tokenService = injector.get(TokenService);
    dataService = injector.get(MockService);

    httpMock = injector.get(HttpTestingController);
  });

  it('should create', () => {
    expect(authService).toBeTruthy();
  });

  it('should refresh the token', () => {
    spyOn(authService, 'isLoggedIn').and.returnValue(true);
    spyOn(tokenService, 'getAccessToken').and.returnValue('expired_access_token');
    // Attempt a request
    dataService.getRequest('/api/test').subscribe();

    // HTTP Request to get a resource requiring authentication
    const initialRequest = httpMock.expectOne('/api/test');
    expect(initialRequest.request.method).toBe('GET');
    const initialAuthHeader = initialRequest.request.headers.get('Authorization');
    expect(initialAuthHeader).toBe('Bearer expired_access_token');
    initialRequest.flush(null, getUnauthorizedResponse());

    // HTTP Request to refresh tokens
    const refreshRequest = httpMock.expectOne('/api/auth/refresh');
    expect(refreshRequest.request.method).toBe('GET');
    refreshRequest.flush(mockMessage);

    // Retried HTTP request with original endpoint.
    const repeatedRequest = httpMock.expectOne('/api/test');
    expect(repeatedRequest.request.method).toBe('GET');
  });

});

function getUnauthorizedResponse() {
  return {status: 401, statusText: 'You don\'t have permission for this resource.'};
}
