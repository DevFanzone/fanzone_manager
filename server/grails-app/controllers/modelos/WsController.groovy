package modelos

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.rest.*
import com.google.cloud.storage.*;
import javax.imageio.ImageIO;
import seguridad.Role
import seguridad.UserRole

@Secured(['permitAll'])
class WsController extends RestfulController {
    static responseFormats = ['json', 'xml']

    WsController() {
        super(Ws)
    }

    def springSecurityService
    def errorHandlerService
    def permisosService
    def passwordEncoder
    def mailService

    def categoriasAtleta() {
        def respuestaReturn = [:]
        def lstCategoria = []
        def conf = Configuracion.findAll()

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def categorias = Categoria.findAllByAtletaAndEstatus(params.atletaId.toLong(), "0")
        categorias.each() {
            def urlArchivoOriginal = ""
            def urlArchivoNormal = ""
            def urlArchivoMini = ""
            def urlSplah = ""

            // iniciar servicios cloud
            def storage =StorageOptions.getDefaultInstance().getService();
            def acls = new java.util.ArrayList<Acl>()
            acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

            // obtener url de archivos en cloud
            def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreOriginal.toString()}").setAcl(acls).build()
            urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

            def blobNormal = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreNormal.toString()}").setAcl(acls).build()
            urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

            def blobMini = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreMini.toString()}").setAcl(acls).build()
            urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

            def blobSplash = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreSplash.toString()}").setAcl(acls).build()
            urlSplah=  storage.signUrl(blobSplash, 2, java.util.concurrent.TimeUnit.DAYS)

            def categoriaCount = Publicacion.findAllByCategoria(it.id.toLong())

            if (categoriaCount.size() > 0) {
                lstCategoria.add([
                        id                   : it.id,
                        fechaRegistro        : it.fechaRegistro,
                        nombre               : it.nombre,
                        descripcion          : it.descripcion,
                        archivoNombreOriginal: it.archivoNombreOriginal,
                        urlArchivoOriginal   : urlArchivoOriginal,
                        archivoNombreNormal  : it.archivoNombreNormal,
                        urlArchivoNormal     : urlArchivoNormal,
                        archivoNombreMini    : it.archivoNombreMini,
                        urlArchivoMini       : urlArchivoMini,
                        estatus              : it.estatus,
                        totalPosts           : categoriaCount.size(),
                        urlSplah             : urlSplah,
                        lastUpdateSplash     : it.lastUpdateSplash,
                        archivoNombreSplash  : it.archivoNombreSplash

                ])
            }
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.categorias = lstCategoria

        respond respuestaReturn
    }

    def categoria() {
        def respuestaReturn = [:]
        def lstCategoria = []
        def conf = Configuracion.findAll()

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def categoria = Categoria.get(params.collectionId.toLong())
        def categoriaCount = Publicacion.findAllByCategoria(params.collectionId.toLong())

        categoria.each() {
            def urlArchivoOriginal = ""
            def urlArchivoNormal = ""
            def urlArchivoMini = ""

            // iniciar servicios cloud
            def storage =StorageOptions.getDefaultInstance().getService();
            def acls = new java.util.ArrayList<Acl>()
            acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

            // obtener url de archivos en cloud
            def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreOriginal.toString()}").setAcl(acls).build()
            urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

            def blobNormal = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreNormal.toString()}").setAcl(acls).build()
            urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

            def blobMini = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreMini.toString()}").setAcl(acls).build()
            urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

            if (it.estatus == "0") {
                lstCategoria.add([
                        id                   : it.id,
                        fechaRegistro        : it.fechaRegistro,
                        nombre               : it.nombre,
                        descripcion          : it.descripcion,
                        archivoNombreOriginal: it.archivoNombreOriginal,
                        urlArchivoOriginal   : urlArchivoOriginal,
                        archivoNombreNormal  : it.archivoNombreNormal,
                        urlArchivoNormal     : urlArchivoNormal,
                        archivoNombreMini    : it.archivoNombreMini,
                        urlArchivoMini       : urlArchivoMini,
                        nombreArchivoOriginal: it.nombreArchivoOriginal.toString(),
                        urlArchivoOriginal   : urlArchivoOriginal.toString(),
                        nombreArchivoNormal  : it.nombreArchivoNormal.toString(),
                        urlArchivoNormal     : urlArchivoNormal.toString(),
                        nombreArchivoMini    : it.nombreArchivoMini.toString(),
                        urlArchivoMini       : urlArchivoMini.toString(),
                        estatus              : it.estatus,
                        urlSplah             : it.urlSplah,
                        lastUpdateSplash     : it.lastUpdateSplash,
                        archivoNombreSplash  : it.archivoNombreSplash
                ])
            }
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.categoria = lstCategoria
        respuestaReturn.totalPublicaciones = categoriaCount.size()

        respond respuestaReturn
    }

    def categoriaMedia() {
        def respuestaReturn = [:]
        def lstMedia = []
        def lstMediaAux = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        Publicacion.createCriteria().list([sort: ["fechaRegistro": "desc"], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
            and {
                eq("categoria", Categoria.get(params.collectionId.toLong()))
                eq("estatus", "0")
                ne("tipoPost", "3")
                ne("tipoPost", "4")
                ne("publicar", '2')
            }
        }.collect {
            def objtMedia = []
            def idAtleta = it.atleta.id
            if (it.media.size() > 0) {
                it.media.each() {
                    def urlArchivoOriginal = ""
                    def urlArchivoNormal = ""
                    def urlArchivoMini = ""
                    def urlArchivoDifuminada = ""
                    def widthOriginal = 0
                    def heightOriginal = 0
                    def widthNormal = 0
                    def heightNormal = 0
                    def widthMini = 0
                    def heightMini = 0

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // obtener url de archivos en cloud
                    def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                    urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                    urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                    urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                    urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)

                    if(it.tipo != "video/mp4") {
                        def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                        widthNormal=    imgNormal.getWidth()
                        heightNormal=   imgNormal.getHeight()
                    }

                    objtMedia.add([
                            descripcion            : it.descripcion,
                            bucket                 : it.bucket,
                            nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                            urlArchivoOriginal     : urlArchivoOriginal.toString(),
                            nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                            urlArchivoNormal       : urlArchivoNormal.toString(),
                            nombreArchivoMini      : it.nombreArchivoMini.toString(),
                            urlArchivoMini         : urlArchivoMini.toString(),
                            nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                            urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                            error                  : it.error,
                            tipo                   : it.tipo,
                            mapsOriginal           : it.mapsOriginal,
                            mapsNormal             : it.mapsNormal,
                            mapsMini               : it.mapsMini,
                            widthOriginal          : widthOriginal,
                            heightOriginal         : heightOriginal,
                            widthNormal            : widthNormal,
                            heightNormal           : heightNormal,
                            widthMini              : widthMini,
                            heightMini             : heightMini
                    ])
                }
            }

            def likes = Likes.findAllByPublicacion(it.id.toLong())
            def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

            lstMedia.add([
                    id                        : it.id,
                    fechaRegistro             : it.fechaRegistro,
                    fechaPublicacion          : it.fechaPublicacion,
                    tituloPost                : it.tituloPost,
                    descripcionPost           : it.descripcionPost,
                    estatus                   : it.estatus,
                    error                     : it.error,
                    idToken                   : it.idToken,
                    tipoPost                  : it.tipoPost,
                    publicar                  : it.publicar,
                    media                     : objtMedia,
                    idCategoria               : it.categoria.id,
                    nombreCategoria           : it.categoria.nombre,
                    linkInsignia              : it.linkInsignia,
                    urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                    likesCount                : likes.size(),
                    commentsCount             : comentarios.size()
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.publicaciones = lstMedia

        respond respuestaReturn
    }

    //postInfo
    def publicacion() {
        def respuestaReturn = [:]
        def lstMedia = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def publicacion = Publicacion.get(params.postId.toLong(), [sort: ["fechaRegistro": "desc"]])
        publicacion.each() {
            def objtMedia = []
            def idAtleta = it.atleta.id
            if (it.media.size() > 0) {
                it.media.each() {
                    def urlArchivoOriginal = ""
                    def urlArchivoNormal = ""
                    def urlArchivoMini = ""
                    def urlArchivoDifuminada = ""
                    def widthOriginal = 0
                    def heightOriginal = 0
                    def widthNormal = 0
                    def heightNormal = 0
                    def widthMini = 0
                    def heightMini = 0

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // obtener url de archivos en cloud
                    def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                    urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                    urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                    urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                    urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                    if(it.tipo != "video/mp4") {
                       def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                        widthNormal=    imgNormal.getWidth()
                        heightNormal=   imgNormal.getHeight()
                    }


                    objtMedia.add([
                            descripcion            : it.descripcion,
                            bucket                 : it.bucket,
                            nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                            urlArchivoOriginal     : urlArchivoOriginal.toString(),
                            nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                            urlArchivoNormal       : urlArchivoNormal.toString(),
                            nombreArchivoMini      : it.nombreArchivoMini.toString(),
                            urlArchivoMini         : urlArchivoMini.toString(),
                            nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                            urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                            error                  : it.error,
                            tipo                   : it.tipo,
                            mapsOriginal           : it.mapsOriginal,
                            mapsNormal             : it.mapsNormal,
                            mapsMini               : it.mapsMini,
                            widthOriginal          : widthOriginal,
                            heightOriginal         : heightOriginal,
                            widthNormal            : widthNormal,
                            heightNormal           : heightNormal,
                            widthMini              : widthMini,
                            heightMini             : heightMini
                    ])
                }
            }

            def likes = Likes.findAllByPublicacion(it.id.toLong())
            def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

            lstMedia.add([
                    id                        : it.id,
                    fechaRegistro             : it.fechaRegistro,
                    fechaPublicacion          : it.fechaPublicacion,
                    tituloPost                : it.tituloPost,
                    descripcionPost           : it.descripcionPost,
                    estatus                   : it.estatus,
                    error                     : it.error,
                    idToken                   : it.idToken,
                    tipoPost                  : it.tipoPost,
                    publicar                  : it.publicar,
                    media                     : objtMedia,
                    idCategoria               : it.categoria.id,
                    nombreCategoria           : it.categoria.nombre,
                    linkInsignia              : it.linkInsignia,
                    urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                    likesCount                : likes.size(),
                    commentsCount             : comentarios.size()
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.publicacion = lstMedia

        respond respuestaReturn
    }

    //latestPosts
    def publicacionesToday() {
        def respuestaReturn = [:]
        def lstMedia = []
        try {
            Publicacion.createCriteria().list([sort: ["fechaRegistro": "desc"], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
                and {
                    eq("atleta", Atleta.get(params.atletaId.toLong()))
                    eq("estatus", "0")
                    ne("tipoPost", "3")
                    ne("tipoPost", "4")
                    ne("publicar", '2')
                }
            }.each() {
                def objtMedia = []
                def fechaPost = ""
                def idAtleta = it.atleta.id

                if (it.media.size() > 0) {
                    it.media.each() {
                        def urlArchivoOriginal = ""
                        def urlArchivoNormal = ""
                        def urlArchivoMini = ""
                        def urlArchivoDifuminada = ""
                        def widthOriginal = 0
                        def heightOriginal = 0
                        def widthNormal = 0
                        def heightNormal = 0
                        def widthMini = 0
                        def heightMini = 0

                        // iniciar servicios cloud
                        def storage =StorageOptions.getDefaultInstance().getService();
                        def acls = new java.util.ArrayList<Acl>()
                        acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                        // obtener url de archivos en cloud
                        def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                        urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                        urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                        urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                        urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                        if(it.tipo != "video/mp4") {
                            def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                            widthNormal=    imgNormal.getWidth()
                            heightNormal=   imgNormal.getHeight()
                        }

                        objtMedia.add([
                                descripcion            : it.descripcion,
                                bucket                 : it.bucket,
                                nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                                urlArchivoOriginal     : urlArchivoOriginal.toString(),
                                nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                                urlArchivoNormal       : urlArchivoNormal.toString(),
                                nombreArchivoMini      : it.nombreArchivoMini.toString(),
                                urlArchivoMini         : urlArchivoMini.toString(),
                                nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                                urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                                error                  : it.error,
                                tipo                   : it.tipo,
                                mapsOriginal           : it.mapsOriginal,
                                mapsNormal             : it.mapsNormal,
                                mapsMini               : it.mapsMini,
                                widthOriginal          : widthOriginal,
                                heightOriginal         : heightOriginal,
                                widthNormal            : widthNormal,
                                heightNormal           : heightNormal,
                                widthMini              : widthMini,
                                heightMini             : heightMini
                        ])
                    }
                }

                def likes = Likes.findAllByPublicacion(it.id.toLong())
                def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

                lstMedia.add([
                        id                        : it.id,
                        fechaRegistro             : it.fechaRegistro,
                        fechaPublicacion          : it.fechaPublicacion,
                        tituloPost                : it.tituloPost,
                        descripcionPost           : it.descripcionPost,
                        estatus                   : it.estatus,
                        error                     : it.error,
                        idToken                   : it.idToken,
                        tipoPost                  : it.tipoPost,
                        publicar                  : it.publicar,
                        media                     : objtMedia,
                        idCategoria               : it.categoria.id,
                        nombreCategoria           : it.categoria.nombre,
                        linkInsignia              : it.linkInsignia,
                        urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                        likesCount                : likes.size(),
                        commentsCount             : comentarios.size()
                ])
            }
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Exito"
            respuestaReturn.publicaciones = lstMedia
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }
        respond respuestaReturn
    }

    def buscarPublicaciones() {
        def publicaciones = []
        def respuestaReturn = [:]
        def lstMedia = []
        try {
            def categoria = Categoria.findAllByNombreIlike('%' + params.keyword + '%').id
            def user = Usuario.findByUsername(params.username)
            def roleUsuario

            if(user) {
                roleUsuario = UserRole.findByUser(user)
            }

            if (roleUsuario && (roleUsuario.role.id == '6' || roleUsuario.role.id == 6) && (params.keyword.toLowerCase() == 'exclusive content' || (params.keyword.toLowerCase().contains('exclusive c') && params.keyword.length() >= 11))) {
                publicaciones = Publicacion.createCriteria().list([sort: ['fechaRegistro': 'desc'], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
                    eq("atleta", Atleta.get(params.atletaId.toLong()))
                    eq("estatus", '0')
                    eq("tipoPost", '1')
                    ne("publicar", '2')
                }
            } else {
                publicaciones = Publicacion.createCriteria().list([sort: ['fechaRegistro': 'desc'], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
                    eq("atleta", Atleta.get(params.atletaId.toLong()))
                    eq("estatus", '0')
                    ne("publicar", '2')
                    ne("tipoPost", '3')
                    ne("tipoPost", '4')
                    or {
                        ilike('tituloPost', '%' + params.keyword + '%')
                        ilike('descripcionPost', '%' + params.keyword + '%')
                        categoria.each() {
                            eq('categoria', it)
                        }
                    }
                }
            }


            publicaciones.each() {
                def objtMedia = []
                def fechaPost = ""
                def idAtleta = it.atleta.id

                if (it.media.size() > 0) {
                    it.media.each() {
                        def urlArchivoOriginal = ""
                        def urlArchivoNormal = ""
                        def urlArchivoMini = ""
                        def urlArchivoDifuminada = ""
                        def widthOriginal = 0
                        def heightOriginal = 0
                        def widthNormal = 0
                        def heightNormal = 0
                        def widthMini = 0
                        def heightMini = 0

                        // iniciar servicios cloud
                        def storage =StorageOptions.getDefaultInstance().getService();
                        def acls = new java.util.ArrayList<Acl>()
                        acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                        // obtener url de archivos en cloud
                        def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                        urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                        urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                        urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                        urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                        if(it.tipo != "video/mp4") {
                            def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                            widthNormal=    imgNormal.getWidth()
                            heightNormal=   imgNormal.getHeight()
                        }

                        objtMedia.add([
                                descripcion            : it.descripcion,
                                bucket                 : it.bucket,
                                nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                                urlArchivoOriginal     : urlArchivoOriginal.toString(),
                                nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                                urlArchivoNormal       : urlArchivoNormal.toString(),
                                nombreArchivoMini      : it.nombreArchivoMini.toString(),
                                urlArchivoMini         : urlArchivoMini.toString(),
                                nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                                urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                                error                  : it.error,
                                tipo                   : it.tipo,
                                mapsOriginal           : it.mapsOriginal,
                                mapsNormal             : it.mapsNormal,
                                mapsMini               : it.mapsMini,
                                widthOriginal          : widthOriginal,
                                heightOriginal         : heightOriginal,
                                widthNormal            : widthNormal,
                                heightNormal           : heightNormal,
                                widthMini              : widthMini,
                                heightMini             : heightMini
                        ])
                    }
                }

                def likes = Likes.findAllByPublicacion(it.id.toLong())
                def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

                lstMedia.add([
                        id                        : it.id,
                        fechaRegistro             : it.fechaRegistro,
                        fechaPublicacion          : it.fechaPublicacion,
                        tituloPost                : it.tituloPost,
                        descripcionPost           : it.descripcionPost,
                        estatus                   : it.estatus,
                        error                     : it.error,
                        idToken                   : it.idToken,
                        tipoPost                  : it.tipoPost,
                        publicar                  : it.publicar,
                        media                     : objtMedia,
                        idCategoria               : it.categoria.id,
                        nombreCategoria           : it.categoria.nombre,
                        linkInsignia              : it.linkInsignia,
                        urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                        likesCount                : likes.size(),
                        commentsCount             : comentarios.size()
                ])
            }
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Exito"
            respuestaReturn.publicaciones = lstMedia
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }
        respond respuestaReturn
    }

    def publicacionesPublicitarias() {
        def respuestaReturn = [:]
        def lstMedia = []
        def lstMediaAux = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        Publicacion.createCriteria().list([sort: ["fechaRegistro": "desc"], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
            and {
                eq("atleta", Atleta.get(params.atletaId.toLong()))
                eq("estatus", "0")
                eq("tipoPost", "4")
                ne("publicar", '2')
            }
        }.collect {
            def objtMedia = []
            def idAtleta = it.atleta.id

            if (it.media.size() > 0) {
                it.media.each() {
                    def urlArchivoOriginal = ""
                    def urlArchivoNormal = ""
                    def urlArchivoMini = ""
                    def urlArchivoDifuminada = ""
                    def widthOriginal = 0
                    def heightOriginal = 0
                    def widthNormal = 0
                    def heightNormal = 0
                    def widthMini = 0
                    def heightMini = 0

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // obtener url de archivos en cloud
                    def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                    urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                    urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                    urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                    urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                    if(it.tipo != "video/mp4") {
                        def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                        widthNormal=    imgNormal.getWidth()
                        heightNormal=   imgNormal.getHeight()
                    }

                    objtMedia.add([
                            descripcion            : it.descripcion,
                            bucket                 : it.bucket,
                            nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                            urlArchivoOriginal     : urlArchivoOriginal.toString(),
                            nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                            urlArchivoNormal       : urlArchivoNormal.toString(),
                            nombreArchivoMini      : it.nombreArchivoMini.toString(),
                            urlArchivoMini         : urlArchivoMini.toString(),
                            nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                            urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                            error                  : it.error,
                            tipo                   : it.tipo,
                            mapsOriginal           : it.mapsOriginal,
                            mapsNormal             : it.mapsNormal,
                            mapsMini               : it.mapsMini,
                            widthOriginal          : widthOriginal,
                            heightOriginal         : heightOriginal,
                            widthNormal            : widthNormal,
                            heightNormal           : heightNormal,
                            widthMini              : widthMini,
                            heightMini             : heightMini
                    ])
                }
            }

            def likes = Likes.findAllByPublicacion(it.id.toLong())
            def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

            lstMedia.add([
                    id                        : it.id,
                    fechaRegistro             : it.fechaRegistro,
                    fechaPublicacion          : it.fechaPublicacion,
                    tituloPost                : it.tituloPost,
                    descripcionPost           : it.descripcionPost,
                    estatus                   : it.estatus,
                    error                     : it.error,
                    idToken                   : it.idToken,
                    tipoPost                  : it.tipoPost,
                    publicar                  : it.publicar,
                    media                     : objtMedia,
                    idCategoria               : it.categoria.id,
                    nombreCategoria           : it.categoria.nombre,
                    linkInsignia              : it.linkInsignia,
                    urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                    likesCount                : likes.size(),
                    commentsCount             : comentarios.size()
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.publicaciones = lstMedia.sort { a, b -> b.fechaRegistro <=> a.fechaRegistro }

        respond respuestaReturn
    }

    def listaBanners() {
        def respuestaReturn = [:]
        def lstMedia = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def conf = Configuracion.findAll()
        def slider = Slider.createCriteria().list() {
            and {
                eq("atleta", Atleta.get(params.atletaId.toLong()))
                eq("estatus", "0")
            }
        }

        slider.each() {
            def idAtleta = it.atleta.id
            def urlArchivoOriginal = ""
            def widthOriginal = 0
            def heightOriginal = 0

            // iniciar servicios cloud
            def storage =StorageOptions.getDefaultInstance().getService();
            def acls = new java.util.ArrayList<Acl>()
            acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

            // guardar archivo en cloud
            def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${idAtleta.toString()}/${it.archivoNombreOriginal.toString()}").setAcl(acls).build()
            urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)


            if (it.tipo != "video/mp4") {
                def imgOriginal = ImageIO.read(new URL(urlArchivoOriginal.toString()));
                widthOriginal = imgOriginal.getWidth()
                heightOriginal = imgOriginal.getHeight()
            }

            lstMedia.add([
                    id                   : it.id,
                    fechaRegistro        : it.fechaRegistro,
                    nombre               : it.nombre,
                    tipo                 : it.tipo,
                    height               : heightOriginal,
                    width                : widthOriginal,
                    archivoNombreOriginal: it.archivoNombreOriginal,
                    urlArchivoOriginal   : urlArchivoOriginal,
                    mapsOriginal         : it.mapas,
                    estatus              : it.estatus
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.sliders = lstMedia

        respond respuestaReturn
    }

    def publicacionesExclusivas() {
        def respuestaReturn = [:]
        def lstMedia = []
        def lstMediaAux = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        Publicacion.createCriteria().list([sort: ["fechaRegistro": "desc"], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
            and {
                eq("atleta", Atleta.get(params.atletaId.toLong()))
                eq("tipoPost", params.typePost.toString())
                eq("estatus", "0")
                ne("publicar", '2')
            }
        }.collect {
            def objtMedia = []
            def idAtleta = it.atleta.id

            if (it.media.size() > 0) {
                it.media.each() {
                    def urlArchivoOriginal = ""
                    def urlArchivoNormal = ""
                    def urlArchivoMini = ""
                    def urlArchivoDifuminada = ""

                    def widthOriginal = 0
                    def heightOriginal = 0
                    def widthNormal = 0
                    def heightNormal = 0
                    def widthMini = 0
                    def heightMini = 0

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // obtener url de archivos en cloud
                    def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                    urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                    urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                    urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                    urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                    if(it.tipo != "video/mp4") {
                        def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                        widthNormal=    imgNormal.getWidth()
                        heightNormal=   imgNormal.getHeight()

                    }

                    objtMedia.add([
                            descripcion            : it.descripcion,
                            bucket                 : it.bucket,
                            nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                            urlArchivoOriginal     : urlArchivoOriginal.toString(),
                            nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                            urlArchivoNormal       : urlArchivoNormal.toString(),
                            nombreArchivoMini      : it.nombreArchivoMini.toString(),
                            urlArchivoMini         : urlArchivoMini.toString(),
                            nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                            urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                            error                  : it.error,
                            tipo                   : it.tipo,
                            mapsOriginal           : it.mapsOriginal,
                            mapsNormal             : it.mapsNormal,
                            mapsMini               : it.mapsMini,
                            widthOriginal          : widthOriginal,
                            heightOriginal         : heightOriginal,
                            widthNormal            : widthNormal,
                            heightNormal           : heightNormal,
                            widthMini              : widthMini,
                            heightMini             : heightMini
                    ])
                }
            }

            def likes = Likes.findAllByPublicacion(it.id.toLong())
            def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

            lstMedia.add([
                    id                        : it.id,
                    fechaRegistro             : it.fechaRegistro,
                    fechaPublicacion          : it.fechaPublicacion,
                    tituloPost                : it.tituloPost,
                    descripcionPost           : it.descripcionPost,
                    estatus                   : it.estatus,
                    error                     : it.error,
                    idToken                   : it.idToken,
                    tipoPost                  : it.tipoPost,
                    publicar                  : it.publicar,
                    media                     : objtMedia,
                    idCategoria               : it.categoria.id,
                    nombreCategoria           : it.categoria.nombre,
                    linkInsignia              : it.linkInsignia,
                    urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                    likesCount                : likes.size(),
                    commentsCount             : comentarios.size()
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.publicaciones = lstMedia

        respond respuestaReturn
    }

    def listarVersiones() {
        def respuestaReturn = [:]
        def listVersiones = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        Versiones.createCriteria().list() {
            and {
                eq("atleta", Atleta.get(params.atletaId.toLong()))
                eq("plataforma", params.platform.toString())
            }
        }.collect {
            listVersiones.add([
                    id        : it.id,
                    plataforma: it.plataforma,
                    version   : it.version
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.versiones = listVersiones

        respond respuestaReturn

    }

    def serchInsignia(idAtleta, nombreArchivoOriginal) {

        def conf = Configuracion.findAll()
        def urlArchivoOriginal = ""

        // iniciar servicios cloud
        def storage = StorageOptions.getDefaultInstance().getService();
        def acls = new java.util.ArrayList<Acl>()
        acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

        // obtener url de archivos en cloud
        def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${idAtleta.toString()}/${nombreArchivoOriginal.toString()}").setAcl(acls).build()
        urlArchivoOriginal = storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

        return urlArchivoOriginal
    }

    def saveMapspublicacion() {
        def params = request.JSON
        def respuestaReturn = [:]
        def listaElementos = []
        def maps = []
        try {
            if (params.params.type == "1") {
                for (int b = 0; b < params.params.media.size(); b++) {
                    def listaMapsOriginal = []
                    def listaMapsNormal = []
                    def listaMapsMini = []
                    for (int r = 0; r < params.params.media[b].mapsOriginal.size(); r++) {
                        def map = [:]
                        map.description = params.params.media[b].mapsOriginal[r].description
                        map.link_url = params.params.media[b].mapsOriginal[r].link_url
                        map.coords = params.params.media[b].mapsOriginal[r].coords
                        listaMapsOriginal.push(map)
                    }
                    for (int w = 0; w < params.params.media[b].mapsNormal.size(); w++) {
                        def map = [:]
                        map.description = params.params.media[b].mapsNormal[w].description
                        map.link_url = params.params.media[b].mapsNormal[w].link_url
                        map.coords = params.params.media[b].mapsNormal[w].coords
                        listaMapsNormal.push(map)
                    }
                    for (int n = 0; n < params.params.media[b].mapsMini.size(); n++) {
                        def map = [:]
                        map.description = params.params.media[b].mapsMini[n].description
                        map.link_url = params.params.media[b].mapsMini[n].link_url
                        map.coords = params.params.media[b].mapsMini[n].coords
                        listaMapsMini.push(map)
                    }

                    def elemento = [:]
                    elemento.descripcion = params.params.media[b].descripcion
                    elemento.bucket = params.params.media[b].bucket
                    elemento.error = params.params.media[b].error
                    elemento.tipo = params.params.media[b].tipo
                    elemento.nombreArchivoOriginal = params.params.media[b].nombreArchivoOriginal
                    elemento.urlArchivoOriginal = ""
                    elemento.nombreArchivoNormal = params.params.media[b].nombreArchivoNormal
                    elemento.urlArchivoNormal = ""
                    elemento.nombreArchivoMini = params.params.media[b].nombreArchivoMini
                    elemento.urlArchivoMini = ""
                    elemento.nombreArchivoDifuminada = params.params.media[b].nombreArchivoDifuminada
                    elemento.urlArchivoDifuminada = ""
                    elemento.mapsOriginal = listaMapsOriginal
                    elemento.mapsNormal = listaMapsNormal
                    elemento.mapsMini = listaMapsMini
                    listaElementos.push(elemento)
                }

                def publicacionNew = Publicacion.get(params.params.id.toLong())
                publicacionNew.media = listaElementos

                if (publicacionNew.save(flush: true)) {
                } else {
                    def erroresList = []
                    publicacionNew.errors.allErrors.each {
                        erroresList.push(it.toString())
                    }
                }
            } else {
                for (int b = 0; b < params.params.media.size(); b++) {
                    def listaMapsOriginal = []
                    for (int r = 0; r < params.params.media[b].mapsOriginal.size(); r++) {
                        def map = [:]
                        map.description = params.params.media[b].mapsOriginal[r].description
                        map.link_url = params.params.media[b].mapsOriginal[r].link_url
                        map.coords = params.params.media[b].mapsOriginal[r].coords
                        listaMapsOriginal.push(map)
                    }
                    def sliderUpdate = Slider.get(params.params.media[b].id.toLong())
                    sliderUpdate.mapas = listaMapsOriginal

                    if (sliderUpdate.save(flush: true)) {
                    } else {
                        def erroresList = []
                        sliderUpdate.errors.allErrors.each {
                            erroresList.push(it.toString())
                        }
                    }
                }
            }
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Exito"

        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }
        respond respuestaReturn
    }

    def comentariosPost() {
        def params = request.JSON
        def respuestaReturn = [:]
        def fecha = new Date()

        try {
            def usuario = Usuario.findByUsername(params.userName)
            def comentariosNew = new Comentarios()
            comentariosNew.fechaRegistro = fecha
            comentariosNew.comentario = params.comment
            comentariosNew.publicacion = Publicacion.get(params.postId.toLong())
            comentariosNew.usuario = Usuario.get(usuario.id.toLong())

            if (comentariosNew.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "save commnet"
            } else {
                def erroresList = []
                comentariosNew.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "An error has occurred.";
            }
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }

        respond respuestaReturn
    }

    def listComentariosPost() {
        def respuestaReturn = [:]

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def comentarios = Comentarios.findAllByPublicacion(params.postId.toLong())

        def comentariosList = []
        if (comentarios) {
            comentarios.each {
                def comentario = [:]

                comentario.id = it.id
                comentario.fechaRegistro = it.fechaRegistro
                comentario.comentario = it.comentario
                comentario.idUsuario = it.usuario.id
                comentario.nombreUsuario = it.usuario.nombre
                comentario.apellidosUsuario = it.usuario.apellidos
                comentario.socialNetworkUsuario = it.usuario.socialNetwork
                comentario.socialIdUsuario = it.usuario.socialId
                comentario.postId = params.postId

                comentariosList.push(comentario)
            }
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.comentarios = comentariosList

        respond respuestaReturn
    }

    def listLikesPost() {
        def respuestaReturn = [:]

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def likes = Likes.findAllByPublicacion(params.postId.toLong())

        def likesList = []
        if (likes) {
            likes.each {
                def like = [:]

                like.id = it.id
                like.fechaRegistro = it.fechaRegistro
                like.comentario = it.comentario
                like.idUsuario = it.usuario.id
                like.nombreUsuario = it.usuario.nombre
                like.apellidosUsuario = it.usuario.apellidos
                like.socialNetworkUsuario = it.usuario.socialNetwork
                like.socialIdUsuario = it.usuario.socialId
                like.postId = params.postId

                likesList.push(like)
            }
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.likes = likesList

        respond respuestaReturn
    }

    def likesPost() {
        def params = request.JSON
        def respuestaReturn = [:]
        def fecha = new Date()
        try {
            def usuario = Usuario.findByUsername(params.userName)
            def likeUsuario = Likes.findAllByUsuarioAndPublicacion(usuario.id.toLong(), params.postId.toLong())

            if (likeUsuario) {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "exist this user in the table likes.";
            } else {
                def likesNew = new Likes()
                likesNew.fechaRegistro = fecha
                likesNew.comentario = "like"
                likesNew.publicacion = Publicacion.get(params.postId.toLong())
                likesNew.usuario = Usuario.get(usuario.id.toLong())

                if (likesNew.save(flush: true)) {
                    respuestaReturn.errorAPI = false
                    respuestaReturn.mensajeAPI = "save like"
                } else {
                    def erroresList = []
                    likesNew.errors.allErrors.each {
                        erroresList.push(it.toString())
                    }
                    respuestaReturn.errorAPI = true
                    respuestaReturn.mensajeAPI = "An error has occurred.";
                }
            }
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }
        respond respuestaReturn
    }

    def supportEmail() {
        def params = request.JSON
        def respReturn = [:]
        try {
            mailService.sendMail {
                to 'alexiscrmr@gmail.com'
                subject params.subject
                html params.issue
            }
        } catch (Exception e) {
            respReturn.errorAPI = true
            respReturn.mensajeAPI = 'Ocurrio un error en el envio de correo.'
            respond respReturn
            return
        }
        respReturn.errorAPI = false
        respReturn.mensajeAPI = 'Exito en el envio de correo'
        respond respReturn
    }

    def crearCuenta() {
        def params = request.JSON
        def respuestaReturn = [:]
        def usuario = new Usuario()

        usuario.nombre = params.firstName
        usuario.apellidos = params.lastName
        usuario.telefono = params.phone
        usuario.username = params.email
        usuario.password = params.password
        usuario.accessTokenFacebook = "sn"
        usuario.expiresInFacebook = "sn"
        usuario.accessTokenTwitter = "sn"
        usuario.expiresInTwitter = "sn"
        usuario.accessTokenInstagram = "sn"
        usuario.expiresInInstagram = "sn"
        usuario.expiresInInstagram = "sn"
        usuario.socialNetwork = params.socialNetwork
        usuario.socialId = params.socialId
        usuario.foto_perfil = params.profile_picture

        def usuarioExist = Usuario.findByUsername(params.email)
        if (usuarioExist) {
            def roleUsuario = UserRole.findByUser(usuarioExist)
            if (roleUsuario.role.id == '5' || roleUsuario.role.id == 5) {
                updateRoleToPremium(usuarioExist.id)
            }
        }

        if (usuario.save(flush: true)) {
            permisosService.setRole(usuario, params.plan)

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Usuario creado"
            respuestaReturn.usuario = usuario
        } else {
            def erroresList = []
            usuario.errors.allErrors.each {
                erroresList.push(it.toString())
            }
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = erroresList
        }
        respond respuestaReturn
    }

    def updateRoleToPremium(idUser) {
        def user = Usuario.findById(idUser)
        def userRole = UserRole.findByUser(user)
        if (userRole) {
            UserRole.remove(user, userRole.getRole())
            new UserRole(user: user, role: Role.get(6)).save(flush: true)
        }
    }

    def publicacionesTodayExclusive() {
        def respuestaReturn = [:]
        def lstMedia = []
        try {
            Publicacion.createCriteria().list([sort: ["fechaRegistro": "desc"], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
                and {
                    eq("atleta", Atleta.get(params.atletaId.toLong()))
                    eq("tipoPost", "1")
                    eq("estatus", "0")
                    ne("publicar", "2")
                }
            }.each() {
                def objtMedia = []
                def fechaPost = ""
                def idAtleta = it.atleta.id
                if (it.media.size() > 0) {
                    it.media.each() {
                        def urlArchivoOriginal = ""
                        def urlArchivoNormal = ""
                        def urlArchivoMini = ""
                        def urlArchivoDifuminada = ""
                        def widthOriginal = 0
                        def heightOriginal = 0
                        def widthNormal = 0
                        def heightNormal = 0
                        def widthMini = 0
                        def heightMini = 0

                        // iniciar servicios cloud
                        def storage =StorageOptions.getDefaultInstance().getService();
                        def acls = new java.util.ArrayList<Acl>()
                        acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                        // obtener url de archivos en cloud
                        def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                        urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                        urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                        urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                        def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                        urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                        if(it.tipo != "video/mp4") {
                            def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                            widthNormal=    imgNormal.getWidth()
                            heightNormal=   imgNormal.getHeight()
                        }

                        objtMedia.add([
                                descripcion            : it.descripcion,
                                bucket                 : it.bucket,
                                nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                                urlArchivoOriginal     : urlArchivoOriginal.toString(),
                                nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                                urlArchivoNormal       : urlArchivoNormal.toString(),
                                nombreArchivoMini      : it.nombreArchivoMini.toString(),
                                urlArchivoMini         : urlArchivoMini.toString(),
                                nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                                urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                                error                  : it.error,
                                tipo                   : it.tipo,
                                mapsOriginal           : it.mapsOriginal,
                                mapsNormal             : it.mapsNormal,
                                mapsMini               : it.mapsMini,
                                widthOriginal          : widthOriginal,
                                heightOriginal         : heightOriginal,
                                widthNormal            : widthNormal,
                                heightNormal           : heightNormal,
                                widthMini              : widthMini,
                                heightMini             : heightMini
                        ])
                    }
                }
                def likes = Likes.findAllByPublicacion(it.id.toLong())
                def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())
                lstMedia.add([
                        id                        : it.id,
                        fechaRegistro             : it.fechaRegistro,
                        fechaPublicacion          : it.fechaPublicacion,
                        tituloPost                : it.tituloPost,
                        descripcionPost           : it.descripcionPost,
                        estatus                   : it.estatus,
                        error                     : it.error,
                        idToken                   : it.idToken,
                        tipoPost                  : it.tipoPost,
                        publicar                  : it.publicar,
                        media                     : objtMedia,
                        idCategoria               : it.categoria.id,
                        nombreCategoria           : it.categoria.nombre,
                        linkInsignia              : it.linkInsignia,
                        urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                        likesCount                : likes.size(),
                        commentsCount             : comentarios.size()
                ])
            }
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Exito"
            respuestaReturn.publicaciones = lstMedia
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }
        respond respuestaReturn
    }

    def postsInCollectionExclusive() {
        def respuestaReturn = [:]
        def lstMedia = []
        def lstMediaAux = []

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        Publicacion.createCriteria().list([sort: ["fechaRegistro": "desc"], max: params.max.toInteger(), offset: params.offset.toInteger()]) {
            and {
                eq("categoria", Categoria.get(params.collectionId.toLong()))
                eq("tipoPost", "1")
                eq("estatus", "0")
                ne("publicar", "2")
            }
        }.collect {
            def objtMedia = []
            def idAtleta = it.atleta.id

            if (it.media.size() > 0) {

                it.media.each() {

                    def urlArchivoOriginal = ""
                    def urlArchivoNormal = ""
                    def urlArchivoMini = ""
                    def urlArchivoDifuminada = ""
                    def widthOriginal = 0
                    def heightOriginal = 0
                    def widthNormal = 0
                    def heightNormal = 0
                    def widthMini = 0
                    def heightMini = 0

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))


                    // obtener url de archivos en cloud
                    def blob = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoOriginal.toString()}").setAcl(acls).build()
                    urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobNormal = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoNormal.toString()}").setAcl(acls).build()
                    urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobMini = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoMini.toString()}").setAcl(acls).build()
                    urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                    def blobDifu = BlobInfo.newBuilder(it.bucket, "${idAtleta.toString()}/${it.nombreArchivoDifuminada.toString()}").setAcl(acls).build()
                    urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)


                    if(it.tipo != "video/mp4") {
                        def imgNormal = ImageIO.read(new URL(urlArchivoNormal.toString()));
                        widthNormal=    imgNormal.getWidth()
                        heightNormal=   imgNormal.getHeight()
                    }

                    objtMedia.add([
                            descripcion            : it.descripcion,
                            bucket                 : it.bucket,
                            nombreArchivoOriginal  : it.nombreArchivoOriginal.toString(),
                            urlArchivoOriginal     : urlArchivoOriginal.toString(),
                            nombreArchivoNormal    : it.nombreArchivoNormal.toString(),
                            urlArchivoNormal       : urlArchivoNormal.toString(),
                            nombreArchivoMini      : it.nombreArchivoMini.toString(),
                            urlArchivoMini         : urlArchivoMini.toString(),
                            nombreArchivoDifuminada: it.nombreArchivoDifuminada.toString(),
                            urlArchivoDifuminada   : urlArchivoDifuminada.toString(),
                            error                  : it.error,
                            tipo                   : it.tipo,
                            mapsOriginal           : it.mapsOriginal,
                            mapsNormal             : it.mapsNormal,
                            mapsMini               : it.mapsMini,
                            widthOriginal          : widthOriginal,
                            heightOriginal         : heightOriginal,
                            widthNormal            : widthNormal,
                            heightNormal           : heightNormal,
                            widthMini              : widthMini,
                            heightMini             : heightMini
                    ])
                }
            }

            def likes = Likes.findAllByPublicacion(it.id.toLong())
            def comentarios = Comentarios.findAllByPublicacion(it.id.toLong())

            lstMedia.add([
                    id                        : it.id,
                    fechaRegistro             : it.fechaRegistro,
                    fechaPublicacion          : it.fechaPublicacion,
                    tituloPost                : it.tituloPost,
                    descripcionPost           : it.descripcionPost,
                    estatus                   : it.estatus,
                    error                     : it.error,
                    idToken                   : it.idToken,
                    tipoPost                  : it.tipoPost,
                    publicar                  : it.publicar,
                    media                     : objtMedia,
                    idCategoria               : it.categoria.id,
                    nombreCategoria           : it.categoria.nombre,
                    linkInsignia              : it.linkInsignia,
                    urlArchivoOriginalInsignia: serchInsignia(it.atleta.id, it.icon.archivoNombreOriginal),
                    likesCount                : likes.size(),
                    commentsCount             : comentarios.size()
            ])
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.publicaciones = lstMedia

        respond respuestaReturn
    }

    def countLikesComments() {
        def respuestaReturn = [:]

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def likes = Likes.findAllByPublicacion(params.postId.toLong())
        def comentarios = Comentarios.findAllByPublicacion(params.postId.toLong())

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.likesCount = likes.size()
        respuestaReturn.commentsCount = comentarios.size()

        respond respuestaReturn
    }

    def elementosPosts() {
        def respuestaReturn = [:]

        respuestaReturn.errorAPI = true
        respuestaReturn.mensajeAPI = "No se recibieron parametros"
        respond respuestaReturn
    }

    def listaCategoriasPreferencias() {
        def respuestaReturn = [:]

        if (!params) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No se recibieron parametros"
            respond respuestaReturn
            return
        }

        def usuario = Usuario.findByUsername(params.userName)
        def preferencias = Preferencias.findAllByUsuario(usuario.id.toLong())

        def listpreferencias = []
        if (preferencias) {
            preferencias.each {
                def preferencia = [:]
                preferencia.id = it.id
                preferencia.fechaRegistro = it.fechaRegistro
                preferencia.estatus = it.estatus
                preferencia.idUsuario = it.usuario.id
                preferencia.nombreUsuario = it.usuario.nombre
                preferencia.apellidosUsuario = it.usuario.apellidos

                listpreferencias.push(preferencia)
            }
        }

        respuestaReturn.errorAPI = false
        respuestaReturn.mensajeAPI = "Exito"
        respuestaReturn.preferencias = listpreferencias

        respond respuestaReturn
    }

    def guardarPreferencias() {
        def params = request.JSON
        def respuestaReturn = [:]
        def fecha = new Date()
        def usuario = Usuario.findByUsername(params.userName)

        if (params.preference == false || params.preference == "false") {
            def preferencias = new Preferencias()

            preferencias.fechaRegistro = fecha
            preferencias.estatus = params.preference
            preferencias.categoria = Categoria.get(params.collectionId.toLong())
            preferencias.usuario = Usuario.get(usuario.id.toLong())

            if (preferencias.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "preferencia creada"
                respuestaReturn.preferencias = preferencias
            } else {
                def erroresList = []
                preferencias.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = erroresList
            }
        } else {
            def deletePreferencia = Preferencias.findAllByUsuarioAndCategoria(usuario.id.toLong(), params.collectionId.toLong())
            deletePreferencia.delete(flush: true);

            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "se elimino el registro"
        }

        respond respuestaReturn
    }

    def perfilUsuario() {
        def respuestaReturn = [:]
        try {
            def usuarioInstance = Usuario.findAllByUsername(params.userName)
            respuestaReturn.errorAPI = false
            respuestaReturn.usuarioInstance = usuarioInstance
        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }
        respond respuestaReturn
    }
}
