import model.Candidato
import model.Empresa
import controller.LinketinderController
import spock.lang.Specification

class CadastroSpec extends Specification {

    LinketinderController cadastro = new LinketinderController()

    def "deve cadastrar um novo candidato na lista"() {
        given:
        List<Candidato> candidatos = []

        when:
        cadastro.novoCandidato(candidatos, "Carlos", "carlos@email.com", "12345678900", 25, "BA", "44000000", "Desenvolvedor", ["Java", "Groovy"])

        then:
        candidatos.size() == 1
        candidatos[0].nome == "Carlos"
        candidatos[0].email == "carlos@email.com"
        candidatos[0].cpf == "12345678900"
    }

    def "deve cadastrar uma nova empresa na lista"() {
        given:
        List<Empresa> empresas = []

        when:
        cadastro.novaEmpresa(empresas, "Global Tech","contato@globaltech.com", "12345678000199", "BA", "44000000", "Empresa de tecnologia", ["Java", "Groovy"], "Brasil")

        then:
        empresas.size() == 1
        empresas[0].nome == "Global Tech"
        empresas[0].email == "contato@globaltech.com"
        empresas[0].cnpj == "12345678000199"
    }
}