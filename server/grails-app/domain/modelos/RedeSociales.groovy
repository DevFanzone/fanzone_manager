package modelos

class RedeSociales {

    static hasMany=[publicacion:Publicacion]
    static belongsTo= [atleta:Atleta]
    Date fechaRegistro
    String nombre
    String tipoRed
    String usuario
    String contrasena

    String accessToken
    String expiresIn

    String estatus

    static constraints = {
        tipoRed     nullable: true, blank: true,size:1..255
        nombre      nullable: true, blank: true,size:1..255
        usuario     nullable: true, blank: true,size:1..255
        contrasena  nullable: true, blank: true,size:1..255

        accessToken nullable: true, blank: true,size:1..2300
        expiresIn   nullable: true, blank: true,size:1..2300

        estatus    nullable: true, blank: true,size:1..255
    }
}
