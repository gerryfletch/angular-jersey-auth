import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';

import {EMPTY, Observable, throwError} from 'rxjs';
import {TokenService} from '../_services/token.service';
import {AuthenticationService} from '../_services/authentication.service';
import {catchError} from 'rxjs/internal/operators';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private isRefreshing;
  private cachedRequests: HttpRequest<any>[];

  constructor(private tokenService: TokenService, private authService: AuthenticationService) {
    this.isRefreshing = false;
    this.cachedRequests = [];
  }

  createAuthRequest(req: HttpRequest<any>): HttpRequest<any> {
    return req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.tokenService.getAccessToken())
    });
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url === '/api/auth/refresh') {
      return next.handle(req);
    }

    // Add auth
    const authReq = this.createAuthRequest(req);

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        // attempt to refresh
        if (err.status === 401) {
          this.cachedRequests.push(req);
          if (this.isRefreshing) {
            return;
          }

          this.isRefreshing = true;

          this.authService.refreshTokens()
            .subscribe(
              () => {
                // the tokens have been refreshed, reattempt request.
                for (const request of this.cachedRequests) {
                  next.handle(request);
                }

                this.isRefreshing = false;
                this.cachedRequests = [];
              },
              () => {
                this.authService.logout();
                return EMPTY;
              }
            );
        }

        return throwError('You don\'t have permission for this.');
      })
    );
  }

  // refreshToken() {
  //   if (this.refreshInProgress) {
  //     return new Observable(observer => {
  //       this.tokenRefreshed$.subscribe(() => {
  //         observer.next();
  //         observer.complete();
  //       });
  //     });
  //   } else {
  //     this.refreshInProgress = true;
  //
  //     return this.authService.refreshTokens().pipe(
  //       map(() => {
  //         this.refreshInProgress = false;
  //         this.tokenRefreshedSource.next();
  //       })
  //     );
  //   }
  // }

}
