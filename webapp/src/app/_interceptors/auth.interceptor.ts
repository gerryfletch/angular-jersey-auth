import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';

import {EMPTY, Observable, Subject, throwError} from 'rxjs';
import {TokenService} from '../_services/token.service';
import {AuthenticationService} from '../_services/authentication.service';
import {catchError, map, mergeMap} from 'rxjs/internal/operators';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private requests$: Observable<HttpEvent<any>>;
  private requests: Subject<HttpEvent<any>>;

  constructor(private tokenService: TokenService, private authService: AuthenticationService) {
    this.requests = new Subject<HttpEvent<any>>();
    this.requests$ = this.requests.asObservable();
  }

  createAuthRequest(req: HttpRequest<any>): HttpRequest<any> {
    return req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.tokenService.getAccessToken())
    });
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('Attempting request: ');
    console.log(req);
    if (req.url === '/api/auth/refresh') {
      return next.handle(req);
    }

    // Add auth
    const authReq = this.createAuthRequest(req);

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        // attempt to refresh
        if (err.status === 401) {
          this.authService.refreshTokens()
            .subscribe(
              () => {
                // the tokens have been refreshed, reattempt request.
                console.log('The tokens have been refreshed. Retrying request: ');
                console.log(req);
                next.handle(req).subscribe(
                  res => {
                    this.requests.next(res);
                    this.requests.complete();
                  }
                );
              },
              (e) => {
                this.authService.logout();
                this.requests.next(e);
                this.requests.complete();
                // return EMPTY;
              }
            );
        }

        return this.requests;
      })
    );
  }

}
