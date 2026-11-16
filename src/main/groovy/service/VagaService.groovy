package service

import model.entity.Vaga
import model.repository.VagaRepository

class VagaService {

    private final VagaRepository repo

    VagaService(VagaRepository repo) {
        this.repo = repo
    }

    void inserir(Vaga vaga) {
        if (vaga == null || vaga.empresa == null || vaga.empresa.id == null || vaga.empresa.id == 0) {
            throw new IllegalArgumentException("Os dados da vaga e o ID da empresa são obrigatórios.")
        }
        repo.inserir(vaga)
    }

    void atualizar(Vaga vaga) {
        if (vaga == null || vaga.id == null || vaga.id == 0 || vaga.empresa == null || vaga.empresa.id == null || vaga.empresa.id == 0) {
            throw new IllegalArgumentException("Os dados, o ID da vaga e o ID da empresa são obrigatórios para atualização.")
        }
        repo.atualizar(vaga)
    }

    Vaga buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID da vaga deve ser válido.")
        }
        return repo.buscarPorId(id)
    }

    List<Vaga> listarTodas() {
        return repo.listarTodas()
    }

    List<Vaga> listarNaoCurtidas(int idCandidato) {
        if (idCandidato <= 0) {
            throw new IllegalArgumentException("O ID do candidato deve ser válido.")
        }
        return repo.listarNaoCurtidas(idCandidato)
    }
}