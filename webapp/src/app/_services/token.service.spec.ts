import {TokenService} from './token.service';
import {Tokens} from '../_models/tokens.model';
import Spy = jasmine.Spy;

/**
 * I'm not sure of the best way to test the local storage getting and setting.
 */
fdescribe('TokenService', () => {

  let localStorageGetSpy: Spy;
  let localStorageSetSpy: Spy;
  let service: TokenService;

  const mockTokens = {
    refresh_token: 'xxx.yyy.zzz',
    access_token: 'aaa.bbb.ccc'
  } as Tokens;

  const REFRESH_KEY = 'refresh_token';
  const ACCESS_KEY = 'access_token';

  beforeEach(() => {
    service = new TokenService();
    localStorageGetSpy = spyOn(localStorage, 'getItem');
    localStorageSetSpy = spyOn(localStorage, 'setItem');
  });

  // Clear the local storage
  afterEach(() => {
    localStorage.clear();
  });

  describe('Checking if the user is logged in', () => {
    it('returns false if no tokens exist', () => {
      localStorageGetSpy.and.returnValue(null);

      const isLoggedIn = service.isLoggedIn();

      expect(isLoggedIn).toBeFalsy();
      expect(localStorageGetSpy).toHaveBeenCalledWith(REFRESH_KEY);
    });

    it('returns true if tokens exist', () => {
      localStorageGetSpy.and.returnValue(mockTokens.refresh_token);

      const isLoggedIn = service.isLoggedIn();

      expect(isLoggedIn).toBeTruthy();
      expect(localStorageGetSpy).toHaveBeenCalledWith(REFRESH_KEY);
    });
  });

  describe('Getting tokens', () => {

    it('should return a refresh token', () => {
      localStorageGetSpy.and.returnValue(mockTokens.refresh_token);

      const refreshToken = service.getRefreshToken();

      expect(localStorageGetSpy).toHaveBeenCalledWith(REFRESH_KEY);
      expect(refreshToken).toBe(mockTokens.refresh_token);
    });


    it('should return an access token', () => {
      localStorageGetSpy.and.returnValue(mockTokens.access_token);

      const accessToken = service.getAccessToken();

      expect(accessToken).toBe(mockTokens.access_token);
    });
  });

  describe('Setting tokens', () => {
    it('should call set item for both keys', () => {
      service.setTokens(mockTokens);

      expect(localStorageSetSpy).toHaveBeenCalledWith(REFRESH_KEY, mockTokens.refresh_token);
      expect(localStorageSetSpy).toHaveBeenCalledWith(ACCESS_KEY, mockTokens.access_token);
    });

  });

});
