import {Component, OnInit} from '@angular/core';
import {MessageService} from '../../_services/message.service';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  message: string;

  constructor(private messageService: MessageService) {
  }

  ngOnInit() {
    this.messageService.getMessage()
      .subscribe(
        data => {
          this.message = data.message;
        }
      );
  }

}
