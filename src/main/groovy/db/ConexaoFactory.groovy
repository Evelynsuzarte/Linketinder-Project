package db

import groovy.sql.Sql

interface ConexaoFactory {
    Sql criarConexao()
}
