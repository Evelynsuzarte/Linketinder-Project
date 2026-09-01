package model;
import groovy.transform.MapConstructor

@MapConstructor(includeSuperProperties = true)
class PessoaFisica extends Pessoa{
    String cpf
    int idade

    def exibirDados() {
        return "Candidato: ${nome} | email: ${email} | Idade: ${idade} | " +
                "Descricao:${descricao} | Estado: ${estado} | Competencias: ${competencias}";
    }
}
