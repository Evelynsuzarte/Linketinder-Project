package controller

import controller.api.ApiServlet
import db.dao.CandidatoDao
import model.entity.Candidato
import service.CandidatoService

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/candidatos")
class CandidatoController extends ApiServlet {

    private final CandidatoService service = new CandidatoService(new CandidatoDao())

    // metodo get para listar todos os candidatos
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            responder(resp, HttpServletResponse.SC_OK, service.listarTodos())
        }
    }

    // metodo post para cadastrar os candidatos
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["cpf", "nome", "email", "senha"])

            Candidato candidato = new Candidato(
                    cpf: body.cpf,
                    nome: body.nome,
                    sobrenome: body.sobrenome,
                    idade: body.idade ? body.idade as Integer : null,
                    email: body.email,
                    pais: body.pais,
                    cep: body.cep,
                    descricao: body.descricao,
                    senha: body.senha,
                    competencias: body.competencias ?: []
            )

            service.inserir(candidato)

            responder(resp, HttpServletResponse.SC_CREATED, [
                    mensagem: "Candidato cadastrado com sucesso",
                    id: candidato.id
            ])
        }
    }
}