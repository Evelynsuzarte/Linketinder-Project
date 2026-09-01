# 💼Linketinder💼

Autor: Evelyn Suzarte Fernandes

<div align="center">
  <img src="src/main/resources/logo.png" alt="Texto Alternativo" width="300">
</div>

## Sobre o projeto

Linketinder é um sistema de linha de comando (CLI) feito em Groovy que simula uma plataforma de match entre candidatos (pessoas físicas) e empresas (pessoas jurídicas), no estilo "Tinder para vagas de emprego".

O programa permite:

- Cadastrar candidatos, com nome, e-mail, CPF, idade, estado, CEP, descrição e competências.
- Cadastrar empresas, com nome, e-mail, CNPJ, estado, CEP, descrição, competências procuradas e país.
- Listar todos os candidatos e todas as empresas cadastrados.
- Fazer o match entre candidatos e empresas com base na interseção de competências:
    - Match individual: escolhendo um candidato ou uma empresa específica pelo nome.
    - Match geral: comparando todos os candidatos com todas as empresas de uma vez.

O projeto já vem com 5 candidatos e 5 empresas pré-cadastrados para facilitar os testes.

## 🗂️ Estrutura do projeto

```
Main.groovy                     -> classe principal, contém o menu interativo
model/Pessoa.groovy              -> classe base com atributos comuns (nome, email, descrição, cep, estado, competências)
model/PessoaFisica.groovy        -> representa o candidato (estende Pessoa)
model/PessoaJuridica.groovy      -> representa a empresa (estende Pessoa)
controller/LinketinderController.groovy -> regras de negócio: cadastro, listagem e lógica de match
```

## Pré-requisitos

- [Groovy](https://groovy-lang.org/install.html) instalado (versão 3 ou superior).
- Java (JDK 8 ou superior), necessário para rodar o Groovy.


## ▶️ Como executar

1. Organize os arquivos respeitando os pacotes declarados no código, ficando assim:

```
projeto/
├── Main.groovy
├── controller/
│   └── LinketinderController.groovy
└── model/
    ├── Pessoa.groovy
    ├── PessoaFisica.groovy
    └── PessoaJuridica.groovy
```

2. Pelo terminal, na pasta `projeto`, execute:

```bash
groovy Main.groovy
```
ou aperte no play dentro da IDE desejada.

3. O menu interativo vai aparecer no terminal. Basta digitar o número da opção desejada e pressionar Enter:

```
===== LINKETINDER =====
1 - LISTA CANDIDATOS
2 - LISTAR EMPRESAS
3 - CADASTRAR CANDIDADOS NOVOS
4 - CADASTRAR EMPRESAS NOVAS
5 - MATCH CANDIDATO<->EMPRESA
6 - MATCH EMPRESA<->CANDIDATO
0 -  SAIR
===== ESCOLHA:
```

## Como funciona o match

O match compara as competências de um candidato com as competências buscadas por uma empresa (ou vice-versa). Se a quantidade de competências em comum for igual ou maior que "total de competências - 2", o par é considerado um match. Os resultados são ordenados pela quantidade de competências em comum e, no match geral, são exibidos até 3 melhores resultados por candidato/empresa.