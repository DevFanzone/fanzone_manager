import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule, XHRBackend} from '@angular/http';
import {AuthenticateXHRBackend} from './shared/auth/authenticate-xhr.backend';
import {routing} from './app.routing';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {AccountModule} from './account/account.module';
import {ConfigService} from './shared/utils/config.service';
import {AdministratorModule} from "./administrator/administrator.module";
import {PlayersComponent} from "./players/players.component";
import {ModalModule} from 'ng2-bs4-modal/lib/ng2-bs4-modal.module';
import {SharedModule} from "./shared/modules/shared.module";
import {Ng2SearchPipeModule} from "ng2-search-filter";
import {Ng2OrderModule} from "ng2-order-pipe";
import {NgxPaginationModule} from "ngx-pagination";
import {CommunityManagerModule} from "./community-manager/community-manager.module";
import {AuthGuard} from "./shared/auth/auth.guard";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ToastyModule} from "ng2-toasty";
import {NotifyService} from "./shared/services/notify.service";

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        PlayersComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        ModalModule,
        SharedModule,
        Ng2SearchPipeModule,
        Ng2OrderModule,
        NgxPaginationModule,
        NgbModule.forRoot(),
        ToastyModule.forRoot(),

        AccountModule,
        AdministratorModule,
        CommunityManagerModule,
        routing
    ],
    providers: [ConfigService,
        {
            provide: XHRBackend,
            useClass: AuthenticateXHRBackend
        },
        AuthGuard,
        NotifyService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
