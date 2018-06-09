import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  message: string;

  constructor() { }

  ngOnInit() {
     this.message = 'A temporary message. This one is pretty long!';
  }

}
