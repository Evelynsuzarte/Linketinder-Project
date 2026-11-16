package model.repository

import model.entity.Candidato

interface CandidatoRepository {
    void inserir(Candidato c)
    List<Candidato> listarTodos()
    Candidato buscarPorEmail(String email)
    Candidato buscarPorNome(String nomeCompleto)
    Candidato buscarPorId(int id)
    Candidato montarObjeto(linha)
    void atualizar(Candidato c)
}
