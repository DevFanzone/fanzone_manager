import {ModuleWithProviders} from '@angular/core';
import {RouterModule} from '@angular/router';
import {MainComponent} from "./main/main.component";
import {AuthGuard} from "../shared/auth/auth.guard";
import {PlayersComponent} from "../players/players.component";
import {CommunityManagerComponent} from './community-manager/community-manager.component';
import {UserComponent} from './user/user.component';
import {RootComponent} from "./management/root/root.component";
import {CategoriesComponent} from "./management/categories/categories.component";
import {SlidersComponent} from "./management/sliders/sliders.component";

export const routing: ModuleWithProviders = RouterModule.forChild([
    {
        path: 'admin', canActivate: [AuthGuard],
        children: [
            {path: '', redirectTo: 'main', pathMatch: 'full'},
            {path: 'main', component: MainComponent},
            {path: 'cm', component: CommunityManagerComponent},
            {path: 'players', component: PlayersComponent},
            {path: 'users', component: UserComponent},
            {
                path: 'player', component: RootComponent,
                children: [
                    {path: '', redirectTo: 'categories', pathMatch: 'full'},
                    {path: 'categories', component: CategoriesComponent},
                    {path: 'sliders', component: SlidersComponent}
                ]
            }
        ]
    }
]);


