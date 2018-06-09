import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs/index';
import {Tokens} from '../_models/tokens.model';
import {TokenService} from './token.service';
import {catchError, map} from 'rxjs/internal/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private http: HttpClient, private tokenService: TokenService) {
  }

  isLoggedIn(): boolean {
    return this.tokenService.isLoggedIn();
  }

  login(username: string, password: string): Observable<void> {
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
        map(tokens => {
          this.tokenService.setTokens(tokens.refresh_token, tokens.access_token);
        }),
        catchError(this.handleLoginError)
      );
  }

  handleLoginError(error: HttpErrorResponse) {
    return throwError(error.error || 'There\'s something wrong with our servers.');
  }

}
