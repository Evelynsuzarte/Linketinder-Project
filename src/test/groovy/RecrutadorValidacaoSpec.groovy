import service.Validador
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class RecrutadorValidacaoSpec extends Specification {

    @Unroll
    def "validação de e-mail para '#email' deve retornar #esperado"() {
        expect:
        Validador.emailValido(email) == esperado

        where:
        email                        | esperado
        "recrutador@empresa.com"     | true
        "rh.ti+1@empresa.com.br"     | true
        "invalido"                   | false
        "sem-dominio@"               | false
        "@sem-usuario.com"           | false
        ""                           | false
        null                         | false
    }

    @Unroll
    def "validação de CPF para '#cpf' deve retornar #esperado"() {
        expect:
        Validador.cpfValido(cpf) == esperado

        where:
        cpf              | esperado
        "52998224725"    | true
        "529.982.247-25" | true
        "111.111.111-11" | false
        "529.982.247-26" | false
        "1234567890"     | false
        ""               | false
        null             | false
    }

    def "parseDataNascimento deve aceitar apenas formato dd-mm-aaaa e data calendárica válida"() {
        expect:
        Validador.parseDataNascimento("15-05-1995") == LocalDate.of(1995, 5, 15)

        when:
        Validador.parseDataNascimento(entradaInvalida)

        then:
        thrown(IllegalArgumentException)

        where:
        entradaInvalida << ["1995-05-15", "31-02-2000", "15/05/1995", "abc", "", null]
    }

    def "maiorDeIdade deve exigir 18 anos completos"() {
        given:
        LocalDate hoje = LocalDate.of(2026, 11, 21)

        expect:
        Validador.maiorDeIdade(LocalDate.of(2008, 11, 21), hoje)
        Validador.maiorDeIdade(LocalDate.of(1995, 3, 12), hoje)
        !Validador.maiorDeIdade(LocalDate.of(2008, 11, 22), hoje)
        !Validador.maiorDeIdade(LocalDate.of(2027, 1, 1), hoje)
    }
}
