import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {ConfigService} from '../utils/config.service';
import {BaseService} from './base.service';
import '../utils/rxjs-operators';

@Injectable()
export class PlayerService extends BaseService {
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

    getCMs() {
        let headers = this.headers;

        return this.http.post(this.baseUrl + "/managers", null, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    getAtletas(idManager) {
        let headers = this.headers;
        let datos = {usernameManager: idManager}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/atletasManager", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    guardarAtleta(modelo) {
        let headers = this.headers;
        let datos = {"modelo": modelo}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/atleta", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    updateStatus(idPlayer, status) {
        let headers = this.headers;
        let datos = {"idPlayer": idPlayer, "status": status}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/statusPlayer", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    deleteAtleta(idAtleta) {
        let headers = this.headers;
        let datos = {idAtleta: idAtleta}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/deleteAtleta", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }
}
