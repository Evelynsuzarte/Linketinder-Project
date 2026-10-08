package db.dao
import groovy.sql.Sql
import model.Candidato

class CandidatoDao {

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

    Candidato montarObjeto(linha) {
        return new Candidato(id: linha.id, cpf: linha.cpf, nome: linha.nome, sobrenome: linha.sobrenome,
                idade: linha.idade, email: linha.email,
                pais: linha.pais, cep: linha.cep, descricao: linha.descricao, senha: linha.senha,
                competencias: competenciaDAO.listarDoCandidato(linha.id))
    }
}
