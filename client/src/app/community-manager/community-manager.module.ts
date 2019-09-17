import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {routing} from "./community-manager.routing";
import {PostComponent} from './post/post.component';
import {FormsModule} from "@angular/forms";
import {SharedModule} from "../shared/modules/shared.module";
import {ModalModule} from "ng2-bs4-modal/lib/ng2-bs4-modal.module";
import {Ng2SearchPipeModule} from "ng2-search-filter";
import {Ng2OrderModule} from "ng2-order-pipe";
import {NgxPaginationModule} from "ngx-pagination";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FullCalendarModule} from "ng-fullcalendar";
import {MyDatePickerModule} from 'mydatepicker';
import {CalendarPostComponent} from './calendar-post/calendar-post.component';
import {PostService} from "../shared/services/post.service";
import {PlayerService} from "../shared/services/player.service";

@NgModule({
    imports: [
        CommonModule,
        MyDatePickerModule,
        FullCalendarModule,
        FormsModule,
        SharedModule,
        ModalModule,
        Ng2SearchPipeModule,
        Ng2OrderModule,
        NgxPaginationModule,
        NgbModule.forRoot(),
        routing
    ],
    declarations: [PostComponent, CalendarPostComponent],
    providers: [PostService, PlayerService]
})
export class CommunityManagerModule {
}
