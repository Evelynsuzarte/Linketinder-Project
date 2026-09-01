package controller

import model.PessoaJuridica
import model.PessoaFisica

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


    def listagemCandidatos(List<PessoaFisica> candidatos){
        for (p in candidatos){
            println(p.exibirDados())
        }
    }

    def listagemEmpresas(List<PessoaJuridica> empresas){
        for (p in empresas){
            println(p.exibirDados())
        }
    }

    def novoCandidato(List<PessoaFisica> candidatos, String nome, String email,
                      String cpf, int idade, String estado, String cep,
                      String descricao, List<String> competencias){
        PessoaFisica candidato = new PessoaFisica(nome:nome, email: email, descricao: descricao, cep:cep,
                estado:estado, competencias:competencias,cpf:cpf, idade:idade)
        candidatos<<candidato
        println ("!!!! Cadastro realizado com sucesso !!!!")
    }

    def novaEmpresa(List<PessoaJuridica> empresas, String nome, String email,
                      String cnpj, String estado, String cep,
                      String descricao, List<String> competencias, String pais){
        PessoaJuridica empresa = new PessoaJuridica(nome:nome, email: email, descricao: descricao, cep:cep,
                estado:estado, competencias:competencias,cnpj:cnpj,pais:pais)
        empresas.add(empresa)
        println ("!!!! Cadastro realizado com sucesso !!!!")
    }

    def match(List<PessoaFisica> candidatos, List<PessoaJuridica> empresas, String visao){
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

    def matchIndividual(List<PessoaFisica> candidatos, List<PessoaJuridica> empresas, String visao, String nome){
        List<Map> resultados = []
        int nMatch
        int competenciasCand, competenciasEmp
        if (visao == 'c'){
            PessoaFisica candidato = candidatos.find { it.nome == nome }
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
            PessoaJuridica empresa = empresas.find { it.nome == nome }
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

}
