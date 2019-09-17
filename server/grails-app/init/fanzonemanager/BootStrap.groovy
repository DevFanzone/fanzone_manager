package fanzonemanager

import modelos.Usuario
import modelos.Configuracion
import seguridad.Role
import seguridad.User
import seguridad.UserRole

class BootStrap {

    def init = { servletContext ->

        /* CONFIGURACION DE USUARIOS Y ROLES DEFAULT */
        Role.findByAuthority("ROLE_SUPER_ADMINISTRADOR") ?: new Role(authority: 'ROLE_SUPER_ADMINISTRADOR').save()
        Role.findByAuthority("ROLE_ADMINISTRADOR") ?: new Role(authority: 'ROLE_ADMINISTRADOR').save()
        Role.findByAuthority("ROLE_COMMUNITY_MANAGER") ?: new Role(authority: 'ROLE_COMMUNITY_MANAGER').save()
        Role.findByAuthority("ROLE_WS") ?: new Role(authority: 'ROLE_WS').save()

        Role.findByAuthority("ROLE_SINGLE") ?: new Role(authority: 'ROLE_SINGLE').save()
        Role.findByAuthority("ROLE_DOUBLE") ?: new Role(authority: 'ROLE_DOUBLE').save()
        Role.findByAuthority("ROLE_TRIPLE") ?: new Role(authority: 'ROLE_TRIPLE').save()

        /* Creamos el administrador global para configurar el bucket y la ruta donde se van a guardar los archivos temporales*/
        Usuario.findByUsername("superAdmin") ?: new Usuario(username: 'superAdmin', password: 'admin', nombre: 'Nestor Alfredo', apellidos: 'Contreras Valencia', telefono:'7771087173').save()
        def roleUsuarioSuperAdmin = UserRole.findByRoleAndUser(
                Role.findByAuthority("ROLE_SUPER_ADMINISTRADOR"),
                Usuario.findByUsername("superAdmin"))
        if(!roleUsuarioSuperAdmin) {
            UserRole.create(Usuario.findByUsername("superAdmin"), Role.findByAuthority("ROLE_SUPER_ADMINISTRADOR"), true)

        }
       /* creamos el administrador*/
        Usuario.findByUsername("admin") ?: new Usuario(username: 'admin', password: 'admin', nombre: 'Nestor Alfredo', apellidos: 'Contreras Valencia', telefono:'7771087173').save()
        def roleUsuarioAdmin = UserRole.findByRoleAndUser(
                Role.findByAuthority("ROLE_ADMINISTRADOR"),
                Usuario.findByUsername("admin"))
        if(!roleUsuarioAdmin) {
            UserRole.create(Usuario.findByUsername("admin"), Role.findByAuthority("ROLE_ADMINISTRADOR"), true)

        }
        /* creamos el administrador*/
        Usuario.findByUsername("ws@ws.com") ?: new Usuario(username: 'ws@ws.com', password: 'admin', nombre: 'Nestor Alfredo', apellidos: 'Contreras Valencia', telefono:'7771087173').save()
        def roleUsuarioWs = UserRole.findByRoleAndUser(
                Role.findByAuthority("ROLE_WS"),
                Usuario.findByUsername("ws@ws.com"))
        if(!roleUsuarioWs) {
            UserRole.create(Usuario.findByUsername("ws@ws.com"), Role.findByAuthority("ROLE_WS"), true)

        }
       /* creamos registros en la tabla configuracion*/
        Configuracion.findByBucket("fanzone-test2") ?: new Configuracion(bucket: 'fanzone-test2',rutaTempArchivos: '/Users/Nestor-kynne/Desktop/grails_3_2_projects/fanzoneManager/client/src/images/archivosPublicaciones/',rutaMaps:'http://192.168.100.10:8888/ng-img-map-gh-pages/index.html?data=').save()

    }
    def destroy = {
    }
}
