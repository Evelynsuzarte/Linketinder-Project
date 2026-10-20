package repository

interface MatchRepository {
    void curtirVaga(int idCandidato, int idVaga)
    List<Map> listarInteressados(int idEmpresa)
    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga)
    List<Map> matchesDoCandidato(int idCandidato)
    List<Map> matchesDaEmpresa(int idEmpresa)
}
