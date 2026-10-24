package db

import groovy.sql.Sql

class H2ConexaoFactory implements ConexaoFactory {
    Sql criarConexao() {
        return Sql.newInstance(
                "jdbc:h2:mem:linketinder;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "",
                "org.h2.Driver")
    }
}