package db.dao
import groovy.sql.Sql

class MatchDao {

    Sql sql = ConexaoDB.getSql()
    CompetenciaDao competenciaDAO = new CompetenciaDao()

    void curtirVaga(int idCandidato, int idVaga) {
        sql.executeInsert("INSERT INTO curtida_candidato_vaga (id_candidato, id_vaga) VALUES (?, ?)",
                [idCandidato, idVaga])
    }

    // interessados nas vagas da empresa que a empresa ainda nao avaliou (so descricao e competencias)
    List<Map> listarInteressados(int idEmpresa) {
        def linhas = sql.rows("""
            SELECT c.id AS id_candidato, c.descricao, v.id AS id_vaga, v.nome AS nome_vaga
            FROM curtida_candidato_vaga cv
            JOIN vagas v ON v.id = cv.id_vaga
            JOIN candidatos c ON c.id = cv.id_candidato
            WHERE v.id_empresa = ?
            AND NOT EXISTS (SELECT 1 FROM curtida_empresa_candidato ce
                            WHERE ce.id_candidato = cv.id_candidato AND ce.id_vaga = cv.id_vaga)""",
                [idEmpresa])

        return linhas.collect { l ->
            [idCandidato: l.id_candidato,
             idVaga     : l.id_vaga,
             nomeVaga   : l.nome_vaga,
             descricao  : l.descricao,
             competencias: competenciaDAO.listarDoCandidato(l.id_candidato)]
        }
    }

    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga) {
        sql.withTransaction {
            sql.executeInsert("""INSERT INTO curtida_empresa_candidato (id_empresa, id_candidato, id_vaga)
                                 VALUES (?, ?, ?)""", [idEmpresa, idCandidato, idVaga])
            sql.executeInsert("""INSERT INTO match (id_candidato, id_empresa, id_vaga)
                                 VALUES (?, ?, ?)""", [idCandidato, idEmpresa, idVaga])
        }
    }

    // visao do candidato
    List<Map> matchesDoCandidato(int idCandidato) {
        return sql.rows("""
            SELECT v.nome AS vaga, e.nome AS empresa, v.local_vaga AS local
            FROM match m
            JOIN vagas v ON v.id = m.id_vaga
            JOIN empresas e ON e.id = m.id_empresa
            WHERE m.id_candidato = ?""", [idCandidato])
    }

    // visao da empresa
    List<Map> matchesDaEmpresa(int idEmpresa) {
        return sql.rows("""
            SELECT v.nome AS vaga, c.nome, c.sobrenome, c.email
            FROM match m
            JOIN vagas v ON v.id = m.id_vaga
            JOIN candidatos c ON c.id = m.id_candidato
            WHERE m.id_empresa = ?""", [idEmpresa])
    }
}