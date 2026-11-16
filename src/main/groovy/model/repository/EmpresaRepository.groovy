package model.repository

import groovy.sql.GroovyRowResult
import model.entity.Empresa

interface EmpresaRepository {
    void inserir(Empresa e)
    void atualizar(Empresa e)
    List<Empresa> listarTodas()
    Empresa buscarPorEmail(String email)
    Empresa buscarPorNome(String nome)
    Empresa montarObjeto(GroovyRowResult linha)
    Empresa buscarPorId(int id)
}
