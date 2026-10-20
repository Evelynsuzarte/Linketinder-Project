package model
import groovy.transform.MapConstructor

@MapConstructor(includeSuperProperties = true)
class Empresa extends Usuario{
    String cnpj;
    List<Vaga> vagas = []

    def exibirDados() {
        return "Empresa: ${nome} | Descricao:${descricao} | Estado: ${estado} | ";
    }
}
