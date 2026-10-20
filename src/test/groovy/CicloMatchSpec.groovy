import repository.MatchRepository
import spock.lang.Specification

class CicloMatchSpec extends Specification {

    static final int ANA = 1
    static final int ZG = 10
    static final int VAGA_GROOVY = 100

    MatchRepository matchRepo

    def setup() {
        matchRepo = new MatchRepositoryFake()
    }

    def "quando só o candidato curte, não deve haver match"() {
        when: "a Ana curte a vaga"
        matchRepo.curtirVaga(ANA, VAGA_GROOVY)

        then: "a empresa vê a Ana como interessada, mas ainda não existe match"
        matchRepo.listarInteressados(ZG).size() == 1
        matchRepo.matchesDoCandidato(ANA).isEmpty()
        matchRepo.matchesDaEmpresa(ZG).isEmpty()
    }

    def "quando os dois curtem, deve haver match nos dois lados"() {
        given: "a Ana curtiu a vaga"
        matchRepo.curtirVaga(ANA, VAGA_GROOVY)

        when: "a empresa curte a Ana de volta"
        matchRepo.curtirCandidato(ZG, ANA, VAGA_GROOVY)

        then: "o match aparece para a candidata e para a empresa"
        matchRepo.matchesDoCandidato(ANA)[0].idVaga == VAGA_GROOVY
        matchRepo.matchesDaEmpresa(ZG)[0].idCandidato == ANA
    }
}