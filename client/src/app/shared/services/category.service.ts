import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {ConfigService} from '../utils/config.service';
import {BaseService} from './base.service';
import '../utils/rxjs-operators';

@Injectable()
export class CategoryService extends BaseService {

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

    getCategorias(idPlayer) {
        let headers = this.headers;
        let datos = {idPlayer: idPlayer}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/categorias", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    guardarCategoria(modelo) {
        let headers = this.headers;
        let datos = {"modelo": modelo}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/categoria", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    desactivarCategoria(idCategoria) {
        let headers = this.headers;
        let datos = {idCategoria: idCategoria, estatus: "1"}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/changueStatusCategory", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    activateCategoria(idCategoria) {
        let headers = this.headers;
        let datos = {idCategoria: idCategoria, estatus: "0"}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/changueStatusCategory", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    deleteCategoria(idCategoria) {
        let headers = this.headers;
        let datos = {idCategoria: idCategoria}
        let params = JSON.stringify(datos);

        return this.http.post(this.baseUrl + "/deleteCategoria", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

}
