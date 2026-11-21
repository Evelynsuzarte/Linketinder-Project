import service.SegurancaSenha
import spock.lang.Specification

class SegurancaSenhaSpec extends Specification {

    def "deve gerar hash PBKDF2 com salt aleatório diferente a cada chamada e nunca igual à senha pura"() {
        when:
        String hash1 = SegurancaSenha.gerarHash("123456")
        String hash2 = SegurancaSenha.gerarHash("123456")

        then:
        hash1 != "123456"
        hash1 != hash2
        SegurancaSenha.estaHasheada(hash1)
        SegurancaSenha.estaHasheada(hash2)
    }

    def "deve verificar corretamente senha válida e rejeitar senha incorreta"() {
        given:
        String hash = SegurancaSenha.gerarHash("minhaSenha123")

        expect:
        SegurancaSenha.verificarSenha("minhaSenha123", hash)
        !SegurancaSenha.verificarSenha("outraSenha", hash)
        !SegurancaSenha.verificarSenha("", hash)
        !SegurancaSenha.verificarSenha("minhaSenha123", "texto-puro")
    }
}
