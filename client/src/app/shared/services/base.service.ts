import {Observable} from 'rxjs/Rx';
import {TemplateRef} from "@angular/core";


export abstract class BaseService {
    constructor() {
    }

    protected handleError(error: any) {
        var applicationError = error.headers.get('Application-Error');

        if (applicationError) {
            return Observable.throw(applicationError);
        }

        var modelStateErrors: string = '';
        var serverError = error.json();

        if (!serverError.type) {
            for (var key in serverError) {
                if (serverError[key])
                    modelStateErrors += serverError[key] + '\n';
            }
        }

        modelStateErrors = modelStateErrors = '' ? null : modelStateErrors;
        return Observable.throw(modelStateErrors || 'Server error');
    }
}