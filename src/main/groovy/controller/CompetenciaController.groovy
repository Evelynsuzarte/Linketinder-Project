package controller

import controller.api.ApiServlet
import db.dao.CompetenciaDao
import service.CompetenciaService

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/competencias")
class CompetenciaController extends ApiServlet {

    private final CompetenciaService service

    CompetenciaController() {
        this(new CompetenciaService(new CompetenciaDao()))
    }

    CompetenciaController(CompetenciaService service) {
        this.service = service
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            String idCandidatoParam = req.getParameter("idCandidato")
            if (idCandidatoParam) {
                int idCandidato = idCandidatoParam.toInteger()
                if (idCandidato <= 0) {
                    throw new IllegalArgumentException("O parâmetro 'idCandidato' deve ser válido.")
                }
                responder(resp, HttpServletResponse.SC_OK, service.listarDoCandidato(idCandidato))
                return
            }
            responder(resp, HttpServletResponse.SC_OK, service.listarTodas())
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            Map body = lerDados(req)

            if (body.idCandidato != null) {
                int idCandidato = (body.idCandidato as Integer)
                if (idCandidato <= 0) {
                    throw new IllegalArgumentException("O campo 'idCandidato' deve ser válido.")
                }
                if (!(body.competencias instanceof List)) {
                    throw new IllegalArgumentException("O campo 'competencias' deve ser uma lista.")
                }
                List<String> competencias = (body.competencias as List)
                        .collect { it?.toString()?.trim() }
                        .findAll { it }
                        .unique()

                if (competencias.isEmpty()) {
                    throw new IllegalArgumentException("Informe ao menos uma competência válida.")
                }

                service.atualizarDoCandidato(idCandidato, competencias)
                responder(resp, HttpServletResponse.SC_CREATED, [
                        mensagem    : "Competências vinculadas ao candidato com sucesso",
                        idCandidato : idCandidato,
                        competencias: competencias
                ])
                return
            }

            validarCampos(body, ["nome"])
            String nome = body.nome.toString().trim()
            int id = service.buscarOuCriar(nome)
            responder(resp, HttpServletResponse.SC_CREATED, [
                    mensagem: "Competência cadastrada com sucesso",
                    id      : id,
                    nome    : nome
            ])
        }
    }
}
