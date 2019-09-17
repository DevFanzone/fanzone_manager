export interface Slider {


    id:                    string;
    fechaRegistro:         string;
    nombre:                string;
    tipo:                  string;
    archivoNombreOriginal: string;
    urlArchivoOriginal:    string;
    estatus:               string;

    idManager: string;
    nombreManager: string;
    idAtleta: string;
    nombreAtleta: string;

    username:string;

    errorAPI?: boolean;
    mensajeAPI?: string;
}
