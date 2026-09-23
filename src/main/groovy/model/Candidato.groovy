package model;
import groovy.transform.MapConstructor

@MapConstructor(includeSuperProperties = true)
class Candidato extends Pessoa{
    String cpf
    int idade
    List<Vaga> vagasInteresse

    def exibirDados() {
        return "Candidato: ${nome} | email: ${email} | Idade: ${idade} | " +
                "Descricao:${descricao} | Estado: ${estado} | Competencias: ${competencias}";
    }
}
