import { Component, OnInit, OnDestroy, AfterContentChecked } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AuthService } from '../shared/services/auth.service';
import {Router} from "@angular/router";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy, AfterContentChecked {

    status: boolean;
    subscription: Subscription;
    role: string;

    constructor(private userService: AuthService, private route: Router) {
    }

    logout() {
        this.userService.logout();
    }

    ngOnInit() {
    }

    ngAfterContentChecked() {
        this.role = localStorage.getItem('auth_role');
        this.subscription = this.userService.authNavStatus$.subscribe(status => this.status = status);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    goToMain() {
        if (this.role == 'ROLE_ADMINISTRADOR') {
            this.route.navigate(['/admin/main'])
        } else if (this.role == 'ROLE_COMMUNITY_MANAGER') {
            this.route.navigate(['/cm'])
        } else {
            this.route.navigate(['/'])
        }
    }
}
