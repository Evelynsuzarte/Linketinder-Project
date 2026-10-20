package repository

import model.Candidato

interface CandidatoRepository {
    void inserir(Candidato c)
    List<Candidato> listarTodos()
    Candidato buscarPorEmail(String email)
    Candidato buscarPorNome(String nomeCompleto)
    Candidato montarObjeto(linha)

}
