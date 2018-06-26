import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';

import {Observable, throwError} from 'rxjs';
import {TokenService} from '../_services/token.service';
import {AuthenticationService} from '../_services/authentication.service';
import {catchError, switchMap} from 'rxjs/internal/operators';
import {AppSettings} from '../AppSettings';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private WHITELIST: Set<string>;

  constructor(private tokenService: TokenService, private authService: AuthenticationService) {
    this.WHITELIST = AppSettings.HTTP_WHITELIST;
  }

  /**
   * @returns {HttpRequest<any>} A cloned http request with the authorization appended.
   */
  createAuthRequest(req: HttpRequest<any>): HttpRequest<any> {
    return req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.tokenService.getAccessToken())
    });
  }

  /**
   * Intercept different life cycles of a http request.
   * Authentication is added to all requests unless they belong to the AppSettings whitelist.
   * If a 401 unauthorized response is returned, the auth service attempts to refresh the token.
   * On success, the request will be resent and its response returned. On failure, the user will be logged out
   * and an error returned.
   * @param {HttpRequest<any>} req The http request to process.
   * @param {HttpHandler} next The next handler in the interceptor barrel.
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.WHITELIST.has(req.url)) {
      return next.handle(req);
    }

    let authReq: HttpRequest<any>;

    if (this.authService.isLoggedIn()) {
      authReq = this.createAuthRequest(req);
    } else {
      authReq = req;
    }

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        // Check if the response is unauthorized.
        if (err.status !== 401) {
          return throwError(err);
        }

        if (!this.authService.isLoggedIn()) {
          return throwError(err);
        }

        // New tokens are required. Refresh and get the authenticated response or error.
        return this.refreshAndRetry(req, next);
      })
    );
  }

  /**
   * Attempt to refresh the access token. If authentication is successful, repeat the failed request and return
   * its observable. Otherwise, log the user out and throw the error response.
   * @param req The original (failed) request to be retried after authentication.
   * @param next The next http handler in the interceptor barrel.
   * @returns {Observable<any>} Either the http response or an error.
   */
  private refreshAndRetry(req, next): Observable<any> {
    return this.authService.refreshAccessToken().pipe(
      switchMap(() => next.handle(this.createAuthRequest(req))),
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401) {
          this.authService.logout();
        }

        return throwError(err);
      })
    );
  }

}
