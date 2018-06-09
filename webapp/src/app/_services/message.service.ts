import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/index';
import {Message} from '../_models/message.model';
import {HttpClient} from '@angular/common/http';
import {AuthenticationService} from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http: HttpClient, private authService: AuthenticationService) { }

  getMessage(): Observable<Message> {
    if (this.authService.isLoggedIn()) {
      return this.http.get<Message>('/api/message/private');
    } else {
      return this.http.get<Message>('/api/message/public');
    }
  }

}
