import db.ConexaoDB
import db.ConexaoFactory
import db.H2ConexaoFactory
import db.PostgresConexaoFactory
import groovy.sql.Sql
import spock.lang.Specification

class ConexaoDBSpec extends Specification {

    def setup() {
        ConexaoDB.configurar(new H2ConexaoFactory())
    }

    def cleanup() {
        ConexaoDB.fechar()
    }

    def "Singleton: duas chamadas devem devolver a mesma conexão"() {
        when:
        Sql primeira = ConexaoDB.getSql()
        Sql segunda = ConexaoDB.getSql()

        then:
        primeira.is(segunda)
    }

    def "Factory: a fábrica só é chamada uma vez, mesmo com várias chamadas"() {
        given:
        int chamadas = 0
        ConexaoFactory fabricaContadora = [criarConexao: { chamadas++; new H2ConexaoFactory().criarConexao() }] as ConexaoFactory
        ConexaoDB.configurar(fabricaContadora)

        when:
        ConexaoDB.getSql()
        ConexaoDB.getSql()
        ConexaoDB.getSql()

        then:
        chamadas == 1
    }

    def "Factory: a conexão criada pela fábrica H2 deve executar SQL"() {
        given:
        Sql sql = ConexaoDB.getSql()
        sql.execute("CREATE TABLE IF NOT EXISTS teste (id INT PRIMARY KEY, nome VARCHAR(50))")

        when:
        sql.executeInsert("INSERT INTO teste (id, nome) VALUES (?, ?)", [1, "Ana"])

        then:
        sql.firstRow("SELECT nome FROM teste WHERE id = 1").nome == "Ana"
    }

    def "Factory: o tipo de banco escolhe qual fábrica é usada"() {
        expect:
        ConexaoDB.fabricaPara("h2") instanceof H2ConexaoFactory
        ConexaoDB.fabricaPara("H2") instanceof H2ConexaoFactory
        ConexaoDB.fabricaPara("postgres") instanceof PostgresConexaoFactory
        ConexaoDB.fabricaPara(null) instanceof PostgresConexaoFactory
    }

    def "Factory: trocar a fábrica cria uma nova conexão"() {
        given:
        Sql antiga = ConexaoDB.getSql()

        when:
        ConexaoDB.configurar(new H2ConexaoFactory())
        Sql nova = ConexaoDB.getSql()

        then:
        !antiga.is(nova)
    }
}