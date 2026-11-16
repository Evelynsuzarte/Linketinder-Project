package controller

import controller.api.ApiServlet
import db.dao.EmpresaDao
import model.entity.Empresa
import service.EmpresaService

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/empresas")
class EmpresaController extends ApiServlet {

    private final EmpresaService service = new EmpresaService(new EmpresaDao())

    // metodo get para listar todas as empresas
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            responder(resp, HttpServletResponse.SC_OK, service.listarTodas())
        }
    }

    // metodo post para cadastras as empresas
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        executar(resp) {
            def body = lerDados(req)
            validarCampos(body, ["cnpj", "nome", "email", "senha"])

            Empresa empresa = new Empresa(
                    cnpj: body.cnpj,
                    nome: body.nome,
                    email: body.email,
                    pais: body.pais,
                    cep: body.cep,
                    descricao: body.descricao,
                    senha: body.senha,
                    estado: body.estado
            )

            service.inserir(empresa)

            responder(resp, HttpServletResponse.SC_CREATED, [
                    mensagem: "Empresa cadastrada com sucesso",
                    id: empresa.id
            ])
        }
    }
}