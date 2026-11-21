import model.entity.Empresa
import model.entity.Vaga
import model.repository.MatchRepository
import model.repository.VagaRepository
import service.MatchService
import service.VagaService
import spock.lang.Specification

class CicloMatchSpec extends Specification {

    static final int ANA = 1
    static final int ZG = 10
    static final int OUTRA_EMPRESA = 20
    static final int RECRUTADOR_1 = 50
    static final int VAGA_GROOVY = 100

    MatchRepository matchRepo
    MatchService matchService

    def setup() {
        matchRepo = new MatchRepositoryFake()
        matchService = new MatchService(matchRepo)
    }

    def "quando só o candidato curte, não deve haver match"() {
        when: "a Ana curte a vaga"
        matchRepo.curtirVaga(ANA, VAGA_GROOVY)

        then: "a empresa vê a Ana como interessada, mas ainda não existe match"
        matchRepo.listarInteressados(ZG).size() == 1
        matchRepo.matchesDoCandidato(ANA).isEmpty()
        matchRepo.matchesDaEmpresa(ZG).isEmpty()
    }

    def "quando o recrutador curte, gera match com idRecrutador e remove da fila dos demais recrutadores"() {
        given: "a Ana curtiu a vaga"
        matchService.curtirVaga(ANA, VAGA_GROOVY)

        when: "o primeiro recrutador da empresa curte a Ana de volta"
        matchService.curtirCandidato(ZG, ANA, VAGA_GROOVY, RECRUTADOR_1)

        then: "o match aparece para ambos com idRecrutador e a fila de interessados fica vazia"
        matchService.matchesDoCandidato(ANA)[0].idVaga == VAGA_GROOVY
        matchService.matchesDaEmpresa(ZG)[0].idCandidato == ANA
        matchService.matchesDaEmpresa(ZG)[0].idRecrutador == RECRUTADOR_1
        matchService.listarInteressados(ZG).isEmpty()
    }

    def "quando o recrutador rejeita, o candidato sai da fila e vai para a lista anônima de rejeitados"() {
        given: "a Ana curtiu a vaga"
        matchService.curtirVaga(ANA, VAGA_GROOVY)

        when: "o recrutador rejeita o perfil"
        matchService.rejeitarCandidato(ZG, ANA, VAGA_GROOVY, RECRUTADOR_1)

        then: "sai da fila principal e aparece nos rejeitados sem dados pessoais"
        matchService.listarInteressados(ZG).isEmpty()
        def rejeitados = matchService.listarRejeitados(ZG)
        rejeitados.size() == 1
        rejeitados[0].idCandidato == ANA
        !rejeitados[0].containsKey("nome")
        !rejeitados[0].containsKey("email")
        !rejeitados[0].containsKey("cpf")
    }

    def "recrutador não pode editar vaga de outra empresa"() {
        given:
        VagaRepository vagaRepo = Mock()
        VagaService vagaService = new VagaService(vagaRepo)
        vagaRepo.buscarPorId(VAGA_GROOVY) >> new Vaga(id: VAGA_GROOVY, empresa: new Empresa(id: OUTRA_EMPRESA))

        when:
        vagaService.atualizarComoRecrutador(new Vaga(id: VAGA_GROOVY, nome: "Nova"), ZG)

        then:
        thrown(IllegalArgumentException)
        0 * vagaRepo.atualizar(_)
    }

    def "recrutador pode remover vaga da própria empresa e não pode remover vaga de outra empresa"() {
        given:
        VagaRepository vagaRepo = Mock()
        VagaService vagaService = new VagaService(vagaRepo)
        vagaRepo.buscarPorId(VAGA_GROOVY) >> new Vaga(id: VAGA_GROOVY, empresa: new Empresa(id: ZG))
        vagaRepo.buscarPorId(200) >> new Vaga(id: 200, empresa: new Empresa(id: OUTRA_EMPRESA))

        when:
        vagaService.removerComoRecrutador(VAGA_GROOVY, ZG)

        then:
        1 * vagaRepo.remover(VAGA_GROOVY)

        when:
        vagaService.removerComoRecrutador(200, ZG)

        then:
        thrown(IllegalArgumentException)
        0 * vagaRepo.remover(200)
    }
}
