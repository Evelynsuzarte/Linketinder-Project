package repository

import groovy.sql.GroovyRowResult
import model.Empresa

interface EmpresaRepository {
    void inserir(Empresa e)
    List<Empresa> listarTodas()
    Empresa buscarPorEmail(String email)
    Empresa buscarPorNome(String nome)
    Empresa montarObjeto(GroovyRowResult linha)
}
