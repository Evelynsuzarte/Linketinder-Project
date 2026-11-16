
package controller.api

import javax.servlet.annotation.WebServlet
import javax.servlet.ServletException

import model.entity.Candidato
import service.CandidatoService
impo

@WebServlet("/candidatos")
class CandidatoServlet extends ApiServlet {

    private CandidatoService candidatoService

    @Override
    void init() throws ServletException {
        candidatoService = servletContext.getAttribute("candidatoService") as CandidatoService
        if (candidatoService == null) {throw new ServletException("CandidatoService não foi configurado.")
        }
    }

    @Override
    Map executar(Map dados) {
        validarCampos(dados, ["nome", "sobrenome", "email", "cpf", "senha"])

        if (dados.idade <= 0) {
            throw new IllegalArgumentException(
                    "A idade deve ser maior que zero."
            )
        }

        if (!(dados.competencias == null ||
                dados.competencias instanceof List)) {
            throw new IllegalArgumentException(
                    "Competências deve ser uma lista."
            )
        }

        Candidato candidato = new Candidato(

                nome: dados.nome,
                sobrenome: dados.sobrenome,
                email: dados.email,
                descricao: dados.descricao ?: "",
                cep: dados.cep ?: "",
                estado: dados.estado ?: "",
                senha: dados.senha,
                pais: dados.pais ?: "",
                cpf: dados.cpf,
                idade: dados.idade,
                competencias: leitor.lerCompetencias(dados, "Competências (separadas por vírgula): ")
        )

        candidatoService.salvar(candidato)

        return [
                mensagem: "Candidato cadastrado com sucesso!"
        ]
    }
}
