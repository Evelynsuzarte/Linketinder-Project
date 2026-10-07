import controller.LinketinderController
import model.Candidato
import model.Empresa
import model.Vaga

class Main {
    static void main(String[] args) {
        List<Empresa> empresas = []
        List<Candidato> candidatos = []
        List<Vaga> vagas = []
        LinketinderController gerenciador = new LinketinderController()
        Scanner scanner = new Scanner(System.in)
        int opcao
        String nome

        candidatos.add(new Candidato(nome: "Carlos Silva", email: "carlos.silva@email.com", cpf: "123.456.789-00", idade: 28, estado: "SP", cep: "01001-000", descricao: "Desenvolvedor Java sênior", competencias: ["Java", "Groovy", "SQL"]))
        candidatos.add(new Candidato(nome: "Ana Costa", email: "ana.costa@email.com", cpf: "987.654.321-11", idade: 24, estado: "RJ", cep: "20020-010", descricao: "Designer UX/UI", competencias: ["Figma", "Design System", "HTML"]))
        candidatos.add(new Candidato(nome: "Bruno Alves", email: "bruno.a@email.com", cpf: "456.123.789-22", idade: 35, estado: "MG", cep: "30140-050", descricao: "Gerente de Projetos", competencias: ["Scrum", "Agile", "Jira"]))
        candidatos.add(new Candidato(nome: "Mariana Souza", email: "mari.souza@email.com", cpf: "789.456.123-33", idade: 31, estado: "PR", cep: "80010-000", descricao: "Cientista de Dados", competencias: ["Python", "Pandas", "SQL"]))
        candidatos.add(new Candidato(nome: "Ricardo Lima", email: "ricardo.l@email.com", cpf: "321.654.987-44", idade: 27, estado: "SC", cep: "88010-100", descricao: "Dev DevOps", competencias: ["Docker", "AWS", "Linux"]))

        empresas.add(new Empresa(nome: "Tech Solutions Ltda", email: "contato@techsolutions.com", cnpj: "12.345.678/0001-99", estado: "SP", cep: "04538-133", descricao: "Foco em desenvolvimento web", competencias: ["Java", "Node.js", "React"], pais: "Brasil"))
        empresas.add(new Empresa(nome: "Inova Digital", email: "vagas@inovadigital.com", cnpj: "98.765.432/0001-88", estado: "RJ", cep: "22020-001", descricao: "Agência de marketing e design", competencias: ["Figma", "Photoshop", "HTML"], pais: "Brasil"))
        empresas.add(new Empresa(nome: "Data Corp", email: "recrutamento@datacorp.com", cnpj: "45.678.901/0001-77", estado: "MG", cep: "31150-120", descricao: "Consultoria em inteligência de dados", competencias: ["Python", "SQL", "Cloud"], pais: "Brasil"))
        empresas.add(new Empresa(nome: "Global Development", email: "hr@globaldev.com", cnpj: "23.456.789/0001-66", estado: "SP", cep: "01310-200", descricao: "Fábrica de software internacional", competencias: ["Java", "Groovy", "SQL"], pais: "Brasil"))
        empresas.add(new Empresa(nome: "Nexus Security", email: "info@nexussec.com", cnpj: "34.567.890/0001-55", estado: "RS", cep: "90010-240", descricao: "Segurança da informação e infraestrutura", competencias: ["Linux", "Docker", "Python"], pais: "Brasil"))

        Empresa techBank = empresas[0]
        Empresa dataCorp = empresas[2]
        Empresa cloudSolutions = empresas[4]

        vagas.add(new Vaga(nome: "Desenvolvedora Java Jr", empresa: techBank, local: "São Paulo - SP", descricao: "Atuação com APIs REST em Spring Boot e bancos relacionais.", competencias: ["Java", "Spring Boot", "SQL"]))
        vagas.add(new Vaga(nome: "Engenheiro de Dados Pleno", empresa: dataCorp, local: "Belo Horizonte - MG", descricao: "Construção de pipelines de dados em Python e SQL.", competencias: ["Python", "SQL", "Cloud"]))
        vagas.add(new Vaga(nome: "Desenvolvedora Fullstack", empresa: techBank, local: "São Paulo - SP", descricao: "Manutenção de microsserviços e interfaces com integração contínua.", competencias: ["Java", "React", "Node.js"]))
        vagas.add(new Vaga(nome: "Analista de DevOps", empresa: cloudSolutions, local: "Porto Alegre - RS", descricao: "Gerenciamento de containers Docker e pipelines de CI/CD na AWS.", competencias: ["Docker", "AWS", "Linux"]))
        vagas.add(new Vaga(nome: "Desenvolvedor Back-end Pleno", empresa: cloudSolutions, local: "Remoto", descricao: "Desenvolvimento de microsserviços escaláveis e mensageria com Kafka.", competencias: ["Java", "Kafka", "Docker"]))
        vagas.add(new Vaga(nome: "Analista de QA / Automação", empresa: techBank, local: "São Paulo - SP", descricao: "Criação de testes automatizados unitários, de carga e de integração.", competencias: ["Java", "Selenium", "Jira"]))

        // liga cada vaga à sua empresa (necessário para a empresa ver os interessados)
        for (v in vagas) {
            v.empresa.vagas.add(v)
        }

        while (true){
            opcao = gerenciador.menu()
            switch (opcao){
                case 0:
                    println("Encerrando...")
                    System.exit(0)
                    break;
                case 1:
                    gerenciador.listagemCandidatos(candidatos)
                    break;
                case 2:
                    gerenciador.listagemEmpresas(empresas)
                    break;
                case 3:
                    candidatos = gerenciador.novoCadastro(candidatos, scanner)
                    break;
                case 4:
                    empresas = gerenciador.novaEmpresa(empresas, scanner)
                    break;
                case 5:
                    gerenciador.novaVaga(vagas, empresas, scanner)
                    break;
                case 6:
                    print("Seu nome: ")
                    nome = scanner.nextLine()
                    Candidato candidato_buscado = candidatos.find { it.nome == nome }
                    if (candidato_buscado == null)
                        println("Candidato não encontrado.")
                    else
                        gerenciador.avaliarVagas(candidato_buscado, vagas, scanner)
                    break
                case 7:
                    print("Nome da empresa: ")
                    nome = scanner.nextLine()
                    Empresa emp = empresas.find { it.nome == nome }
                    if (emp == null)
                        println("Empresa não encontrada.")
                    else
                        gerenciador.avaliarCandidatos(emp, scanner)
                    break
                case 8:
                    println("Visão do \n c = candidato\ne = empresa: ")
                    String visao = scanner.nextLine()
                    print("Nome: ")
                    gerenciador.verMatches(candidatos, empresas, visao, scanner.nextLine())
                    break;
                default:
                    println("Tente novamente. Você digitou errado!")
                    break;
            }
        }
    }
}