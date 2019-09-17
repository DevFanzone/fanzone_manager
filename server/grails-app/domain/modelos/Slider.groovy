package modelos

class Slider {
    static belongsTo= [atleta:Atleta,usuario:Usuario]
    Date fechaRegistro
    String nombre
    String tipo
    String height
    String width
    String archivoNombreOriginal
    String urlArchivoOriginal
    List<Object> mapas
    String estatus

    static constraints = {
        nombre nullable: true, blank: true,size:1..255
        tipo nullable: true, blank: true,size:1..2300
        height nullable: true, blank: true,size:1..255
        width nullable: true, blank: true,size:1..255
        archivoNombreOriginal nullable: true, blank: true,size:1..2300
        urlArchivoOriginal nullable: true, blank: true,size:1..2300
        estatus nullable: true, blank: true,size:1..255
    }
}
