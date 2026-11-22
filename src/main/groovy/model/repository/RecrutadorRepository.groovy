package model.repository

import model.entity.Recrutador

interface RecrutadorRepository {
    void inserir(Recrutador recrutador)
    List<Recrutador> listarPorEmpresa(int idEmpresa)
    Recrutador buscarPorId(int id)
    Recrutador buscarPorEmail(String email)
    Recrutador buscarPorCpf(String cpf)
    Recrutador buscarPorNome(String nome)
    void atualizar(Recrutador recrutador)
    void remover(int id)
}
