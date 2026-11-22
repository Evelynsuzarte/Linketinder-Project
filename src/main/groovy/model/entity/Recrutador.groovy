package model.entity

import groovy.transform.MapConstructor

import java.time.LocalDate

@MapConstructor
class Recrutador {
    int id
    Empresa empresa
    String nome
    String areaRecrutamento
    String descricao
    String email
    String cpf
    LocalDate dataNascimento
    String senha

    String exibirDados() {
        String nomeEmpresa = empresa?.nome ?: "Sem empresa"
        return "Recrutador: ${nome} | Área: ${areaRecrutamento} | Empresa: ${nomeEmpresa} | Email: ${email} | Descrição: ${descricao ?: ''}"
    }
}
