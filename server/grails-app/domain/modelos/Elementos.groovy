package modelos

class Elementos {

    static belongsTo= [publicacion:Publicacion]
    String  descripcion
    String  bukker
    String  nombreArchivo
    String  urlArchivo
    List<Object> publicaciones
    boolean error
    static constraints = {
        descripcion nullable: true, blank: true,size:1..255
        bukker nullable: true, blank: true,size:1..255
        nombreArchivo nullable: true, blank: true,size:1..255
        urlArchivo nullable: true, blank: true,size:1..255
        error nullable: true, blank: true,size:1..255
    }
}
