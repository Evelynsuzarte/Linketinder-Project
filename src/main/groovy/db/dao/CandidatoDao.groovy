package db.dao

import db.ConexaoDB
import groovy.sql.Sql
import model.entity.Candidato
import model.repository.CandidatoRepository

class CandidatoDao implements CandidatoRepository {

    Sql sql = ConexaoDB.getSql()
    CompetenciaDao competenciaDAO = new CompetenciaDao()

    void inserir(Candidato c) {
        def chaves = sql.executeInsert("""
            INSERT INTO candidatos (cpf, nome, sobrenome, idade, email, pais, cep, descricao, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                [c.cpf, c.nome, c.sobrenome, c.idade,
                 c.email, c.pais, c.cep, c.descricao, c.senha], ['id'])
        c.id = chaves[0][0]
        competenciaDAO.salvarDoCandidato(c.id, c.competencias)
        println("!!!! Cadastro realizado com sucesso !!!!")
    }

    void atualizar(Candidato c) {
        sql.executeUpdate("""
            UPDATE candidatos 
            SET cpf = ?, nome = ?, sobrenome = ?, idade = ?, 
                email = ?, pais = ?, cep = ?, descricao = ?, senha = ?
            WHERE id = ?""",
                    [c.cpf, c.nome, c.sobrenome, c.idade,
                     c.email, c.pais, c.cep, c.descricao, c.senha, c.id])

        if (c.competencias != null) {
            competenciaDAO.atualizarDoCandidato(c.id, c.competencias)
        }

        println("!!!! Cadastro atualizado com sucesso !!!!")
    }

    List<Candidato> listarTodos() {
        return sql.rows("SELECT * FROM candidatos").collect { montarObjeto(it) }
    }

    Candidato buscarPorEmail(String email) {
        def linha = sql.firstRow("SELECT * FROM candidatos WHERE email = ?", [email])
        if (linha == null) {
            return null
        } else {
            return montarObjeto(linha)
        }
    }

    Candidato buscarPorNome(String nomeCompleto) {
        def linha = sql.firstRow(
                "SELECT * FROM candidatos WHERE LOWER(nome || ' ' || sobrenome) = LOWER(?)",
                [nomeCompleto.trim()])
        if (linha == null) {
            return null
        } else {
            return montarObjeto(linha)
        }
    }

    Candidato buscarPorId(int id) {
        def linha = sql.firstRow("SELECT * FROM candidatos WHERE id = ?", [id])
        return linha ? montarObjeto(linha) : null
    }

    Candidato montarObjeto(linha) {
        return new Candidato(id: linha.id, cpf: linha.cpf, nome: linha.nome, sobrenome: linha.sobrenome,
                idade: linha.idade, email: linha.email,
                pais: linha.pais, cep: linha.cep, descricao: linha.descricao, senha: linha.senha,
                competencias: competenciaDAO.listarDoCandidato(linha.id))
    }
}
