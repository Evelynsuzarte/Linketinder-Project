//package db
//import groovy.sql.Sql
//
//class ConexaoDB {
//    private static Sql sql
//
//    static Sql getSql() {
//        if (sql == null) {
//            sql = Sql.newInstance(
//                    "jdbc:postgresql://localhost:5432/linketinder",
//                    "postgres",
//                    "1234",
//                    "org.postgresql.Driver")
//        }
//        return sql
//    }
//}


package db

import groovy.sql.Sql

class ConexaoDB {

    private static ConexaoFactory fabrica = fabricaPara(System.getenv('DB_TIPO'))
    private static Sql instancia

    private ConexaoDB() {
    }

    static ConexaoFactory fabricaPara(String tipo) {
        if (tipo != null) {
            String tipoTratado = tipo.trim()
            if (tipoTratado.equalsIgnoreCase('h2')) {
                return new H2ConexaoFactory()
            }
        }
        return new PostgresConexaoFactory()
    }

    static synchronized void configurar(ConexaoFactory novaFabrica) {
        fechar()
        fabrica = novaFabrica
    }

    static synchronized Sql getSql() {
        if (instancia == null) {
            instancia = fabrica.criarConexao()
        }
        return instancia
    }

    static synchronized void fechar() {
        if (instancia != null) {
            instancia.close()
        }
        instancia = null
    }

}