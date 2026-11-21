# AGENTS.md — Linketinder

Guia de contexto, arquitetura, regras de negócio e convenções para agentes atuando neste repositório.

---

## 1. Visão Geral

**Linketinder** é uma plataforma de match entre **candidatos** (pessoas físicas) e **empresas** (pessoas jurídicas) para vagas de emprego, inspirada no modelo do Tinder.

O projeto possui três interfaces/camadas de execução:
1. **API RESTful (ponto de entrada principal `Main.groovy`):** servidor **Apache Tomcat embutido** na porta `8080` com servlets para `/candidatos`, `/empresas` e `/vagas` (`GET` para listar e `POST` para cadastrar).
2. **CLI Interativo (`MainTerminal.groovy`):** aplicação de linha de comando com menu de 10 opções para listar/cadastrar candidatos, empresas e vagas, avaliar vagas (candidato), avaliar candidatos interessados (empresa) e consultar matches.
3. **Front-end Web Estático (`src/main/resources/frontend/`):** páginas HTML, CSS e TypeScript compilado para JS, operando de forma independente sem back-end (persistência via `localStorage` do navegador, incluindo gráfico com Chart.js no painel da empresa).

> Nota: Há também versões legadas sem banco em `Main_semDB.groovy` e `controller/LinketinderController_semBD.groovy`.

---

## 2. Stack e Estrutura

### Stack
- **Linguagem principal (Back-end):** Groovy (`org.apache.groovy:groovy:5.0.8`) sobre Java/JVM (UTF-8).
- **Build & Dependências:** Gradle (`./gradlew` / `.\gradlew.bat`).
- **Banco de Dados:** PostgreSQL (`org.postgresql:postgresql:42.7.2`) via JDBC com `groovy.sql.Sql` (`groovy-sql`).
- **Banco em Memória (Testes):** H2 Database (`com.h2database:h2:2.2.224` em `MODE=PostgreSQL`).
- **Servidor Web / API:** Apache Tomcat Embutido (`org.apache.tomcat.embed:tomcat-embed-core:9.0.96`) + `groovy-json` (`JsonSlurper` / `JsonGenerator`).
- **Testes:** Spock Framework (`spock-core:2.4-groovy-5.0`) + JUnit Platform (`junit-jupiter`).
- **Front-end:** HTML5, CSS3 e TypeScript (`src/main/resources/frontend/ts/*.ts` compilado para `js/`).

### Estrutura de Pastas

```text
├── build.gradle
├── settings.gradle
├── README.md
└── src
    ├── main
    │   ├── groovy
    │   │   ├── Main.groovy                         # Sobe o Tomcat embutido na porta 8080 (API REST)
    │   │   ├── MainTerminal.groovy                 # Ponto de entrada do modo console (CLI)
    │   │   ├── Main_semDB.groovy                   # Versão legada em memória
    │   │   ├── controller
    │   │   │   ├── api/ApiServlet.groovy           # Servlet base (parse JSON, validação, resposta e tratamento de erro)
    │   │   │   ├── CandidatoController.groovy      # Servlet REST /candidatos
    │   │   │   ├── EmpresaController.groovy        # Servlet REST /empresas
    │   │   │   ├── VagaController.groovy           # Servlet REST /vagas
    │   │   │   └── LinketinderController.groovy    # Controlador do fluxo CLI
    │   │   ├── db
    │   │   │   ├── ConexaoDB.groovy                # Singleton de conexão SQL (seleciona fábrica via DB_TIPO)
    │   │   │   ├── ConexaoFactory.groovy           # Interface da fábrica de conexões
    │   │   │   ├── PostgresConexaoFactory.groovy   # Conexão PostgreSQL (usa DB_URL, DB_USER, DB_PASSWORD)
    │   │   │   ├── H2ConexaoFactory.groovy         # Conexão H2 em memória (modo PostgreSQL)
    │   │   │   └── dao/                            # CandidatoDao, EmpresaDao, VagaDao, CompetenciaDao, MatchDao
    │   │   ├── model
    │   │   │   ├── entity/                         # Usuario (abstrata), Candidato, Empresa, Vaga
    │   │   │   └── repository/                     # Interfaces: CandidatoRepository, EmpresaRepository, VagaRepository, CompetenciaRepository, MatchRepository
    │   │   ├── service/                            # CandidatoService, EmpresaService, VagaService, CompetenciaService, MatchService, Validador
    │   │   └── view/                               # Menu, LeitorConsole, AvisoErros
    │   └── resources
    │       ├── db/modelagem.sql                    # DDL de criação das tabelas e inserts iniciais
    │       ├── frontend/                           # html/, css/, ts/, js/
    │       └── images/                             # logo.png, fundo.jpg
    └── test
        └── groovy/                                 # Specs Spock: CadastroSpec, CandidatoDaoIntegracaoSpec, CicloMatchSpec, ConexaoDBSpec, MatchRepositoryFake
```

---

## 3. Como Executar e Testar

### Rodar os testes automatizados
Os testes utilizam Spock e banco H2 em memória (não exigem PostgreSQL rodando):

```powershell
# Windows
.\gradlew.bat test

# Linux/macOS
./gradlew test
```

### Rodar a aplicação (API RESTful — `Main.groovy`)
Certifique-se de que o PostgreSQL está rodando com o banco `linketinder` criado e populado:

```powershell
# Windows
.\gradlew.bat run

# Linux/macOS
./gradlew run
```
O servidor sobe em `http://localhost:8080`.

### Testar a API RESTful
Com o servidor rodando em `http://localhost:8080` (via `.\gradlew.bat run`), envie requisições com **Postman**, **Insomnia**, **Thunder Client** ou `curl.exe` (sempre com `Content-Type: application/json` nos envios `POST`):

- **Endpoints atuais:**
  - `GET /candidatos` e `POST /candidatos` (obrigatórios: `cpf`, `nome`, `email`, `senha`)
  - `GET /empresas` e `POST /empresas` (obrigatórios: `cnpj`, `nome`, `email`, `senha`)
  - `GET /vagas` e `POST /vagas` (obrigatórios: `nome`, `descricao`, `local`, `idEmpresa`)
- **Exemplo (`GET` e `POST` com `curl.exe`):**
  ```powershell
  curl.exe http://localhost:8080/vagas
  curl.exe -X POST http://localhost:8080/vagas -H "Content-Type: application/json" -d "{\"nome\":\"Dev Backend\",\"descricao\":\"API REST\",\"local\":\"Remoto\",\"idEmpresa\":1,\"competencias\":[\"Groovy\",\"SQL\"]}"
  ```

### Rodar o modo Terminal (CLI — `MainTerminal.groovy`)
Execute a classe `MainTerminal` diretamente pela IDE (com o classpath do projeto carregado) ou configurando `mainClass` para `MainTerminal`.

### Rodar o Front-end Web
> **Status de integração:** o front-end **ainda não está integrado ao back-end**. Não há chamadas HTTP para a API REST (`Main.groovy`); os dados são persistidos no `localStorage` do navegador com carga inicial fictícia definida em `src/main/resources/frontend/ts/storage.ts`.

1. **Compilar o TypeScript** *(necessário apenas se alterar arquivos em `ts/`, pois os `.js` já vêm compilados)*:
   ```powershell
   cd src/main/resources/frontend
   npm install   # caso node_modules não esteja instalado
   npx tsc       # lê ./ts e gera os módulos ES em ./js (conforme tsconfig.json)
   ```
2. **Servir via servidor HTTP local** *(os HTMLs usam `<script type="module">` e não funcionam via `file://`)*:
   - Sirva `src/main/resources/` para que os caminhos de imagens (`../../images/logo.png`) também resolvam:
     ```powershell
     npx serve src/main/resources
     ```
     Acesse `http://localhost:<porta>/frontend/html/index.html`.
   - Alternativamente, abra `src/main/resources/frontend/html/index.html` via Live Server (VS Code) ou servidor embutido da IDE.
3. **Credenciais de teste** *(gravadas no `localStorage` ao abrir `index.html`)*:
   - **Candidatos:** `gabriel@email.com` (`123`), `mariana@email.com` (`abc`), `carlos@email.com` (`456`)
   - **Empresas:** `vagas@techsolutions.com` (`admin`), `rh@inovastudio.com` (`inova`), `careers@softglobal.com` (`cloud`)

---

## 4. Convenções

- **Arquitetura em camadas:**
  - `model/entity`: entidades de domínio.
  - `model/repository`: contratos (interfaces) de persistência.
  - `db/dao`: implementações JDBC/SQL das interfaces de repositório.
  - `service`: regras de negócio e intermediação entre controllers e repositórios.
  - `controller`: coordenação de fluxo (`LinketinderController` no CLI; servlets estendendo `ApiServlet` na API REST).
  - `view`: exclusivamente entrada e saída de console (`Menu`, `LeitorConsole`, `AvisoErros`).
- **Nomenclatura:**
  - Classes em `PascalCase` e sufixos explícitos por camada (`*Controller`, `*Service`, `*Repository`, `*Dao`, `*Factory`, `*Spec`).
  - Métodos e variáveis em `camelCase` em português, descrevendo a intenção (ex.: `entradaDadosCandidato`, `verMatchesEmpresa`, `listarNaoCurtidas`).
- **Estilo e Boas Práticas (Clean Code / SOLID):**
  - Funções pequenas com responsabilidade única e retorno explícito (`void` ou tipo específico; evitar `def` em assinaturas quando o tipo é conhecido).
  - Separar leitura de entrada/validação da gravação em banco.
  - Depender de interfaces (`*Repository`, `ConexaoFactory`) em vez de implementações concretas para manter testabilidade com fakes e H2.
  - Centralizar tratamento de erros: `AvisoErros` para erros SQL no CLI e `ApiServlet.executar` para respostas HTTP (`200`, `201`, `400`, `500`) em UTF-8.
  - Codificação **UTF-8** obrigatória em arquivos fonte e recursos.

---

## 5. Restrições

- **Não alterar funcionalidades existentes fora do escopo da feature solicitada** sem pedido explícito do usuário.
- Não quebrar contratos das interfaces em `model/repository/` nem alterar fluxos de CLI, API ou front-end que não façam parte da tarefa atual.
- Manter comentários/documentação existentes que não estejam relacionados às alterações solicitadas.

---

## 6. Domínio do Linketinder

As entidades principais em `src/main/groovy/model/entity/` se relacionam da seguinte forma:

- **`Usuario` (classe abstrata):** define atributos comuns (`id`, `nome`, `email`, `descricao`, `cep`, `estado`, `senha`, `pais`) e o método abstrato `exibirDados()`. Não pode ser instanciada diretamente.
- **`Candidato` (estende `Usuario`):** adiciona `sobrenome`, `cpf`, `idade`, `vagasInteresse` (`List<Vaga>`) e `competencias` (`List<String>`).
- **`Empresa` (estende `Usuario`):** adiciona `cnpj` e `vagas` (`List<Vaga>`). As competências não pertencem à empresa, mas às suas vagas.
- **`Vaga`:** possui `id`, `nome`, `descricao`, `local`, referência à `Empresa` (`empresa`) e listas de `competencias` (`List<String>`), `interessados` (`List<Candidato>`) e `matches` (`List<Candidato>`).

---

## 7. Regras de Negócio Centrais (Curtidas e Match)

O **match** acontece quando os dois lados demonstram interesse mútuo:

1. **Candidato curte a vaga (opção 7 no CLI):**
   - O candidato informa seu nome e visualiza as vagas ainda não curtidas uma de cada vez.
   - Para cada vaga, são exibidos apenas o **nome da vaga, as competências e o local**.
   - Digitando `s`, a vaga é registrada na lista de interesses do candidato (`curtida_candidato_vaga`). Vagas já curtidas não aparecem novamente.
2. **Empresa curte o candidato (opção 8 no CLI):**
   - A empresa informa seu nome e visualiza os candidatos que demonstraram interesse nas suas vagas.
   - Para preservar o anonimato, aparecem apenas a **descrição e as competências** do candidato (além do nome da vaga de interesse).
   - Digitando `s`, a curtida da empresa é registrada (`curtida_empresa_candidato`) e o **match é gerado** (`match`).
3. **Ver matches (opção 9 no CLI):**
   - Escolhendo a visão `c` (candidato) ou `e` (empresa) e informando o nome:
     - **Visão do candidato:** exibe o **nome da vaga, a empresa e o local**.
     - **Visão da empresa:** exibe o **nome da vaga, o nome e o sobrenome do candidato e o e-mail**.

### Observações importantes
- Vagas ou candidatos recusados (`n`) passam a ser registrados em `rejeicao_empresa_candidato` e podem ser consultados na lista anônima de rejeitados.
- No back-end, as senhas (`Recrutador`, `Candidato` e `Empresa`) são armazenadas com hash seguro **`PBKDF2WithHmacSHA256`** + salt aleatório de 16 bytes (`SegurancaSenha`).
- **Senhas de teste iniciais (para consulta):**
  - **Banco SQL (`modelagem.sql` / `migracao_recrutador.sql`):** todos os candidatos, empresas e recrutadores iniciais utilizam a senha original **`123456`** (armazenada em hash no banco).
  - **Front-end (`localStorage` / `storage.ts`):** candidatos (`gabriel@email.com` / `123`, `mariana@email.com` / `abc`, `carlos@email.com` / `456`), empresas (`vagas@techsolutions.com` / `admin`, `rh@inovastudio.com` / `inova`, `careers@softglobal.com` / `cloud`) e recrutadores (`fernanda@techsolutions.com` / `123`, `rafael@inovastudio.com` / `123`).
- Candidatos e empresas são buscados pelo **nome** no fluxo CLI; nomes repetidos podem causar ambiguidade.

---

## 8. Princípio do Anonimato

A exibição de dados segue estritamente a regra de anonimato antes do match:

| Perspectiva | Antes do Match | Depois do Match |
|---|---|---|
| **Empresa** | Descrição do candidato e suas competências (sem nome, sobrenome, CPF ou e-mail) | Nome da vaga, nome e sobrenome do candidato e e-mail |
| **Candidato** | Nome da vaga, suas competências e local (sem expor a empresa na avaliação) | Nome da vaga, nome da empresa e local |

---

## 9. Banco de Dados

- **Script SQL:** [`src/main/resources/db/modelagem.sql`](src/main/resources/db/modelagem.sql)
  - Contém a criação de todas as tabelas (`candidatos`, `empresas`, `vagas`, `competencias`, `candidato_competencia`, `vaga_competencia`, `curtida_candidato_vaga`, `curtida_empresa_candidato`, `match`).
  - Contém as inserções iniciais de dados de exemplo (candidatos, empresas, competências e vagas) para facilitar testes locais.
- **Configuração de Conexão (`ConexaoDB.groovy`):**
  - Variável de ambiente `DB_TIPO`: quando definida como `h2` (case-insensitive), utiliza `H2ConexaoFactory` (`jdbc:h2:mem:linketinder;MODE=PostgreSQL;DB_CLOSE_DELAY=-1`); caso contrário, utiliza `PostgresConexaoFactory`.
  - Variáveis opcionais do PostgreSQL: `DB_URL` (padrão `jdbc:postgresql://localhost:5432/linketinder`), `DB_USER` (padrão `postgres`) e `DB_PASSWORD` (padrão `1234`).
