import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {By} from '@angular/platform-browser';
import {Component, NO_ERRORS_SCHEMA} from '@angular/core';

/**
 * Examples:
 * MockComponent({ selector: 'cranium' });
 * MockComponent({ selector: 'arm', inputs: ['side'] });
 */
export function MockComponent(selector: string): Component {
  const metadata: Component = {
    selector: selector,
    template: null,
    inputs: null,
    outputs: null
  };
  return new Component(metadata);
}
describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      schemas: [ NO_ERRORS_SCHEMA ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
  });

  it('should create the app', async(() => {
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

  it('should have a message component', () => {
    const messageDirective = fixture.debugElement.query(By.css('app-message'));

    expect(messageDirective).toBeTruthy();
  });

  it('should have a login component', () => {
    const loginDirective = fixture.debugElement.query(By.css('app-login'));

    expect(loginDirective).toBeTruthy();
  });
});
