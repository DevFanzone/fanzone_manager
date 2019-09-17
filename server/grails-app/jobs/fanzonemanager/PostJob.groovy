package fanzonemanager

import modelos.Publicacion

class PostJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def execute() {
        listaRedacciones()
    }


    def listaRedacciones() {
        def publicaciones = Publicacion.findAllByPublicar("2");
        if (publicaciones) {
            publicaciones.each {
                if (it.estatus == "0") {
                    def now = new Date()
                    def programDate = Date.parse("dd/MM/yyyy HH:mm:ss", it.fechaPublicacion)
                    if (now.getTime() >= programDate.getTime()) {
                        publicar(it.id)
                    }
                }
            }
        }
    }

    def publicar(id) {
        def publicacion = Publicacion.findById(id);
        publicacion.fechaRegistro = Date.parse("dd/MM/yyyy HH:mm:ss", publicacion.fechaPublicacion)
        publicacion.fechaPublicacion = '';
        publicacion.publicar = '1';

        publicacion.save(flush: true);
    }
}
