import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {myFocus} from '../directives/focus.directive';
import {SpinnerComponent} from '../spinner/spinner.component';
import {LoaderComponent} from '../loader/loader.component';

@NgModule({
    imports: [CommonModule],
    declarations: [myFocus, SpinnerComponent, LoaderComponent],
    exports: [myFocus, SpinnerComponent, LoaderComponent],
    providers: []
})
export class SharedModule {
}
