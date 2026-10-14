# 💼Linketinder💼

Autor: Evelyn Suzarte Fernandes

<div align="center">
  <img src="src/main/resources/images/logo.png" alt="Texto Alternativo" width="300">
</div>

## Sobre o projeto

Linketinder é um sistema de linha de comando (CLI) feito em Groovy, com banco de dados PostgreSQL, que simula uma plataforma de match entre candidatos (pessoas físicas) e empresas (pessoas jurídicas), no estilo "Tinder para vagas de emprego".

O programa permite:

- Cadastrar candidatos, com nome, sobrenome, e-mail, CPF, idade, CEP, país, descrição, competências e senha.
- Cadastrar empresas, com nome, e-mail, CNPJ, CEP, país, descrição e senha.
- Cadastrar vagas, ligadas a uma empresa, com nome, descrição, local e competências exigidas.
- Listar todos os candidatos, empresas e vagas cadastrados.
- Candidato: ver as vagas disponíveis e curtir as que têm interesse.
- Empresa: ver os candidatos interessados nas suas vagas e curtir os perfis que quiser.
- Ver os matches, tanto na visão do candidato quanto na visão da empresa.

Todos os dados ficam salvos no PostgreSQL, acessado via JDBC com `groovy.sql.Sql`. O script `modelagem.sql` cria as tabelas e já traz dados de exemplo (5 candidatos, 5 empresas, 12 competências e 6 vagas) para facilitar os testes.

## 🗂️ Estrutura do projeto

```
Main.groovy                              -> classe principal, contém o menu interativo
model/Usuario.groovy                     -> classe base com atributos comuns (nome, email, descrição, cep, país, senha)
model/Candidato.groovy                   -> representa o candidato (estende Usuario)
model/Empresa.groovy                     -> representa a empresa (estende Usuario)
model/Vaga.groovy                        -> representa a vaga (nome, descrição, local, empresa, competências)
controller/LinketinderController.groovy  -> fluxo do menu: cadastros, listagens, curtidas e matches
db/dao/ConexaoDB.groovy                  -> conexão única com o PostgreSQL
db/dao/CandidatoDao.groovy               -> inserir e buscar candidatos
db/dao/EmpresaDao.groovy                 -> inserir e buscar empresas
db/dao/VagaDao.groovy                    -> inserir e listar vagas (todas ou as ainda não curtidas pelo candidato)
db/dao/CompetenciaDao.groovy             -> competências do candidato e das vagas
db/dao/MatchDao.groovy                   -> curtidas e matches
modelagem.sql                            -> criação das tabelas e dados de exemplo
```

## 🗄️ Modelagem do banco de dados

| Tabela | Função |
|---|---|
| `candidatos` | dados do candidato |
| `empresas` | dados da empresa |
| `vagas` | vagas de cada empresa (`id_empresa`) |
| `competencias` | lista de competências, sem repetição |
| `candidato_competencia` | liga candidatos às suas competências |
| `vaga_competencia` | liga vagas às competências exigidas |
| `curtida_candidato_vaga` | vagas que o candidato curtiu (lista de interesses) |
| `curtida_empresa_candidato` | candidatos que a empresa curtiu, para uma vaga |
| `match` | matches gerados (candidato, empresa e vaga) |

As competências da empresa não ficam na empresa: elas são definidas em cada vaga.

## Pré-requisitos

- [Groovy](https://groovy-lang.org/install.html) instalado (versão 3 ou superior).
- Java (JDK 8 ou superior), necessário para rodar o Groovy.
- [PostgreSQL](https://www.postgresql.org/download/) instalado e rodando.
- Dependências do projeto: `groovy-sql` e o driver JDBC do PostgreSQL (`org.postgresql:postgresql`), por exemplo via Gradle.

## ▶️ Como executar

1. Crie o banco no PostgreSQL (pelo pgAdmin ou pelo terminal):

```sql
CREATE DATABASE linketinder;
```

2. Dentro do banco `linketinder`, execute o script `modelagem.sql`. Ele cria as tabelas e insere os dados de exemplo.

3. Confira em `db/dao/ConexaoDB.groovy` se a URL, o usuário e a senha batem com o seu PostgreSQL:

```groovy
"jdbc:postgresql://localhost:5432/linketinder"
```

4. Execute a classe `Main` pela IDE (botão de play), com as dependências do projeto carregadas.

5. O menu interativo vai aparecer no terminal. Basta digitar o número da opção desejada e pressionar Enter:

```
===== LINKETINDER =====
1 - LISTAR CANDIDATOS
2 - LISTAR EMPRESAS
3 - LISTAR VAGAS
4 - CADASTRAR CANDIDADOS NOVOS
5 - CADASTRAR EMPRESAS NOVAS
6 - CADASTRAR VAGAS NOVAS
7 - CANDIDATO: CURTIR VAGAS
8 - EMPRESA: CURTIR CANDIDATOS
9 - VER MATCHS
10 -  SAIR
===== ESCOLHA:
```

## Como funciona o match

O match acontece quando os dois lados demonstram interesse:

1. **Candidato curte a vaga (opção 7):** o candidato informa o nome e o sobrenome e vê as vagas uma de cada vez, mostrando apenas o **nome da vaga, as competências e o local**. Digitando `s`, a vaga entra na lista de interesses dele. Vagas já curtidas não aparecem de novo.
2. **Empresa curte o candidato (opção 8):** a empresa informa o nome e vê os candidatos interessados nas suas vagas. Para preservar o anonimato, aparecem apenas a **descrição e as competências** do candidato. Digitando `s`, o match é gerado.
3. **Ver matches (opção 9):** escolhendo a visão `c` (candidato) ou `e` (empresa) e informando o nome:
- Na visão do candidato aparecem a vaga, a empresa e o local.
- Na visão da empresa aparecem a vaga e o nome, o sobrenome e o e-mail do candidato.

### Observações

- Vagas ou candidatos recusados (`n`) não são guardados e voltam a aparecer na próxima avaliação.
- As senhas são salvas em texto puro, por se tratar de um projeto de estudo.
- Candidatos e empresas são identificados pelo nome, então nomes repetidos podem causar confusão.


## 🧹 Refatoração e boas práticas (Clean Code)

O `LinketinderController` foi refatorado para ter funções pequenas, com uma responsabilidade cada, e nomes que dizem o que a função faz. A lógica do programa continua a mesma: mudou a organização do código e a forma de lidar com entradas inválidas e erros do banco.

### O que foi feito

1. **Retorno de `avaliarVagas`:** o tipo de retorno passou de `def` para `void`, já que a função não devolve nada (igual às demais).

2. **Entrada de dados separada da gravação no banco:** os cadastros foram divididos em duas funções cada, uma só para ler o teclado e montar o objeto, e outra só para gravar.

| Cadastro | Lê os dados e monta o objeto | Grava no banco |
|---|---|---|
| Candidato | `entradaDadosCandidato` | `novoCandidato` |
| Empresa | `entradaDadosEmpresa` | `novaEmpresa` |
| Vaga | `entradaDadosVaga` | `novaVaga` |

Assim a leitura do teclado fica separada do acesso ao banco, o que facilita reutilizar e testar cada parte. Na vaga, a função de entrada devolve `null` quando a empresa não existe, e `novaVaga` não grava nada nesse caso.

3. **`verMatches` dividido por visão:** a busca e a listagem foram para duas funções, `verMatchesCandidato(nome)` e `verMatchesEmpresa(nome)`. Elas devolvem um `StatusMatch` (`CANDIDATO_NAO_ENCONTRADO`, `EMPRESA_NAO_ENCONTRADA`, `COM_MATCHES` ou `SEM_MATCHES`), e o `verMatches` só pergunta a visão e escolhe a mensagem de acordo com o resultado.

4. **Sequências de `println` extraídas:** a exibição de uma vaga virou `exibirVaga(vaga)`, usada em `avaliarVagas`, e a exibição do perfil de um candidato interessado virou `exibirMatch(perfil)`, usada em `avaliarCandidatos`.

5. **Aviso quando não há o que avaliar:** `avaliarVagas` e `avaliarCandidatos` conferem se a lista está vazia e encerram com uma mensagem, antes de começar o laço.

6. **Tratamento de erros simples:** entradas inválidas e falhas do banco não fecham mais o programa.
    - O menu aceita só números: letras viram opção inválida.
    - `lerObrigatorio` (nome, CPF/CNPJ, senha...), `lerInteiro` (idade) e `lerCompetencias` (descarta itens vazios) repetem a pergunta até a entrada ser válida.
    - Cadastros e curtidas têm `try/catch` de `SQLException`, e `avisarErroDeBanco` mostra uma mensagem clara (CPF/CNPJ repetido, texto maior que a coluna ou erro de acesso ao banco).
    - `verMatches` avisa quando a visão não é `c` nem `e`, e a mensagem de "não encontrado" aparece uma vez só.

### Como a refatoração melhorou o código

| Princípio do Clean Code | Antes | Depois |
|---|---|---|
| **Funções pequenas e de responsabilidade única** | `novoCandidato` lia dez campos, montava o objeto e gravava no banco. `verMatches` perguntava, buscava, imprimia e tratava os casos de erro, tudo numa função só | Cada função faz uma coisa: ler (`entradaDados*`), gravar (`novoCandidato`, `novaEmpresa`, `novaVaga`), buscar e listar (`verMatchesCandidato`, `verMatchesEmpresa`) e exibir (`exibirVaga`, `exibirMatch`) |
| **Nomes que explicam a intenção** | Nomes genéricos para funções que faziam várias tarefas, e mensagens decididas no meio da lógica | O nome diz a tarefa (`entradaDadosVaga`, `verMatchesEmpresa`, `lerObrigatorio`), e o enum `StatusMatch` dá nome a cada resultado possível em vez de depender de mensagens soltas |
| **Não se repetir (DRY)** | Blocos de `println` com as linhas separadoras e os dados da vaga ou do perfil escritos dentro do laço, e a validação de cada campo repetida na própria pergunta | O formato de exibição fica em um só lugar (`exibirVaga` e `exibirMatch`), e a leitura de campos obrigatórios, números e competências fica em três funções auxiliares |
| **Tratamento de erros** | Letra no menu ou na idade lançava `NumberFormatException` e fechava o programa. CPF repetido ou texto maior que a coluna lançava `SQLException`. Campos vazios eram gravados | As entradas são validadas na leitura, os erros do banco são capturados e traduzidos em mensagens claras, e o programa volta ao menu |
| **Fluxo mais claro** | O laço rodava mesmo sem itens, terminando com uma mensagem enganosa de "você já viu todas as vagas" | Retorno antecipado: se não há nada para avaliar, a função avisa e termina logo, deixando o caminho principal sem aninhamento extra |
| **Separação de responsabilidades e testabilidade** | A lógica de busca decidia o que imprimir, e a leitura do teclado estava misturada com o banco | A busca devolve um `StatusMatch` e quem chama decide a mensagem. A gravação pode ser testada sem teclado, e a leitura de dados sem banco |
| **Consistência** | `avaliarVagas` era declarada com `def`, diferente das demais | Todas as funções declaram o tipo de retorno que realmente têm |

## 🌐 Versão web (front-end)

Além do CLI, o Linketinder tem uma versão web feita com **HTML, CSS e TypeScript**, sem back-end: os dados ficam salvos no `localStorage` do navegador.

### Páginas

- `index.html`: página inicial.
- `novo_cadastro.html`: cadastro de candidato ou empresa (os campos mudam conforme o tipo escolhido).
- `acessar.html`: login por e-mail e senha.
- `painel_candidato.html`: lista de vagas com filtro entre "Novas vagas" e "Minhas vagas inscritas", com os botões **Inscrever-se** e **Cancelar interesse**.
- `painel_empresa.html`: vagas da empresa com os candidatos inscritos (anônimos), botões **Match** e **Não Interessado** e um gráfico de candidatos por competência, feito com Chart.js.

### TypeScript

- `storage.ts`: interfaces (`Usuario`, `Candidato`, `Empresa`, `Vaga`), dados fictícios iniciais (3 candidatos, 3 empresas e 9 vagas) e funções de leitura e gravação no `localStorage`. As outras páginas importam este arquivo.
- `acesso.ts`: valida o login e redireciona para o painel do tipo de usuário.
- `novo_cadastro.ts`: valida os campos, impede e-mail repetido e salva o novo usuário.
- `painel_candidato.ts` e `painel_empresa.ts`: desenham as tabelas e o gráfico na tela e tratam os cliques dos botões, salvando as alterações.

O candidato guarda os ids das vagas de interesse (`vagasInteresse`) e a empresa guarda os ids das suas vagas (`vagas`).

### HTML e CSS

- O HTML tem a estrutura fixa de cada página, e as linhas das tabelas e os cards de vagas são criados pelo TypeScript.
- Cada página tem sua folha de estilo (como `acessar_style.css`, `painel_candidato_style.css` e `painel_empresa_style.css`), com CSS simples e as fontes do Google Fonts (Cagliostro, Josefin Sans e Tilt Warp).

### Como executar

1. Compile o TypeScript: `tsc`.
2. Sirva a pasta do projeto por um servidor local (Live Server, `npx serve` ou o servidor embutido do IntelliJ), porque os módulos ES não funcionam abrindo o HTML direto.
3. Abra `index.html` no navegador. Na primeira abertura, os dados fictícios são gravados automaticamente.

Logins de teste: candidato `gabriel@email.com` / `123` e empresa `vagas@techsolutions.com` / `admin`.