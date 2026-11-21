package service

import model.entity.Empresa
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

    void atualizarComoRecrutador(Vaga vaga, int idEmpresaRecrutador) {
        if (vaga == null || vaga.id <= 0 || idEmpresaRecrutador <= 0) {
            throw new IllegalArgumentException("Os dados, o ID da vaga e o ID da empresa do recrutador são obrigatórios.")
        }
        Vaga existente = repo.buscarPorId(vaga.id)
        if (existente == null) {
            throw new IllegalArgumentException("Vaga não encontrada.")
        }
        if (existente.empresa?.id != idEmpresaRecrutador) {
            throw new IllegalArgumentException("Recrutador não tem permissão para editar vaga de outra empresa.")
        }
        vaga.empresa = new Empresa(id: idEmpresaRecrutador)
        repo.atualizar(vaga)
    }

    void remover(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID da vaga deve ser válido.")
        }
        if (repo.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Vaga não encontrada.")
        }
        repo.remover(id)
    }

    void removerComoRecrutador(int idVaga, int idEmpresaRecrutador) {
        if (idVaga <= 0 || idEmpresaRecrutador <= 0) {
            throw new IllegalArgumentException("O ID da vaga e o ID da empresa do recrutador são obrigatórios.")
        }
        Vaga existente = repo.buscarPorId(idVaga)
        if (existente == null) {
            throw new IllegalArgumentException("Vaga não encontrada.")
        }
        if (existente.empresa?.id != idEmpresaRecrutador) {
            throw new IllegalArgumentException("Recrutador não tem permissão para remover vaga de outra empresa.")
        }
        repo.remover(idVaga)
    }

    Vaga buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID da vaga deve ser válido.")
        }
        return repo.buscarPorId(id)
    }

    List listarTodas() {
        return repo.listarTodas()
    }

    List listarPorEmpresa(int idEmpresa) {
        if (idEmpresa <= 0) {
            throw new IllegalArgumentException("O ID da empresa deve ser válido.")
        }
        return repo.listarPorEmpresa(idEmpresa)
    }

    List<Vaga> listarNaoCurtidas(int idCandidato) {
        if (idCandidato <= 0) {
            throw new IllegalArgumentException("O ID do candidato deve ser válido.")
        }
        return repo.listarNaoCurtidas(idCandidato)
    }
}