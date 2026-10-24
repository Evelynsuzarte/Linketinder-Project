import db.CandidatoDao
import db.ConexaoDB
import db.H2ConexaoFactory
import groovy.sql.Sql
import model.Candidato
import spock.lang.Specification

class CandidatoDaoIntegracaoSpec extends Specification {

    def setup() {
        ConexaoDB.configurar(new H2ConexaoFactory())
        Sql sql = ConexaoDB.getSql()
        sql.execute("DROP TABLE IF EXISTS candidato_competencia")
        sql.execute("DROP TABLE IF EXISTS competencias")
        sql.execute("DROP TABLE IF EXISTS candidatos")
        sql.execute("""CREATE TABLE candidatos (id SERIAL PRIMARY KEY, cpf VARCHAR(14), nome VARCHAR(50),
                       sobrenome VARCHAR(50), idade INT, email VARCHAR(100), pais VARCHAR(50),
                       cep VARCHAR(10), descricao TEXT, senha VARCHAR(50))""")
        sql.execute("CREATE TABLE competencias (id SERIAL PRIMARY KEY, nome VARCHAR(50))")
        sql.execute("""CREATE TABLE candidato_competencia (id_candidato INT, id_competencia INT)""")
    }

    def cleanup() {
        ConexaoDB.fechar()
    }

    def "candidato inserido deve poder ser encontrado pelo e-mail"() {
        given:
        def dao = new CandidatoDao()
        def ana = new Candidato(nome: "Ana", sobrenome: "Silva", email: "ana@email.com", cpf: "123",
                idade: 25, competencias: ["Java", "Groovy"])

        when:
        dao.inserir(ana)
        def encontrada = dao.buscarPorEmail("ana@email.com")

        then:
        ana.id > 0
        encontrada.nome == "Ana"
        encontrada.competencias.sort() == ["Groovy", "Java"]
    }
}