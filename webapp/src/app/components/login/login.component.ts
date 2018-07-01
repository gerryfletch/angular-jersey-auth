import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from '../../_services/authentication.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  isLogged: boolean;

  username: string;
  password: string;

  isError: boolean;
  error: string;

  constructor(private authService: AuthenticationService) {
    this.error = '';
    this.username = '';
    this.password = '';
  }

  ngOnInit() {
    this.isLogged = this.authService.isLoggedIn();
  }

  login() {
    if (! this.username || ! this.password) {
      this.isError = true;
      if (!this.username && ! this.password) {
        this.error = 'Enter a username and password.';
      } else if (!this.username) {
        this.error = 'Enter a username.';
      } else {
        this.error = 'Enter a password.';
      }
      return;
    }

    this.authService.login(this.username, this.password)
      .subscribe(
        _ => {
          this.isError = false;
          this.error = '';
          this.isLogged = true;
        },
        (error: HttpErrorResponse) => {
          this.isError = true;
          this.error = error.message;
        }
      );
  }

}
