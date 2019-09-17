import {Subscription} from 'rxjs';
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Credentials} from '../../shared/models/credentials.interface';
import {AuthService} from '../../shared/services/auth.service';
import {NotifyService} from "../../shared/services/notify.service";
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-login-form',
    templateUrl: './login-form.component.html',
    styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit, OnDestroy {
    @ViewChild('f') f: FormGroup;

    private subscription: Subscription;
    errors: string;
    isRequesting: boolean;
    submitted: boolean = false;
    credentials: Credentials = {username: '', password: ''};
    role: string;

    constructor(private toasty: NotifyService, private authService: AuthService, private router: Router, private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.subscription = this.activatedRoute.queryParams.subscribe(
            (param: any) => {
                this.credentials.username = param['username'];
            });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    login({value, valid}: { value: Credentials, valid: boolean }) {
        if (valid) {
            this.submitted = true;
            this.isRequesting = true;
            this.errors = '';
            this.authService.login(value.username, value.password)
                .finally(() => this.isRequesting = false)
                .subscribe(
                    result => {
                        if (result) {
                            this.toasty.addToast("", "Welcome " + value.username, "default");
                            this.role = localStorage.getItem('auth_role');
                            if (this.role === 'ROLE_SUPER_ADMINISTRADOR') {

                            }
                            if (this.role === 'ROLE_ADMINISTRADOR') {
                                this.router.navigate(['/admin/main']);
                            }
                            if (this.role === 'ROLE_COMMUNITY_MANAGER') {
                                this.router.navigate(['/cm']);
                            }
                        }
                    },
                    error => {
                        this.errors = error
                        this.toasty.addToast("", "Unauthorized access. ", "error");
                        this.f.reset()
                    });
        } else {
            this.toasty.addToast("", "Some required fields are missing. ", "error");
            for (let i in this.f.controls)
                this.f.controls[i].markAsTouched();
        }
    }
}
