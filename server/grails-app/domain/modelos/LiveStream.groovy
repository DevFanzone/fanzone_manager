package modelos

class LiveStream {

    static belongsTo= [atleta:Atleta]

    String server
    boolean isOnline

    static constraints = {
        server nullable: true, blank: true,size:1..2300
    }
}
