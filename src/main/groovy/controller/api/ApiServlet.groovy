package controller.api
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class ApiServlet extends HttpServlet {

    protected Map lerDados(HttpServletRequest request) {
        def dados = new JsonSlurper().parse(request.inputStream)
        if (!(dados instanceof Map)) {
            throw new IllegalArgumentException("O corpo da requisição deve ser um objeto JSON.")
        }
        return dados as Map
    }

    protected void responder(
            HttpServletResponse response,
            int status,
            Object dados
    ) {
        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(JsonOutput.toJson(dados))
    }

    protected void validarCampos(Map dados, List<String> campos) {
        campos.each { campo ->
            if (dados[campo] == null || dados[campo].toString().trim().isEmpty()) {
                throw new IllegalArgumentException("O campo '${campo}' é obrigatório.")
            }
        }
    }
}