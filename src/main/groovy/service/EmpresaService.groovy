package service

import model.entity.Empresa
import model.repository.EmpresaRepository

class EmpresaService{

    private final EmpresaRepository repo

    EmpresaService(EmpresaRepository repo) {
        this.repo = repo
    }

    void inserir(Empresa empresa) {
        if (!empresa) {
            throw new IllegalArgumentException("Os dados da empresa não podem ser nulos.")
        }
        if (empresa.senha?.trim() && !SegurancaSenha.estaHasheada(empresa.senha)) {
            empresa.senha = SegurancaSenha.gerarHash(empresa.senha)
        }
        repo.inserir(empresa)
    }

    void editar(Empresa empresa) {
        if (!empresa || !empresa.id) {
            throw new IllegalArgumentException("Os dados e o ID da empresa são obrigatórios para edição.")
        }
        if (empresa.senha?.trim() && !SegurancaSenha.estaHasheada(empresa.senha)) {
            empresa.senha = SegurancaSenha.gerarHash(empresa.senha)
        }
        repo.editar(empresa)
    }

    List<Empresa> listarTodas() {
        return repo.listarTodas()
    }

    Empresa buscarPorEmail(String email) {
        if (!email?.trim()) {
            throw new IllegalArgumentException("O e-mail não pode ser vazio.")
        }
        return repo.buscarPorEmail(email.trim())
    }

    Empresa buscarPorNome(String nome) {
        if (!nome?.trim()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.")
        }
        return repo.buscarPorNome(nome.trim())
    }

    Empresa buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID da empresa deve ser válido.")
        }
        return repo.buscarPorId(id)
    }
}