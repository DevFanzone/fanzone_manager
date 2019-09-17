package utils

import grails.gorm.transactions.Transactional

@Transactional
class ErrorHandlerService {

    def errorOnSaveDomain(def objetoInstance) {
        println "@@@@@@@---------->request@errorHandlerService.erroronsavedomain"
        def erroresList = []
        def erroresReturn = [:]
        println objetoInstance.errors.allErrors
        objetoInstance.errors.allErrors.each {
            //println "@@@@@@@ ------->>>>>>> error: " + it
            erroresList.push(it.toString())
        }
        erroresReturn.errorAPI = true
        erroresReturn.mensajeAPI = erroresList
        return erroresReturn

    }
}
