import controller.LoginController
import groovy.json.JsonSlurper
import model.entity.Candidato
import model.entity.Empresa
import model.entity.Recrutador
import model.repository.CandidatoRepository
import model.repository.EmpresaRepository
import model.repository.RecrutadorRepository
import service.CandidatoService
import service.EmpresaService
import service.RecrutadorService
import service.SegurancaSenha
import spock.lang.Specification

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

class LoginControllerSpec extends Specification {

    private CandidatoRepository candidatoRepo = Mock()
    private EmpresaRepository empresaRepo = Mock()
    private RecrutadorRepository recrutadorRepo = Mock()

    private CandidatoService candidatoService = new CandidatoService(candidatoRepo)
    private EmpresaService empresaService = new EmpresaService(empresaRepo)
    private RecrutadorService recrutadorService = new RecrutadorService(recrutadorRepo, empresaRepo)
    private LoginController controller = new LoginController(candidatoService, empresaService, recrutadorService)

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

    def "deve autenticar candidato e retornar dados seguros sem senha"() {
        given:
        def candidato = new Candidato(
                id: 1, nome: "Gabriel", sobrenome: "Silva", email: "gabriel@email.com",
                cpf: "111.111.111-11", senha: SegurancaSenha.gerarHash("123456"),
                competencias: ["Groovy", "SQL"]
        )
        candidatoRepo.buscarPorEmail("gabriel@email.com") >> candidato

        def req = criarRequestJson('{"email":"gabriel@email.com","senha":"123456"}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        body.tipo == "candidato"
        body.id == 1
        body.nome == "Gabriel"
        body.email == "gabriel@email.com"
        !body.containsKey("senha")
        !body.containsKey("cpf")
    }

    def "deve autenticar empresa e retornar dados seguros sem senha"() {
        given:
        def empresa = new Empresa(
                id: 2, nome: "TechSolutions", email: "vagas@techsolutions.com",
                senha: SegurancaSenha.gerarHash("123456")
        )
        candidatoRepo.buscarPorEmail("vagas@techsolutions.com") >> null
        empresaRepo.buscarPorEmail("vagas@techsolutions.com") >> empresa

        def req = criarRequestJson('{"email":"vagas@techsolutions.com","senha":"123456"}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        body.tipo == "empresa"
        body.id == 2
        body.nome == "TechSolutions"
        !body.containsKey("senha")
    }

    def "deve autenticar recrutador sem expor senha, cpf ou dataNascimento"() {
        given:
        def empresa = new Empresa(id: 3, nome: "InovaStudio")
        def recrutador = new Recrutador(
                id: 5, nome: "Fernanda", areaRecrutamento: "TI",
                email: "fernanda@techsolutions.com", cpf: "222.222.222-22",
                senha: SegurancaSenha.gerarHash("123456"), empresa: empresa
        )
        candidatoRepo.buscarPorEmail("fernanda@techsolutions.com") >> null
        empresaRepo.buscarPorEmail("fernanda@techsolutions.com") >> null
        recrutadorRepo.buscarPorEmail("fernanda@techsolutions.com") >> recrutador

        def req = criarRequestJson('{"email":"fernanda@techsolutions.com","senha":"123456"}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        body.tipo == "recrutador"
        body.id == 5
        body.idEmpresa == 3
        !body.containsKey("senha")
        !body.containsKey("cpf")
        !body.containsKey("dataNascimento")
    }

    def "deve retornar 401 quando senha estiver incorreta ou usuario nao existir"() {
        given:
        def candidato = new Candidato(id: 1, email: "gabriel@email.com", senha: SegurancaSenha.gerarHash("123456"))
        candidatoRepo.buscarPorEmail("gabriel@email.com") >> candidato

        def req = criarRequestJson('{"email":"gabriel@email.com","senha":"errada"}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        body.erro == "E-mail ou senha inválidos."
    }

    def "deve retornar 400 quando email ou senha estiverem ausentes"() {
        given:
        def req = criarRequestJson('{"email":"gabriel@email.com"}')
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) { getWriter() >> new PrintWriter(writer) }

        when:
        controller.doPost(req, resp)
        def body = new JsonSlurper().parseText(writer.toString()) as Map

        then:
        1 * resp.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        body.erro.contains("senha")
    }
}
