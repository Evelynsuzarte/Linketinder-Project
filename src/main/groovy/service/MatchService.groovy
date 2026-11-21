package service

import model.repository.MatchRepository

class MatchService {

    private final MatchRepository repo

    MatchService(MatchRepository repo) {
        this.repo = repo
    }

    void curtirVaga(int idCandidato, int idVaga) {
        if (idCandidato <= 0 || idVaga <= 0) {
            throw new IllegalArgumentException("Os IDs do candidato e da vaga são obrigatórios.")
        }
        repo.curtirVaga(idCandidato, idVaga)
    }

    List<Map> listarInteressados(int idEmpresa) {
        if (idEmpresa <= 0) {
            throw new IllegalArgumentException("O ID da empresa deve ser válido.")
        }
        return repo.listarInteressados(idEmpresa)
    }

    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga, Integer idRecrutador = null) {
        if (idEmpresa <= 0 || idCandidato <= 0 || idVaga <= 0) {
            throw new IllegalArgumentException("Os IDs da empresa, do candidato e da vaga são obrigatórios.")
        }
        repo.curtirCandidato(idEmpresa, idCandidato, idVaga, idRecrutador)
    }

    void rejeitarCandidato(int idEmpresa, int idCandidato, int idVaga, Integer idRecrutador = null) {
        if (idEmpresa <= 0 || idCandidato <= 0 || idVaga <= 0) {
            throw new IllegalArgumentException("Os IDs da empresa, do candidato e da vaga são obrigatórios.")
        }
        repo.rejeitarCandidato(idEmpresa, idCandidato, idVaga, idRecrutador)
    }

    List<Map> listarRejeitados(int idEmpresa) {
        if (idEmpresa <= 0) {
            throw new IllegalArgumentException("O ID da empresa deve ser válido.")
        }
        return repo.listarRejeitados(idEmpresa)
    }

    List<Map> matchesDoCandidato(int idCandidato) {
        if (idCandidato <= 0) {
            throw new IllegalArgumentException("O ID do candidato deve ser válido.")
        }
        return repo.matchesDoCandidato(idCandidato)
    }

    List<Map> matchesDaEmpresa(int idEmpresa) {
        if (idEmpresa <= 0) {
            throw new IllegalArgumentException("O ID da empresa deve ser válido.")
        }
        return repo.matchesDaEmpresa(idEmpresa)
    }
}
