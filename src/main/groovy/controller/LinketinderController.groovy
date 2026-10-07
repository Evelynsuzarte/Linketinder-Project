package controller

import model.Empresa
import model.Candidato
import model.Vaga

class LinketinderController {

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


    def listagemCandidatos(List<Candidato> candidatos){
        for (p in candidatos){
            println(p.exibirDados())
        }
    }

    def listagemEmpresas(List<Empresa> empresas){
        for (p in empresas){
            println(p.exibirDados())
        }
    }

//    def novoCandidato(List<Candidato> candidatos, String nome, String email,
//                      String cpf, int idade, String estado, String cep,
//                      String descricao, List<String> competencias){
//        Candidato candidato = new Candidato(nome:nome, email: email, descricao: descricao, cep:cep,
//                estado:estado, competencias:competencias,cpf:cpf, idade:idade)
//        candidatos<<candidato
//        println ("!!!! Cadastro realizado com sucesso !!!!")
//        return candidatos
//
//    }


    def novoCadastro(List<Candidato> candidatos, Scanner scanner){
        String nome, email, cpf, estado, cep, descricao
        List<String> competencias = []
        int idade

        println ("--- CADASTRO DE CANDIDATO ---")
        print("Nome: ")
        nome = scanner.nextLine()

        print("Email: ")
        email = scanner.nextLine()

        print("CPF: ")
        cpf = scanner.nextLine()

        print("Idade: ")
        idade = scanner.nextLine().toInteger()

        print("Estado (UF): ")
        estado = scanner.nextLine()

        print("CEP: ")
        cep = scanner.nextLine()

        print("Descrição: ")
        descricao = scanner.nextLine()

        print("Competências (separadas por vírgula): ")
        competencias = scanner.nextLine().split(",").collect { it.trim() }

        //gerenciador.novoCandidato(candidatos,nome, email, cpf, idade, estado, cep, descricao, competencias)

        Candidato candidato = new Candidato(nome:nome, email: email, descricao: descricao, cep:cep,
                estado:estado, competencias:competencias,cpf:cpf, idade:idade)
        candidatos<<candidato

        return candidatos
    }

    def novaEmpresa(List<Empresa> empresas, Scanner scanner){
        String nome, email, cnpj, cpf, estado, cep, pais, descricao
        List<String> competencias = []

        println ("\n--- CADASTRO DE EMPRESA ---")
        print("Nome da Empresa: ")
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

        print("Competências procuradas (separadas por vírgula): ")
        competencias = scanner.nextLine().split(",").collect { it.trim() }

        print("País: ")
        pais = scanner.nextLine()

        //gerenciador.novaEmpresa(empresas, nome, email, cnpj, estado, cep, descricao, competencias, pais)

        Empresa empresa = new Empresa(nome:nome, email: email, descricao: descricao, cep:cep,
                estado:estado, competencias:competencias,cnpj:cnpj,pais:pais)
        empresas.add(empresa)

        return empresas
    }

//    def novaEmpresa(List<Empresa> empresas, String nome, String email,
//                    String cnpj, String estado, String cep,
//                    String descricao, List<String> competencias, String pais){
//        Empresa empresa = new Empresa(nome:nome, email: email, descricao: descricao, cep:cep,
//                estado:estado, competencias:competencias,cnpj:cnpj,pais:pais)
//        empresas.add(empresa)
//        println ("!!!! Cadastro realizado com sucesso !!!!")
//        return empresas
//    }

//    def novaVaga(List<Vaga> vagas, Empresa empresa, String nome, String descricao,
//                 String local, List<String> competencias) {
//        Vaga vaga = new Vaga(nome: nome, descricao: descricao, local: local,
//                competencias: competencias, empresa: empresa)
//        vagas << vaga
//        empresa.vagas << vaga
//        println("!!!! Vaga cadastrada com sucesso !!!!")
//        return vagas
//    }

    def novaVaga(List<Vaga> vagas, List<Empresa> empresas, Scanner scanner) {
        String nome, descricao, local, nomeEmpresa
        List<String> competencias = []

        println("\n--- CADASTRO DE VAGA ---")


        print("Nome da empresa responsável pela vaga: ")
        nomeEmpresa = scanner.nextLine()

        Empresa empresa = empresas.find { it.nome.equalsIgnoreCase(nomeEmpresa) }

        if (empresa == null) {
            println("Erro: Empresa não encontrada. Cadastre a empresa primeiro!")
            return vagas
        }

        print("Nome da vaga: ")
        nome = scanner.nextLine()

        print("Descrição da vaga: ")
        descricao = scanner.nextLine()

        print("Local: ")
        local = scanner.nextLine()

        print("Competências exigidas (separadas por vírgula): ")
        competencias = scanner.nextLine().split(",").collect { it.trim() }

        Vaga vaga = new Vaga(nome: nome, descricao: descricao, local: local, competencias: competencias, empresa: empresa)

        vagas.add(vaga)
        empresa.vagas.add(vaga)

        return [vagas: vagas, empresas: empresas]
    }





    def listarVagas(List<Vaga> vagas) {
        for (v in vagas) {
            println("Vaga: ${v.nome} | Local: ${v.local} | Descricao: ${v.descricao} | Competencias: ${v.competencias}")
        }
    }


    def match(List<Candidato> candidatos, List<Empresa> empresas, String visao){
        List<Map> matchesGerais = []
        int nMatch, competenciasCand
        List<Map> resultados = []

        if (visao == 'c'){
            for (c in candidatos){
                resultados = []
                for (e in empresas){
                    nMatch = c.competencias.intersect(e.competencias).size()
                    competenciasCand = c.competencias.size()
                    if (nMatch >= competenciasCand - 2){
                        resultados.add([
                                candidato: c.nome,
                                empresa: e.nome,
                                quantidade: nMatch,
                                competencias: c.competencias.intersect(e.competencias)
                        ])
                    }
                }
                resultados.sort { a, b ->
                    b.quantidade <=> a.quantidade
                }
                matchesGerais.addAll(resultados.take(3))
            }
        } else if (visao == 'e'){
            int competenciasEmp
            for (e in empresas){
                resultados = []

                for (c in candidatos){
                    nMatch = e.competencias.intersect(c.competencias).size()
                    competenciasEmp = e.competencias.size()

                    if (nMatch >= competenciasEmp - 2){
                        resultados.add([
                                candidato: c.nome,
                                empresa: e.nome,
                                quantidade: nMatch,
                                competencias: e.competencias.intersect(c.competencias)
                        ])
                    }
                }
                resultados.sort { a, b ->
                    b.quantidade <=> a.quantidade
                }
                matchesGerais.addAll(resultados.take(3))
            }
        }
        return matchesGerais

    }

    def matchIndividual(List<Candidato> candidatos, List<Empresa> empresas, String visao, String nome){
        List<Map> resultados = []
        int nMatch
        int competenciasCand, competenciasEmp
        if (visao == 'c'){
            Candidato candidato = candidatos.find { it.nome == nome }
            if (candidato == null){
                return []
            }
            competenciasCand = candidato.competencias.size()

            for (e in empresas){
                nMatch = candidato.competencias.intersect(e.competencias).size()
                if (nMatch >= competenciasCand - 2){
                    resultados.add([
                            candidato: candidato.nome,
                            empresa: e.nome,
                            quantidade: nMatch,
                            competencias: candidato.competencias.intersect(e.competencias)
                    ])
                }
            }

        } else if (visao == 'e'){
            Empresa empresa = empresas.find { it.nome == nome }
            if (empresa == null){
                return []
            }
            competenciasEmp = empresa.competencias.size()
            for (c in candidatos){
                nMatch = empresa.competencias.intersect(c.competencias).size()
                if (nMatch >= competenciasEmp - 2){
                    resultados.add([
                            candidato: c.nome,
                            empresa: empresa.nome,
                            quantidade: nMatch,
                            competencias: empresa.competencias.intersect(c.competencias)
                    ])
                }
            }
        }
        resultados.sort { a, b ->
            b.quantidade <=> a.quantidade
        }
        return resultados.take(3)
    }

    def verMatches(List<Candidato> candidatos, List<Empresa> empresas, String visao, String nome) {
        boolean achou = false

        if (visao == 'c') {
            Candidato candidato = candidatos.find { it.nome == nome }
            if (candidato == null) {
                println("Candidato não encontrado.")
                return
            }
            println("=== MATCHES DE ${candidato.nome} ===")
            for (vaga in candidato.vagasInteresse) {
                if (vaga.matches.contains(candidato)) {
                    println("Vaga: ${vaga.nome} | Empresa: ${vaga.empresa.nome} | Local: ${vaga.local}")
                    achou = true
                }
            }
        } else if (visao == 'e') {
            Empresa empresa = empresas.find { it.nome == nome }
            if (empresa == null) {
                println("Empresa não encontrada.")
                return
            }
            println("=== MATCHES DA ${empresa.nome} ===")
            for (vaga in empresa.vagas) {
                for (c in vaga.matches) {
                    println("Vaga: ${vaga.nome} | Candidato: ${c.nome} | Email: ${c.email}")
                    achou = true
                }
            }
        }

        if (!achou) {
            println("Nenhum match ainda.")
        }
    }

    def exibirMatchEmpresas(List<Map> resultados) {
        println("para empresa:")
        resultados.groupBy { it.empresa }.each { empresa, matches ->
            println("${empresa}")
            matches.sort { a, b -> b.quantidade <=> a.quantidade }
            matches.each { match ->
                println("${match.candidato} - ${match.quantidade} de match - ${match.competencias}")
            }
            println("")
        }
    }

    def avaliarVagas(Candidato candidato, List<Vaga> vagas, Scanner scanner) {
        println("\n=== BEM-VINDO AO SISTEMA DE VAGAS, ${candidato.nome} ===")
        println("Digite 's' para Curtir ou 'n' para Passar.")

        for (vaga in vagas) {
            if (candidato.vagasInteresse.contains(vaga)) {
                continue
            }
            println("\n---------------------------------------------------")
            println("VAGA: ${vaga.nome}")
            println("Competências: ${vaga.competencias.join(', ')}")
            println("Local: ${vaga.local}")
            println("---------------------------------------------------")

            print("Tem interesse nesta vaga? (s/n): ")
            String escolha = scanner.nextLine()

            if (escolha == 's') {
                candidato.vagasInteresse << vaga
                vaga.interessados << candidato
                println("!!! Você curtiu a vaga: ${vaga.nome}!")
            } else {
                println("-> Vaga ignorada.")
            }
        }
        println("\nVocê já viu todas as vagas disponíveis no momento!")
    }


    def avaliarCandidatos(Empresa empresa, Scanner scanner) {
        println("\n=== BEM-VINDO AO SISTEMA DE VAGAS, RECRUTADOR DA ${empresa.nome} ===")
        println("Digite 's' para Curtir ou 'n' para Passar.")

        for (vaga in empresa.vagas) {
            for (candidato in vaga.interessados) {
                if (vaga.matches.contains(candidato)) {
                    continue
                }
                println("\n---------------------------------------------------")
                println("Interessado na vaga: ${vaga.nome}")
                println("Descrição: ${candidato.descricao}")
                println("Competências: ${candidato.competencias.join(', ')}")
                println("---------------------------------------------------")

                print("Tem interesse neste perfil? (s/n): ")
                String escolha = scanner.nextLine()

                if (escolha == 's') {
                    vaga.matches << candidato
                    println("!!! MATCH !!!")
                } else {
                    println(" Perfil ignorado! Próximo!!!")
                }
            }
        }
        println("\nVocê já viu todos os candidatos interessados no momento!")
    }



}
