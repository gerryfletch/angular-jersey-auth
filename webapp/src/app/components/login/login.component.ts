import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from '../../_services/authentication.service';

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
  }

  ngOnInit() {
    this.isLogged = this.authService.isLoggedIn();
  }

  login() {
    if (!this.username || !this.password) {
      this.isError = true;
      this.error = 'Enter a username and password.';
      return;
    }

    this.authService.login(this.username, this.password)
      .subscribe(
        _ => {
          this.isError = false;
          this.error = '';
          this.isLogged = true;
        },
        error => {
          this.isError = true;
          this.error = error;
        }
      );
  }

}
