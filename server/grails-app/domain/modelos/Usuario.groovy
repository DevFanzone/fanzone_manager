package modelos

import seguridad.User

class Usuario extends User {

    static hasMany=[atleta:Atleta,categoria:Categoria,publicacion:Publicacion,comentarios:Comentarios,likes:Likes,preferencias:Preferencias,slider:Slider,icon:Icon]

    String nombre
    String apellidos
    String telefono
    String accessTokenFacebook
    String expiresInFacebook
    String accessTokenTwitter
    String expiresInTwitter
    String accessTokenInstagram
    String expiresInInstagram
    String socialNetwork
    String socialId
    String foto_perfil

    static constraints = {
        nombre nullable: false, blank: false,size:1..255
        apellidos nullable: false, blank: false,size:1..255
        telefono nullable: true, blank: true,size:1..255
        accessTokenFacebook nullable: true, blank: true,size:1..255
        expiresInFacebook nullable: true, blank: true,size:1..255
        accessTokenTwitter nullable: true, blank: true,size:1..255
        expiresInTwitter nullable: true, blank: true,size:1..255
        accessTokenInstagram nullable: true, blank: true,size:1..255
        expiresInInstagram nullable: true, blank: true,size:1..255
        socialNetwork nullable: true, blank: true,size:1..255
        socialId nullable: true, blank: true,size:1..255
        foto_perfil nullable: true, blank: true,size:1..255
    }

}
