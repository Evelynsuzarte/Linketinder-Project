package db

import groovy.sql.Sql

class PostgresConexaoFactory implements ConexaoFactory {
    Sql criarConexao() {
        return Sql.newInstance(
                System.getenv('DB_URL') ?: "jdbc:postgresql://localhost:5432/linketinder",
                System.getenv('DB_USER') ?: "postgres",
                System.getenv('DB_PASSWORD') ?: "1234",
                "org.postgresql.Driver")
    }
}
