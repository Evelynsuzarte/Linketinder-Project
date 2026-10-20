package repository

import model.Vaga

interface VagaRepository {
    void inserir(Vaga v)
    List<Vaga> listarTodas()
    List<Vaga> listarNaoCurtidas(int idCandidato)
    Vaga montar(linha)
}
