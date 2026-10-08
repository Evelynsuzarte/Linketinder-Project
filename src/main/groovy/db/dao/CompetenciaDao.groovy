package db.dao
import groovy.sql.Sql


class CompetenciaDao {

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
}

