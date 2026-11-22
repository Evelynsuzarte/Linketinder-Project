package model.repository

import model.entity.Candidato

interface CompetenciaRepository {
    int buscarOuCriar(String nome)
    void salvarDoCandidato(int idCandidato, List<String> nomes_competencias)
    void salvarDaVaga(int idVaga, List<String> nomes_competencias)
    List<String> listarDoCandidato(int idCandidato)
    List<String> listarDaVaga(int idVaga)
    List<String> listarTodas()
    void atualizarDoCandidato(int idCandidato, List<String> nomes_competencias)
    void atualizarDaVaga(int idVaga, List<String> nomes_competencias)
    String buscarPorId(int id)
}
