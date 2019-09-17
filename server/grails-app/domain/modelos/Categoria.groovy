package modelos

class Categoria {

    static hasMany=[publicacion:Publicacion,preferencias:Preferencias]
    static belongsTo= [atleta:Atleta,usuario:Usuario]
    Date fechaRegistro
    String nombre
    String descripcion
    String archivoNombreOriginal
    String urlArchivoOriginal
    String archivoNombreNormal
    String urlArchivoNormal
    String archivoNombreMini
    String urlArchivoMini
    String estatus

    //code Alexis
    String archivoNombreSplash
    String urlSplah
    Date lastUpdateSplash

    static constraints = {
        nombre nullable: true, blank: true,size:1..255
        descripcion nullable: true, blank: true,size:1..2300
        archivoNombreOriginal nullable: true, blank: true,size:1..2300
        urlArchivoOriginal nullable: true, blank: true,size:1..2300
        archivoNombreNormal nullable: true, blank: true,size:1..2300
        urlArchivoNormal nullable: true, blank: true,size:1..2300
        archivoNombreMini nullable: true, blank: true,size:1..2300
        urlArchivoMini nullable: true, blank: true,size:1..2300
        estatus nullable: true, blank: true,size:1..255

        //code Alexis
        archivoNombreSplash nullable: true, blank: true,size:1..2300
        urlSplah nullable: true, blank: true,size:1..2300

    }
}
