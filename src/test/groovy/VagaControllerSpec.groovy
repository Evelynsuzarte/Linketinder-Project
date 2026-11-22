import controller.VagaController
import groovy.json.JsonSlurper
import model.entity.Empresa
import model.entity.Vaga
import model.repository.EmpresaRepository
import model.repository.RecrutadorRepository
import model.repository.VagaRepository
import service.RecrutadorService
import service.VagaService
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VagaControllerSpec extends Specification {

    private VagaRepository vagaRepo = Mock()
    private RecrutadorRepository recrutadorRepo = Mock()
    private EmpresaRepository empresaRepo = Mock()

    private VagaService vagaService = new VagaService(vagaRepo)
    private RecrutadorService recrutadorService = new RecrutadorService(recrutadorRepo, empresaRepo)
    private VagaController controller = new VagaController(vagaService, recrutadorService)

    def "GET /vagas?idCandidato=1 deve retornar vagas nao curtidas sem expor dados da empresa"() {
        given:
        def empresa = new Empresa(id: 10, nome: "TechSolutions", cnpj: "00.000.000/0001-00", email: "rh@tech.com")
        def vaga = new Vaga(
                id: 3,
                nome: "Dev Groovy",
                descricao: "API REST",
                local: "Remoto",
                empresa: empresa,
                competencias: ["Groovy", "SQL"]
        )
        vagaRepo.listarNaoCurtidas(1) >> [vaga]

        def req = Mock(HttpServletRequest) {
            getParameter("idCandidato") >> "1"
        }
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) {
            getWriter() >> new PrintWriter(writer)
        }

        when:
        controller.doGet(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as List<Map>

        then:
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        body.size() == 1
        body[0].id == 3
        body[0].nome == "Dev Groovy"
        body[0].descricao == "API REST"
        body[0].local == "Remoto"
        body[0].competencias == ["Groovy", "SQL"]
        !body[0].containsKey("empresa")
    }

    def "GET /vagas?idCandidato=0 deve retornar 400 Bad Request"() {
        given:
        def req = Mock(HttpServletRequest) {
            getParameter("idCandidato") >> "0"
        }
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) {
            getWriter() >> new PrintWriter(writer)
        }

        when:
        controller.doGet(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        body.erro != null
    }
}
