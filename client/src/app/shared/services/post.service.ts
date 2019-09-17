import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import { ConfigService } from '../utils/config.service';
import { BaseService } from './base.service';
import '../utils/rxjs-operators';

@Injectable()
export class PostService extends BaseService {

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

  getCategorias(idAtleta) {
    let headers = this.headers;
    let datos = {idAtleta:idAtleta}
    let params = JSON.stringify(datos);

    return this.http.post(this.baseUrl + "/categoriasAtleta", params, { headers })
        .map(response => response.json()).catch(this.handleError);
  }

  getPosts(idPlayer) {
    let headers = this.headers;
    let datos = {idPlayer: idPlayer}
    let params = JSON.stringify(datos);

    return this.http.post(this.baseUrl + "/redacciones", params, {headers})
        .map(response => response.json()).catch(this.handleError);
  }

  getPost(idRedaccion) {
    let headers = this.headers;
    let datos = {id:idRedaccion}

    let params = JSON.stringify(datos);
    return this.http.post(this.baseUrl + "/redaccion", params, { headers })
        .map(response => response.json()).catch(this.handleError);
  }

  enviarPost(totalElementos,modelo, redes,totalRedes,elementos) {
    let headers = this.headers;
    let datos = {"totalElementos":totalElementos, "modelo" : modelo, "redes" : redes, "totalRedes" : totalRedes, "elementos": elementos}
    let params = JSON.stringify(datos);

    return this.http.post(this.baseUrl +"/enviarRedaccion", params, { headers })
        .map(response => response.json()).catch(this.handleError);
  }

  updateStatusPost(idPost, status) {
    let headers = this.headers;
    let datos = {idPost: idPost, status: status}
    let params = JSON.stringify(datos);

    return this.http.post(this.baseUrl + "/changueStatus", params, {headers})
        .map(response => response.json()).catch(this.handleError);
  }

  updateDatePost(idPost, dateTime) {
    let headers = this.headers;
    let datos = {idPost: idPost, fechaPublicacion: dateTime}
    let params = JSON.stringify(datos);

    return this.http.post(this.baseUrl + "/changueDatePost", params, {headers})
        .map(response => response.json()).catch(this.handleError);
  }

  deletePost(idPost) {
    let headers = this.headers;
    let datos = {idPost: idPost}
    let params = JSON.stringify(datos);

    return this.http.post(this.baseUrl + "/deletePost", params, {headers})
        .map(response => response.json()).catch(this.handleError);
  }
}
