import controller.AvisoErros
import controller.LeitorConsole
import controller.LinketinderController
import db.CandidatoDao
import db.CompetenciaDao
import db.EmpresaDao
import db.MatchDao
import db.VagaDao

class Main {
    static void main(String[] args) {
        LinketinderController gerenciador = new LinketinderController(
                new CandidatoDao(), new EmpresaDao(),
                new VagaDao(), new MatchDao(),
                new LeitorConsole(), new AvisoErros())

        Scanner scanner = new Scanner(System.in)
        int opcao

        while (true){
            opcao = gerenciador.menu(scanner)
            switch (opcao){
                case 1:
                    gerenciador.listagemCandidatos()
                    break;
                case 2:
                    gerenciador.listagemEmpresas()
                    break;
                case 3:
                    gerenciador.listagemVagas()
                    break;
                case 4:
                    gerenciador.novoCandidato(scanner)
                    break;
                case 5:
                    gerenciador.novaEmpresa(scanner)
                    break;
                case 6:
                    gerenciador.novaVaga(scanner)
                    break;
                case 7:
                    gerenciador.avaliarVagas(scanner)
                    break
                case 8:
                    gerenciador.avaliarCandidatos(scanner)
                    break
                case 9:
                    gerenciador.verMatches(scanner)
                    break;
                case 10:
                    println("Encerrando...")
                    System.exit(0)
                    break;
                default:
                    println("Tente novamente. Você digitou errado!\n")
                    break;
            }
        }
    }
}