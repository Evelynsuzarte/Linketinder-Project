import model.repository.MatchRepository

class MatchRepositoryFake implements MatchRepository {

    private final List<List<Integer>> curtidasDeCandidatos = []   // [idCandidato, idVaga]
    private final List<Map> matches = []                          // [idEmpresa, idCandidato, idVaga, idRecrutador]
    private final List<Map> rejeicoes = []                        // [idEmpresa, idCandidato, idVaga, idRecrutador]

    void curtirVaga(int idCandidato, int idVaga) {
        curtidasDeCandidatos << [idCandidato, idVaga]
    }

    List<Map> listarInteressados(int idEmpresa) {
        return curtidasDeCandidatos.findAll { curtida ->
            boolean jaDeuMatch = matches.any { it.idEmpresa == idEmpresa && it.idCandidato == curtida[0] && it.idVaga == curtida[1] }
            boolean jaRejeitado = rejeicoes.any { it.idEmpresa == idEmpresa && it.idCandidato == curtida[0] && it.idVaga == curtida[1] }
            return !jaDeuMatch && !jaRejeitado
        }.collect { [idCandidato: it[0], idVaga: it[1], descricao: "Perfil anônimo", competencias: ["Groovy"]] }
    }

    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga) {
        curtirCandidato(idEmpresa, idCandidato, idVaga, null)
    }

    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga, Integer idRecrutador) {
        matches << [idEmpresa: idEmpresa, idCandidato: idCandidato, idVaga: idVaga, idRecrutador: idRecrutador]
    }

    void rejeitarCandidato(int idEmpresa, int idCandidato, int idVaga, Integer idRecrutador) {
        rejeicoes << [idEmpresa: idEmpresa, idCandidato: idCandidato, idVaga: idVaga, idRecrutador: idRecrutador]
    }

    List<Map> listarRejeitados(int idEmpresa) {
        return rejeicoes.findAll { it.idEmpresa == idEmpresa }.collect {
            [idCandidato: it.idCandidato, idVaga: it.idVaga, descricao: "Perfil anônimo", competencias: ["Groovy"]]
        }
    }

    List<Map> matchesDoCandidato(int idCandidato) {
        return matches.findAll { it.idCandidato == idCandidato }.collect { [idVaga: it.idVaga] }
    }

    List<Map> matchesDaEmpresa(int idEmpresa) {
        return matches.findAll { it.idEmpresa == idEmpresa }.collect {
            [idCandidato: it.idCandidato, idVaga: it.idVaga, idRecrutador: it.idRecrutador]
        }
    }
}
