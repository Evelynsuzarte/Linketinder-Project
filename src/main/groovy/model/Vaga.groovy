package model

class Vaga {
    int id
    String nome;
    Empresa empresa;
    String descricao;
    String local;
    List<String> competencias = [];
    List<Candidato> interessados = [];
    List<Candidato> matches = [];
}
