package db.dao

import db.ConexaoDB
import groovy.sql.Sql
import model.entity.Empresa
import model.entity.Vaga
import model.repository.VagaRepository

class VagaDao implements VagaRepository {

    Sql sql = ConexaoDB.getSql()
    CompetenciaDao competenciaDAO = new CompetenciaDao()

    private static final String SELECT_VAGAS = """
        SELECT v.id, v.nome, v.descricao, v.local_vaga, e.id AS id_empresa, e.nome AS nome_empresa
        FROM vagas v
        JOIN empresas e ON e.id = v.id_empresa"""

    void inserir(Vaga v) {
        def chaves = sql.executeInsert("""
            INSERT INTO vagas (nome, descricao, local_vaga, id_empresa)
            VALUES (?, ?, ?, ?)""",
                [v.nome, v.descricao, v.local, v.empresa.id], ['id'])
        v.id = chaves[0][0]
        competenciaDAO.salvarDaVaga(v.id, v.competencias)
        println("!!!! Vaga cadastrada com sucesso !!!!")
    }

    void atualizar(Vaga v) {
        sql.executeUpdate("""
            UPDATE vagas 
            SET nome = ?, descricao = ?, local_vaga = ?, id_empresa = ?
            WHERE id = ?""",
                    [v.nome, v.descricao, v.local, v.empresa.id, v.id])

        if (v.competencias != null) {
            competenciaDAO.atualizarDaVaga(v.id, v.competencias)
        }

        println("!!!! Vaga atualizada com sucesso !!!!")
    }

    Vaga buscarPorId(int id) {
        def linha = sql.firstRow(SELECT_VAGAS + " WHERE v.id = ?", [id])
        return linha ? montar(linha) : null
    }

    List<Vaga> listarTodas() {
        return sql.rows(SELECT_VAGAS).collect { montar(it) }
    }

    List<Vaga> listarNaoCurtidas(int idCandidato) {
        return sql.rows(SELECT_VAGAS + """
            WHERE v.id NOT IN (SELECT id_vaga FROM curtida_candidato_vaga WHERE id_candidato = ?)""",
                [idCandidato]).collect { montar(it) }
    }

    Vaga montar(linha) {
        Empresa empresa = new Empresa(id: linha.id_empresa, nome: linha.nome_empresa)
        return new Vaga(id: linha.id, nome: linha.nome, descricao: linha.descricao,
                local: linha.local_vaga, empresa: empresa,
                competencias: competenciaDAO.listarDaVaga(linha.id))
    }
}