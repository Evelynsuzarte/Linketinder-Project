package db.dao

import db.ConexaoDB
import groovy.sql.Sql
import model.repository.CompetenciaRepository


class CompetenciaDao implements CompetenciaRepository {

    Sql sql = ConexaoDB.getSql()

    int buscarOuCriar(String nome) {
        def linha = sql.firstRow("SELECT id FROM competencias WHERE nome = ?", [nome])
        if (linha != null) {
            return linha.id
        }
        return sql.executeInsert("INSERT INTO competencias (nome) VALUES (?)", [nome], ['id'])[0][0]
    }

    void salvarDoCandidato(int idCandidato, List<String> nomes_competencias) {
        for (nome in nomes_competencias.collect { it.trim() }.unique()) {
            int idCompetencia = buscarOuCriar(nome)
            sql.executeInsert("INSERT INTO candidato_competencia (id_candidato, id_competencia) VALUES (?, ?)",
                    [idCandidato, idCompetencia])
        }
    }

    void salvarDaVaga(int idVaga, List<String> nomes_competencias) {
        for (nome in nomes_competencias.collect { it.trim() }.unique()) {
            int idCompetencia = buscarOuCriar(nome)
            sql.executeInsert("INSERT INTO vaga_competencia (id_vaga, id_competencia) VALUES (?, ?)",
                    [idVaga, idCompetencia])
        }
    }

    List<String> listarDoCandidato(int idCandidato) {
        return sql.rows(
                """SELECT c.nome FROM competencias c
                           JOIN candidato_competencia cc ON cc.id_competencia = c.id WHERE cc.id_candidato = ?""", [idCandidato])*.nome
    }

    List<String> listarDaVaga(int idVaga) {
        return sql.rows("""SELECT c.nome FROM competencias c
                           JOIN vaga_competencia vc ON vc.id_competencia = c.id
                           WHERE vc.id_vaga = ?""", [idVaga])*.nome
    }

    void atualizarDoCandidato(int idCandidato, List<String> nomes_competencias) {
        sql.withTransaction {
            sql.executeUpdate("DELETE FROM candidato_competencia WHERE id_candidato = ?", [idCandidato])
            if (nomes_competencias) {
                salvarDoCandidato(idCandidato, nomes_competencias)
            }
        }
    }

    void atualizarDaVaga(int idVaga, List<String> nomes_competencias) {
        if (!nomes_competencias) return

        sql.withTransaction {
            sql.executeUpdate("DELETE FROM vaga_competencia WHERE id_vaga = ?", [idVaga])
            salvarDaVaga(idVaga, nomes_competencias)
        }
    }

    String buscarPorId(int id) {
        def linha = sql.firstRow("SELECT nome FROM competencias WHERE id = ?", [id])
        return linha?.nome
    }

    List<String> listarTodas() {
        return sql.rows("SELECT nome FROM competencias ORDER BY nome")*.nome
    }

}


