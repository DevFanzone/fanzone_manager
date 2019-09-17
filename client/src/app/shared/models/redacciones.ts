export interface Redacciones {
     id: string;
     fechaRegistro: string;
     fechaPublicacion: string;
     tituloPost: string;
     descripcionPost: string;
     estatus: string;
     error: string;
     media: any[];
     idToken: string;
     tipoPost: string;
     tipoPostNombre: string;
     publicar: string;
     publicarNombre: string;

    idAtleta:string;
    nombreAtleta:string;

    idCategoria:string;
    nombreCategoria:string;

    idRed:string;
    nombreRed:string;
    tipoRed:string;

    idUsuario:string;
    nombreUsuario:string;

    numeroElementos:number;
    tags:string;

    errorAPI?: boolean;
    mensajeAPI?: string;
}

