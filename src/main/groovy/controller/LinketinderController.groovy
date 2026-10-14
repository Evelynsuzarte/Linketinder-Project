package controller

import model.Empresa
import model.Candidato
import model.Vaga
import db.dao.CandidatoDao
import db.dao.EmpresaDao
import db.dao.MatchDao
import db.dao.VagaDao
import java.sql.SQLException


class LinketinderController {

    CandidatoDao candidatoDao = new CandidatoDao()
    EmpresaDao empresaDao = new EmpresaDao()
    VagaDao vagaDao = new VagaDao()
    MatchDao matchDao = new MatchDao()

    enum StatusMatch {
        EMPRESA_NAO_ENCONTRADA,
        CANDIDATO_NAO_ENCONTRADO,
        COM_MATCHES,
        SEM_MATCHES
    }

    int menu(){
        Scanner scanner = new Scanner(System.in)

        println("===== LINKETINDER =====")
        println("1 - LISTAR CANDIDATOS")
        println("2 - LISTAR EMPRESAS")
        println("3 - LISTAR VAGAS")
        println("4 - CADASTRAR CANDIDADOS NOVOS")
        println("5 - CADASTRAR EMPRESAS NOVAS")
        println("6 - CADASTRAR VAGAS NOVAS")
        println("7 - CANDIDATO: CURTIR VAGAS")
        println("8 - EMPRESA: CURTIR CANDIDATOS")
        println("9 - VER MATCHS")
        println("10 -  SAIR")
        println("===== ESCOLHA:")

        String texto = scanner.nextLine().trim()
        return texto.isInteger() ? texto.toInteger() : -1
    }


    private String lerObrigatorio(Scanner scanner, String mensagem) {
        String texto = ""
        while (texto.isEmpty()) {
            print(mensagem)
            texto = scanner.nextLine().trim()
            if (texto.isEmpty()) {
                println("Campo obrigatório. Tente novamente.")
            }
        }
        return texto
    }

    private int lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            print(mensagem)
            String texto = scanner.nextLine().trim()
            if (texto.isInteger()) {
                return texto.toInteger()
            }
            println("Digite apenas números.")
        }
    }

    private List<String> lerCompetencias(Scanner scanner, String mensagem) {
        print(mensagem)
        return scanner.nextLine().split(",").collect { it.trim() }.findAll { !it.isEmpty() }
    }

    private void avisarErroDeBanco(SQLException erro) {
        switch (erro.SQLState) {
            case '23505':
                println("\nErro: já existe um registro com esses dados (CPF ou CNPJ repetido).")
                break
            case '22001':
                println("\nErro: algum campo tem mais caracteres do que o permitido.")
                break
            default:
                println("\nErro ao acessar o banco de dados: ${erro.message}")
        }
    }


    void listagemCandidatos(){
        println()
        for (p in candidatoDao.listarTodos()){
            println(p.exibirDados())
        }
        println()
    }

    void listagemEmpresas(){
        println()
        for (p in empresaDao.listarTodas()){
            println(p.exibirDados())
        }
        println()
    }

    void listagemVagas() {
        println()
        for (v in vagaDao.listarTodas()) {
            println("Vaga: ${v.nome} | Empresa: ${v.empresa.nome} | Local: ${v.local} | Descricao: ${v.descricao} | Competencias: ${v.competencias}")
        }
        println()
    }

    Candidato entradaDadosCandidato(Scanner scanner){
        String nome, email, cpf, estado, cep, descricao, sobrenome, pais, senha
        int idade
        List<String> competencias = []

        println ("--- CADASTRO DE CANDIDATO ---")
        nome = lerObrigatorio(scanner, "Nome: ")
        sobrenome = lerObrigatorio(scanner, "Sobrenome: ")
        email = lerObrigatorio(scanner, "Email: ")
        print("Descrição sobre você: ")
        descricao = scanner.nextLine()
        competencias = lerCompetencias(scanner, "Competências (separadas por vírgula): ")
        cpf = lerObrigatorio(scanner, "CPF: ")
        idade = lerInteiro(scanner, "Idade: ")
        print("Estado (UF): ")
        estado = scanner.nextLine()
        print("CEP: ")
        cep = scanner.nextLine()
        print("País: ")
        pais = scanner.nextLine()
        senha = lerObrigatorio(scanner, "Digite sua senha: ")

        Candidato candidato = new Candidato(nome: nome, sobrenome: sobrenome, email: email, cpf: cpf,
                idade: idade, pais: pais, cep: cep, descricao: descricao,
                competencias: competencias, senha: senha, estado:estado)
        return candidato
    }


    void novoCandidato(Scanner scanner){
        Candidato candidato = entradaDadosCandidato(scanner)
        try {
            candidatoDao.inserir(candidato)
        } catch (SQLException erro) {
            avisarErroDeBanco(erro)
        }
    }

    Empresa entradaDadosEmpresa(Scanner scanner){
        String nome, email, cnpj, estado, cep, pais, descricao, senha

        println ("\n--- CADASTRO DE EMPRESA ---")
        nome = lerObrigatorio(scanner, "Nome da empresa: ")
        email = lerObrigatorio(scanner, "Email: ")
        cnpj = lerObrigatorio(scanner, "CNPJ: ")
        print("Estado (UF): ")
        estado = scanner.nextLine()
        print("CEP: ")
        cep = scanner.nextLine()
        print("Descrição: ")
        descricao = scanner.nextLine()
        print("País: ")
        pais = scanner.nextLine()
        senha = lerObrigatorio(scanner, "Digite sua senha: ")

        Empresa empresa = new Empresa(nome: nome, email: email, cnpj: cnpj, pais: pais,
                cep: cep, descricao: descricao, senha: senha)
        return empresa
    }

    void novaEmpresa(Scanner scanner){
        Empresa empresa = entradaDadosEmpresa(scanner)
        try {
            empresaDao.inserir(empresa)
        } catch (SQLException erro) {
            avisarErroDeBanco(erro)
        }
    }

    Vaga entradaDadosVaga(Scanner scanner){
        String nome, descricao, local, nomeEmpresa
        List<String> competencias = []

        println("\n--- CADASTRO DE VAGA ---")
        nomeEmpresa = lerObrigatorio(scanner, "Nome da empresa responsável pela vaga: ")

        Empresa empresa = empresaDao.buscarPorNome(nomeEmpresa)

        if (empresa == null) {
            return null
        }

        nome = lerObrigatorio(scanner, "Nome da vaga: ")
        print("Descrição da vaga: ")
        descricao = scanner.nextLine()
        print("Local: ")
        local = scanner.nextLine()
        competencias = lerCompetencias(scanner, "Competências exigidas (separadas por vírgula): ")

        Vaga vaga = new Vaga(nome: nome, descricao: descricao, local: local,
                competencias: competencias, empresa: empresa)
        return vaga
    }

    void novaVaga(Scanner scanner) {
        Vaga vaga = entradaDadosVaga(scanner)
        if (vaga == null) {
            println("\nErro: Empresa não encontrada. Cadastre a empresa primeiro!")
            return
        }

        try {
            vagaDao.inserir(vaga)
        } catch (SQLException erro) {
            avisarErroDeBanco(erro)
        }
    }


    StatusMatch verMatchesCandidato(String nome){
        boolean achou = false

        Candidato candidato = candidatoDao.buscarPorNome(nome)
        if (candidato == null) {
            return StatusMatch.CANDIDATO_NAO_ENCONTRADO
        }
        println("=== MATCHES DE ${candidato.nome} ===")
        for (m in matchDao.matchesDoCandidato(candidato.id)) {
            println("Vaga: ${m.vaga} | Empresa: ${m.empresa} | Local: ${m.local}")
            achou = true
        }

        if (achou) {
            return StatusMatch.COM_MATCHES
        } else {
            return StatusMatch.SEM_MATCHES
        }
    }

    StatusMatch verMatchesEmpresa(String nome){
        boolean achou = false

        Empresa empresa = empresaDao.buscarPorNome(nome)
        if (empresa == null) {
            return StatusMatch.EMPRESA_NAO_ENCONTRADA
        }
        println("=== MATCHES DA ${empresa.nome} ===")
        for (m in matchDao.matchesDaEmpresa(empresa.id)) {
            println("Vaga: ${m.vaga} | Candidato: ${m.nome} ${m.sobrenome} | Email: ${m.email}")
            achou = true
        }

        if (achou) {
            return StatusMatch.COM_MATCHES
        } else {
            return StatusMatch.SEM_MATCHES
        }
    }


    void verMatches(Scanner scanner) {
        String visao, nome

        print("Visão (c = candidato, e = empresa): ")
        visao = scanner.nextLine().trim().toLowerCase()

        if (visao != 'c' && visao != 'e') {
            println("Visão inválida. Digite 'c' ou 'e'.")
            return
        }

        nome = lerObrigatorio(scanner, "Seu nome: ")

        if (visao == 'c') {
            def resultado = verMatchesCandidato(nome)
            switch (resultado) {
                case StatusMatch.CANDIDATO_NAO_ENCONTRADO:
                    println("Candidato não encontrado.")
                    break
                case StatusMatch.SEM_MATCHES:
                    println("Nenhum match ainda.")
                    break
                case StatusMatch.COM_MATCHES:
                    println("Matches listados com sucesso!")
                    break
            }

        } else {
            def resultado = verMatchesEmpresa(nome)
            switch (resultado) {
                case StatusMatch.EMPRESA_NAO_ENCONTRADA:
                    println("Empresa não encontrada.")
                    break
                case StatusMatch.SEM_MATCHES:
                    println("Nenhum match ainda.")
                    break
                case StatusMatch.COM_MATCHES:
                    println("Matches listados com sucesso!")
                    break
            }
        }
    }

    void exibirVaga(Vaga vaga){
        println("\n---------------------------------------------------")
        println("VAGA: ${vaga.nome}")
        println("Competências: ${vaga.competencias.join(', ')}")
        println("Local: ${vaga.local}")
        println("---------------------------------------------------")
    }

    void exibirMatch (Object item){
        println("\n---------------------------------------------------")
        println("Perfil interessado na vaga: ${item.nomeVaga}")
        println("Descrição do candidato: ${item.descricao}")
        println("Competências do candidato: ${item.competencias.join(', ')}")
        println("---------------------------------------------------")
    }

    void avaliarVagas(Scanner scanner) {
        String nome

        println("\n=== BEM-VINDO AO SISTEMA DE VAGAS ===")
        println("Digite 's' para Curtir ou 'n' para Passar.")

        nome = lerObrigatorio(scanner, "Seu nome: ")
        Candidato candidato_buscado = candidatoDao.buscarPorNome(nome)

        if (candidato_buscado == null){
            println("\nCandidato não encontrado.")
            return
        }

        if (vagaDao.listarNaoCurtidas(candidato_buscado.id).isEmpty()){
            println("\nNenhuma vaga para curtir")
            return
        }

        for (vaga in vagaDao.listarNaoCurtidas(candidato_buscado.id)) {
            exibirVaga(vaga)
            print("Tem interesse nesta vaga? (s/n): ")
            String escolha = scanner.nextLine()

            if (escolha == 's') {
                try {
                    matchDao.curtirVaga(candidato_buscado.id, vaga.id)
                    println("!!! Você curtiu a vaga: ${vaga.nome}!")
                } catch (SQLException erro) {
                    avisarErroDeBanco(erro)
                }
            } else {
                println("-> Vaga ignorada.")
            }
        }
        println("\nVocê já viu todas as vagas disponíveis no momento!")
    }


    void avaliarCandidatos(Scanner scanner) {
        String nomeEmpresa = lerObrigatorio(scanner, "Nome da empresa: ")
        Empresa empresa = empresaDao.buscarPorNome(nomeEmpresa)
        if (empresa == null) {
            println("\nEmpresa não encontrada.")
            return
        }
        println("\n=== BEM-VINDO AO SISTEMA DE VAGAS, RECRUTADOR DA ${empresa.nome} ===")
        println("Digite 's' para Curtir ou 'n' para Passar.")

        if (matchDao.listarInteressados(empresa.id).isEmpty()){
            println("\nNenhuma vaga para curtir")
            return
        }

        for (perfilMatch in matchDao.listarInteressados(empresa.id)) {
            exibirMatch(perfilMatch)

            print("Tem interesse neste perfil? (s/n): ")
            String escolha = scanner.nextLine()

            if (escolha == 's') {
                try {
                    matchDao.curtirCandidato(empresa.id, perfilMatch.idCandidato, perfilMatch.idVaga)
                    println("\n!!! MATCH !!!")
                } catch (SQLException erro) {
                    avisarErroDeBanco(erro)
                }
            } else {
                println("\nPerfil ignorado! Próximo!!!")
            }
        }
        println("\nVocê já viu todos os candidatos interessados no momento!")
    }


}