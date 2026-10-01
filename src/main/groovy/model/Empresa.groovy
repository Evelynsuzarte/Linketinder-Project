package model
import groovy.transform.MapConstructor

@MapConstructor(includeSuperProperties = true)
class Empresa extends Pessoa{
    String cnpj;
    String pais;
    List<Vaga> vagas = []

    def exibirDados() {
        return "Empresa: ${nome} | Descricao:${descricao} | Estado: ${estado} | " +
                "Competencias: ${competencias}";
    }
}
