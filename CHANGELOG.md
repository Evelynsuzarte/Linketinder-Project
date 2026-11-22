# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Integração do frontend e backend]

### Adicionado
- Especificação técnica da feature de integração entre front-end e back-end em `docs/specs/integracao-frontend-backend/spec.md` (contexto e objetivo, regras de negócio, critérios de aceite em Dado/Quando/Então, impacto e fora de escopo).
- Plano de implementação em `docs/specs/integracao-frontend-backend/plano.md`.
- Suporte a requisições preflight `OPTIONS` (HTTP `204`) e cabeçalhos CORS em `ApiServlet` (`ApiServletCorsSpec`) para consumo da API REST pelo front-end.
- Endpoint de autenticação `POST /login` (`LoginController` registrado em `Main.groovy` e `LoginControllerSpec`) validando hash `PBKDF2WithHmacSHA256` para candidatos, empresas e recrutadores com retorno seguro (sem `senha`, `cpf` de recrutador ou `dataNascimento`).
- Endpoint `/competencias` (`CompetenciaController` registrado em `Main.groovy` e `CompetenciaControllerSpec`) com `GET` (listagem geral ou por `?idCandidato=`) e `POST` (`201 Created` para vincular competências ao candidato ou cadastrar nova competência).
- Camada HTTP Client tipada em TypeScript puro (`src/main/resources/frontend/ts/api.ts` compilada para `js/api.js`), sem uso de frameworks, centralizando requisições REST (`HttpClient`, `HttpError`), sessão no navegador e contratos dos endpoints.

### Modificado
- `RecrutadorService` atualizado com `buscarPorEmail` para suporte à autenticação.
- `CandidatoController` e `EmpresaController` atualizados para retornar projeção segura no `GET` sem expor `senha` (nem `cpf` na listagem de candidatos).
- `CompetenciaRepository`, `CompetenciaDao` e `CompetenciaService` atualizados com `listarTodas()`.
- `VagaController` atualizado com filtro `?idCandidato=` (`VagaControllerSpec`) retornando apenas vagas ainda não curtidas em projeção anônima (sem expor dados da empresa antes do match).
- `Main.groovy` atualizado para servir os arquivos estáticos do front-end (`src/main/resources`) via `DefaultServlet` e redirecionar `http://localhost:8080/` para `/frontend/html/index.html`.
- Tela de login (`acesso.ts` / `js/acesso.js`) integrada ao endpoint `POST /login` via `api.ts`, armazenando sessão segura no navegador e redirecionando para o painel de candidato, empresa ou recrutador.
- Tela de cadastro (`novo_cadastro.html`, `cadastro.ts` / `js/cadastro.js`) integrada a `POST /candidatos`, `POST /empresas`, `GET /competencias` e `POST /competencias` (`201 Created`), incluindo etapa pós-cadastro de competências para candidato e redirecionamento para `painel_candidato.html`.
- Painéis e criação de vagas (`criar_vaga.ts`, `painel_candidato.ts`, `painel_empresa.ts`, `painel_recrutador.ts` e respectivos `.js` compilados) integrados aos endpoints `/vagas`, `/recrutadores` e `/matches`, preservando o Princípio do Anonimato para candidato, empresa e recrutador.

### Corrigido

### Removido

## [Feature de adicionar recrutador no sistema]

### Adicionado
- Documentação de contexto do projeto em `AGENTS.md`.
- Adaptação da skill `skills/feature-com-spec/SKILL.md` para o Linketinder (specs em `docs/specs/<nome-da-feature>/`, validação do Princípio do Anonimato e atualização obrigatória do `CHANGELOG.md` por tarefa).
- Especificação técnica da feature Recrutador em `docs/specs/recrutador/spec.md`.
- Script de migração incremental `src/main/resources/db/migracao_recrutador.sql` para criação de `recrutadores`, `rejeicao_empresa_candidato` e coluna `id_recrutador` em bancos existentes.
- Entidade `Recrutador`, contrato `RecrutadorRepository` e suíte de testes unitários `RecrutadorValidacaoSpec`.
- Persistência `RecrutadorDao` com CRUD completo e testes de integração em H2 (`RecrutadorDaoIntegracaoSpec`).
- Camada de serviço `RecrutadorService` com validações de negócio, projeção segura (sem expor `cpf`, `dataNascimento` ou `senha`) e testes em `RecrutadorServiceSpec`.
- Endpoints REST `/recrutadores` (`RecrutadorController` com `GET`, `POST`, `PUT`, `DELETE`) e `/matches` (`MatchController` para interessados, curtida, rejeição, rejeitados e matches).
- Painel do Recrutador no front-end (`painel_recrutador.html`, `painel_recrutador_style.css`, `painel_recrutador.ts`) para criação/edição de vagas da empresa, avaliação anônima de candidatos, lista anônima de rejeitados e visualização de matches.
- Tela de criação de vaga no front-end (`criar-vaga.html` e `criar_vaga.ts`) para empresa e recrutador cadastrarem novas vagas no `localStorage`.
- Remoção de vagas (`VagaRepository`, `VagaDao`, `VagaService`, `DELETE /vagas`, CLI e botões `Remover Vaga` em `painel_empresa.ts` e `painel_recrutador.ts`).
- Armazenamento seguro de senhas com hash `PBKDF2WithHmacSHA256` + salt (`SegurancaSenha`, `SegurancaSenhaSpec`) nos serviços de `Recrutador`, `Candidato` e `Empresa`, além de documentação das senhas de teste em `AGENTS.md` e `spec.md`.
- Registro do fluxo SDD em `docs/PROMPTS.md`.

### Modificado
- `README.md` atualizado com o fluxo de Recrutador, rejeições, hash de senhas, novos endpoints REST e telas do front-end.
- Instruções de teste da API RESTful adicionadas em `AGENTS.md`.
- Modelagem completa em `src/main/resources/db/modelagem.sql` atualizada para incluir `recrutadores`, `rejeicao_empresa_candidato` e `id_recrutador` em `curtida_empresa_candidato` e `match`.
- `Validador` atualizado com validações de e-mail, CPF e data de nascimento (`dd-mm-aaaa` com idade mínima de 18 anos).
- `VagaController` atualizado com filtro `?idEmpresa` e suporte a `PUT /vagas`; novos servlets registrados em `Main.groovy`.
- CLI (`Menu`, `LinketinderController` e `MainTerminal`) atualizado com gestão de recrutadores pela empresa, criação/edição de vagas e avaliação de candidatos pelo recrutador, e consulta anônima de candidatos rejeitados.
- Front-end (`storage.ts` e `acesso.ts`) atualizado com modelo e persistência de `Recrutador` e `Rejeicao` no `localStorage`, além de login de recrutador redirecionando para `painel_recrutador.html`.
- Painel da Empresa (`painel_empresa.html`, `painel_empresa_style.css`, `painel_empresa.ts`) atualizado com gestão de recrutadores (CRUD com validações) e lista anônima de candidatos rejeitados.

### Corrigido

### Removido
