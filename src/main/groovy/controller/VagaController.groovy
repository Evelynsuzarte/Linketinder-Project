package controller

import controller.api.ApiServlet
import db.dao.EmpresaDao
import db.dao.RecrutadorDao
import db.dao.VagaDao
import model.entity.Empresa
import model.entity.Recrutador
import model.entity.Vaga
import service.RecrutadorService
import service.VagaService

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/vagas")
class VagaController extends ApiServlet {

    private final VagaService service = new VagaService(new VagaDao())
    private final RecrutadorService recrutadorService = new RecrutadorService(new RecrutadorDao(), new EmpresaDao())

    // metodo get para listar as vagas as empresas
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            String idEmpresaParam = req.getParameter("idEmpresa")
            if (idEmpresaParam) {
                responder(resp, HttpServletResponse.SC_OK, service.listarPorEmpresa(idEmpresaParam.toInteger()))
                return
            }
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["id", "nome", "descricao", "local", "idEmpresa"])

            Vaga vaga = new Vaga(
                    id: body.id as Integer,
                    nome: body.nome,
                    descricao: body.descricao,
                    local: body.local,
                    empresa: new Empresa(id: body.idEmpresa as Integer),
                    competencias: body.competencias ?: []
            )

            if (body.idRecrutador != null) {
                Recrutador recrutador = recrutadorService.buscarPorId(body.idRecrutador as Integer)
                if (recrutador == null) {
                    throw new IllegalArgumentException("Recrutador não encontrado.")
                }
                service.atualizarComoRecrutador(vaga, recrutador.empresa.id)
            } else {
                service.atualizar(vaga)
            }

            responder(resp, HttpServletResponse.SC_OK, [
                    mensagem: "Vaga atualizada com sucesso",
                    id: vaga.id
            ])
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            String idParam = req.getParameter("id")
            String idRecrutadorParam = req.getParameter("idRecrutador")
            int id
            Integer idRecrutador = idRecrutadorParam ? idRecrutadorParam.toInteger() : null

            if (idParam) {
                id = idParam.toInteger()
            } else {
                def body = lerDados(req)
                validarCampos(body, ["id"])
                id = body.id as Integer
                if (body.idRecrutador != null) {
                    idRecrutador = body.idRecrutador as Integer
                }
            }

            if (idRecrutador != null) {
                Recrutador recrutador = recrutadorService.buscarPorId(idRecrutador)
                if (recrutador == null) {
                    throw new IllegalArgumentException("Recrutador não encontrado.")
                }
                service.removerComoRecrutador(id, recrutador.empresa.id)
            } else {
                service.remover(id)
            }

            responder(resp, HttpServletResponse.SC_OK, [mensagem: "Vaga removida com sucesso"])
        }
    }
}