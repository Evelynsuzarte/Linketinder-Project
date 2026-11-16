package service

import model.entity.Candidato
import model.repository.CandidatoRepository

class CandidatoService {

    private final CandidatoRepository repo

    CandidatoService(CandidatoRepository repo) {
        this.repo = repo
    }

    Candidato inserir(Candidato candidato) {
        return repo.inserir(candidato)
    }

    Candidato editar(Candidato candidato) {
        return repo.atualizar(candidato)
    }

    List<Candidato> listarTodos() {
        return repo.listarTodos()
    }

    Candidato buscarPorEmail(String email) {
        return repo.buscarPorEmail(email)
    }

    Candidato buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID do candidato deve ser válido.")
        }
        return repo.buscarPorId(id)
    }
}