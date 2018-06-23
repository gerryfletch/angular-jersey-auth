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
  const testApiEndpoint = '/api/test';
  const refreshApiEndpoint = '/api/auth/refresh';

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

  it('should append the authorization header for a logged in user', () => {
    const token = 'token';
    spyOn(tokenService, 'getAccessToken').and.returnValue(token);
    spyOn(authService, 'isLoggedIn').and.returnValue(true);

    dataService.getRequest(testApiEndpoint).subscribe();

    const req = httpMock.expectOne(testApiEndpoint);
    expect(req.request.headers.get('Authorization')).toContain(token);
  });

  /**
   * After a failed request with a 401 response, for a logged in user, the interceptor should make a request
   * to refresh. On success, it should then retry the original request.
   */
  it('should refresh the token', () => {
    spyOn(authService, 'isLoggedIn').and.returnValue(true);
    spyOn(tokenService, 'getAccessToken').and.returnValue('expired_access_token');

    dataService.getRequest(testApiEndpoint).subscribe(); // Attempt a request

    // HTTP Request to get a resource requiring authentication
    const initialRequest = httpMock.expectOne(testApiEndpoint);
    expect(initialRequest.request.method).toBe('GET');
    const initialAuthHeader = initialRequest.request.headers.get('Authorization');
    expect(initialAuthHeader).toBe('Bearer expired_access_token');
    initialRequest.flush(null, getUnauthorizedResponse());

    // HTTP Request to refresh tokens
    const refreshRequest = httpMock.expectOne(refreshApiEndpoint);
    expect(refreshRequest.request.method).toBe('GET');
    refreshRequest.flush(mockMessage);

    // Retried HTTP request with original endpoint.
    const repeatedRequest = httpMock.expectOne(testApiEndpoint);
    expect(repeatedRequest.request.method).toBe('GET');
  });

});

function getUnauthorizedResponse() {
  return {status: 401, statusText: 'You don\'t have permission for this resource.'};
}
