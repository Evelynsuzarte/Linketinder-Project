package service

import model.repository.CompetenciaRepository

class CompetenciaService {

    private final CompetenciaRepository repo

    CompetenciaService(CompetenciaRepository repo) {
        this.repo = repo
    }

    int buscarOuCriar(String nome) {
        if (!nome?.trim()) {
            throw new IllegalArgumentException("O nome da competência não pode ser vazio.")
        }
        return repo.buscarOuCriar(nome.trim())
    }

    void salvarDoCandidato(int idCandidato, List<String> nomesCompetencias) {
        if (nomesCompetencias) {
            repo.salvarDoCandidato(idCandidato, nomesCompetencias)
        }
    }

    void salvarDaVaga(int idVaga, List<String> nomesCompetencias) {
        if (nomesCompetencias) {
            repo.salvarDaVaga(idVaga, nomesCompetencias)
        }
    }

    List<String> listarDoCandidato(int idCandidato) {
        return repo.listarDoCandidato(idCandidato)
    }

    List<String> listarDaVaga(int idVaga) {
        return repo.listarDaVaga(idVaga)
    }

    void atualizarDoCandidato(int idCandidato, List<String> nomesCompetencias) {
        repo.atualizarDoCandidato(idCandidato, nomesCompetencias)
    }

    String buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID da competência deve ser válido.")
        }
        return repo.buscarPorId(id)
    }

    List<String> listarTodas() {
        return repo.listarTodas()
    }
}