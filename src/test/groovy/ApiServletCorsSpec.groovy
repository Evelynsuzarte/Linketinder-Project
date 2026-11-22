import controller.api.ApiServlet
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiServletCorsSpec extends Specification {

    static class ServletTeste extends ApiServlet {
        void testarResposta(HttpServletResponse resp, int status, Object dados) {
            responder(resp, status, dados)
        }

        void testarOptions(HttpServletRequest req, HttpServletResponse resp) {
            doOptions(req, resp)
        }
    }

    def "responder deve incluir cabecalhos CORS e escrever JSON UTF-8"() {
        given:
        def servlet = new ServletTeste()
        def writer = new StringWriter()
        def resp = Mock(HttpServletResponse) {
            getWriter() >> new PrintWriter(writer)
        }

        when:
        servlet.testarResposta(resp, HttpServletResponse.SC_OK, [mensagem: "ok"])

        then:
        1 * resp.setHeader("Access-Control-Allow-Origin", "*")
        1 * resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        1 * resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
        1 * resp.setStatus(HttpServletResponse.SC_OK)
        1 * resp.setContentType("application/json")
        1 * resp.setCharacterEncoding("UTF-8")
        writer.toString() == '{"mensagem":"ok"}'
    }

    def "doOptions deve configurar cabecalhos CORS e retornar 204 No Content"() {
        given:
        def servlet = new ServletTeste()
        def req = Mock(HttpServletRequest)
        def resp = Mock(HttpServletResponse)

        when:
        servlet.testarOptions(req, resp)

        then:
        1 * resp.setHeader("Access-Control-Allow-Origin", "*")
        1 * resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        1 * resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
        1 * resp.setStatus(HttpServletResponse.SC_NO_CONTENT)
    }
}
