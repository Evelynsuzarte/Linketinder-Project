package controller

import java.sql.SQLException

class AvisoErros {

    void avisarErroDeBanco(SQLException erro) {
        switch (erro.SQLState) {
            case '23505':
                println("\nErro: já existe um registro com esses dados (CPF ou CNPJ repetido).")
                break
            case '22001':
                println("\nErro: algum campo tem mais caracteres do que o permitido.")
                break
            default:
                println("\nErro ao acessar o banco de dados: ${erro.message}")
        }
    }
}
