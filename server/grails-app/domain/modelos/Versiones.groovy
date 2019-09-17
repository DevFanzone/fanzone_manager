package modelos

class Versiones {

    static belongsTo= [atleta:Atleta]

    String plataforma
    String version

    static constraints = {
    }
}
