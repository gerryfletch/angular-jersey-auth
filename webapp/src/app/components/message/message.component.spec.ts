import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MessageComponent} from './message.component';
import {MessageService} from '../../_services/message.service';
import {Observable, of} from 'rxjs';
import {Message} from '../../_models/message.model';
import {By} from '@angular/platform-browser';

class MessageServiceStub {
  getMessage(): Observable<Message> {
    return of({'message': 'message'} as Message);
  }
}

describe('MessageComponent', () => {
  let component: MessageComponent;
  let fixture: ComponentFixture<MessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MessageComponent],
      providers: [
        {provide: MessageService, useClass: MessageServiceStub}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display a message', () => {
    const de = fixture.debugElement.query(By.css('h2'));
    const el: HTMLElement = de.nativeElement;

    expect(el.innerText).toContain('message');
  });

});
