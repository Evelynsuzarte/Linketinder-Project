import controller.LinketinderController
import model.PessoaFisica
import model.PessoaJuridica

class Main {
    static void main(String[] args) {
        List<PessoaJuridica> empresas = []
        List<PessoaFisica> candidatos = []
        LinketinderController gerenciador = new LinketinderController()
        Scanner scanner = new Scanner(System.in)
        int opcao, idade
        String nome, email, cnpj, cpf, estado, cep, pais, descricao
        List<String> competencias = []

        gerenciador.novoCandidato(candidatos, "Carlos Silva", "carlos.silva@email.com", "123.456.789-00", 28,
                "SP", "01001-000", "Desenvolvedor Java sênior", ["Java", "Groovy", "SQL"])
        gerenciador.novoCandidato(candidatos, "Ana Costa", "ana.costa@email.com", "987.654.321-11", 24,
                "RJ", "20020-010", "Designer UX/UI", ["Figma", "Design System", "HTML"])
        gerenciador.novoCandidato(candidatos, "Bruno Alves", "bruno.a@email.com", "456.123.789-22", 35,
                "MG", "30140-050", "Gerente de Projetos", ["Scrum", "Agile", "Jira"])
        gerenciador.novoCandidato(candidatos, "Mariana Souza", "mari.souza@email.com", "789.456.123-33", 31,
                "PR", "80010-000", "Cientista de Dados", ["Python", "Pandas", "SQL"])
        gerenciador.novoCandidato(candidatos, "Ricardo Lima", "ricardo.l@email.com", "321.654.987-44", 27,
                "SC", "88010-100", "Dev DevOps", ["Docker", "AWS", "Linux"])

        gerenciador.novaEmpresa(empresas, "Tech Solutions Ltda", "contato@techsolutions.com", "12.345.678/0001-99",
                "SP", "04538-133", "Foco em desenvolvimento web", ["Java", "Node.js", "React"], "Brasil")
        gerenciador.novaEmpresa(empresas, "Inova Digital", "vagas@inovadigital.com", "98.765.432/0001-88",
                "RJ", "22020-001", "Agência de marketing e design", ["Figma", "Photoshop", "HTML"], "Brasil")
        gerenciador.novaEmpresa(empresas, "Data Corp", "recrutamento@datacorp.com", "45.678.901/0001-77",
                "MG", "31150-120", "Consultoria em inteligência de dados", ["Python", "SQL", "Cloud"], "Brasil")
        gerenciador.novaEmpresa(empresas, "Global Development", "hr@globaldev.com", "23.456.789/0001-66",
                "SP", "01310-200", "Fábrica de software internacional", ["Java", "Groovy", "SQL"], "Brasil")
        gerenciador.novaEmpresa(empresas, "Nexus Security", "info@nexussec.com", "34.567.890/0001-55",
                "RS", "90010-240", "Segurança da informação e infraestrutura", ["Linux", "Docker", "Python"], "Brasil")

        while (true){
            opcao = gerenciador.menu()
            switch (opcao){
                case 0:
                    "Encerrando..."
                    System.exit(0)
                    break;
                case 1:
                    gerenciador.listagemCandidatos(candidatos)
                    break;
                case 2:
                    gerenciador.listagemEmpresas(empresas)
                    break;
                case 3:
                    println ("--- CADASTRO DE CANDIDATO ---")
                    print("Nome: ")
                    nome= scanner.nextLine()

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

                    gerenciador.novoCandidato(candidatos,nome, email, cpf, idade, estado, cep, descricao, competencias)
                    break;
                case 4:
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

                    gerenciador.novaEmpresa(empresas, nome, email, cnpj, estado, cep, descricao, competencias, pais)
                    break;
                case 5:
                    println("--- MATCH CANDIDATO<->EMPRESA ---")
                    println("1 - MATCH INDIVIDUAL DE CANDIDATOS")
                    println("2 - MATCH GERAL DOS CANDIDATOS")
                    println("==== ESCOLHA")
                    opcao = scanner.nextLine().toInteger()
                    if (opcao == 1){
                        println("--- MATCH INDIVIDUAL DE CANDIDATOS ---")
                        println("Digite o nome do candidato")
                        nome = scanner.nextLine()

                        List<Map> matchs = gerenciador.matchIndividual(candidatos, empresas, "c", nome)
                        gerenciador.exibirMatchCandidatos(matchs)
                    } else{
                        println("--- MATCH GERAL DOS CANDIDATOS ---")
                        List<Map> matchs = gerenciador.match(candidatos, empresas, "c")
                        gerenciador.exibirMatchCandidatos(matchs)
                    }

                    break;
                case 6:
                    println("--- MATCH EMPRESA<->CANDIDATO ---")
                    println("1 - MATCH INDIVIDUAL DA EMPRESA")
                    println("2 - MATCH GERAL DAS EMPRESAS")
                    println("==== ESCOLHA")
                    opcao = scanner.nextLine().toInteger()
                    if (opcao == 1){
                        println("--- MATCH INDIVIDUAL DA EMPRESA ---")
                        println("Digite o nome da empresa")
                        nome = scanner.nextLine()

                        List<Map> matchs = gerenciador.matchIndividual(candidatos, empresas, "e", nome)
                        gerenciador.exibirMatchEmpresas(matchs)
                    } else{
                        println("--- MATCH GERAL DAS EMPRESA ---")
                        List<Map> matchs = gerenciador.match(candidatos, empresas, "e")
                        gerenciador.exibirMatchEmpresas(matchs)
                    }
                    break;
                default:
                    println("Tente novamente. Você digitou errado!")
                    break;

            }

        }


    }
}
