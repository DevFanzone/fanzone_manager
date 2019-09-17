package modelos

class Configuracion {

    String bucket
    String rutaTempArchivos
    String rutaMaps

    static constraints = {

        bucket nullable: true, blank: true,size:1..255
        rutaTempArchivos nullable: true, blank: true,size:1..255
        rutaMaps nullable: true, blank: true,size:1..255
    }
}
