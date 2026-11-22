package controller

import controller.api.ApiServlet
import db.dao.MatchDao
import service.MatchService

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/matches")
class MatchController extends ApiServlet {

    private final MatchService service

    MatchController() {
        this(new MatchService(new MatchDao()))
    }

    MatchController(MatchService service) {
        this.service = service
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            String tipo = req.getParameter("tipo") ?: "empresa"
            String idEmpresaParam = req.getParameter("idEmpresa")
            String idCandidatoParam = req.getParameter("idCandidato")

            switch (tipo) {
                case "interessados":
                    if (!idEmpresaParam) {
                        throw new IllegalArgumentException("O parâmetro 'idEmpresa' é obrigatório.")
                    }
                    responder(resp, HttpServletResponse.SC_OK, service.listarInteressados(idEmpresaParam.toInteger()))
                    break
                case "rejeitados":
                    if (!idEmpresaParam) {
                        throw new IllegalArgumentException("O parâmetro 'idEmpresa' é obrigatório.")
                    }
                    responder(resp, HttpServletResponse.SC_OK, service.listarRejeitados(idEmpresaParam.toInteger()))
                    break
                case "candidato":
                    if (!idCandidatoParam) {
                        throw new IllegalArgumentException("O parâmetro 'idCandidato' é obrigatório.")
                    }
                    responder(resp, HttpServletResponse.SC_OK, service.matchesDoCandidato(idCandidatoParam.toInteger()))
                    break
                case "empresa":
                    if (!idEmpresaParam) {
                        throw new IllegalArgumentException("O parâmetro 'idEmpresa' é obrigatório.")
                    }
                    responder(resp, HttpServletResponse.SC_OK, service.matchesDaEmpresa(idEmpresaParam.toInteger()))
                    break
                default:
                    throw new IllegalArgumentException("Tipo de consulta inválido: '${tipo}'.")
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["acao"])

            String acao = body.acao.toString()
            Integer idRecrutador = body.idRecrutador != null ? (body.idRecrutador as Integer) : null

            switch (acao) {
                case "curtirVaga":
                    validarCampos(body, ["idCandidato", "idVaga"])
                    service.curtirVaga(body.idCandidato as Integer, body.idVaga as Integer)
                    responder(resp, HttpServletResponse.SC_CREATED, [mensagem: "Curtida na vaga registrada com sucesso"])
                    break
                case "curtirCandidato":
                    validarCampos(body, ["idEmpresa", "idCandidato", "idVaga"])
                    service.curtirCandidato(body.idEmpresa as Integer, body.idCandidato as Integer, body.idVaga as Integer, idRecrutador)
                    responder(resp, HttpServletResponse.SC_CREATED, [mensagem: "Match registrado com sucesso"])
                    break
                case "rejeitarCandidato":
                    validarCampos(body, ["idEmpresa", "idCandidato", "idVaga"])
                    service.rejeitarCandidato(body.idEmpresa as Integer, body.idCandidato as Integer, body.idVaga as Integer, idRecrutador)
                    responder(resp, HttpServletResponse.SC_CREATED, [mensagem: "Candidato adicionado à lista de rejeitados"])
                    break
                default:
                    throw new IllegalArgumentException("Ação inválida: '${acao}'.")
            }
        }
    }
}
