package model;
import groovy.transform.MapConstructor

@MapConstructor(includeSuperProperties = true)
class Candidato extends Usuario{
    String sobrenome
    String cpf

    int idade
    List<Vaga> vagasInteresse = []
    List<String> competencias = []

    def exibirDados() {
        return "Candidato: ${nome} ${sobrenome} | email: ${email} | Idade: ${idade} | " +
                "Descricao:${descricao} | Estado: ${estado} | Competencias: ${competencias}";
    }
}
