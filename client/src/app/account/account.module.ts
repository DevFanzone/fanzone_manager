import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../shared/modules/shared.module';
import {AuthService} from '../shared/services/auth.service';
import {EmailValidator} from '../shared/directives/email.validator.directive';
import {LoginFormComponent} from './login-form/login-form.component';
import {routing} from './account.routing';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

@NgModule({
    imports: [CommonModule, FormsModule, routing, SharedModule, NgbModule],
    declarations: [EmailValidator, LoginFormComponent],
    providers: [AuthService]
})
export class AccountModule {
}
