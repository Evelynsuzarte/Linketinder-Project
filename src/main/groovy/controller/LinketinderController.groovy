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
        println("5 - MATCH CANDIDATO<->EMPRESA")
        println("6 - MATCH EMPRESA<->CANDIDATO")
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

    def novoCandidato(List<Candidato> candidatos, String nome, String email,
                      String cpf, int idade, String estado, String cep,
                      String descricao, List<String> competencias){
        Candidato candidato = new Candidato(nome:nome, email: email, descricao: descricao, cep:cep,
                estado:estado, competencias:competencias,cpf:cpf, idade:idade)
        candidatos<<candidato
        println ("!!!! Cadastro realizado com sucesso !!!!")
        return candidatos

    }

    def novaEmpresa(List<Empresa> empresas, String nome, String email,
                    String cnpj, String estado, String cep,
                    String descricao, List<String> competencias, String pais){
        Empresa empresa = new Empresa(nome:nome, email: email, descricao: descricao, cep:cep,
                estado:estado, competencias:competencias,cnpj:cnpj,pais:pais)
        empresas.add(empresa)
        println ("!!!! Cadastro realizado com sucesso !!!!")
        return empresas
    }

    def novaVaga(List<Vaga> vagas, String nome, Empresa empresa) {
        Vaga vaga = new Vaga(nome, empresa)
        vagas.add(vaga)
        println ("!!!! Vaga cadastrada com sucesso !!!!")
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

    def exibirMatchCandidatos(List<Map> resultados) {
        resultados.groupBy { it.candidato }.each { candidato, matches ->
            println("${candidato}:")
            matches.sort { a, b -> b.quantidade <=> a.quantidade }
            matches.each { match ->
                println("${match.empresa} - ${match.quantidade} de match - ${match.competencias}")
            }
            println("")
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

    def avaliarVagas(Candidato candidato, List<Vaga> vagas) {
        Scanner scanner = new Scanner(System.in)
        List<Vaga> interesse = []
        println("\n=== BEM-VINDO, ${candidato.nome} ===")
        println("Encontre sua próxima oportunidade! Digite 'S' para Curtir ou 'N' para Passar.")

        for (vaga in vagas) {
            println("\n---------------------------------------------------")
            println("VAGA DISPONÍVEL: ${vaga.nome}")
            println("Descrição: ${vaga.descricao}")
            println("---------------------------------------------------")

            print("Tem interesse nesta vaga? (S/N): ")
            String escolha = scanner.nextLine().toUpperCase()

            if (escolha == 'S') {
                println("-> Você curtiu a vaga: ${vaga.nome}!")
                interesse = candidato.getVagasInteresse()
                interesse<<vaga
                // TODO: Lógica para salvar o interesse.
                // Exemplo: candidato.vagasCurtidas.add(vaga)
                // Se a vaga também já curtiu o candidato, aqui rolaria o "MATCH!"
            } else {
                println("-> Vaga ignorada.")
            }
        }
        println("\nVocê já viu todas as vagas disponíveis no momento!")
    }


    def avaliarCandidatos(Empresa empresa, List<Candidato> candidatos) {
        Scanner scanner = new Scanner(System.in)
        println("\n=== BEM-VINDO, RECRUTADOR DA ${empresa.nome} ===")
        println("Encontre talentos! Digite 'S' para Curtir ou 'N' para Passar.")

        for (candidato in candidatos) {
            println("\n---------------------------------------------------")
            println("PERFIL ANÔNIMO")
            println("Idade: ${candidato.idade} anos | Estado: ${candidato.estado}")
            println("Descrição: ${candidato.descricao}")
            println("Competências: ${candidato.competencias.join(', ')}")
            println("---------------------------------------------------")

            print("A empresa tem interesse neste perfil? (S/N): ")
            String escolha = scanner.nextLine().toUpperCase()

            if (escolha == 'S') {
                println("-> Você curtiu este perfil!")
                // TODO: Lógica para salvar o interesse da empresa
                // Exemplo: empresa.candidatosCurtidos.add(candidato)
            } else {
                println("-> Perfil ignorado.")
            }
        }
        println("\nVocê já viu todos os candidatos disponíveis no momento!")
    }



}
