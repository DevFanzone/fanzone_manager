package modelos

class Publicacion {

    static belongsTo= [atleta:Atleta,categoria:Categoria,redeSociales:RedeSociales,usuario:Usuario,icon:Icon]
    static hasMany=[elementos:Elementos,comentarios:Comentarios,likes:Likes]

    Date fechaRegistro
    String fechaPublicacion
    String tituloPost
    String descripcionPost
    String estatus
    boolean error
    List<Object> media
    List<Object> redes
    String idToken
    String tipoPost
    String publicar
    String linkInsignia

    static constraints = {
        fechaPublicacion nullable: true, blank: true,size:1..255
        descripcionPost nullable: true, blank: true,size:1..2300
        estatus nullable: true, blank: true,size:1..255
        error nullable: true, blank: true,size:1..255
        idToken nullable: true, blank: true,size:1..2300
        tipoPost nullable: true, blank: true,size:1..255
        publicar nullable: true, blank: true,size:1..255
        redeSociales nullable: true, blank: true,size:1..255
        linkInsignia nullable: true, blank: true,size:1..2300
    }
}
