package repository

import java.sql.SQLException


interface CompetenciaRepository {
    int buscarOuCriar(String nome)
    void salvarDoCandidato(int idCandidato, List<String> nomes_competencias)
    void salvarDaVaga(int idVaga, List<String> nomes_competencias)
    List<String> listarDoCandidato(int idCandidato)
    List<String> listarDaVaga(int idVaga)

}
