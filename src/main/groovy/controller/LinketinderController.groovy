package controller

import model.Empresa
import model.Candidato
import model.Vaga
import db.dao.CandidatoDao
import db.dao.EmpresaDao
import db.dao.MatchDao
import db.dao.VagaDao


class LinketinderController {

    CandidatoDao candidatoDao = new CandidatoDao()
    EmpresaDao empresaDao = new EmpresaDao()
    VagaDao vagaDao = new VagaDao()
    MatchDao matchDao = new MatchDao()


    int menu(){
        int opcao
        Scanner scanner = new Scanner(System.in)

        println("===== LINKETINDER =====")
        println("1 - LISTA CANDIDATOS")
        println("2 - LISTAR EMPRESAS")
        println("3 - CADASTRAR CANDIDADOS NOVOS")
        println("4 - CADASTRAR EMPRESAS NOVAS")
        println("5 - CADASTRAR VAGAS NOVAS")
        println("6 - CANDIDATO: CURTIR VAGAS")
        println("7 - EMPRESA: CURTIR CANDIDATOS")
        println("8 - VER MATCHS")
        println("0 -  SAIR")
        println("===== ESCOLHA:")

        opcao = scanner.nextLine().toInteger()
        return opcao
    }


    void listagemCandidatos(){
        println()
        for (p in candidatoDao.listarTodos()){
            println(p.exibirDados())
        }
        println()
    }

    void listagemEmpresas(){
        for (p in empresaDao.listarTodas()){
            println(p.exibirDados())
        }
    }


    void novoCadastro(Scanner scanner){
        String nome, email, cpf, estado, cep, descricao, sobrenome, pais, senha
        List<String> competencias = []
        int idade

        println ("--- CADASTRO DE CANDIDATO ---")
        print("Nome: ")
        nome = scanner.nextLine()
        print("Sobrenome: ")
        sobrenome = scanner.nextLine()
        print("Email: ")
        email = scanner.nextLine()
        print("Descrição sobre você: ")
        descricao = scanner.nextLine()
        print("Competências (separadas por vírgula): ")
        competencias = scanner.nextLine().split(",").collect { it.trim() }
        print("CPF: ")
        cpf = scanner.nextLine()
        print("Idade: ")
        idade = scanner.nextLine().toInteger()
        print("Estado (UF): ")
        estado = scanner.nextLine()
        print("CEP: ")
        cep = scanner.nextLine()
        print("País: ")
        pais = scanner.nextLine()
        print("Digite sua senha: ")
        senha = scanner.nextLine()


        Candidato candidato = new Candidato(nome: nome, sobrenome: sobrenome, email: email, cpf: cpf,
                idade: idade, pais: pais, cep: cep, descricao: descricao,
                competencias: competencias, senha: senha, estado:estado)
        candidatoDao.inserir(candidato)
    }

    void novaEmpresa(Scanner scanner){
        String nome, email, cnpj, estado, cep, pais, descricao, senha

        println ("\n--- CADASTRO DE EMPRESA ---")
        print("Nome da empresa: ")
        nome = scanner.nextLine()
        print("Email: ")
        email = scanner.nextLine()
        print("CNPJ: ")
        cnpj = scanner.nextLine()
        print("Estado (UF): ")
        estado = scanner.nextLine()
        print("CEP: ")
        cep = scanner.nextLine()
        print("Descrição: ")
        descricao = scanner.nextLine()
        print("País: ")
        pais = scanner.nextLine()
        print("Digite sua senha: ")
        senha = scanner.nextLine()

        Empresa empresa = new Empresa(nome: nome, email: email, cnpj: cnpj, pais: pais,
                cep: cep, descricao: descricao, senha: senha)
        empresaDao.inserir(empresa)
    }



    void novaVaga(Scanner scanner) {
        String nome, descricao, local, nomeEmpresa
        List<String> competencias = []

        println("\n--- CADASTRO DE VAGA ---")

        print("Nome da empresa responsável pela vaga: ")
        nomeEmpresa = scanner.nextLine()

        Empresa empresa = empresaDao.buscarPorNome(nomeEmpresa)

        if (empresa == null) {
            println("Erro: Empresa não encontrada. Cadastre a empresa primeiro!")
            return
        }

        print("Nome da vaga: ")
        nome = scanner.nextLine()

        print("Descrição da vaga: ")
        descricao = scanner.nextLine()

        print("Local: ")
        local = scanner.nextLine()

        print("Competências exigidas (separadas por vírgula): ")
        competencias = scanner.nextLine().split(",").collect { it.trim() }

        Vaga vaga = new Vaga(nome: nome, descricao: descricao, local: local,
                competencias: competencias, empresa: empresa)
        vagaDao.inserir(vaga)
    }


    void listarVagas() {
        for (v in vagaDao.listarTodas()) {
            println("Vaga: ${v.nome} | Empresa: ${v.empresa.nome} | Local: ${v.local} | Descricao: ${v.descricao} | Competencias: ${v.competencias}")
        }
    }


    void verMatches(Scanner scanner) {
        String visao, nome
        boolean achou = false
        
        print("Visão (c = candidato, e = empresa): ")
        visao = scanner.nextLine()
        print("Seu nome: ")
        nome = scanner.nextLine()
        

        if (visao == 'c') {
            Candidato candidato = candidatoDao.buscarPorNome(nome)
            if (candidato == null) {
                println("Candidato não encontrado.")
                return
            }
            println("=== MATCHES DE ${candidato.nome} ===")
            for (m in matchDao.matchesDoCandidato(candidato.id)) {
                println("Vaga: ${m.vaga} | Empresa: ${m.empresa} | Local: ${m.local}")
                achou = true
            }
        } else if (visao == 'e') {
            Empresa empresa = empresaDao.buscarPorNome(nome)
            if (empresa == null) {
                println("Empresa não encontrada.")
                return
            }
            println("=== MATCHES DA ${empresa.nome} ===")
            for (m in matchDao.matchesDaEmpresa(empresa.id)) {
                println("Vaga: ${m.vaga} | Candidato: ${m.nome} ${m.sobrenome} | Email: ${m.email}")
                achou = true
            }
        }

        if (!achou) {
            println("Nenhum match ainda.")
        }
    }


    def avaliarVagas(Scanner scanner) {
        String nome

        println("\n=== BEM-VINDO AO SISTEMA DE VAGAS ===")
        println("Digite 's' para Curtir ou 'n' para Passar.")

        print("Seu nome: ")
        nome = scanner.nextLine()
        Candidato candidato_buscado = candidatoDao.buscarPorNome(nome)

        if (candidato_buscado == null){
            println("Candidato não encontrado.")
            return
        }

        for (vaga in vagaDao.listarNaoCurtidas(candidato_buscado.id)) {
            println("\n---------------------------------------------------")
            println("VAGA: ${vaga.nome}")
            println("Competências: ${vaga.competencias.join(', ')}")
            println("Local: ${vaga.local}")
            println("---------------------------------------------------")

            print("Tem interesse nesta vaga? (s/n): ")
            String escolha = scanner.nextLine()

            if (escolha == 's') {
                matchDao.curtirVaga(candidato_buscado.id, vaga.id)
                println("!!! Você curtiu a vaga: ${vaga.nome}!")
            } else {
                println("-> Vaga ignorada.")
            }
        }
        println("\nVocê já viu todas as vagas disponíveis no momento!")
    }


    void avaliarCandidatos(Scanner scanner) {
        print("Nome da empresa: ")
        Empresa empresa = empresaDao.buscarPorNome(scanner.nextLine())
        if (empresa == null) {
            println("Empresa não encontrada.")
            return
        }
        println("\n=== BEM-VINDO AO SISTEMA DE VAGAS, RECRUTADOR DA ${empresa.nome} ===")
        println("Digite 's' para Curtir ou 'n' para Passar.")

        for (item in matchDao.listarInteressados(empresa.id)) {
            println("\n---------------------------------------------------")
            println("Interessado na vaga: ${item.nomeVaga}")
            println("Descrição: ${item.descricao}")
            println("Competências: ${item.competencias.join(', ')}")
            println("---------------------------------------------------")

            print("Tem interesse neste perfil? (s/n): ")
            String escolha = scanner.nextLine()

            if (escolha == 's') {
                matchDao.curtirCandidato(empresa.id, item.idCandidato, item.idVaga)
                println("!!! MATCH !!!")
            } else {
                println(" Perfil ignorado! Próximo!!!")
            }
        }
        println("\nVocê já viu todos os candidatos interessados no momento!")
    }


}
