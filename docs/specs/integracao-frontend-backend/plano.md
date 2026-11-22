# Plano de Implementação

Este plano divide a implementação de [spec.md](spec.md) em tarefas pequenas e numeradas, respeitando [AGENTS.md](../../../AGENTS.md) e o fluxo da skill `feature-com-spec`.

- [x] **1. Habilitar CORS na API REST (`ApiServlet`)**
  - **O que será feito:** Adicionar cabeçalhos CORS (`Access-Control-Allow-Origin`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers`) nas respostas de `ApiServlet` e suporte a requisições `OPTIONS` (preflight) para permitir consumo pelo front-end em servidor local, sem alterar a lógica de negócio dos controllers.
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/groovy/controller/api/ApiServlet.groovy`
    - Criar/Alterar: `src/test/groovy/ApiServletCorsSpec.groovy`
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Executar `.\gradlew.bat test` e verificar que todos os testes Spock passam (incluindo headers CORS e `OPTIONS 200/204`).

- [x] **2. Criar endpoint de autenticação (`POST /login`) e sanitizar retornos `GET` sensíveis**
  - **O que será feito:** Criar `LoginController` mapeado em `/login` no `Main.groovy`, validando `email` e `senha` contra `CandidatoService`, `EmpresaService` e `RecrutadorService` usando `SegurancaSenha.verificarSenha`. Retornar `200 OK` com projeção segura do usuário autenticado (tipo, `id`, `nome`, `email`, `idEmpresa` quando recrutador — nunca `senha`, `cpf` de recrutador ou `dataNascimento`) e `401 Unauthorized` para credenciais inválidas. Garantir também que `GET /candidatos` e `GET /empresas` não exponham `senha`.
  - **Arquivos criados ou alterados:**
    - Criar: `src/main/groovy/controller/LoginController.groovy`
    - Alterar: `src/main/groovy/Main.groovy`
    - Alterar: `src/main/groovy/controller/CandidatoController.groovy` (projeção segura sem `senha` no `GET`)
    - Alterar: `src/main/groovy/controller/EmpresaController.groovy` (projeção segura sem `senha` no `GET`)
    - Criar: `src/test/groovy/LoginControllerSpec.groovy`
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Executar `.\gradlew.bat test` e validar os cenários de login válido/inválido para candidato, empresa e recrutador com H2 + `SegurancaSenha`.

- [x] **3. Criar endpoint de competências (`GET /competencias` e `POST /competencias`)**
  - **O que será feito:** Expor `CompetenciaController` mapeado em `/competencias` no `Main.groovy`. No `GET /competencias`, listar todas as competências cadastradas no banco (ou por `?idCandidato=`) reutilizando `CompetenciaDao`/`CompetenciaService`. No `POST /competencias`, receber `{ idCandidato, competencias }` (ou `{ nome }`) e persistir via `CompetenciaService`, retornando `201 Created`.
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/groovy/model/repository/CompetenciaRepository.groovy` e `src/main/groovy/db/dao/CompetenciaDao.groovy` (adicionar método de listagem geral de competências, se ausente)
    - Alterar: `src/main/groovy/service/CompetenciaService.groovy`
    - Criar: `src/main/groovy/controller/CompetenciaController.groovy`
    - Alterar: `src/main/groovy/Main.groovy`
    - Criar: `src/test/groovy/CompetenciaControllerSpec.groovy`
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Executar `.\gradlew.bat test` e verificar `GET /competencias` (`200 OK`) e `POST /competencias` (`201 Created`).

- [ ] **4. Garantir projeção anônima de vagas para avaliação do candidato (`GET /vagas?idCandidato=`)**
  - **O que será feito:** No `VagaController`, adicionar suporte ao parâmetro opcional `idCandidato` utilizando `VagaService.listarNaoCurtidas(idCandidato)` e retornando apenas `id`, `nome`, `descricao`, `local` e `competencias` (ocultando dados da empresa antes do match, conforme o Princípio do Anonimato).
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/groovy/controller/VagaController.groovy`
    - Criar/Alterar: `src/test/groovy/VagaControllerSpec.groovy`
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Executar `.\gradlew.bat test` e conferir que `GET /vagas?idCandidato=<id>` não retorna campos da empresa.

- [ ] **5. Configurar camada HTTP Client em TypeScript no front-end (`httpClient.ts` / `api.ts`)**
  - **O que será feito:** Explorar e integrar `typescript-http-client` (ou cliente HTTP tipado em TypeScript puro sobre `fetch`, compatível com ES Modules no navegador e sem frameworks) para centralizar requisições `GET`, `POST`, `PUT` e `DELETE` para `http://localhost:8080`, tratando status HTTP (`200`, `201`, `400`, `401`, `500`) e erros de rede. Atualizar `storage.ts` (ou criar `sessao.ts`) para manter apenas a sessão do usuário autenticado.
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/frontend/package.json` (se instalar `typescript-http-client`) e `tsconfig.json` (se necessário para resolução de módulos)
    - Criar: `src/main/resources/frontend/ts/api.ts`
    - Alterar: `src/main/resources/frontend/ts/storage.ts` (manter tipos e helpers de sessão sem depender de dados fictícios)
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Compilar o TypeScript com `npx tsc` em `src/main/resources/frontend/` sem erros e verificar os módulos `.js` gerados em `js/`.

- [ ] **6. Integrar Login (`acessar.html` / `acesso.ts`) com `POST /login`**
  - **O que será feito:** Atualizar `acesso.ts` para autenticar via `POST /login` usando o cliente HTTP criado na tarefa 5, armazenar os dados seguros da sessão retornados pelo back-end e redirecionar para `painel_candidato.html`, `painel_empresa.html` ou `painel_recrutador.html`, exibindo mensagem de erro em caso de credenciais inválidas ou falha de conexão.
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/frontend/ts/acesso.ts` (e recompilar `js/acesso.js`)
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Subir a API (`.\gradlew.bat run`), servir `src/main/resources/` e testar login de candidato, empresa e recrutador com senha `123456` e com senha inválida.

- [ ] **7. Integrar Cadastro e Competências (`novo_cadastro.html` / `cadastro.ts`) com a API REST**
  - **O que será feito:** Atualizar `cadastro.ts` para enviar `POST /candidatos` e `POST /empresas` via HTTP client, tratando resposta `201 Created` e erros `400 Bad Request`. Para candidato recém-cadastrado (caso de referência Sandubinha), carregar competências existentes via `GET /competencias`, permitir selecionar/inserir novas competências e enviar `POST /competencias` (`201 Created`) antes de prosseguir para a visualização de vagas.
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/frontend/html/novo_cadastro.html` (se necessário para seleção/entrada de competências)
    - Alterar: `src/main/resources/frontend/ts/cadastro.ts` (e recompilar `js/cadastro.js`)
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Compilar com `npx tsc` e testar manualmente o fluxo completo de cadastro de candidato + competências + cadastro de empresa persistindo no PostgreSQL.

- [ ] **8. Integrar Painéis (`painel_candidato.ts`, `painel_empresa.ts`, `painel_recrutador.ts`) e Criação de Vagas (`criar_vaga.ts`) com a API REST**
  - **O que será feito:** Conectar os painéis e a tela de criação de vagas aos endpoints `/vagas`, `/recrutadores` e `/matches` (`curtirVaga`, `curtirCandidato`, `rejeitarCandidato`, listagens de interessados, rejeitados e matches), garantindo proteção de sessão ao carregar cada painel e respeito estrito ao Princípio do Anonimato para candidato, empresa e recrutador.
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/frontend/ts/criar_vaga.ts`
    - Alterar: `src/main/resources/frontend/ts/painel_candidato.ts`
    - Alterar: `src/main/resources/frontend/ts/painel_empresa.ts`
    - Alterar: `src/main/resources/frontend/ts/painel_recrutador.ts`
    - Recompilar `src/main/resources/frontend/js/*.js`
    - Alterar: `CHANGELOG.md` (seção `[Integração do frontend e backend]`)
  - **Como validar:**
    - Executar `.\gradlew.bat test` e `npx tsc`; validar manualmente no navegador todo o ciclo (criar vaga, candidato curtir vaga, empresa/recrutador avaliar candidato anonimamente, curtir/rejeitar e visualizar matches) conferindo o Princípio do Anonimato.
