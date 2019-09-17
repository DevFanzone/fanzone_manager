import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {ConfigService} from '../utils/config.service';
import {BaseService} from './base.service';
import '../utils/rxjs-operators';

@Injectable()
export class SliderService extends BaseService {

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

    getSliders(idPlayer) {
        let headers = this.headers;
        let datos = {idPlayer: idPlayer}
        let params = JSON.stringify(datos);
        return this.http.post(this.baseUrl + "/banners", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    deleteSlider(idSlider) {
        let headers = this.headers;
        let datos = {idSlider: idSlider}
        let params = JSON.stringify(datos);
        return this.http.post(this.baseUrl + "/deleteSlider", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    getTotalSlider(idAtleta) {
        let headers = this.headers;
        let datos = {idAtleta: idAtleta}
        let params = JSON.stringify(datos);
        return this.http.post(this.baseUrl + "/countsSliders", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }

    saveSlider(totalElementos, modelo, elementos) {
        let headers = this.headers;
        let datos = {"totalElementos": totalElementos, "modelo": modelo, "elementos": elementos}
        let params = JSON.stringify(datos);
        return this.http.post(this.baseUrl + "/saveSliders", params, {headers})
            .map(response => response.json()).catch(this.handleError);

    }

    updateSlider(idBanner, status) {
        let headers = this.headers;
        let datos = {"idBanner": idBanner, "status": status}
        let params = JSON.stringify(datos);
        return this.http.post(this.baseUrl + "/statusBanner", params, {headers})
            .map(response => response.json()).catch(this.handleError);
    }
}
