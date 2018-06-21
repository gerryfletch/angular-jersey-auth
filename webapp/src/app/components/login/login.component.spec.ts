import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginComponent} from './login.component';
import {AuthenticationService} from '../../_services/authentication.service';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {EMPTY, of, throwError} from 'rxjs';

class AuthenticationServiceStub {
  isLoggedIn() {
  }

  login() {
  }
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let isLoggedInSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [LoginComponent],
      providers: [
        {provide: AuthenticationService, useClass: AuthenticationServiceStub}
      ],
    })
      .compileComponents();

  }));

  it('should create', () => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('The user is not logged in', () => {

    /**
     * Set the service to return that the user is logged out.
     * Instantiate the isLoggedIn and login function spies.
     */
    beforeEach(() => {
      const service = TestBed.get(AuthenticationService);
      isLoggedInSpy = spyOn(service, 'isLoggedIn').and.returnValue(false);

      fixture = TestBed.createComponent(LoginComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should call the auth service on creation', () => {
      expect(isLoggedInSpy).toHaveBeenCalled();
    });

    it('should not be logged in', () => {
      expect(component.isLogged).toBeFalsy();
    });

    it('should display the login form', () => {
      const de: HTMLElement = fixture.debugElement.query(By.css('form')).nativeElement;

      expect(de).toBeTruthy();
    });

    it('should not have any errors', () => {
      expect(component.isError).toBeFalsy();
      expect(component.error).toBe('');
    });

    it('should not show any errors', () => {
      const de = fixture.debugElement.query(By.css('.error h2'));

      expect(de).toBeFalsy();
    });

    describe('Attempting a login', () => {

      let loginButton: DebugElement;
      let usernameInput: HTMLInputElement;
      let passwordInput: HTMLInputElement;

      beforeEach(() => {
        loginButton = fixture.debugElement.query(By.css('button'));
        usernameInput = fixture.debugElement.query(By.css('input[name="username"]')).nativeElement;
        passwordInput = fixture.debugElement.query(By.css('input[name="password"]')).nativeElement;
      });

      function getErrorDE(): DebugElement {
        return fixture.debugElement.query(By.css('.error h2'));
      }

      it('should call the login function on form submit', () => {
        spyOn(component, 'login');

        loginButton.triggerEventHandler('click', null);

        expect(component.login).toHaveBeenCalled();
      });

      it('should store the username and password inputs in the component', () => {
        const username = 'test';
        const password = 'testing';

        usernameInput.value = username;
        passwordInput.value = password;

        usernameInput.dispatchEvent(new Event('input'));
        passwordInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();

        expect(component.username).toEqual(username);
        expect(component.password).toEqual(password);
      });

      describe('With empty usernames and passwords', () => {
        it('should error if username and password are missing', () => {
          component.login();

          expect(component.isError).toBeTruthy();
          expect(component.error).toContain('Enter a username and password.');
        });

        it('should error if password is missing', () => {
          component.username = 'testing';

          component.login();

          expect(component.isError);
          expect(component.error).toContain('Enter a password.');
        });

        it('should error if username is missing', () => {
          component.password = 'testing';

          component.login();

          expect(component.isError);
          expect(component.error).toContain('Enter a username.');
        });

        it('should display an error if the username and password are missing', () => {
          loginButton.triggerEventHandler('click', null);
          fixture.detectChanges();

          const errorDE = getErrorDE();
          const errorEl: HTMLElement = errorDE.nativeElement;

          expect(errorDE).toBeTruthy();
          expect(errorEl.innerText).toContain('Enter a username and password.');
        });

        it('should display an error if the password is missing', () => {
          usernameInput.value = 'testing';
          usernameInput.dispatchEvent(new Event('input'));

          loginButton.triggerEventHandler('click', null);
          fixture.detectChanges();

          const errorDE = getErrorDE();
          const errorEl: HTMLElement = errorDE.nativeElement;

          expect(errorDE).toBeTruthy();
          expect(errorEl.innerText).toContain('Enter a password.');
        });

        it('should display an error if the username is missing', () => {
          passwordInput.value = 'testing';
          passwordInput.dispatchEvent(new Event('input'));

          loginButton.triggerEventHandler('click', null);
          fixture.detectChanges();

          const errorDE = getErrorDE();
          const errorEl: HTMLElement = errorDE.nativeElement;

          expect(errorDE).toBeTruthy();
          expect(errorEl.innerText).toContain('Enter a username.');
        });
      });

      describe('With correct credentials', () => {
        it('should have no errors', () => {
          spyOn(TestBed.get(AuthenticationService), 'login').and.returnValue(EMPTY);
          component.username = 'test';
          component.password = 'testing';

          component.login();
          fixture.detectChanges();

          expect(component.isError).toBeFalsy();
          expect(component.error).toBe('');
        });

        it('should have no errors after being in an error state', () => {
          spyOn(TestBed.get(AuthenticationService), 'login').and.returnValue(of({}));
          component.isError = true;
          component.error = 'Test error.';
          component.username = 'test';
          component.password = 'testing';

          component.login();

          expect(component.isError).toBeFalsy();
          expect(component.error).toBe('');
        });

        it('should hide the form after a successful login', () => {
          spyOn(TestBed.get(AuthenticationService), 'login').and.returnValue(of({}));
          usernameInput.value = 'test';
          passwordInput.value = 'testing';

          usernameInput.dispatchEvent(new Event('input'));
          passwordInput.dispatchEvent(new Event('input'));
          loginButton.triggerEventHandler('click', null);
          fixture.detectChanges();

          const form = fixture.debugElement.query(By.css('form'));
          expect(form).toBeFalsy();
        });
      });

      describe('With incorrect credentials', () => {
        // Unit
        it('should error', () => {
          const error = 'Bad username or password.';
          spyOn(TestBed.get(AuthenticationService), 'login').and.returnValue(throwError(error));
          component.username = 'test';
          component.password = 'testing';

          component.login();

          expect(component.isError).toBeTruthy();
          expect(component.error).toContain(error);
        });
        // Integration
        it('should show an error', () => {
          const error = 'Bad username or password.';
          spyOn(TestBed.get(AuthenticationService), 'login').and.returnValue(throwError(error));
          usernameInput.value = 'test';
          passwordInput.value = 'testing';

          usernameInput.dispatchEvent(new Event('input'));
          passwordInput.dispatchEvent(new Event('input'));
          loginButton.triggerEventHandler('click', null);
          fixture.detectChanges();

          const errorDE = getErrorDE();
          const errorEl: HTMLElement = errorDE.nativeElement;

          expect(errorDE).toBeTruthy();
          expect(errorEl.innerText).toContain(error);
        });
      });

    });

  });

  describe('The user is logged in', () => {

    // Set the user to logged in
    beforeEach(() => {
      const service = TestBed.get(AuthenticationService);
      isLoggedInSpy = spyOn(service, 'isLoggedIn').and.returnValue(true);

      fixture = TestBed.createComponent(LoginComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should call the auth service on creation', () => {
      expect(isLoggedInSpy).toHaveBeenCalled();
    });

    it('should be logged in', () => {
      expect(component.isLogged).toBeTruthy();
    });

    it('should not display the login form', () => {
      const form = fixture.debugElement.query(By.css('form'));

      expect(form).toBeFalsy();
    });

  });

});
