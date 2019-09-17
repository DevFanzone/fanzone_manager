import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {ConfigService} from '../utils/config.service';
import {BaseService} from './base.service';
import {UsuarioModel} from '../models/usuario-model';
import {IRole} from '../models/irole';
import '../utils/rxjs-operators';

@Injectable()
export class UserService extends BaseService {
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

    getCms(): Observable<UsuarioModel> {
        let headers = this.headers;
        return this.http.get(this.baseUrl + "/cms", {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    getUsers(): Observable<UsuarioModel> {
        let headers = this.headers;
        return this.http.get(this.baseUrl + "/usuario", {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    getRoles(): Observable<IRole> {
        let headers = this.headers;
        return this.http.get(this.baseUrl + "/usuario/catalogoRoles", {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    saveUsuario(usuario: UsuarioModel): Observable<UsuarioModel> {
        let headers = this.headers;

        return this.http.post(this.baseUrl + "/usuario", JSON.stringify(usuario), {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    updateAccount(usuario: any): Observable<any> {
        let headers = this.headers;

        return this.http.post(this.baseUrl + "/updateAccount", JSON.stringify(usuario), {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    updateAccountUser(usuario: any): Observable<any> {
        let headers = this.headers;

        return this.http.post(this.baseUrl + "/updateuser", JSON.stringify(usuario), {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    updateStatus(idUser, status){
        let headers = this.headers;
        let datos = {"idUser": idUser, "status": status}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/usuario/delete", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }
}
