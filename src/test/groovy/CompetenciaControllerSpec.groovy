import controller.CompetenciaController
import groovy.json.JsonSlurper
import model.repository.CompetenciaRepository
import service.CompetenciaService
import spock.lang.Specification

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

class CompetenciaControllerSpec extends Specification {

    private CompetenciaRepository repo = Mock()
    private CompetenciaService service = new CompetenciaService(repo)
    private CompetenciaController controller = new CompetenciaController(service)

    private HttpServletRequest criarRequestJson(String json) {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8)
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes)
        ServletInputStream sis = new ServletInputStream() {
            @Override boolean isFinished() { bais.available() == 0 }
            @Override boolean isReady() { true }
            @Override void setReadListener(ReadListener readListener) {}
            @Override int read() { bais.read() }
        }
        return Mock(HttpServletRequest) {
            getInputStream() >> sis
        }
    }

    def "GET /competencias deve listar todas as competencias cadastradas"() {
        given:
        repo.listarTodas() >> ["Groovy", "Java", "SQL"]
        def req = Mock(HttpServletRequest) { getParameter("idCandidato") >> null }
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doGet(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as List

        then:
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        body == ["Groovy", "Java", "SQL"]
    }

    def "GET /competencias?idCandidato=1 deve listar competencias do candidato"() {
        given:
        repo.listarDoCandidato(1) >> ["Groovy", "TypeScript"]
        def req = Mock(HttpServletRequest) { getParameter("idCandidato") >> "1" }
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doGet(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as List

        then:
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        body == ["Groovy", "TypeScript"]
    }

    def "POST /competencias com idCandidato deve vincular competencias e retornar 201 Created"() {
        given:
        def req = criarRequestJson('{"idCandidato":7,"competencias":["Groovy","SQL","Groovy"]}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * repo.atualizarDoCandidato(7, ["Groovy", "SQL"])
        1 * resp.setStatus(HttpServletResponse.SC_CREATED)
        body.idCandidato == 7
        body.competencias == ["Groovy", "SQL"]
    }

    def "POST /competencias com nome deve cadastrar competencia avulsa e retornar 201 Created"() {
        given:
        repo.buscarOuCriar("TypeScript") >> 12
        def req = criarRequestJson('{"nome":"TypeScript"}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_CREATED)
        body.id == 12
        body.nome == "TypeScript"
    }

    def "POST /competencias com lista vazia deve retornar 400 Bad Request"() {
        given:
        def req = criarRequestJson('{"idCandidato":7,"competencias":[]}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        body.erro != null
    }
}
