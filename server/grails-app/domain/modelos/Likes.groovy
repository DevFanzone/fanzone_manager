package modelos

class Likes {

    static belongsTo= [usuario:Usuario,publicacion:Publicacion]
    Date fechaRegistro
    String comentario

    static constraints = {
        comentario nullable: true, blank: true,size:1..500
    }
}
