import model.entity.Empresa
import model.entity.Recrutador
import model.repository.EmpresaRepository
import model.repository.RecrutadorRepository
import service.RecrutadorService
import service.SegurancaSenha
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorServiceSpec extends Specification {

    RecrutadorRepository repo = Mock()
    EmpresaRepository empresaRepo = Mock()
    RecrutadorService service = new RecrutadorService(repo, empresaRepo)

    private static Recrutador novoRecrutadorValido() {
        return new Recrutador(
                empresa: new Empresa(id: 1, nome: "Pastelsoft"),
                nome: "Fernanda Lima",
                areaRecrutamento: "TI",
                descricao: "Tech Recruiter",
                email: "fernanda@pastelsoft.com",
                cpf: "529.982.247-25",
                dataNascimento: LocalDate.of(1995, 3, 12),
                senha: "123"
        )
    }

    def "deve cadastrar recrutador válido normalizando CPF e validando empresa"() {
        given:
        def recrutador = novoRecrutadorValido()
        empresaRepo.buscarPorId(1) >> new Empresa(id: 1, nome: "Pastelsoft")
        repo.buscarPorEmail("fernanda@pastelsoft.com") >> null
        repo.buscarPorCpf("52998224725") >> null

        when:
        service.inserir(recrutador)

        then:
        1 * repo.inserir({ it.cpf == "52998224725" && it.senha != "123" && SegurancaSenha.verificarSenha("123", it.senha) })
    }

    def "deve rejeitar cadastro quando empresa não existe ou há e-mail/CPF duplicado"() {
        given:
        def recrutador = novoRecrutadorValido()
        empresaRepo.buscarPorId(1) >> null

        when:
        service.inserir(recrutador)

        then:
        thrown(IllegalArgumentException)

        when: "e-mail já cadastrado"
        empresaRepo.buscarPorId(1) >> new Empresa(id: 1)
        repo.buscarPorEmail("fernanda@pastelsoft.com") >> new Recrutador(id: 99)
        service.inserir(recrutador)

        then:
        thrown(IllegalArgumentException)

        when: "CPF já cadastrado"
        repo.buscarPorEmail("fernanda@pastelsoft.com") >> null
        repo.buscarPorCpf("52998224725") >> new Recrutador(id: 99)
        service.inserir(recrutador)

        then:
        thrown(IllegalArgumentException)
    }

    def "edição sem nova senha deve preservar a senha atual"() {
        given:
        def existente = novoRecrutadorValido()
        existente.id = 5
        existente.senha = "senha_original"

        def editado = novoRecrutadorValido()
        editado.id = 5
        editado.senha = ""
        editado.areaRecrutamento = "RH"

        repo.buscarPorId(5) >> existente
        empresaRepo.buscarPorId(1) >> new Empresa(id: 1, nome: "Pastelsoft")
        repo.buscarPorEmail("fernanda@pastelsoft.com") >> existente
        repo.buscarPorCpf("52998224725") >> existente

        when:
        service.atualizar(editado)

        then:
        1 * repo.atualizar({ it.senha == "senha_original" && it.areaRecrutamento == "RH" })
    }

    def "visão segura nunca deve expor cpf, dataNascimento ou senha"() {
        given:
        def r = novoRecrutadorValido()
        r.id = 10
        repo.listarPorEmpresa(1) >> [r]

        when:
        def lista = service.listarSegurosPorEmpresa(1)

        then:
        lista.size() == 1
        lista[0].nome == "Fernanda Lima"
        lista[0].areaRecrutamento == "TI"
        lista[0].email == "fernanda@pastelsoft.com"
        !lista[0].containsKey("cpf")
        !lista[0].containsKey("dataNascimento")
        !lista[0].containsKey("senha")
    }
}
