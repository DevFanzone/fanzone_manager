package modelos

class Atleta {

    static belongsTo= [usuario:Usuario]
    static hasMany=[redeSociales:RedeSociales,categoria:Categoria,publicacion:Publicacion,liveStream:LiveStream,slider:Slider,icon:Icon,versiones:Versiones]

    Date fechaRegistro
    String nombre
    String sexo
    String telefono
    String email
    String sitioWeb
    String ocupacion
    String foto
    String fotoNombre
    String estatus
    boolean liveStreaming

    static constraints = {
        nombre nullable: true, blank: true,size:1..255
        sexo nullable: true, blank: true,size:1..255
        telefono nullable: true, blank: true,size:1..255
        email nullable: true, blank: true,size:1..255
        sitioWeb nullable: true, blank: true,size:1..255
        ocupacion nullable: true, blank: true,size:1..255
        foto nullable: true, blank: true,size:1..2300
        fotoNombre nullable: true, blank: true,size:1..2300
    }

}
