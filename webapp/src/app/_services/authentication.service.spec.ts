import {AuthenticationService} from './authentication.service';
import {getTestBed, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {Tokens} from '../_models/tokens.model';
import {TokenService} from './token.service';
import {Observable, of, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';

describe('AuthenticationService', () => {

  let injector: TestBed;
  let service: AuthenticationService;
  let tokenService: TokenService;
  let httpMock: HttpTestingController;

  const username = 'test';
  const password = 'test';

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
    tokenService = injector.get(TokenService);
    httpMock = injector.get(HttpTestingController);
  });

  // Make sure that there are no outstanding requests
  afterEach(() => {
    httpMock.verify();
  });

  describe('Is the user logged in', () => {
    it('should be logged in', () => {
      spyOn(tokenService, 'isLoggedIn').and.returnValue(true);

      const isLoggedIn = service.isLoggedIn();

      expect(isLoggedIn).toBeTruthy();
    });

    it('should not be logged in', () => {
      spyOn(tokenService, 'isLoggedIn').and.returnValue(false);

      const isLoggedIn = service.isLoggedIn();

      expect(isLoggedIn).toBeFalsy();
    });
  });

  describe('Logging in', () => {

    const loginApi = '/api/auth/login';

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
      service.login(username, password).subscribe(() => {
      }, err => {
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
      service.login(username, password).subscribe(() => {
      }, err => {
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

  describe('Logging out', () => {
    it('should log out', () => {
      const tokenServiceSpy = spyOn(tokenService, 'clearTokens');

      service.logout();

      expect(tokenServiceSpy).toHaveBeenCalled();
    });
  });

  describe('Refreshing tokens', () => {

    const refreshApi = '/api/auth/refresh';

    describe('When a refresh is in progress', () => {
      /**
       * The first request should be the only one to send out a HTTP request.
       * All requests before the HTTP request is completed should return
       * the observable that is currently waiting on it.*
       */
      it('should return the observable instance', () => {
        const spy = spyOn(TestBed.get(HttpClient), 'get').and.callThrough();

        const firstRefresh = service.refreshTokens();
        firstRefresh.subscribe();
        httpMock.expectOne(refreshApi);

        const secondRefresh = service.refreshTokens();
        firstRefresh.subscribe();
        httpMock.expectOne(refreshApi);

        expect(spy).toHaveBeenCalledTimes(1);
        expect(secondRefresh).toBe(firstRefresh);
      });

      it('should store the retrieved tokens', () => {
        const tokenServiceSpy = spyOn(tokenService, 'setTokens').and.callFake(() => {});

        service.refreshTokens().subscribe(() => {
          expect(tokenServiceSpy).toHaveBeenCalledWith(mockTokens);
        });

        const req = httpMock.expectOne(refreshApi);
        expect(req.request.method).toBe('GET');
        req.flush(mockTokens);
      });

    });

    it('Should return new tokens', () => {

    });
  });


});