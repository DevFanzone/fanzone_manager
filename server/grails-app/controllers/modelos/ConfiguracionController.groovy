package modelos

import grails.plugin.springsecurity.annotation.Secured
import grails.rest.*

@Secured(['ROLE_SUPER_ADMINISTRADOR'])
class ConfiguracionController extends RestfulController {

    ConfiguracionController() {
        super(Configuracion)
    }

    def listConfigurations() {
        def params = request.JSON

        def respuestaReturn = [:]
        def usuario = Usuario.findByUsername(params.usernameManager)

        def configuraciones = Configuracion.findAll()
        if (configuraciones) {
            def configuracionList = []
            configuraciones.each {
                def configuracion = [:]

                configuracion.id = it.id
                configuracion.bucket = it.bucket
                configuracion.rutaTempArchivos = it.rutaTempArchivos
                configuracionList.push(configuracion)
            }
            respuestaReturn.configurations = configuracionList
        }
        else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No hay configuraciones registradas"
        }
        respond respuestaReturn
    }

    def saveConfiguration (){
        def params = request.JSON
        def respuestaReturn = [:]
        def configuracion = null

        if (params.modelo.id=="0" || params.modelo.id==0) {
            configuracion = new Configuracion()
            configuracion.bucket = params.modelo.bucket
            configuracion.rutaTempArchivos = params.modelo.rutaTempArchivos
        } else {
            configuracion = Configuracion.get(params.modelo.id.toLong())
            configuracion.bucket = params.modelo.bucket
            configuracion.rutaTempArchivos = params.modelo.rutaTempArchivos
        }
        if (configuracion.save(flush: true)) {
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "configuracion save"
        } else {
            def erroresList = []
            configuracion.errors.allErrors.each {
                erroresList.push(it.toString())
            }
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = erroresList
        }

        respond respuestaReturn
    }
}
