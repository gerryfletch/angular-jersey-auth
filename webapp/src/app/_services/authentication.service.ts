import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs/index';
import {Tokens} from '../_models/tokens.model';
import {TokenService} from './token.service';
import {catchError, tap} from 'rxjs/internal/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private authReqHandler: Observable<Tokens>;

  constructor(private http: HttpClient, private tokenService: TokenService) {
    this.authReqHandler = null;
  }

  isLoggedIn(): boolean {
    return this.tokenService.areTokensSet();
  }

  /**
   * Construct an encoded form and post it to the backend auth.
   */
  login(username: string, password: string): Observable<Tokens> {
    const body = new HttpParams()
      .set('username', username)
      .set('password', password);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/x-www-form-urlencoded'
      })
    };

    return this.http.post<Tokens>('/api/auth/login', body.toString(), httpOptions)
      .pipe(
        tap((tokens: Tokens) => {
          this.tokenService.setTokens(tokens);
        }),
        catchError(this.handleLoginError)
      );
  }

  private handleLoginError(error: HttpErrorResponse) {
    return throwError(error.error || 'There\'s something wrong with our servers.');
  }

  logout() {
    this.tokenService.clearTokens();
  }

  /**
   * Makes a white-listed request to the refresh end point with the refresh token.
   * A new refresh-access token pair is returned, or an error is thrown.
   *
   * To prevent multiple concurrent requests to refresh tokens, we store an observable
   * of tokens, 'authReqHandler'. The initial http request is assigned to this observable,
   * and it is returned to any subsequent requests.
   * @returns {Observable<Tokens>}  A new valid refresh-access token pair.
   */
  refreshTokens(): Observable<Tokens> {
    if (!! this.authReqHandler) {
      return this.authReqHandler;
    }

    const header = 'Bearer ' + this.tokenService.getRefreshToken();

    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': header
      })
    };

    this.authReqHandler = this.http.get<Tokens>('/api/auth/refresh', httpOptions)
      .pipe(
        tap((tokens: Tokens) => {
          this.tokenService.setTokens(tokens);
        })
      );

    return this.authReqHandler;
  }

}
