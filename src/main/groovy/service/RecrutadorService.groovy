package service

import model.entity.Empresa
import model.entity.Recrutador
import model.repository.EmpresaRepository
import model.repository.RecrutadorRepository

class RecrutadorService {

    private final RecrutadorRepository repo
    private final EmpresaRepository empresaRepo

    RecrutadorService(RecrutadorRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo
        this.empresaRepo = empresaRepo
    }

    void inserir(Recrutador recrutador) {
        if (recrutador == null) {
            throw new IllegalArgumentException("Os dados do recrutador não podem ser nulos.")
        }
        validarEmpresaExistente(recrutador.empresa?.id ?: 0)
        validarCamposComuns(recrutador)

        if (!recrutador.senha?.trim()) {
            throw new IllegalArgumentException("A senha do recrutador é obrigatória.")
        }
        if (repo.buscarPorEmail(recrutador.email) != null) {
            throw new IllegalArgumentException("Já existe um recrutador cadastrado com este e-mail.")
        }
        if (repo.buscarPorCpf(recrutador.cpf) != null) {
            throw new IllegalArgumentException("Já existe um recrutador cadastrado com este CPF.")
        }

        recrutador.senha = SegurancaSenha.gerarHash(recrutador.senha)
        repo.inserir(recrutador)
    }

    void atualizar(Recrutador recrutador) {
        if (recrutador == null || recrutador.id <= 0) {
            throw new IllegalArgumentException("Os dados e o ID do recrutador são obrigatórios para edição.")
        }
        Recrutador existente = repo.buscarPorId(recrutador.id)
        if (existente == null) {
            throw new IllegalArgumentException("Recrutador não encontrado.")
        }

        int idEmpresa = recrutador.empresa?.id ?: existente.empresa?.id ?: 0
        Empresa empresa = validarEmpresaExistente(idEmpresa)
        recrutador.empresa = empresa

        validarCamposComuns(recrutador)

        Recrutador porEmail = repo.buscarPorEmail(recrutador.email)
        if (porEmail != null && porEmail.id != recrutador.id) {
            throw new IllegalArgumentException("Já existe um recrutador cadastrado com este e-mail.")
        }
        Recrutador porCpf = repo.buscarPorCpf(recrutador.cpf)
        if (porCpf != null && porCpf.id != recrutador.id) {
            throw new IllegalArgumentException("Já existe um recrutador cadastrado com este CPF.")
        }

        if (!recrutador.senha?.trim()) {
            recrutador.senha = existente.senha
        } else if (!SegurancaSenha.estaHasheada(recrutador.senha)) {
            recrutador.senha = SegurancaSenha.gerarHash(recrutador.senha)
        }

        repo.atualizar(recrutador)
    }

    boolean autenticar(String email, String senha) {
        if (!email?.trim() || !senha) {
            return false
        }
        Recrutador recrutador = repo.buscarPorEmail(email.trim())
        return recrutador != null && SegurancaSenha.verificarSenha(senha, recrutador.senha)
    }

    void remover(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID do recrutador deve ser válido.")
        }
        if (repo.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Recrutador não encontrado.")
        }
        repo.remover(id)
    }

    List<Map> listarSegurosPorEmpresa(int idEmpresa) {
        if (idEmpresa <= 0) {
            throw new IllegalArgumentException("O ID da empresa deve ser válido.")
        }
        return repo.listarPorEmpresa(idEmpresa).collect { paraVisaoSegura(it) }
    }

    Map buscarSeguroPorId(int id) {
        Recrutador recrutador = buscarPorId(id)
        return recrutador ? paraVisaoSegura(recrutador) : null
    }

    Recrutador buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID do recrutador deve ser válido.")
        }
        return repo.buscarPorId(id)
    }

    Recrutador buscarPorNome(String nome) {
        if (!nome?.trim()) {
            throw new IllegalArgumentException("O nome do recrutador não pode ser vazio.")
        }
        return repo.buscarPorNome(nome.trim())
    }

    Recrutador buscarPorEmail(String email) {
        if (!email?.trim()) {
            throw new IllegalArgumentException("O e-mail do recrutador não pode ser vazio.")
        }
        return repo.buscarPorEmail(email.trim())
    }

    static Map paraVisaoSegura(Recrutador r) {
        return [
                id              : r.id,
                idEmpresa       : r.empresa?.id,
                nomeEmpresa     : r.empresa?.nome,
                nome            : r.nome,
                areaRecrutamento: r.areaRecrutamento,
                descricao       : r.descricao,
                email           : r.email
        ]
    }

    private Empresa validarEmpresaExistente(int idEmpresa) {
        if (idEmpresa <= 0) {
            throw new IllegalArgumentException("O ID da empresa é obrigatório.")
        }
        Empresa empresa = empresaRepo.buscarPorId(idEmpresa)
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa não encontrada.")
        }
        return empresa
    }

    private static void validarCamposComuns(Recrutador recrutador) {
        if (!recrutador.nome?.trim()) {
            throw new IllegalArgumentException("O nome do recrutador é obrigatório.")
        }
        if (!recrutador.areaRecrutamento?.trim()) {
            throw new IllegalArgumentException("A área de recrutamento é obrigatória.")
        }
        if (!Validador.emailValido(recrutador.email)) {
            throw new IllegalArgumentException("E-mail do recrutador inválido.")
        }
        recrutador.email = recrutador.email.trim()

        if (!Validador.cpfValido(recrutador.cpf)) {
            throw new IllegalArgumentException("CPF do recrutador inválido.")
        }
        recrutador.cpf = Validador.limparCpf(recrutador.cpf)

        if (!Validador.maiorDeIdade(recrutador.dataNascimento)) {
            throw new IllegalArgumentException("O recrutador deve ter no mínimo 18 anos e data de nascimento válida.")
        }
    }
}
