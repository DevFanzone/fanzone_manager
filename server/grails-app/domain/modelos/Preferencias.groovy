package modelos

class Preferencias {

    static belongsTo= [usuario:Usuario,categoria:Categoria]

    Date fechaRegistro
    boolean estatus

    static constraints = {
    }
}
