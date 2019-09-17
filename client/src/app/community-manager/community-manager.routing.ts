import {ModuleWithProviders} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AuthGuard} from "../shared/auth/auth.guard";
import {PlayersComponent} from "../players/players.component";
import {PostComponent} from "./post/post.component";
import {CalendarPostComponent} from "./calendar-post/calendar-post.component";

export const routing: ModuleWithProviders = RouterModule.forChild([
    {
        path: 'cm', canActivate: [AuthGuard],
        children: [
            {path: '', redirectTo: 'players', pathMatch: 'full'},
            {path: 'players', component: PlayersComponent},
            {path: 'post', component: PostComponent},
            {path: 'calendar', component: CalendarPostComponent}
        ]
    }
]);


