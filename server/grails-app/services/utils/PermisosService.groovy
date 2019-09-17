package utils

import grails.gorm.transactions.Transactional
import modelos.Usuario
import seguridad.UserRole
import seguridad.Role

@Transactional
class PermisosService {

    def setRole (Usuario usuarioInstance, def roleId) {
        def role = Role.get(roleId as long)
        def permiso = new UserRole()
        permiso.setUser(usuarioInstance)
        permiso.setRole(role)
        permiso.save(flush: true)
    }

    def deleteRole (Usuario usuarioInstance) {
        List<UserRole> permisos = UserRole.findAllByUser(usuarioInstance)
        permisos*.delete()
    }
}
