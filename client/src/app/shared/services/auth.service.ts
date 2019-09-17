import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import { ConfigService } from '../utils/config.service';
import {BaseService} from "./base.service";
import { BehaviorSubject } from 'rxjs/Rx';
import '../utils/rxjs-operators';
import {Router} from "@angular/router";

@Injectable()

export class AuthService extends BaseService {
  baseUrl: string = '';

  private _authNavStatusSource = new BehaviorSubject<boolean>(false);
  private _authNavRoleSource = new BehaviorSubject<string>("");
  authNavStatus$ = this._authNavStatusSource.asObservable();

  private loggedIn = false;

  constructor(private http: Http, private configService: ConfigService, private route:Router) {
    super();
    this.loggedIn = !!localStorage.getItem('auth_token');
    this._authNavStatusSource.next(this.loggedIn);
    this._authNavRoleSource.next("");
    this.baseUrl = configService.getApiURI();
  }

  login(username, password) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    let url = this.baseUrl + '/login';
    let params = JSON.stringify({ username, password });
    return this.http
        .post(url, params, { headers })
        .map(res => res.json())
        .map(res => {
          localStorage.setItem('auth_token', res.access_token);
          localStorage.setItem('auth_role', res.roles[0]);
          localStorage.setItem('username', res.username);
          this.loggedIn = true;
          this._authNavStatusSource.next(true);
          this._authNavRoleSource.next(res.roles[0]);
          return true;
        })
        .catch(this.handleError);
  }

  logout() {
    localStorage.clear()
    this.loggedIn = false;
    this._authNavStatusSource.next(false);
    this._authNavRoleSource.next("");
    this.route.navigate(['/login'])
  }

  isLoggedIn() {
    return this.loggedIn;
  }
}

