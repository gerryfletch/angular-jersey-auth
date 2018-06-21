import {AuthenticationService} from './authentication.service';
import {getTestBed, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {Tokens} from '../_models/tokens.model';
import {TokenService} from './token.service';

describe('AuthenticationService', () => {

  let injector: TestBed;
  let service: AuthenticationService;
  let httpMock: HttpTestingController;

  const mockTokens = {
    refresh_token: 'xxx.yyy.zzz',
    access_token: 'xxx.yyy.zzz'
  } as Tokens;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthenticationService]
    });

    injector = getTestBed();
    service = injector.get(AuthenticationService);
    httpMock = injector.get(HttpTestingController);
  });

  // Make sure that there are no outstanding requests
  afterEach(() => {
    httpMock.verify();
  });

  describe('Logging in', () => {

    const loginApi = '/api/auth/login';

    const username = 'test';
    const password = 'test';

    it('should return tokens with valid credentials', () => {
      service.login(username, password).subscribe((data: Tokens) => {
        expect(data).toBe(mockTokens);
      });

      // Mock successful HTTP request
      const req = httpMock.expectOne(loginApi);
      expect(req.request.method).toBe('POST');
      req.flush(mockTokens);
    });

    it('should store the tokens on valid login', () => {
      const tokenService = injector.get(TokenService);
      const tokenSpy = spyOn(tokenService, 'setTokens');

      service.login(username, password).subscribe(_ => {
        expect(tokenSpy).toHaveBeenCalled();
      });

      // Mock successful HTTP request
      const req = httpMock.expectOne(loginApi);
      expect(req.request.method).toBe('POST');
      req.flush(mockTokens);
    });

    it('should throw an error with bad credentials', () => {
      const error = 'Invalid username or password.';
      service.login(username, password).subscribe(() => {}, err => {
        expect(err).toBe(error);
      });

      const req = httpMock.expectOne(loginApi);
      expect(req.request.method).toBe('POST');
      const mockErrorResponse = {
        status: 400, statusText: 'You aren\'t authorized to access this resource.'
      };

      req.flush(error, mockErrorResponse);
    });

    it('should give a friendly error if none is provided', () => {
      service.login(username, password).subscribe(() => {}, err => {
        expect(err).toContain('There\'s something wrong with our servers.');
      });

      const req = httpMock.expectOne(loginApi);
      expect(req.request.method).toBe('POST');
      const mockErrorResponse = {
        status: 500, statusText: 'Internal server error.'
      };

      req.flush(null, mockErrorResponse);
    });

  });



});
