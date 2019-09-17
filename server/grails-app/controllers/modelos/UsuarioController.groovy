package modelos

import com.google.cloud.storage.*;
import grails.plugin.springsecurity.annotation.Secured
import grails.rest.*
import org.apache.commons.io.FileUtils
import seguridad.Role
import seguridad.UserRole
import javax.imageio.ImageIO
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage

@Secured(['ROLE_ADMINISTRADOR'])
class UsuarioController extends RestfulController {
    def permisosService

    UsuarioController() {
        super(Usuario)
    }

    def updateUser() {
        def params = request.JSON
        def respuestaReturn = [:]
        def usuario = null
        def usuarioExis = Usuario.findByUsername(params.username)

        if (params.id) {
            usuario = Usuario.get(params.id.toLong())
            if (usuario.nombre != params.nombre || usuario.username != params.username) {
                usuario.nombre = params.nombre

                if (usuario.username != params.username) {
                    if (usuarioExis) {
                        respuestaReturn.errorAPI = true
                        respuestaReturn.mensajeAPI = "existing user.";
                        respond respuestaReturn
                        return
                    } else {
                        usuario.username = params.username
                    }
                }
                usuario.save(flush: true)
            }
            permisosService.deleteRole(usuario)
            permisosService.setRole(usuario, params.role)
            respuestaReturn.errorAPI = false
        }
        respond respuestaReturn
    }

    def saveCommunityManager() {
        def params = request.JSON
        def respuestaReturn = [:]
        def usuario = null
        def usuarioExis = Usuario.findByUsername(params.username)

        if (params.id) {
            usuario = Usuario.get(params.id.toLong())
            if (usuario.nombre != params.nombre || usuario.apellidos != params.apellidos || usuario.telefono != params.telefono || usuario.password != params.password || usuario.username != params.username) {
                usuario.nombre = params.nombre
                usuario.apellidos = params.apellidos
                usuario.telefono = params.telefono

                if (usuario.password != params.password) {
                    usuario.password = params.password
                }

                if (usuario.username != params.username) {
                    if (usuarioExis) {
                        respuestaReturn.errorAPI = true
                        respuestaReturn.mensajeAPI = "existing user.";
                        respond respuestaReturn
                        return
                    } else {
                        usuario.username = params.username
                    }
                }

                if (usuario.save(flush: true)) {
                    permisosService.deleteRole(usuario)
                    permisosService.setRole(usuario, params.role)
                    respuestaReturn.errorAPI = false
                }
            }
        } else {
            usuario = new Usuario()
            if (usuarioExis) {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "existing user.";
                respond respuestaReturn
                return
            } else {
                usuario.nombre = params.nombre
                usuario.apellidos = params.apellidos
                usuario.telefono = params.telefono
                usuario.username = params.username
                usuario.password = params.password
                usuario.accessTokenFacebook = "sn"
                usuario.expiresInFacebook = "sn"
                usuario.accessTokenTwitter = "sn"
                usuario.expiresInTwitter = "sn"
                usuario.accessTokenInstagram = "sn"
                usuario.expiresInInstagram = "sn"
            }

            if (usuario.save(flush: true)) {
                permisosService.setRole(usuario, params.role)
                respuestaReturn.errorAPI = false
            }
        }
        respond respuestaReturn

    }

    def listCommunityManager() {
        def respuestaReturn = [:]
        def usuarios = Usuario.findAll([sort: "id", order: "desc"])
        if (usuarios) {
            def usuariosList = []
            usuarios.each {
                def usuario = [:]
                def roleUsuario = UserRole.findByUser(it)
                def roleNombre = "INVITADO"
                def role = 0
                if (roleUsuario) {
                    role = roleUsuario.role.id
                    roleNombre = roleUsuario.role.authority
                }
                if (role == 3) {
                    usuario.id = it.id
                    usuario.username = it.username
                    usuario.password = it.password
                    usuario.nombre = it.nombre
                    usuario.apellidos = it.apellidos
                    usuario.telefono = it.telefono
                    usuario.role = role
                    usuario.roleNombre = roleNombre
                    usuario.accessToken = it.accessToken
                    usuario.expiresIn = it.expiresIn
                    usuario.status = it.enabled
                    usuariosList.push(usuario)
                }
            }

            respuestaReturn = usuariosList
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No hay usuarios registrados"
        }
        respond respuestaReturn
    }

    def listUsers() {
        def respuestaReturn = [:]
        def usuarios = Usuario.findAll([sort: "id", order: "desc"])
        if (usuarios) {
            def usuariosList = []
            usuarios.each {
                def usuario = [:]
                def roleUsuario = UserRole.findByUser(it)
                def roleNombre = "INVITADO"
                def role = 0
                if (roleUsuario) {
                    role = roleUsuario.role.id
                    roleNombre = roleUsuario.role.authority
                }
                if (role == 5 || role == 6 || role == 7) {
                    usuario.id = it.id
                    usuario.username = it.username
                    usuario.nombre = it.nombre
                    usuario.role = role
                    usuario.roleNombre = roleNombre
                    usuario.status = it.enabled
                    usuario.socialNetwork = it.socialNetwork
                    usuario.foto_perfil = it.foto_perfil
                    usuariosList.push(usuario)
                }
            }
            respuestaReturn = usuariosList
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "No hay usuarios registrados"
        }
        respond respuestaReturn
    }

    def updateAccount() {
        def params = request.JSON
        def respuestaReturn = [:]
        def usuario = null
        def usuarioExis = Usuario.findByUsername(params.username)

        if (params.id) {
            usuario = Usuario.get(params.id.toLong())

            usuario.nombre = params.nombre
            if (usuario.username != params.username) {
                if (usuarioExis) {
                    respuestaReturn.errorAPI = true
                    respuestaReturn.mensajeAPI = "existing user.";
                    respond respuestaReturn
                    return
                } else {
                    usuario.username = params.username
                }
            }
        }

        if (usuario.save(flush: true)) {
            permisosService.deleteRole(usuario)
            permisosService.setRole(usuario, params.role)
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "success."
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

    def updateStatusUser() {
        def params = request.JSON
        def respuestaReturn = [:]
        try {
            def user = Usuario.get(params.idUser.toLong())
            user.enabled = params.status

            if (user.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "success"
            } else {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "An error has occurred."
            }
        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }
        respond respuestaReturn
    }

    def setRole() {
        respond Usuario.list()
    }

    def roles() {
        def roles = Role.findAll()
        def rolesList = []
        if (roles) {
            roles.each {
                def role = [:]
                if (it.id == 5 || it.id == 6 || it.id == 7) {
                    role.id = it.id
                    role.authority = it.authority
                    rolesList.push(role)
                }
            }
        }
        respond rolesList
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
                            it.estatusName = it.estatus == '0' ? 'enabled' : 'disabled'
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

                    atleta.id = it.id
                    atleta.fechaRegistro = it.fechaRegistro
                    atleta.nombre = it.nombre
                    atleta.sexo = it.sexo
                    atleta.telefono = it.telefono
                    atleta.email = it.email
                    atleta.sitioWeb = parsearSitioWeb(it.sitioWeb)
                    atleta.ocupacion = it.ocupacion
                    atleta.foto          = urlArchivo
                    atleta.fotoNombre = it.fotoNombre
                    atleta.estatus = it.estatus
                    atleta.manager = it.usuario.username
                    atleta.idManager = it.usuario.id
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

    def deletePlayer() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def deleteAtleta = Atleta.get(params.idAtleta.toLong())
            deleteAtleta.delete(flush: true);

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Player removed"

        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred.";
        }

        respond respuestaReturn
    }

    def saveCategory() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def categoriaNew = null
            def fecha = new Date()
            def usuario = Usuario.findByUsername(params.modelo.username)
            def conf = Configuracion.findAll()
            UUID uuid = UUID.randomUUID();
            def replaceCadena = ""
            def fileOriginal = ""
            def nombreArchivoOriginal = ""
            def urlArchivoOriginal = ""
            def nombreArchivoNormal = ""
            def urlArchivoNormal = ""
            def nombreArchivoMini = ""
            def urlArchivoMini = ""
            def nombreArchivoSplash = ""
            def fileSplash = ""
            def urlSplah = ""
            def lastUpdateSplash = null
            byte[] decodedBytes = null
            def mensajeApi = "";

            if (params.modelo.imgSplashBytes != "noBytes") {
                if (params.modelo.imgSplashType == "image/jpeg") {
                    replaceCadena = "data:image/jpeg;base64,"
                    nombreArchivoSplash = uuid.toString() + "splash.jpg"
                    decodedBytes = Base64.getDecoder().decode(params.modelo.imgSplash.replace(replaceCadena, ""))
                    fileSplash = new File(conf[0].rutaTempArchivos + nombreArchivoSplash)
                    fileSplash.setBytes(decodedBytes)
                } else if (params.modelo.imgSplashType == "image/png") {
                    replaceCadena = "data:image/png;base64,"
                    nombreArchivoSplash = uuid.toString() + "splash.png"
                    decodedBytes = Base64.getDecoder().decode(params.modelo.imgSplash.replace(replaceCadena, ""))
                    fileSplash = new File(conf[0].rutaTempArchivos + nombreArchivoSplash)
                    fileSplash.setBytes(decodedBytes)
                }

                // iniciar servicios cloud
                def storage =StorageOptions.getDefaultInstance().getService();
                def acls = new java.util.ArrayList<Acl>()
                acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                // guardar foto en cloud
                def blob = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoSplash.toString()}").setAcl(acls).build(), fileSplash.getBytes())
                urlSplah=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)
            }

            if (params.modelo.imagenBytes != "noBytes") {
                if (params.modelo.imagenType == "image/jpeg") {
                    replaceCadena = "data:image/jpeg;base64,"
                    nombreArchivoOriginal = uuid.toString() + ".png"
                    nombreArchivoNormal = uuid.toString() + "normal" + ".jpg"
                    nombreArchivoMini = uuid.toString() + "mini" + ".jpg"
                    decodedBytes = Base64.getDecoder().decode(params.modelo.imagen.replace(replaceCadena, ""))
                    fileOriginal = new File(conf[0].rutaTempArchivos + nombreArchivoOriginal)
                    fileOriginal.setBytes(decodedBytes)
                }
                if (params.modelo.imagenType == "image/png") {
                    replaceCadena = "data:image/png;base64,"
                    nombreArchivoOriginal = uuid.toString() + ".png"
                    nombreArchivoNormal = uuid.toString() + "normal" + ".jpg"
                    nombreArchivoMini = uuid.toString() + "mini" + ".jpg"
                    decodedBytes = Base64.getDecoder().decode(params.modelo.imagen.replace(replaceCadena, ""))
                    fileOriginal = new File(conf[0].rutaTempArchivos + nombreArchivoOriginal)
                    fileOriginal.setBytes(decodedBytes)
                }

                InputStream is = new FileInputStream(conf[0].rutaTempArchivos + nombreArchivoOriginal);
                BufferedImage bi = ImageIO.read(is);

                double r = get1080Portion(bi.getWidth(), bi.getHeight());
                File fileNormal = new File(conf[0].rutaTempArchivos + nombreArchivoNormal);
                FileUtils.writeByteArrayToFile(fileNormal, rescaleTo(bi, (int) Math.round(bi.getWidth() / r), (int) Math.round(bi.getHeight() / r), uuid.toString() + "normal"));

                double r2 = get180Portion(bi.getWidth(), bi.getHeight());
                File fileMini = new File(conf[0].rutaTempArchivos + nombreArchivoMini);
                FileUtils.writeByteArrayToFile(fileMini, rescaleTo(bi, (int) Math.round(bi.getWidth() / r2), (int) Math.round(bi.getHeight() / r2), uuid.toString() + "mini"));

                // iniciar servicios cloud
                def storage =StorageOptions.getDefaultInstance().getService();
                def acls = new java.util.ArrayList<Acl>()
                acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                // guardar archivos en cloud
                def blob = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoOriginal.toString()}").setAcl(acls).build(), fileOriginal.getBytes())
                urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                def blobNormal = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(),  "${params.modelo.idAtleta.toString()}/${nombreArchivoNormal.toString()}").setAcl(acls).build(), fileNormal.getBytes())
                urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                def blobMini = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(),  "${params.modelo.idAtleta.toString()}/${nombreArchivoMini.toString()}").setAcl(acls).build(), fileMini.getBytes())
                urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)
            }

            if (params.modelo.id == "undefined" || params.modelo.id == null || params.modelo.id == "0" || params.modelo.id == 0) {
                categoriaNew = new Categoria()

                categoriaNew.fechaRegistro = fecha
                categoriaNew.nombre = params.modelo.nombre
                categoriaNew.descripcion = params.modelo.descripcion
                categoriaNew.archivoNombreOriginal = nombreArchivoOriginal.toString()
                categoriaNew.urlArchivoOriginal = urlArchivoOriginal.toString()
                categoriaNew.archivoNombreNormal = nombreArchivoNormal.toString()
                categoriaNew.urlArchivoNormal = urlArchivoNormal.toString()
                categoriaNew.archivoNombreMini = nombreArchivoMini.toString()
                categoriaNew.urlArchivoMini = urlArchivoMini.toString()
                categoriaNew.estatus = "0"
                categoriaNew.atleta = Atleta.get(params.modelo.idAtleta.toLong())
                categoriaNew.usuario = Usuario.get(usuario.id.toLong())
                categoriaNew.archivoNombreSplash = nombreArchivoSplash.toString()
                categoriaNew.urlSplah = urlSplah
                categoriaNew.lastUpdateSplash = new Date(System.currentTimeMillis())

                mensajeApi = "Created categorie"
            } else {
                categoriaNew = Categoria.get(params.modelo.id.toLong())

                categoriaNew.nombre = params.modelo.nombre
                categoriaNew.descripcion = params.modelo.descripcion
                categoriaNew.atleta = Atleta.get(params.modelo.idAtleta.toLong())
                if (params.modelo.imagenBytes != "noBytes") {
                    categoriaNew.archivoNombreOriginal = nombreArchivoOriginal.toString()
                    categoriaNew.urlArchivoOriginal = urlArchivoOriginal.toString()
                    categoriaNew.archivoNombreNormal = nombreArchivoNormal.toString()
                    categoriaNew.urlArchivoNormal = urlArchivoNormal.toString()
                    categoriaNew.archivoNombreMini = nombreArchivoMini.toString()
                    categoriaNew.urlArchivoMini = urlArchivoMini.toString()
                }

                if (params.modelo.imgSplashBytes != "noBytes") {
                    categoriaNew.archivoNombreSplash = nombreArchivoSplash.toString()
                    categoriaNew.urlSplah = urlSplah.toString()
                    categoriaNew.lastUpdateSplash = new Date(System.currentTimeMillis())
                }

                mensajeApi = "Modified category"
            }

            if (categoriaNew.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = mensajeApi
            } else {
                def erroresList = []
                categoriaNew.errors.allErrors.each {
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

    def listCategory() {
        def params = request.JSON
        def respuestaReturn = [:]
        def conf = Configuracion.findAll()

        def categorias = Categoria.findAllByAtleta(params.idPlayer, [sort: "id", order: "desc"])
        if (categorias) {
            def categoriasList = []
            categorias.each {
                def categoria = [:]
                def urlArchivoOriginal = ""
                def urlArchivoNormal = ""
                def urlArchivoMini = ""
                def urlArchivoSplash = ""

                // iniciar servicios cloud
                def storage =StorageOptions.getDefaultInstance().getService();
                def acls = new java.util.ArrayList<Acl>()
                acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                // obtener url de los archivos en cloud
                def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreOriginal.toString()}").setAcl(acls).build()
                urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                def blobNormal = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreNormal.toString()}").setAcl(acls).build()
                urlArchivoNormal=  storage.signUrl(blobNormal, 2, java.util.concurrent.TimeUnit.DAYS)

                def blobMini = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreMini.toString()}").setAcl(acls).build()
                urlArchivoMini=  storage.signUrl(blobMini, 2, java.util.concurrent.TimeUnit.DAYS)

                def blobSplash = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreSplash.toString()}").setAcl(acls).build()
                urlArchivoSplash=  storage.signUrl(blobSplash, 2, java.util.concurrent.TimeUnit.DAYS)

                categoria.id = it.id
                categoria.fechaRegistro = it.fechaRegistro
                categoria.nombre = it.nombre
                categoria.descripcion = it.descripcion
                categoria.archivoNombreOriginal = it.archivoNombreOriginal
                categoria.urlArchivoOriginal    = urlArchivoOriginal
                categoria.imagen                = urlArchivoNormal
                categoria.imagenNombre = it.archivoNombreNormal
                categoria.archivoNombreMini = it.archivoNombreMini
                categoria.imgSplash           = urlArchivoSplash
                categoria.urlArchivoMini        = urlArchivoMini
                categoria.estatus = it.estatus
                categoria.idManager = it.usuario.id
                categoria.nombreManager = it.usuario.nombre
                categoria.idAtleta = it.atleta.id
                categoria.nombreAtleta = it.atleta.nombre
                categoriasList.push(categoria)
            }
            respuestaReturn.categorias = categoriasList
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "Not there is categories"
        }

        respond respuestaReturn
    }

    def updateStatusCategory() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def categoriaNew = Categoria.get(params.idCategoria.toLong())
            categoriaNew.estatus = params.estatus

            if (categoriaNew.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "Categorie removed"
                respuestaReturn.categoria = categoriaNew
            } else {
                def erroresList = []
                categoriaNew.errors.allErrors.each {
                    erroresList.push(it.toString())
                }
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = erroresList
            }
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }

        respond respuestaReturn
    }

    def deleteCategory() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def deleteCategoria = Categoria.get(params.idCategoria.toLong())
            deleteCategoria.delete(flush: true);

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Category delete"

        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred.";
        }

        respond respuestaReturn
    }

    def saveSlider() {
        def params = request.JSON
        def respuestaReturn = [:]
        def mapas = []

        def listaElementos = []
        params.elementos.each {
            listaElementos << it
        }

        def usuarioModel = Usuario.findByUsername(params.modelo.username)
        def conf = Configuracion.findAll()
        def fecha = new Date()
        try {
            if (params.totalElementos > 0) {
                for (int b = 0; b < listaElementos.size(); b++) {
                    UUID uuid = UUID.randomUUID();
                    def replaceCadena = ""
                    def nombreArchivoOrignal = ""
                    def archivoOriginal = ""
                    def urlArchivoOriginal = ""

                    byte[] decodedBytes = null

                    if (listaElementos[b].tipo == "image/jpeg") {
                        replaceCadena = "data:image/jpeg;base64,"
                        nombreArchivoOrignal = uuid.toString() + ".jpg"
                        decodedBytes = Base64.getDecoder().decode(listaElementos[b].archivo.replace(replaceCadena, ""))
                        archivoOriginal = new File(conf[0].rutaTempArchivos + nombreArchivoOrignal)
                        archivoOriginal.setBytes(decodedBytes)
                    }
                    if (listaElementos[b].tipo == "image/png") {
                        replaceCadena = "data:image/png;base64,"
                        nombreArchivoOrignal = uuid.toString() + ".jpg"
                        decodedBytes = Base64.getDecoder().decode(listaElementos[b].archivo.replace(replaceCadena, ""))
                        archivoOriginal = new File(conf[0].rutaTempArchivos + nombreArchivoOrignal)
                        archivoOriginal.setBytes(decodedBytes)
                    }

                    // iniciar servicios cloud
                    def storage =StorageOptions.getDefaultInstance().getService();
                    def acls = new java.util.ArrayList<Acl>()
                    acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                    // crear archivo en cloud
                    def blobOriginal = storage.create(BlobInfo.newBuilder(conf[0].bucket.toString(), "${params.modelo.idAtleta.toString()}/${nombreArchivoOrignal.toString()}").setAcl(acls).build(), archivoOriginal.getBytes())
                    urlArchivoOriginal=  storage.signUrl(blobOriginal, 2, java.util.concurrent.TimeUnit.DAYS)

                    def newSlider = new Slider()

                    newSlider.fechaRegistro = fecha
                    newSlider.nombre = nombreArchivoOrignal.toString()
                    newSlider.tipo = listaElementos[b].tipo
                    newSlider.archivoNombreOriginal = nombreArchivoOrignal.toString()
                    newSlider.urlArchivoOriginal = urlArchivoOriginal.toString()
                    newSlider.mapas = mapas
                    newSlider.estatus = "0"
                    newSlider.height = "editarDespues"
                    newSlider.width = "editarDespues"
                    newSlider.atleta = Atleta.get(params.modelo.idAtleta.toLong())
                    newSlider.usuario = Usuario.get(usuarioModel.id.toLong())

                    if (newSlider.save(flush: true)) {
                    } else {
                        def erroresList = []
                        newSlider.errors.allErrors.each {
                            erroresList.push(it.toString())
                        }
                    }
                }
            }
            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "The sliders have been saved"
            respuestaReturn.manager = usuarioModel
        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }

        respond respuestaReturn
    }

    def listSliders() {
        def params = request.JSON
        def respuestaReturn = [:]
        def conf = Configuracion.findAll()

        def sliders = Slider.findAllByAtleta(params.idPlayer, [sort: "id", order: "desc"])
        if (sliders) {
            def slidersList = []
            sliders.each {
                def slider = [:]
                def urlArchivoOriginal = ""

                // iniciar servicios cloud
                def storage =StorageOptions.getDefaultInstance().getService();
                def acls = new java.util.ArrayList<Acl>()
                acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))

                // obtener url de los archivos en cloud
                def blob = BlobInfo.newBuilder(conf[0].bucket.toString(), "${it.atleta.id.toString()}/${it.archivoNombreOriginal.toString()}").setAcl(acls).build()
                urlArchivoOriginal=  storage.signUrl(blob, 2, java.util.concurrent.TimeUnit.DAYS)

                slider.id = it.id
                slider.fechaRegistro = it.fechaRegistro
                slider.nombre = it.nombre
                slider.tipo = it.tipo
                slider.archivoNombreOriginal = it.archivoNombreOriginal
                slider.urlArchivoOriginal    = urlArchivoOriginal
                slider.mapas = it.mapas
                slider.estatus = it.estatus
                slider.idManager = it.usuario.id
                slider.nombreManager = it.usuario.nombre
                slider.idAtleta = it.atleta.id
                slider.nombreAtleta = it.atleta.nombre
                slider.rutaMaps = conf[0].rutaMaps
                slidersList.push(slider)
            }
            respuestaReturn.sliders = slidersList
        } else {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "Not there is sliders"
        }

        respond respuestaReturn
    }

    def totalSliders() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            if (!params) {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "No se recibieron parametros"
                respond respuestaReturn
                return
            }

            def sliders = Slider.findAllByAtleta(params.idAtleta.toLong())

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Exito"
            respuestaReturn.numberSliders = sliders.size()

        } catch (e) {
            e.printStackTrace()
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred."
        }

        respond respuestaReturn
    }

    def updateStatusSlider() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def banner = Slider.get(params.idBanner.toLong())
            banner.estatus = params.status

            if (banner.save(flush: true)) {
                respuestaReturn.errorAPI = false
                respuestaReturn.mensajeAPI = "success"
            } else {
                respuestaReturn.errorAPI = true
                respuestaReturn.mensajeAPI = "An error has occurred."
            }
        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred." + e
        }

        respond respuestaReturn
    }

    def deleteSlider() {
        def params = request.JSON
        def respuestaReturn = [:]

        try {
            def deleteSlider = Slider.get(params.idSlider.toLong())
            deleteSlider.delete(flush: true);

            respuestaReturn.errorAPI = false
            respuestaReturn.mensajeAPI = "Slider delete"

        } catch (e) {
            respuestaReturn.errorAPI = true
            respuestaReturn.mensajeAPI = "An error has occurred.";
        }

        respond respuestaReturn
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

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

}
