package db.dao

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import model.Empresa

class EmpresaDao {

    Sql sql = ConexaoDB.getSql()

    void inserir(Empresa e) {
        def dados = sql.executeInsert("""
            INSERT INTO empresas (cnpj, nome, email, pais, cep, descricao, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?)""",
                [e.cnpj, e.nome, e.email, e.pais, e.cep, e.descricao, e.senha], ['id'])
        e.id = dados[0][0]

        println("!!!! Cadastro realizado com sucesso !!!!")
    }

    List<Empresa> listarTodas() {
        return sql.rows("SELECT * FROM empresas").collect { montarObjeto(it) }
    }

    Empresa buscarPorEmail(String email) {
        def linha = sql.firstRow("SELECT * FROM empresas WHERE email = ?", [email])
        if (linha == null) {
            return null
        } else {
            return montarObjeto(linha)
        }
    }

    Empresa buscarPorNome(String nome) {
        def linha = sql.firstRow("SELECT * FROM empresas WHERE nome = ?", [nome])
        if (linha == null) {
            return null
        } else {
            return montarObjeto(linha)
        }
    }

    Empresa montarObjeto(GroovyRowResult linha) {
        return new Empresa(id: linha.id, cnpj: linha.cnpj, nome: linha.nome, email: linha.email,
                pais: linha.pais, cep: linha.cep, descricao: linha.descricao, senha: linha.senha)
    }
}
