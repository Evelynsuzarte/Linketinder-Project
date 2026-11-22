package controller

import controller.api.ApiServlet
import db.dao.EmpresaDao
import db.dao.RecrutadorDao
import model.entity.Empresa
import model.entity.Recrutador
import service.RecrutadorService
import service.Validador

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/recrutadores")
class RecrutadorController extends ApiServlet {

    private final RecrutadorService service

    RecrutadorController() {
        this(new RecrutadorService(new RecrutadorDao(), new EmpresaDao()))
    }

    RecrutadorController(RecrutadorService service) {
        this.service = service
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            String idParam = req.getParameter("id")
            String idEmpresaParam = req.getParameter("idEmpresa")

            if (idParam) {
                Map recrutador = service.buscarSeguroPorId(idParam.toInteger())
                if (recrutador == null) {
                    responder(resp, HttpServletResponse.SC_NOT_FOUND, [erro: "Recrutador não encontrado."])
                    return
                }
                responder(resp, HttpServletResponse.SC_OK, recrutador)
                return
            }

            if (idEmpresaParam) {
                responder(resp, HttpServletResponse.SC_OK, service.listarSegurosPorEmpresa(idEmpresaParam.toInteger()))
                return
            }

            throw new IllegalArgumentException("Informe 'id' ou 'idEmpresa' na consulta.")
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["idEmpresa", "nome", "areaRecrutamento", "email", "cpf", "dataNascimento", "senha"])

            Recrutador recrutador = new Recrutador(
                    empresa: new Empresa(id: body.idEmpresa as Integer),
                    nome: body.nome?.toString(),
                    areaRecrutamento: body.areaRecrutamento?.toString(),
                    descricao: body.descricao?.toString(),
                    email: body.email?.toString(),
                    cpf: body.cpf?.toString(),
                    dataNascimento: Validador.parseDataNascimento(body.dataNascimento?.toString()),
                    senha: body.senha?.toString()
            )

            service.inserir(recrutador)

            responder(resp, HttpServletResponse.SC_CREATED, [
                    mensagem  : "Recrutador cadastrado com sucesso",
                    recrutador: RecrutadorService.paraVisaoSegura(recrutador)
            ])
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["id", "nome", "areaRecrutamento", "email", "cpf", "dataNascimento"])

            Integer idEmpresa = body.idEmpresa != null ? (body.idEmpresa as Integer) : 0
            Recrutador recrutador = new Recrutador(
                    id: body.id as Integer,
                    empresa: idEmpresa > 0 ? new Empresa(id: idEmpresa) : null,
                    nome: body.nome?.toString(),
                    areaRecrutamento: body.areaRecrutamento?.toString(),
                    descricao: body.descricao?.toString(),
                    email: body.email?.toString(),
                    cpf: body.cpf?.toString(),
                    dataNascimento: Validador.parseDataNascimento(body.dataNascimento?.toString()),
                    senha: body.senha?.toString()
            )

            service.atualizar(recrutador)

            responder(resp, HttpServletResponse.SC_OK, [
                    mensagem  : "Recrutador atualizado com sucesso",
                    recrutador: service.buscarSeguroPorId(recrutador.id)
            ])
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            String idParam = req.getParameter("id")
            int id
            if (idParam) {
                id = idParam.toInteger()
            } else {
                def body = lerDados(req)
                validarCampos(body, ["id"])
                id = body.id as Integer
            }

            service.remover(id)
            responder(resp, HttpServletResponse.SC_OK, [mensagem: "Recrutador removido com sucesso"])
        }
    }
}
