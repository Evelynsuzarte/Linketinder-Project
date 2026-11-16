package db.dao

import db.ConexaoDB
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import model.entity.Empresa
import model.repository.EmpresaRepository

class EmpresaDao implements EmpresaRepository {

    Sql sql = ConexaoDB.getSql()
    VagaDao vagaDao = new VagaDao()

    void inserir(Empresa e) {
        def dados = sql.executeInsert("""
            INSERT INTO empresas (cnpj, nome, email, pais, cep, descricao, senha, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)""",
                [e.cnpj, e.nome, e.email, e.pais, e.cep, e.descricao, e.senha, e.estado], ['id'])
        e.id = dados[0][0]

        println("!!!! Cadastro realizado com sucesso !!!!")
    }

    void atualizar(Empresa e) {
        sql.executeUpdate("""
        UPDATE empresas 
        SET cnpj = ?, nome = ?, email = ?, pais = ?, 
            cep = ?, descricao = ?, senha = ?, estado = ?
        WHERE id = ?""",
                [e.cnpj, e.nome, e.email, e.pais,
                 e.cep, e.descricao, e.senha, e.estado, e.id])

        println("!!!! Cadastro atualizado com sucesso !!!!")
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

    Empresa buscarPorId(int id) {
        def linha = sql.firstRow("SELECT * FROM empresas WHERE id = ?", [id])
        return linha ? montarObjeto(linha) : null
    }

    Empresa montarObjeto(GroovyRowResult linha) {
        return new Empresa(id: linha.id, cnpj: linha.cnpj, nome: linha.nome, email: linha.email,
                pais: linha.pais, cep: linha.cep, estado: linha.estado, descricao: linha.descricao, senha: linha.senha,
                vagas: vagaDao.listarPorEmpresa(linha.id))
    }
}
