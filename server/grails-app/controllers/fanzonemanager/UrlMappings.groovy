package fanzonemanager

class UrlMappings {

    static mappings = {
        delete "/$controller/$id(.$format)?"(action: "delete")
        get "/$controller(.$format)?"(action: "index")
        get "/$controller/$id(.$format)?"(action: "show")
        post "/$controller(.$format)?"(action: "save")
        put "/$controller/$id(.$format)?"(action: "update")
        patch "/$controller/$id(.$format)?"(action: "patch")

        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')

        //super administrador
        "/api/configurations"(controller: 'configuracion', action: 'listConfigurations', method: 'POST')
        "/api/configuration"(controller: 'configuracion', action: 'saveConfiguration', method: 'POST')

        //administrator
        "/api/usuario/"(controller: 'usuario', action: 'saveCommunityManager', method: 'POST')
        "/api/updateuser/"(controller: 'usuario', action: 'updateUser', method: 'POST')
        "/api/cms/"(controller: 'usuario', action: 'listCommunityManager', method: 'GET')
        "/api/usuario/"(controller: 'usuario', action: 'listUsers', method: 'GET')
        "/api/updateAccount/"(controller: 'usuario', action: 'updateAccount', method: 'POST')
        "/api/usuario/delete"(controller: 'usuario', action: 'updateStatusUser', method: 'POST')
        "/api/usuario/catalogoRoles"(controller: 'usuario', action: 'roles', method: 'GET')

        "/api/atletas/"(controller: 'usuario', action: 'listPlayers', method: 'GET')
        "/api/deleteAtleta"(controller: 'usuario', action: 'deletePlayer', method: 'POST')

        "/api/categoria"(controller: 'usuario', action: 'saveCategory', method: 'POST')
        "/api/categorias"(controller: 'usuario', action: 'listCategory', method: 'POST')
        "/api/changueStatusCategory"(controller: 'usuario', action: 'updateStatusCategory', method: 'POST')
        "/api/deleteCategoria"(controller: 'usuario', action: 'deleteCategory', method: 'POST')

        "/api/saveSliders"(controller: 'usuario', action: 'saveSlider', method: 'POST')
        "/api/banners"(controller: 'usuario', action: 'listSliders', method: 'POST')
        "/api/countsSliders"(controller: 'usuario', action: 'totalSliders', method: 'POST')
        "/api/statusBanner"(controller: 'usuario', action: 'updateStatusSlider', method: 'POST')
        "/api/deleteSlider"(controller: 'usuario', action: 'deleteSlider', method: 'POST')

        //community manager
        "/api/enviarRedaccion"(controller: 'communityManager', action: 'savePost', method: 'POST')
        "/api/redacciones"(controller: 'communityManager', action: 'listPost', method: 'POST')
        "/api/redaccion"(controller: 'communityManager', action: 'getPost', method: 'POST')
        "/api/changueStatus"(controller: 'communityManager', action: 'updateStatusPost', method: 'POST')
        "/api/changueDatePost"(controller: 'communityManager', action: 'updateDatePost', method: 'POST')
        "/api/deletePost"(controller: 'communityManager', action: 'deletePost', method: 'POST')
        "/api/atletasManager"(controller: 'communityManager', action: 'listPlayers', method: 'POST')

        "/api/atleta"(controller: 'communityManager', action: 'savePlayer', method: 'POST')
        "/api/statusPlayer"(controller: 'communityManager', action: 'updateStatusPlayer', method: 'POST')
        "/api/categoriasAtleta"(controller: 'communityManager', action: 'listCategoriesByPlayer', method: 'POST')
        "/api/managers"(controller: 'communityManager', action: 'listCommunityManagerSelect', method: 'POST')

        //ws
        "/api/collections"(controller: 'ws', action: 'categoriasAtleta', method: 'GET')
        "/api/collectionInfo"(controller: 'ws', action: 'categoria', method: 'GET')
        "/api/postsInCollection"(controller: 'ws', action: 'categoriaMedia', method: 'GET')
        "/api/postInfo"(controller: 'ws', action: 'publicacion', method: 'GET')
        "/api/latestPosts"(controller: 'ws', action: 'publicacionesToday', method: 'GET')
        "/api/searchPosts"(controller: 'ws', action: 'buscarPublicaciones', method: 'GET')
        "/api/postComment"(controller: 'ws', action: 'comentariosPost', method: 'POST')
        "/api/postLike"(controller: 'ws', action: 'likesPost', method: 'POST')
        "/api/listComments"(controller: 'ws', action: 'listComentariosPost', method: 'GET')
        "/api/listLikes"(controller: 'ws', action: 'listLikesPost', method: 'GET')
        "/api/supportEmail"(controller: 'ws', action: 'supportEmail', method: 'POST')
        "/api/createAccount"(controller: 'ws', action: 'crearCuenta', method: 'POST')
        "/api/broadcastingVideo"(controller: 'ws', action: 'listComentariosPost', method: 'GET')
        "/api/postSocialCounts"(controller: 'ws', action: 'countLikesComments', method: 'GET')

        "/api/collectionsPreferences"(controller: 'ws', action: 'listaCategoriasPreferencias', method: 'GET')
        "/api/saveCollectionPreference"(controller: 'ws', action: 'guardarPreferencias', method: 'POST')
        "/api/postPublicity"(controller: 'ws', action: 'publicacionesPublicitarias', method: 'GET')
        "/api/userInfo"(controller: 'ws', action: 'perfilUsuario', method: 'GET')

        "/api/listSliders"(controller: 'ws', action: 'listaBanners', method: 'GET')
        "/api/saveMaps"(controller: 'ws', action: 'saveMapspublicacion', method: 'POST')

        "/api/getExclusivePost"(controller: 'ws', action: 'publicacionesExclusivas', method: 'GET')
        "/api/getVersions"(controller: 'ws', action: 'listarVersiones', method: 'GET')

        "/api/latestPostsExclusive"(controller: 'ws', action: 'publicacionesTodayExclusive', method: 'GET')
        "/api/postsInCollectionExclusive"(controller: 'ws', action: 'postsInCollectionExclusive', method: 'GET')
    }
}
