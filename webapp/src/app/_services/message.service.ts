import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/index';
import {Message} from '../_models/message.model';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http: HttpClient) { }

  // getMessage(): Observable<Message> {
  //
  // }

}
