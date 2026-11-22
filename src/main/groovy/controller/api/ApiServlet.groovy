package controller.api

import groovy.json.JsonException
import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class ApiServlet extends HttpServlet {

    protected Map lerDados(HttpServletRequest request) {
        try {
            def dados = new JsonSlurper().parse(request.inputStream, "UTF-8")
            if (!(dados instanceof Map)) {
                throw new IllegalArgumentException("O corpo da requisição deve ser um objeto JSON.")
            }
            return dados as Map
        } catch (JsonException ignored) {
            throw new IllegalArgumentException("JSON inválido ou corpo da requisição vazio.")
        }
    }

    protected void configurarCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        configurarCors(resp)
        resp.status = HttpServletResponse.SC_NO_CONTENT
    }

    protected void responder(HttpServletResponse response, int status, Object dados) {
        configurarCors(response)
        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        def generator = new JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()

        response.writer.write(generator.toJson(dados))
    }

    protected void validarCampos(Map dados, List<String> campos) {
        campos.each { campo ->
            if (dados[campo] == null || dados[campo].toString().trim().isEmpty()) {
                throw new IllegalArgumentException("O campo '${campo}' é obrigatório.")
            }
        }
    }

    // Centraliza o tratamento de erros para não repetir try/catch nos controllers
    protected void executar(HttpServletResponse response, Closure acao) {
        try {
            acao.call()
        } catch (IllegalArgumentException | NumberFormatException e) {
            responder(response, HttpServletResponse.SC_BAD_REQUEST, [erro: e.message])
        } catch (Exception e) {
            responder(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, [erro: "Erro interno: ${e.message}"])
        }
    }
}