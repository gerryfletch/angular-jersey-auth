import {TokenService} from './token.service';
import Spy = jasmine.Spy;

fdescribe('TokenService', () => {

  let localStorageGetSpy: Spy;
  let localStoragePutSpy: Spy;
  let service: TokenService;

  beforeEach(() => {
    service = new TokenService();
    localStorageGetSpy = spyOn(localStorage, 'getItem');
    localStoragePutSpy = spyOn(localStorage, 'setItem');
  });

  describe('IsLoggedIn', () => {
    it('returns not logged in if no tokens exist', () => {
      localStorageGetSpy.and.returnValue(null);

      expect (service.isLoggedIn()).toBeFalsy();
    });

    it('returns logged in if tokens exist', () => {
      localStorageGetSpy.and.returnValue({});

      expect(service.isLoggedIn()).toBeTruthy();
    });
  });

});
