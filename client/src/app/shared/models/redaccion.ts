export interface Redaccion {

    id: string;
    fechaRegistro: string;
    idAtleta: string;
    idCategoria: string;
    tituloPost:string;
    descripcion: string;
    imagen: string;
    nombreImagen: string;
    sizeImagen: string;
    estatus: string;
    tipoPost:string;
    publicar:string;
    fechaPublicar:string;
    errorPostFacebook:boolean;
    errorPostTwitter:boolean;
    errorPostInstagram:boolean;

    accessTokenFacebook: string;
    expiresInFacebook: string;
    accessTokenTwitter: string;
    expiresInTwitter: string;
    accessTokenInstagram: string;
    expiresInInstagram: string;

    username:string;

    idRedes:string;
    descripcionElemento:string;

    idInsignia:string;
    linkInsignia:string;
    urlImageInsignia:string;

    errorAPI?: boolean;
    mensajeAPI?: string;
}
