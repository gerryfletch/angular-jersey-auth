import {Injectable} from '@angular/core';
import {Tokens} from '../_models/tokens.model';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private REFRESH_KEY = 'refresh_token';
  private ACCESS_KEY = 'access_token';

  constructor() {
  }

  isLoggedIn(): boolean {
    return localStorage.getItem(this.REFRESH_KEY) !== null;
  }

  getRefreshToken(): string {
    return localStorage.getItem(this.REFRESH_KEY);
  }

  getAccessToken(): string {
    return localStorage.getItem(this.ACCESS_KEY);
  }

  setTokens(token: Tokens) {
    localStorage.setItem(this.REFRESH_KEY, token.refresh_token);
    localStorage.setItem(this.ACCESS_KEY, token.access_token);
  }


  clearTokens() {
    localStorage.clear();
  }

}
