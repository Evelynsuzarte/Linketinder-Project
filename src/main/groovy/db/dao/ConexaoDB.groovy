package db.dao
import groovy.sql.Sql

class ConexaoDB {
    private static Sql sql

    static Sql getSql() {
        if (sql == null) {
            sql = Sql.newInstance(
                    "jdbc:postgresql://localhost:5432/linketinder",
                    "postgres",
                    "1234",
                    "org.postgresql.Driver")
        }
        return sql
    }
}