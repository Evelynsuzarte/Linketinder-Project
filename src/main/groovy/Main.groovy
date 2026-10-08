import controller.LinketinderController

class Main {
    static void main(String[] args) {
        LinketinderController gerenciador = new LinketinderController()
        Scanner scanner = new Scanner(System.in)
        int opcao


        while (true){
            opcao = gerenciador.menu()
            switch (opcao){
                case 0:
                    println("Encerrando...")
                    System.exit(0)
                    break;
                case 1:
                    gerenciador.listagemCandidatos()
                    break;
                case 2:
                    gerenciador.listagemEmpresas()
                    break;
                case 3:
                    gerenciador.novoCadastro(scanner)
                    break;
                case 4:
                    gerenciador.novaEmpresa(scanner)
                    break;
                case 5:
                    gerenciador.novaVaga(scanner)
                    break;
                case 6:
                    gerenciador.avaliarVagas(scanner)
                    break
                case 7:
                    gerenciador.avaliarCandidatos(scanner)
                    break
                case 8:
                    gerenciador.verMatches(scanner)
                    break;
                default:
                    println("Tente novamente. Você digitou errado!")
                    break;
            }
        }
    }
}