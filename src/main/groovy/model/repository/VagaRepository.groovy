package model.repository

import model.entity.Vaga

interface VagaRepository {
    void inserir(Vaga v)
    void atualizar(Vaga v)
    void remover(int id)
    List listarTodas()
    List<Vaga> listarNaoCurtidas(int idCandidato)
    Vaga montar(linha)
    Vaga buscarPorId(int id)
    List listarPorEmpresa(int idEmpresa)
}
