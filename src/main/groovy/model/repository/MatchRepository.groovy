package model.repository

interface MatchRepository {
    void curtirVaga(int idCandidato, int idVaga)
    List<Map> listarInteressados(int idEmpresa)
    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga)
    void curtirCandidato(int idEmpresa, int idCandidato, int idVaga, Integer idRecrutador)
    void rejeitarCandidato(int idEmpresa, int idCandidato, int idVaga, Integer idRecrutador)
    List<Map> listarRejeitados(int idEmpresa)
    List<Map> matchesDoCandidato(int idCandidato)
    List<Map> matchesDaEmpresa(int idEmpresa)
}
