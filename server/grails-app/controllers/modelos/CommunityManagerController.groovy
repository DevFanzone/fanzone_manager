package modelos

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.rest.*
import com.google.cloud.storage.*;
import org.apache.commons.io.FileUtils;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

@Secured(['ROLE_COMMUNITY_MANAGER','ROLE_ADMINISTRADOR'])
class CommunityManagerController extends RestfulController {

    CommunityManagerController() {
        super(CommunityManager)
    }

    def savePlayer() {
        def params = request.JSON

        def respuestaReturn = [:]
        def atletaNew = null
        def fecha = new Date()
        def usuario = Usuario.findByUsername(params.modelo.username)
        def conf = Configuracion.findAll()
        println conf[0].bucket + " " + conf[0].rutaTempArchivos
        def archivoNew = ""
        def replaceCadena = ""
        def nombreArchivo = ""
        def nombreArchivoAux = ""
        UUID uuid = UUID.randomUUID();
        def urlArchivo = ""
        byte[] decodedBytes = null
        def mensajeApi = "";

        if (params.modelo.fotoBytes == "noBytes") {
        } else {
            if (params.modelo.fotoType == "image/jpeg") {
                replaceCadena = "data:image/jpeg;base64,"
                nombreArchivoAux = uuid.toString() + ".png"
                decodedBytes = Base64.getDecoder().decode(params.modelo.fotoBytes.replace(replaceCadena, ""))
                archivoNew = new File(conf[0].rutaTempArchivos + nombreArchivoAux)
                archivoNew.setBytes(decodedBytes)
            }
            if (params.modelo.fotoType == "image/png") {
                replaceCadena = "data:image/png;base64,"
                nombreArchivoAux = uuid.toString() + ".png"
                decodedBytes = Base64.getDecoder().decode(params.modelo.fotoBytes.replace(replaceCadena, ""))
                archivoNew = new File(conf[0].rutaTempArchivos + nombreArchivoAux)
                archivoNew.setBytes(decodedBytes)
            }
        }

        try {
            if (params.modelo.id == "undefined" || params.modelo.id == null || params.modelo.id == "0" || params.modelo.id == 0) {
                atletaNew = new Atleta()

                atletaNew.nombre        = params.modelo.nombre
                atletaNew.sexo          = params.modelo.sexo
                atletaNew.telefono      = params.modelo.telefono
                atletaNew.email         = params.modelo.email
                atletaNew.sitioWeb      = params.modelo.sitioWeb
                atletaNew.ocupacion     = params.modelo.ocupacion
                atletaNew.foto          = "sn"
                atletaNew.fotoNombre    = "sn"
                atletaNew.fechaRegistro = fecha
                atletaNew.estatus       = "0"
                atletaNew.usuario       = Usuario.get(usuario.id.toLong())
                atletaNew.liveStreaming = false

                mensajeApi              = "Created player"
            } else {
                atletaNew               = Atleta.get(params.modelo.id.toLong())

                atletaNew.nombre        = params.modelo.nombre
                atletaNew.sexo          = params.modelo.sexo
                atletaNew.telefono      = params.modelo.telefono
                atletaNew.email         = params.modelo.email
                atletaNew.sitioWeb      = params.modelo.sitioWeb
                atletaNew.ocupacion     = params.modelo.ocupacion
                atletaNew.usuario       = Usuario.get(params.modelo.idManager)

                mensajeApi = "Modified player"
            }

            if (atletaNew.save(flush: true)) {

                if (params.modelo.fotoBytes != "noBytes") {
                    InputStream is = new FileInputStream(conf[0].rutaTempArchivos + nombreArchivoAux);
                    BufferedImage bi = ImageIO.read(is);
                    double r = get1080Portion(bi.getWidth(), bi.getHeight());
                    File f = new File(conf[0].rutaTempArchivos + uuid.toString() + "v2.jpg");
                    nombreArchivo = uuid.toString() + "v2.jpg"
                    FileUtils.writeByteArrayToFile(f, rescaleTo(bi, (int) Math.round(bi.getWidth() / r), (int) Math.round(bi.getHeight() / r), uuid.toString() + "v2"));

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // guardar foto en cloud
                    def blob = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${atletaNew.id.toString()}/${nombreArchivo.toString()}").setAcl(acls).build(), f.getBytes())
                    urlArchivo=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    def atletaMod = Atleta.get(atletaNew.id.toLong())
                    atletaMod.foto = urlArchivo.toString()
                    atletaMod.fotoNombre = nombreArchivo

                    if (atletaMod.save(flush: true)) {
                        mensajeApi = "The player have been saved"
                    }
                }
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = mensajeApi
                respuestaReturn.atleta = atletaNew
            } else {
                def erroresList = []
                atletaNew.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "An error has occurred.";
            }
        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred.";
        }

        respond respuestaReturn
    }

    def updateStatusPlayer() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def atleta = Atleta.get(params.idPlayer.toLong())
            atleta.estatus = params.status

            if (atleta.save(flush: true)) {
                respuestaReturn.errorAPI = false
            }
        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }

        respond respuestaReturn
    }

    def listCategoriesByPlayer() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            if (!params) {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "No se recibieron parametros"
                respond respuestaReturn
                return
            }

            def categorias = Categoria.findAllByAtletaAndEstatus(params.idAtleta.toLong(), "0")

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Exito"
            respuestaReturn.categorias = categorias

        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }

        respond respuestaReturn
    }

    def listCommunityManagerSelect() {
        def respuestaReturn = [:]
        def usuarios = []
        def managers = Usuario.findAll()

        if (managers) {
            managers.each {
                def perfil = it.getAuthorities()
                if (perfil[0].authority == 'ROLE_COMMUNITY_MANAGER') {
                    usuarios.push(it)
                }
            }
            respuestaReturn.usuarios = usuarios
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "Not there is users"
        }

        respond respuestaReturn
    }

    def listPlayers() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def usuario = Usuario.findByUsername(params.usernameManager)
            def conf = Configuracion.findAll()
            def role = usuario.getAuthorities()
            def atletas = [];

            if (role[0].authority == 'ROLE_ADMINISTRADOR') {
                def usuarios = Usuario.findAll()
                usuarios.each {
                    def r = it.getAuthorities()
                    if (r[0].authority == 'ROLE_ADMINISTRADOR' || r[0].authority == 'ROLE_COMMUNITY_MANAGER') {
                        def a = Atleta.findAllByUsuario(it.id)
                        a.each {
                            atletas.push(it)
                        }
                    }
                }
            } else if (role[0].authority == 'ROLE_COMMUNITY_MANAGER') {
                atletas = Atleta.createCriteria().list() {
                    eq("usuario", Usuario.get(usuario.id))
                    eq("estatus", '0')
                }.collect {
                    return it;
                }
            }

            atletas = atletas.sort { it.id }
            if (atletas) {
                def atletasList = []
                atletas.each {
                    def atleta = [:]
                    def urlArchivo = ""

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // obtener url de la foto en cloud
                    def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.id.toString()}/${it.fotoNombre.toString()}").setAcl(acls).build()
                    urlArchivo=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                    atleta.id            = it.id
                    atleta.fechaRegistro = it.fechaRegistro
                    atleta.nombre        = it.nombre
                    atleta.sexo          = it.sexo
                    atleta.telefono      = it.telefono
                    atleta.email         = it.email
                    atleta.sitioWeb      = parsearSitioWeb(it.sitioWeb)
                    atleta.ocupacion     = it.ocupacion
                    atleta.foto          = urlArchivo
                    atleta.fotoNombre    = it.fotoNombre
                    atleta.estatus       = it.estatus
                    atleta.manager       = it.usuario.username
                    atleta.idManager     = it.usuario.id
                    atletasList.push(atleta)
                }
                respuestaReturn.atletas = atletasList
                respuestaReturn.manager = usuario
            } else {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "Not there is players"
            }
        } catch (e) {
            println "error " + e
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }

        respond respuestaReturn
    }

    def savePost() {
        def params = request.JSON
        def respuestaReturn = [:]
        def listaElementosCloud = []
        def listaMedia = []
        def listaRedeSociales = []
        def lista = []
        def maps = []
        def listaRedes = []

        params.redes.each {
            listaRedes << it
        }

        def listaElementos = []
        params.elementos.each {
            listaElementos << it
        }

        if (params.modelo.tipoPost == "3") {
            Publicacion.createCriteria().list() {
                eq("categoria", Categoria.get(params.modelo.idCategoria.toLong()))
                eq("tipoPost", "3")
            }.each() {
                lista.add([id: it.id])
            }
            if (lista.size() > 0) {
                def deletePublicacion = Publicacion.get(lista[0].id.toLong())
                deletePublicacion.delete(flush: true);
            }
        }

        def usuarioNew = Usuario.findByUsername(params.modelo.username)
        def conf = Configuracion.findAll()
        def fecha = new Date()
        try {
            if (params.totalElementos > 0) {
                for (int b = 0; b < listaElementos.size(); b++) {
                    UUID uuid = UUID.randomUUID();
                    def replaceCadena = ""
                    def nombreArchivoOrignal = ""
                    def nombreArchivoOrignalCover = ""
                    def archivoCover = ""
                    def urlArchivoCover = ""
                    def archivoOriginal = ""
                    def nombreArchivoNormal = ""
                    def nombreArchivoMini = ""
                    def nombreArchivoDifuminada = ""
                    def urlArchivoOriginal = ""
                    def urlArchivoNormal = ""
                    def urlArchivoMini = ""
                    def urlArchivoDifuminada = ""
                    byte[] decodedBytes = null

                    if (listaElementos[b].imagenBytes == "noBytes") {
                        def archivo = Publicacion.findById(params.modelo.id)
                        archivo.media.each {
                            if (it.nombreArchivoOriginal == listaElementos[b].nombre) {
                                if (listaElementos[b].delete == true) {
                                    if (it.tipo == 'video/mp4') {
                                        def fileOriginal = new File(conf[0].rutaTempArchivos + it.nombreArchivoOriginal);
                                        fileOriginal.delete();

                                        def fileNormal = new File(conf[0].rutaTempArchivos + it.nombreArchivoNormal);
                                        fileNormal.delete();

                                        def fileDifuminada = new File(conf[0].rutaTempArchivos + it.nombreArchivoDifuminada);
                                        fileDifuminada.delete();
                                    } else {
                                        def fileOriginal = new File(conf[0].rutaTempArchivos + it.nombreArchivoOriginal);
                                        fileOriginal.delete();

                                        def fileNormal = new File(conf[0].rutaTempArchivos + it.nombreArchivoNormal);
                                        fileNormal.delete();

                                        def fileMini = new File(conf[0].rutaTempArchivos + it.nombreArchivoMini);
                                        fileMini.delete();

                                        def fileDifuminada = new File(conf[0].rutaTempArchivos + it.nombreArchivoDifuminada);
                                        fileDifuminada.delete();
                                    }
                                } else {
                                    def elemento = [:]
                                    elemento.descripcion             = listaElementos[b].descripcionElemento
                                    elemento.bucket                  = it.bucket
                                    elemento.error                   = it.error
                                    elemento.tipo                    = it.tipo
                                    elemento.nombreArchivoOriginal   = it.nombreArchivoOriginal
                                    elemento.urlArchivoOriginal      = it.urlArchivoOriginal
                                    elemento.nombreArchivoNormal     = it.nombreArchivoNormal
                                    elemento.urlArchivoNormal        = it.urlArchivoNormal
                                    elemento.nombreArchivoMini       = it.nombreArchivoMini
                                    elemento.urlArchivoMini          = it.urlArchivoMini
                                    elemento.nombreArchivoDifuminada = it.nombreArchivoDifuminada
                                    elemento.urlArchivoDifuminada    = it.urlArchivoDifuminada
                                    elemento.mapsOriginal            = it.mapsOriginal
                                    elemento.mapsNormal              = it.mapsNormal
                                    elemento.mapsMini                = it.mapsMini

                                    if (listaElementos[b].cover.cover == true) {
                                        def fileNormal = new File(conf[0].rutaTempArchivos + it.nombreArchivoNormal);
                                        fileNormal.delete();

                                        def extAux = ".png";
                                        if (listaElementos[b].cover.tipo == "image/jpeg")
                                            extAux = ".jpg";
                                        replaceCadena = "data:" + listaElementos[b].cover.tipo + ";base64,"
                                        nombreArchivoOrignalCover = uuid.toString() + extAux
                                        decodedBytes = Base64.getDecoder().decode(listaElementos[b].cover.archivo.replace(replaceCadena, ""))
                                        archivoCover = new File(conf[0].rutaTempArchivos + nombreArchivoOrignalCover)
                                        archivoCover.setBytes(decodedBytes)

                                        // iniciar servicios cloud
                                        def storage =StorageOptions.getDefaultInstance().getService();
                                        def acls = new java.util.ArrayList<Acl>()
                                        acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                                        // guardar cover en cloud
                                        def blobOriginal = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoOrignalCover.toString()}").setAcl(acls).build(), archivoCover.getBytes())
                                        urlArchivoCover=  storage.signUrl(blobOriginal, 2, java.util.concurrent.TimeUnit.DAYS)

                                        elemento.nombreArchivoNormal = nombreArchivoOrignalCover.toString();
                                        elemento.urlArchivoNormal = urlArchivoCover.toString();
                                    }
                                    listaElementosCloud.push(elemento)
                                }
                            }
                        }
                    } else {
                        if (listaElementos[b].tipo == "image/jpeg") {
                            replaceCadena        = "data:image/jpeg;base64,"
                            nombreArchivoOrignal = uuid.toString() + ".jpg"
                            nombreArchivoNormal  = uuid.toString() + "normal" + ".jpg"
                            nombreArchivoMini    = uuid.toString() + "mini" + ".jpg"
                            decodedBytes         = Base64.getDecoder().decode(listaElementos[b].archivo.replace(replaceCadena, ""))
                            archivoOriginal      = new File(conf[0].rutaTempArchivos + nombreArchivoOrignal)
                            archivoOriginal.setBytes(decodedBytes)
                        }
                        if (listaElementos[b].tipo == "image/png") {
                            replaceCadena        = "data:image/png;base64,"
                            nombreArchivoOrignal = uuid.toString() + ".jpg"
                            nombreArchivoNormal  = uuid.toString() + "normal" + ".jpg"
                            nombreArchivoMini    = uuid.toString() + "mini" + ".jpg"
                            decodedBytes         = Base64.getDecoder().decode(listaElementos[b].archivo.replace(replaceCadena, ""))
                            archivoOriginal      = new File(conf[0].rutaTempArchivos + nombreArchivoOrignal)
                            archivoOriginal.setBytes(decodedBytes)
                        }
                        if (listaElementos[b].tipo == "video/mp4") {
                            if (listaElementos[b].cover != null) {
                                def extAux = ".png";
                                if (listaElementos[b].cover.tipo == "image/jpeg")
                                    extAux = ".jpg";
                                replaceCadena             = "data:" + listaElementos[b].cover.tipo + ";base64,"
                                nombreArchivoOrignalCover = uuid.toString() + extAux
                                decodedBytes              = Base64.getDecoder().decode(listaElementos[b].cover.archivo.replace(replaceCadena, ""))
                                archivoCover              = new File(conf[0].rutaTempArchivos + nombreArchivoOrignalCover)
                                archivoCover.setBytes(decodedBytes)
                            }
                            replaceCadena                 = "data:video/mp4;base64,"
                            nombreArchivoOrignal          = uuid.toString() + ".mp4"
                            decodedBytes                  = Base64.getDecoder().decode(listaElementos[b].archivo.replace(replaceCadena, ""))
                            archivoOriginal               = new File(conf[0].rutaTempArchivos + nombreArchivoOrignal)
                            archivoOriginal.setBytes(decodedBytes)
                        }

                        if (listaElementos[b].tipo == "video/quicktime") {
                            replaceCadena        = "data:video/quicktime;base64,"
                            nombreArchivoOrignal = uuid.toString() + ".mov"
                            decodedBytes         = Base64.getDecoder().decode(listaElementos[b].archivo.replace(replaceCadena, ""))
                            archivoOriginal      = new File(conf[0].rutaTempArchivos + nombreArchivoOrignal)
                            archivoOriginal.setBytes(decodedBytes)
                        }
                        if (listaElementos[b].tipo == "image/jpeg" || listaElementos[b].tipo == "image/png") {
                            InputStream is       = new FileInputStream(conf[0].rutaTempArchivos + nombreArchivoOrignal);
                            BufferedImage bi     = ImageIO.read(is);

                            double r             = get1080Portion(bi.getWidth(), bi.getHeight());
                            File fileNormal      = new File(conf[0].rutaTempArchivos + nombreArchivoNormal);
                            FileUtils.writeByteArrayToFile(fileNormal, rescaleTo(bi, (int) Math.round(bi.getWidth() / r), (int) Math.round(bi.getHeight() / r), uuid.toString() + "normal"));

                            double r2            = get180Portion(bi.getWidth(), bi.getHeight());
                            File fileMini        = new File(conf[0].rutaTempArchivos + nombreArchivoMini);
                            FileUtils.writeByteArrayToFile(fileMini, rescaleTo(bi, (int) Math.round(bi.getWidth() / r2), (int) Math.round(bi.getHeight() / r2), uuid.toString() + "mini"));

                            def text                = "Exclusive content by PopFlyXP";
                            nombreArchivoDifuminada = uuid.toString() + "difuminada.jpg"

                            File input              = new File(conf[0].rutaTempArchivos + uuid.toString() + "normal.jpg");
                            File overlay            = new File(conf[0].rutaTempArchivos + "exclusiveContent.png");
                            File fileDifuminada     = new File(conf[0].rutaTempArchivos + nombreArchivoDifuminada);

                            addImageWatermark(overlay, "png", input, fileDifuminada);

                            // iniciar servicios cloud
                            def storage =StorageOptions.getDefaultInstance().getService();
                            def acls = new java.util.ArrayList<Acl>()
                            acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                            // guardar fotos en cloud
                            def blobOriginal = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoOrignal.toString()}").setAcl(acls).build(), archivoOriginal.getBytes())
                            urlArchivoOriginal=  storage.signUrl(blobOriginal, 2, java.util.concurrent.TimeUnit.DAYS)

                            def blobNormal = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoNormal.toString()}").setAcl(acls).build(), fileNormal.getBytes())
                            urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                            def blobMini = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoMini.toString()}").setAcl(acls).build(), fileMini.getBytes())
                            urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                            def blobDifu = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoDifuminada.toString()}").setAcl(acls).build(), fileDifuminada.getBytes())
                            urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)

                            def blobCover = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoOrignalCover.toString()}").setAcl(acls).build(), archivoCover.getBytes())
                            urlArchivoCover=  storage.signUrl(blobCover, 2, java.util.concurrent.TimeUnit.DAYS)
                        }

                        if (listaElementos[b].tipo == "video/mp4" || listaElementos[b].tipo == "video/quicktime") {
                            nombreArchivoDifuminada = "01_" + nombreArchivoOrignal.toString()
                            divideVideo(nombreArchivoOrignal.toString())
                            File videoDifuminado = new File(conf[0].rutaTempArchivos + uuid.toString() + "/" + "01_" + nombreArchivoOrignal.toString());

                            // iniciar servicios cloud
                            def storage =StorageOptions.getDefaultInstance().getService();
                            def acls = new java.util.ArrayList<Acl>()
                            acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                            // guardar archivos en cloud
                            def blob = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoOrignal.toString()}").setAcl(acls).build(), archivoOriginal.getBytes())
                            urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                            def blobDifu = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoDifuminada.toString()}").setAcl(acls).build(), videoDifuminado.getBytes())
                            urlArchivoDifuminada=  storage.signUrl(blobDifu, 2, java.util.concurrent.TimeUnit.DAYS)

                            funcionEliminarCarpeta1(new File(conf[0].rutaTempArchivos + uuid.toString()))
                        }

                        def elemento = [:]
                        elemento.descripcion             = listaElementos[b].descripcionElemento
                        elemento.bucket                  = conf[0].bucket.toString()
                        elemento.error                   = false
                        elemento.tipo                    = listaElementos[b].tipo
                        elemento.nombreArchivoOriginal   = nombreArchivoOrignal.toString()
                        elemento.urlArchivoOriginal      = urlArchivoOriginal.toString()
                        elemento.nombreArchivoNormal     = nombreArchivoNormal.toString()
                        elemento.urlArchivoNormal        = urlArchivoNormal.toString()
                        elemento.nombreArchivoMini       = nombreArchivoMini.toString()
                        elemento.urlArchivoMini          = urlArchivoMini.toString()
                        elemento.nombreArchivoDifuminada = nombreArchivoDifuminada.toString()
                        elemento.urlArchivoDifuminada    = urlArchivoDifuminada.toString()
                        elemento.mapsOriginal            = maps
                        elemento.mapsNormal              = maps
                        elemento.mapsMini                = maps
                        if (nombreArchivoOrignalCover.toString() != '') {
                            elemento.nombreArchivoNormal = nombreArchivoOrignalCover.toString()
                            elemento.urlArchivoNormal    = urlArchivoCover.toString()
                        }

                        listaElementosCloud.push(elemento)
                    }

                    println listaElementosCloud as JSON
                }
            }

            def publicacionNewDos = null;
            if (params.modelo.id == "undefined" || params.modelo.id == null || params.modelo.id == "0" || params.modelo.id == 0) {
                publicacionNewDos = new Publicacion()
                publicacionNewDos.fechaRegistro    = fecha
                publicacionNewDos.fechaPublicacion = params.modelo.fechaPublicar.toString()

                publicacionNewDos.atleta           = Atleta.get(params.modelo.idAtleta.toLong())
                publicacionNewDos.categoria        = Categoria.get(params.modelo.idCategoria.toLong())
                publicacionNewDos.icon             = Icon.get(5)
                publicacionNewDos.tituloPost       = params.modelo.tituloPost
                publicacionNewDos.descripcionPost  = params.modelo.descripcion
                publicacionNewDos.error            = false
                publicacionNewDos.idToken          = "se actualiza cuando se envia a la cola de datos"
                publicacionNewDos.tipoPost         = params.modelo.tipoPost
                publicacionNewDos.publicar         = params.modelo.publicar
                publicacionNewDos.linkInsignia     = ''
                publicacionNewDos.media            = listaElementosCloud
                publicacionNewDos.redes            = listaRedeSociales
                publicacionNewDos.usuario          = Usuario.get(usuarioNew.id)
                publicacionNewDos.estatus          = "0"
            } else {
                publicacionNewDos                  = Publicacion.get(params.modelo.id.toLong())
                publicacionNewDos.categoria        = Categoria.get(params.modelo.idCategoria.toLong())
                publicacionNewDos.tituloPost       = params.modelo.tituloPost
                publicacionNewDos.descripcionPost  = params.modelo.descripcion
                publicacionNewDos.tipoPost         = params.modelo.tipoPost
                publicacionNewDos.publicar         = params.modelo.publicar
                publicacionNewDos.fechaPublicacion = params.modelo.fechaPublicar
                publicacionNewDos.media            = listaElementosCloud

                if (params.modelo.publicar == '2' && params.modelo.estatus == '0') {
                    publicacionNewDos.estatus = "0"
                }
            }

            if (publicacionNewDos.save(flush: true)) {
            } else {
                def erroresList = []
                publicacionNewDos.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
            }

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Saved publication"
            respuestaReturn.manager = usuarioNew
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }

        respond respuestaReturn
    }

    def listPost() {
        def params = request.JSON
        def respuestaReturn = [:]
        def conf = Configuracion.findAll()
        def programDate = ""

        def publicaciones = Publicacion.findAllByAtleta(params.idPlayer, [sort: "fechaRegistro", order: "asc"])
        def publicacionesList = []
        if (publicaciones) {
            publicaciones.each {
                def publicacion = [:]
                if (it.fechaPublicacion && it.fechaPublicacion != 'null') {
                    programDate = Date.parse("dd/MM/yyyy HH:mm:ss", it.fechaPublicacion)
                }

                publicacion.id = it.id
                publicacion.fechaRegistro = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(it.fechaRegistro).toString()
                publicacion.fechaPublicacion = it.fechaPublicacion
                publicacion.tituloPost = it.tituloPost
                publicacion.descripcionPost = it.descripcionPost
                publicacion.estatus = it.estatus
                publicacion.estatusName = it.estatus == '0' ? (it.publicar == '2' ? 'pending' : 'enabled') : 'disabled'
                publicacion.error = it.error
                publicacion.redes = it.redes
                publicacion.media = it.media
                publicacion.numeroElementos = it.media.size()
                publicacion.idToken = it.idToken
                publicacion.tipoPost = it.tipoPost
                publicacion.tipoPostNombre = it.tipoPost == "1" ? "Private" : "Public"
                publicacion.publicar = it.publicar
                publicacion.publicarNombre = it.publicar == "1" ? "Immediately" : "On schedule date"
                publicacion.tags = "sin tags"
                publicacion.idUsuario = it.usuario == null ? 0 : it.usuario.id
                publicacion.nombreUsuario = it.usuario == null ? "" : it.usuario.nombre
                publicacion.idAtleta = it.atleta == null ? 0 : it.atleta.id
                publicacion.nombreAtleta = it.atleta == null ? "" : it.atleta.nombre
                publicacion.idCategoria = it.categoria == null ? 0 : it.categoria.id
                publicacion.nombreCategoria = it.categoria == null ? "" : it.categoria.nombre
                publicacion.rutaMaps = conf[0].rutaMaps
                publicacionesList.push(publicacion)
            }
            respuestaReturn.redacciones = publicacionesList
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "Not there is posts"
        }

        respond respuestaReturn
    }

    def getPost() {
        def params = request.JSON
        def respuestaReturn = [:]
        def publicacion = Publicacion.findAllById(params.id)

        if (publicacion) {
            respuestaReturn.redaccion = publicacion[0]
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "Not there is posts"
        }

        respond respuestaReturn
    }

    def updateStatusPost() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def publicacionNew = Publicacion.get(params.idPost.toLong())
            publicacionNew.estatus = params.status

            if (publicacionNew.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "Changed status"
                respuestaReturn.publicacion = publicacionNew
            } else {
                def erroresList = []
                publicacionNew.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = erroresList
            }
        } catch (e) {
            println "error " + e
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }

        respond respuestaReturn
    }

    def updateDatePost() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def publicacionNew = Publicacion.get(params.idPost.toLong())
            publicacionNew.fechaPublicacion = params.fechaPublicacion

            if (publicacionNew.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "Changed date and time"
                respuestaReturn.publicacion = publicacionNew
            } else {
                def erroresList = []
                publicacionNew.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = erroresList
            }
        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }

        respond respuestaReturn
    }

    def deletePost() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def deletePublicacion = Publicacion.get(params.idPost.toLong())
            deletePublicacion.delete(flush: true);
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Post delete"
        } catch (e) {
            println "error " + e
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred.";
        }

        respond respuestaReturn
    }

    def divideVideo(nameFile) {
        def conf = Configuracion.findAll()
        try {
            File file = new File(conf[0].rutaTempArchivos + nameFile);
            if (file.exists()) {

                String videoFileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                File splitFile = new File(conf[0].rutaTempArchivos + videoFileName);
                if (!splitFile.exists()) {
                    splitFile.mkdirs();
                }

                int i = 01;
                InputStream inputStream = new FileInputStream(file);
                String videoFile = splitFile.getAbsolutePath() + "/" + String.format("%02d", i) + "_" + file.getName();
                OutputStream outputStream = new FileOutputStream(videoFile);
                int totalPartsToSplit = 3;
                int splitSize = inputStream.available() / totalPartsToSplit;
                int streamSize = 0;
                int read = 0;
                while ((read = inputStream.read()) != -1) {

                    if (splitSize == streamSize) {
                        if (i != totalPartsToSplit) {
                            i++;
                            String fileCount = String.format("%02d", i);
                            videoFile = splitFile.getAbsolutePath() + "/" + fileCount + "_" + file.getName();
                            outputStream = new FileOutputStream(videoFile);
                            streamSize = 0;
                        }
                    }
                    outputStream.write(read);
                    streamSize++;
                }

                inputStream.close();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    def parsearSitioWeb(sitio) {
        def webSite = "";
        def parts = []
        parts = sitio.split("://");

        if (parts.size() > 1) {
            if (parts[0] == "http") {
                webSite = sitio;
            } else if (parts[0] == "https") {
                webSite = sitio;
            }
        } else {
            webSite = "http://" + sitio;
        }

        return webSite;
    }

    private static void funcionEliminarCarpeta1(File pArchivo) {
        if (!pArchivo.exists()) {
            return;
        }

        if (pArchivo.isDirectory()) {
            for (File f : pArchivo.listFiles()) {
                funcionEliminarCarpeta1(f);
            }
        }
        pArchivo.delete();
    }

    private static byte[] rescaleTo(BufferedImage bi, int w, int h, String typeName) throws IOException {
        int type = bi.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bi.getType();
        BufferedImage resizedImage = new BufferedImage(w, h, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bi, 0, 0, w, h, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", out);

        return out.toByteArray();
    }

    private static double get1080Portion(int width, int height) {
        final double NEGATIVE = -1.0;
        final double HEIGHT = 1080.0;
        final double WIDTH = 1920.0;
        double portion = NEGATIVE;
        double portion2 = NEGATIVE;

        if (height > HEIGHT) {
            portion = height / HEIGHT;
        }

        if (width > WIDTH) {
            portion2 = width / WIDTH;
        }

        if (portion != NEGATIVE || portion2 != NEGATIVE) {
            return portion < portion2 ? portion2 : portion;
        } else return -NEGATIVE;
    }

    private static double get180Portion(int width, int height) {
        final double NEGATIVE = -1.0;
        final double HEIGHT = 200.0;
        final double WIDTH = 360.0;
        double portion = NEGATIVE;
        double portion2 = NEGATIVE;
        if (height > HEIGHT)
            portion = height / HEIGHT;

        if (width > WIDTH)
            portion2 = width / WIDTH;

        if (portion != NEGATIVE || portion2 != NEGATIVE) {
            return portion < portion2 ? portion2 : portion;

        } else return -NEGATIVE;
    }

    private static void addImageWatermark(File watermark, String type, File source, File destination) throws IOException {
        BufferedImage image = ImageIO.read(source);
        BufferedImage overlay = resize(ImageIO.read(watermark), image.getHeight(), image.getWidth());
        int imageType = "png".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage watermarked = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
        Graphics2D w = (Graphics2D) watermarked.getGraphics();
        w.drawImage(image, 0, 0, null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        w.setComposite(alphaChannel);
        w.drawImage(overlay, 0, 0, null);
        ImageIO.write(watermarked, type, destination);
        w.dispose();
    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
