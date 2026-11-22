import db.ConexaoDB
import db.H2ConexaoFactory
import db.dao.RecrutadorDao
import groovy.sql.Sql
import model.entity.Empresa
import model.entity.Recrutador
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorDaoIntegracaoSpec extends Specification {

    Sql sql
    RecrutadorDao dao

    def setup() {
        ConexaoDB.configurar(new H2ConexaoFactory())
        sql = ConexaoDB.getSql()
        sql.execute("DROP TABLE IF EXISTS recrutadores")
        sql.execute("DROP TABLE IF EXISTS empresas")
        sql.execute("""
            CREATE TABLE empresas (
                id SERIAL PRIMARY KEY NOT NULL,
                cnpj VARCHAR(18) UNIQUE NOT NULL,
                nome VARCHAR(50),
                email VARCHAR(50)
            )""")
        sql.execute("""
            CREATE TABLE recrutadores (
                id SERIAL PRIMARY KEY NOT NULL,
                id_empresa INT NOT NULL,
                nome VARCHAR(100) NOT NULL,
                area_recrutamento VARCHAR(50) NOT NULL,
                descricao VARCHAR(255),
                email VARCHAR(100) UNIQUE NOT NULL,
                cpf VARCHAR(14) UNIQUE NOT NULL,
                data_nascimento DATE NOT NULL,
                senha VARCHAR(255) NOT NULL,
                FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE
            )""")
        sql.executeInsert("INSERT INTO empresas (cnpj, nome, email) VALUES (?, ?, ?)",
                ["11.111.111/0001-11", "Pastelsoft", "contato@pastelsoft.com"])
        dao = new RecrutadorDao()
    }

    def cleanup() {
        ConexaoDB.fechar()
    }

    def "deve inserir, buscar e listar recrutador por empresa"() {
        given:
        def r = new Recrutador(
                empresa: new Empresa(id: 1),
                nome: "Fernanda Lima",
                areaRecrutamento: "TI",
                descricao: "Tech Recruiter",
                email: "fernanda@pastelsoft.com",
                cpf: "52998224725",
                dataNascimento: LocalDate.of(1995, 3, 12),
                senha: "123"
        )

        when:
        dao.inserir(r)
        def porId = dao.buscarPorId(r.id)
        def porEmail = dao.buscarPorEmail("fernanda@pastelsoft.com")
        def porCpf = dao.buscarPorCpf("52998224725")
        def lista = dao.listarPorEmpresa(1)

        then:
        r.id > 0
        porId.nome == "Fernanda Lima"
        porId.empresa.nome == "Pastelsoft"
        porId.dataNascimento == LocalDate.of(1995, 3, 12)
        porEmail.id == r.id
        porCpf.id == r.id
        lista.size() == 1
    }

    def "deve atualizar e remover recrutador"() {
        given:
        def r = new Recrutador(
                empresa: new Empresa(id: 1),
                nome: "Rafael Mendes",
                areaRecrutamento: "RH",
                descricao: "Recrutador",
                email: "rafael@pastelsoft.com",
                cpf: "52998224725",
                dataNascimento: LocalDate.of(1992, 9, 20),
                senha: "123"
        )
        dao.inserir(r)

        when:
        r.areaRecrutamento = "Marketing"
        r.descricao = "Recrutador Sênior"
        dao.atualizar(r)
        def atualizado = dao.buscarPorId(r.id)

        then:
        atualizado.areaRecrutamento == "Marketing"
        atualizado.descricao == "Recrutador Sênior"

        when:
        dao.remover(r.id)

        then:
        dao.buscarPorId(r.id) == null
    }

    def "remover empresa deve excluir recrutadores em cascata"() {
        given:
        def r = new Recrutador(
                empresa: new Empresa(id: 1),
                nome: "Carla Dias",
                areaRecrutamento: "TI",
                descricao: "Recrutadora",
                email: "carla@pastelsoft.com",
                cpf: "52998224725",
                dataNascimento: LocalDate.of(1990, 1, 10),
                senha: "123"
        )
        dao.inserir(r)

        when:
        sql.executeUpdate("DELETE FROM empresas WHERE id = 1")

        then:
        dao.buscarPorId(r.id) == null
        dao.listarPorEmpresa(1).isEmpty()
    }
}
