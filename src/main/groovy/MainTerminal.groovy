import view.AvisoErros
import view.LeitorConsole
import view.Menu
import controller.LinketinderController
import db.dao.CandidatoDao
import db.dao.EmpresaDao
import db.dao.MatchDao
import db.dao.RecrutadorDao
import db.dao.VagaDao
import service.RecrutadorService
import service.VagaService

class MainTerminal {
    static void main(String[] args) {
        EmpresaDao empresaDao = new EmpresaDao()
        VagaDao vagaDao = new VagaDao()
        LinketinderController gerenciador = new LinketinderController(
                new CandidatoDao(), empresaDao,
                vagaDao, new MatchDao(),
                new LeitorConsole(), new AvisoErros(),
                new RecrutadorService(new RecrutadorDao(), empresaDao),
                new VagaService(vagaDao))

        Menu inicio = new Menu(new LeitorConsole())
        Scanner scanner = new Scanner(System.in)
        int opcao

        while (true){
            opcao = inicio.menu(scanner)
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
                    gerenciador.gerenciarRecrutadores(scanner)
                    break;
                case 11:
                    gerenciador.gerenciarVagasRecrutador(scanner)
                    break;
                case 12:
                    gerenciador.avaliarCandidatosComoRecrutador(scanner)
                    break;
                case 13:
                    gerenciador.verRejeitados(scanner)
                    break;
                case 0:
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