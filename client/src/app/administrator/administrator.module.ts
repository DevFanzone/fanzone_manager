import {NgModule} from '@angular/core';
import {routing} from "./administrator.routing";
import {CommonModule} from '@angular/common';
import {MainComponent} from "./main/main.component";
import {CommunityManagerComponent} from './community-manager/community-manager.component';
import {UserComponent} from './user/user.component';
import {RootComponent} from './management/root/root.component';
import {MainPlayerComponent} from './management/main-player/main-player.component';
import {CategoriesComponent} from './management/categories/categories.component';
import {SlidersComponent} from './management/sliders/sliders.component';
import {SharedModule} from "../shared/modules/shared.module";
import {FormsModule} from "@angular/forms";
import {Ng2SearchPipeModule} from "ng2-search-filter";
import {Ng2OrderModule} from "ng2-order-pipe";
import {NgxPaginationModule} from "ngx-pagination";
import {ModalModule} from "ng2-bs4-modal/lib/ng2-bs4-modal.module";
import {UserService} from "../shared/services/user.service";
import {CategoryService} from "../shared/services/category.service";
import {SliderService} from "../shared/services/slider.service";
import {PlayerService} from "../shared/services/player.service";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SharedModule,
        ModalModule,
        Ng2SearchPipeModule,
        Ng2OrderModule,
        NgxPaginationModule,
        routing
    ],
    declarations: [
        MainComponent,
        CommunityManagerComponent,
        UserComponent,
        RootComponent,
        MainPlayerComponent,
        CategoriesComponent,
        SlidersComponent
    ],
    providers: [UserService, CategoryService, SliderService, PlayerService]
})
export class AdministratorModule {
}
