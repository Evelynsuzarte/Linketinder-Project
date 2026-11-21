package db.dao

import db.ConexaoDB
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import model.entity.Empresa
import model.entity.Recrutador
import model.repository.RecrutadorRepository

import java.sql.Date
import java.time.LocalDate

class RecrutadorDao implements RecrutadorRepository {

    Sql sql = ConexaoDB.getSql()

    private static final String SELECT_BASE = """
        SELECT r.*, e.nome AS nome_empresa
        FROM recrutadores r
        LEFT JOIN empresas e ON e.id = r.id_empresa
    """

    @Override
    void inserir(Recrutador recrutador) {
        Date dataSql = recrutador.dataNascimento ? Date.valueOf(recrutador.dataNascimento) : null
        def dados = sql.executeInsert("""
            INSERT INTO recrutadores (id_empresa, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)""",
                [recrutador.empresa.id, recrutador.nome, recrutador.areaRecrutamento,
                 recrutador.descricao, recrutador.email, recrutador.cpf, dataSql, recrutador.senha], ['id'])
        recrutador.id = dados[0][0] as int
    }

    @Override
    List<Recrutador> listarPorEmpresa(int idEmpresa) {
        return sql.rows("${SELECT_BASE} WHERE r.id_empresa = ? ORDER BY r.id", [idEmpresa])
                .collect { montarObjeto(it) }
    }

    @Override
    Recrutador buscarPorId(int id) {
        def linha = sql.firstRow("${SELECT_BASE} WHERE r.id = ?", [id])
        return linha ? montarObjeto(linha) : null
    }

    @Override
    Recrutador buscarPorEmail(String email) {
        def linha = sql.firstRow("${SELECT_BASE} WHERE r.email = ?", [email])
        return linha ? montarObjeto(linha) : null
    }

    @Override
    Recrutador buscarPorCpf(String cpf) {
        def linha = sql.firstRow("${SELECT_BASE} WHERE r.cpf = ?", [cpf])
        return linha ? montarObjeto(linha) : null
    }

    @Override
    Recrutador buscarPorNome(String nome) {
        def linha = sql.firstRow("${SELECT_BASE} WHERE r.nome = ?", [nome])
        return linha ? montarObjeto(linha) : null
    }

    @Override
    void atualizar(Recrutador recrutador) {
        Date dataSql = recrutador.dataNascimento ? Date.valueOf(recrutador.dataNascimento) : null
        sql.executeUpdate("""
            UPDATE recrutadores
            SET id_empresa = ?, nome = ?, area_recrutamento = ?, descricao = ?,
                email = ?, cpf = ?, data_nascimento = ?, senha = ?
            WHERE id = ?""",
                [recrutador.empresa.id, recrutador.nome, recrutador.areaRecrutamento,
                 recrutador.descricao, recrutador.email, recrutador.cpf, dataSql,
                 recrutador.senha, recrutador.id])
    }

    @Override
    void remover(int id) {
        sql.executeUpdate("DELETE FROM recrutadores WHERE id = ?", [id])
    }

    private Recrutador montarObjeto(GroovyRowResult linha) {
        return new Recrutador(
                id: linha.id as int,
                empresa: new Empresa(id: linha.id_empresa as int, nome: linha.nome_empresa),
                nome: linha.nome,
                areaRecrutamento: linha.area_recrutamento,
                descricao: linha.descricao,
                email: linha.email,
                cpf: linha.cpf,
                dataNascimento: converterData(linha.data_nascimento),
                senha: linha.senha
        )
    }

    private static LocalDate converterData(Object valor) {
        if (valor == null) {
            return null
        }
        if (valor instanceof LocalDate) {
            return (LocalDate) valor
        }
        if (valor instanceof Date) {
            return ((Date) valor).toLocalDate()
        }
        return LocalDate.parse(valor.toString())
    }
}
