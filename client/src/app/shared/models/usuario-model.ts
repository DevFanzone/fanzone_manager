export interface UsuarioModel {
    id: string;
    username: string;
    password: string;      
    nombre: string;
    apellidos: string;
    telefono: string;
    status: any;
    role: number;
    roleNombre: string;
    errorAPI?: boolean;
    mensajeAPI?: string;
   
}
