import repository.MatchRepository

class MatchRepositoryFake implements MatchRepository {

    private final List<List<Integer>> curtidasDeCandidatos = []   // [idCandidato, idVaga]
    private final List<List<Integer>> matches = []                // [idEmpresa, idCandidato, idVaga]

    void curtirVaga(int idCandidato, int idVaga) {
        curtidasDeCandidatos << [idCandidato, idVaga]
    }

    List<Map> listarInteressados(int idEmpresa) {
        return curtidasDeCandidatos.collect { [idCandidato: it[0], idVaga: it[1]] }
    }

    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga) {
        matches << [idEmpresa, idCandidato, idVaga]
    }

    List<Map> matchesDoCandidato(int idCandidato) {
        return matches.findAll { it[1] == idCandidato }.collect { [idVaga: it[2]] }
    }

    List<Map> matchesDaEmpresa(int idEmpresa) {
        return matches.findAll { it[0] == idEmpresa }.collect { [idCandidato: it[1], idVaga: it[2]] }
    }
}