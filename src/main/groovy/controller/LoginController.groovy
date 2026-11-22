package controller

import controller.api.ApiServlet
import db.dao.CandidatoDao
import db.dao.EmpresaDao
import db.dao.RecrutadorDao
import model.entity.Candidato
import model.entity.Empresa
import model.entity.Recrutador
import service.CandidatoService
import service.EmpresaService
import service.RecrutadorService
import service.SegurancaSenha

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/login")
class LoginController extends ApiServlet {

    private final CandidatoService candidatoService
    private final EmpresaService empresaService
    private final RecrutadorService recrutadorService

    LoginController() {
        this(
                new CandidatoService(new CandidatoDao()),
                new EmpresaService(new EmpresaDao()),
                new RecrutadorService(new RecrutadorDao(), new EmpresaDao())
        )
    }

    LoginController(
            CandidatoService candidatoService,
            EmpresaService empresaService,
            RecrutadorService recrutadorService
    ) {
        this.candidatoService = candidatoService
        this.empresaService = empresaService
        this.recrutadorService = recrutadorService
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            Map body = lerDados(req)
            validarCampos(body, ["email", "senha"])

            String email = body.email.toString().trim()
            String senha = body.senha.toString()

            Map usuarioAutenticado = autenticarUsuario(email, senha)
            if (usuarioAutenticado == null) {
                responder(resp, HttpServletResponse.SC_UNAUTHORIZED, [erro: "E-mail ou senha inválidos."])
                return
            }

            responder(resp, HttpServletResponse.SC_OK, usuarioAutenticado)
        }
    }

    private Map autenticarUsuario(String email, String senha) {
        Candidato candidato = candidatoService.buscarPorEmail(email)
        if (candidato != null && SegurancaSenha.verificarSenha(senha, candidato.senha)) {
            return [
                    tipo        : "candidato",
                    id          : candidato.id,
                    nome        : candidato.nome,
                    sobrenome   : candidato.sobrenome,
                    email       : candidato.email,
                    idade       : candidato.idade,
                    pais        : candidato.pais,
                    estado      : candidato.estado,
                    cep         : candidato.cep,
                    descricao   : candidato.descricao,
                    competencias: candidato.competencias ?: []
            ]
        }

        Empresa empresa = empresaService.buscarPorEmail(email)
        if (empresa != null && SegurancaSenha.verificarSenha(senha, empresa.senha)) {
            return [
                    tipo     : "empresa",
                    id       : empresa.id,
                    nome     : empresa.nome,
                    email    : empresa.email,
                    pais     : empresa.pais,
                    estado   : empresa.estado,
                    cep      : empresa.cep,
                    descricao: empresa.descricao
            ]
        }

        Recrutador recrutador = recrutadorService.buscarPorEmail(email)
        if (recrutador != null && SegurancaSenha.verificarSenha(senha, recrutador.senha)) {
            return [tipo: "recrutador"] + RecrutadorService.paraVisaoSegura(recrutador)
        }

        return null
    }
}
