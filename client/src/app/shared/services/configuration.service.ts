import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {ConfigService} from '../utils/config.service';
import {BaseService} from './base.service';
import '../utils/rxjs-operators';

@Injectable()
export class ConfigurationService extends BaseService {
    baseUrl: string = '';
    headers: Headers;

    constructor(private http: Http, private configService: ConfigService) {
        super();
        this.baseUrl = configService.getApiURI();

        this.headers = new Headers();
        this.headers.append('Content-Type', 'application/json');
        let authToken = localStorage.getItem('auth_token');
        this.headers.append('Authorization', `Bearer ${authToken}`);
    }

    getConfigurations(username) {
        let headers = this.headers;
        let datos = {usernameManager: username}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/configurations", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    saveConfigurations(modelo) {
        let headers = this.headers;
        let datos = {"modelo": modelo}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/configuration", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

}
