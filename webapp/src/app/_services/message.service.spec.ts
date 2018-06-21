import {getTestBed, TestBed} from '@angular/core/testing';

import {MessageService} from './message.service';
import {AuthenticationService} from './authentication.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

describe('MessageService', () => {

  let injector: TestBed;
  let service: MessageService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MessageService]
    });
    injector = getTestBed();
    service = injector.get(MessageService);
    httpMock = injector.get(HttpTestingController);
  });

  // Make sure that there are no outstanding requests
  afterEach(() => {
    httpMock.verify();
  });

  describe('When not logged in', () => {
    const message = 'public';

    beforeEach(() => {
      spyOn(injector.get(AuthenticationService), 'isLoggedIn').and.returnValue(false);
    });

    it('should return a public message', () => {
      service.getMessage().subscribe(data => {
        expect(data.message).toBe(message);
      });

      const req = httpMock.expectOne('/api/message/public');
      expect(req.request.method).toBe('GET');
      req.flush({'message': message});
    });
  });

  describe('When logged in', () => {
    const message = 'private';

    beforeEach(() => {
      spyOn(injector.get(AuthenticationService), 'isLoggedIn').and.returnValue(true);
    });

    it('should return a private message', () => {
      service.getMessage().subscribe(data => {
        expect(data.message).toBe(message);
      });

      const req = httpMock.expectOne('/api/message/private');
      expect(req.request.method).toBe('GET');
      req.flush({'message': message});
    });
  });

});
