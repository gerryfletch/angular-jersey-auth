import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private REFRESH_KEY = 'refresh_token';
  private ACCESS_KEY =  'access_token';

  private jwtHelper: JwtHelperService;

  constructor() {
    this.jwtHelper = new JwtHelperService();
  }

  isLoggedIn(): boolean {
    return localStorage.getItem(this.REFRESH_KEY) !== null;
  }

  isExpired(): boolean {
    return this.jwtHelper.isTokenExpired(this.getAccessToken());
  }

  getRefreshToken(): string {
    return localStorage.getItem(this.REFRESH_KEY);
  }

  getAccessToken(): string {
    return localStorage.getItem(this.ACCESS_KEY);
  }

  setTokens(refreshToken: string, accessToken: string) {
    localStorage.setItem(this.REFRESH_KEY, refreshToken);
    localStorage.setItem(this.ACCESS_KEY, accessToken);
  }

}
