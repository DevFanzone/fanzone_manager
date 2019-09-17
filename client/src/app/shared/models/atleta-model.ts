export interface AtletaModel {
    id: string;
    nombre: string;
    sexo: string;
    telefono: string;
    email:string;
    sitioWeb:string;
    ocupacion:string;

    fechaRegistro:string;
    estatus:string;
    foto:string;
    fotoBytes:string;
    fotoType:string;
    manager:string;
    idManager:string;

    username:string;

    errorAPI?: boolean;
    mensajeAPI?: string;
   
}
