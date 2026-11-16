package controller

import controller.api.ApiServlet
import db.dao.VagaDao
import model.entity.Empresa
import model.entity.Vaga
import service.VagaService

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/vagas")
class VagaController extends ApiServlet {

    private final VagaService service = new VagaService(new VagaDao())

    // metodo get para listar as vagas as empresas
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            responder(resp, HttpServletResponse.SC_OK, service.listarTodas())
        }
    }

    // metodo post para cadastrar as empresas
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["nome", "descricao", "local", "idEmpresa"])

            Vaga vaga = new Vaga(
                    nome: body.nome,
                    descricao: body.descricao,
                    local: body.local,
                    empresa: new Empresa(id: body.idEmpresa as Integer),
                    competencias: body.competencias ?: []
            )

            service.inserir(vaga)

            responder(resp, HttpServletResponse.SC_CREATED, [
                    mensagem: "Vaga cadastrada com sucesso",
                    id: vaga.id
            ])
        }
    }
}