export interface Categoria {

    id: string;
    fechaRegistro: string;
    nombre: string;
    descripcion: string;
    imagen:string;
    imagenBytes:string;
    imagenType:string;
    estatus:string;

    imgSplash:string;
    imgSplashBytes:string;
    imgSplashType:string;
    lastUpdateSplash: any;

    idAtleta:string;
    nombreAtleta:string;
    idManager:string;
    nombreManager:string;
    username:string;

    errorAPI?: boolean;
    mensajeAPI?: string;
}
