import {Injectable} from '@angular/core';
import {Tokens} from '../_models/tokens.model';
import {Token} from '../_models/token.model';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private REFRESH_KEY = 'refresh_token';
  private ACCESS_KEY = 'access_token';

  constructor() {
  }

  areTokensSet(): boolean {
    return localStorage.getItem(this.REFRESH_KEY) !== null;
  }

  getRefreshToken(): string {
    return localStorage.getItem(this.REFRESH_KEY);
  }

  getAccessToken(): string {
    return localStorage.getItem(this.ACCESS_KEY);
  }

  setAccessToken(token: Token): void {
    localStorage.setItem(this.ACCESS_KEY, token.token);
  }

  setRefreshToken(token: Token): void {
    localStorage.setItem(this.REFRESH_KEY, token.token);
  }

  setTokens(token: Tokens): void {
    localStorage.setItem(this.REFRESH_KEY, token.refresh_token);
    localStorage.setItem(this.ACCESS_KEY, token.access_token);
  }


  clearTokens() {
    localStorage.clear();
  }

}
