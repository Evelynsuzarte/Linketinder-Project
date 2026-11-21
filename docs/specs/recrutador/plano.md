# Plano de Implementação: Recrutador

Este plano divide a implementação de [spec.md](spec.md) em tarefas pequenas e numeradas, respeitando [AGENTS.md](../../../AGENTS.md), na ordem **banco de dados → back-end com testes → front-end**.

---

## Banco de Dados

- [x] **1. Modelagem e script de migração incremental do banco**
  - **Camada:** Banco de dados
  - **O que será feito:**
    - Atualizar `modelagem.sql` com as tabelas `recrutadores` e `rejeicao_empresa_candidato`, a coluna `id_recrutador` (nullable, `ON DELETE SET NULL`) em `curtida_empresa_candidato` e `match`, e `INSERT`s de exemplo.
    - Criar `migracao_recrutador.sql` para atualizar bancos PostgreSQL já existentes sem perda de dados (`CREATE TABLE IF NOT EXISTS`, `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`, `ON CONFLICT DO NOTHING`).
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/db/modelagem.sql`
    - Criar: `src/main/resources/db/migracao_recrutador.sql`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test` (garantir que o schema em H2 sobe sem erros de sintaxe SQL) e revisar o script `migracao_recrutador.sql` para idempotência no PostgreSQL.

---

## Back-end com Testes

- [x] **2. Entidade `Recrutador`, contrato `RecrutadorRepository` e validações**
  - **Camada:** Back-end
  - **O que será feito:**
    - Criar a entidade `Recrutador` com vínculo obrigatório à `Empresa`.
    - Criar a interface `RecrutadorRepository` com contrato de CRUD (inserir, listar por empresa, buscar por id/e-mail, atualizar, remover).
    - Implementar em `Validador` as validações de e-mail, CPF (formato e dígitos verificadores) e data de nascimento (`dd-mm-aaaa`, idade ≥ 18 anos).
    - Criar testes unitários Spock cobrindo validações válidas e inválidas.
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Criar: `src/main/groovy/model/entity/Recrutador.groovy`
    - Criar: `src/main/groovy/model/repository/RecrutadorRepository.groovy`
    - Alterar: `src/main/groovy/service/Validador.groovy`
    - Criar: `src/test/groovy/RecrutadorValidacaoSpec.groovy`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test --tests RecrutadorValidacaoSpec` e `.\gradlew.bat test`.

- [x] **3. Persistência de Recrutador (`RecrutadorDao`) e testes de integração em H2**
  - **Camada:** Back-end
  - **O que será feito:**
    - Criar `RecrutadorDao` implementando `RecrutadorRepository` com SQL parametrizado via `ConexaoDB`.
    - Garantir persistência de `data_nascimento` (`DATE`) e remoção em cascata quando a empresa pai for excluída.
    - Criar testes de integração Spock com H2 em memória cobrindo inserir, listar por empresa, buscar, editar, remover e cascata.
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Criar: `src/main/groovy/db/dao/RecrutadorDao.groovy`
    - Criar: `src/test/groovy/RecrutadorDaoIntegracaoSpec.groovy`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test --tests RecrutadorDaoIntegracaoSpec` e `.\gradlew.bat test`.

- [x] **4. Serviço `RecrutadorService` e projeção segura (sem dados sensíveis)**
  - **Camada:** Back-end
  - **O que será feito:**
    - Criar `RecrutadorService` orquestrando validações (e-mail/CPF únicos, `dd-mm-aaaa` ≥ 18 anos, empresa existente, senha obrigatória no cadastro e preservada na edição se omitida).
    - Implementar projeção/DTO seguro que nunca expõe `cpf`, `dataNascimento` ou `senha` nas listagens/respostas.
    - Criar testes unitários Spock para regras de negócio e privacidade.
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Criar: `src/main/groovy/service/RecrutadorService.groovy`
    - Criar: `src/test/groovy/RecrutadorServiceSpec.groovy`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test --tests RecrutadorServiceSpec` e `.\gradlew.bat test`.

- [x] **5. Edição de vagas, rastreabilidade de match (`id_recrutador`) e lista de rejeitados**
  - **Camada:** Back-end
  - **O que será feito:**
    - Adicionar atualização de vaga em `VagaRepository`, `VagaDao` e `VagaService`, validando que o recrutador só edita vagas da própria empresa.
    - Atualizar `MatchRepository`, `MatchDao` e `MatchService` para: aceitar `idRecrutador` opcional na curtida/match, registrar rejeição em `rejeicao_empresa_candidato`, filtrar rejeitados da fila principal de `listarInteressados` e listar rejeitados mantendo o **Princípio do Anonimato** (`idCandidato`, `idVaga`, `nomeVaga`, `descricao`, `competencias`).
    - Atualizar `MatchRepositoryFake` e expandir `CicloMatchSpec` cobrindo múltiplos recrutadores na mesma empresa, rejeição, consulta de rejeitados e anonimato.
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/groovy/model/repository/VagaRepository.groovy`
    - Alterar: `src/main/groovy/db/dao/VagaDao.groovy`
    - Alterar: `src/main/groovy/service/VagaService.groovy`
    - Alterar: `src/main/groovy/model/repository/MatchRepository.groovy`
    - Alterar: `src/main/groovy/db/dao/MatchDao.groovy`
    - Alterar: `src/main/groovy/service/MatchService.groovy`
    - Alterar: `src/test/groovy/MatchRepositoryFake.groovy`
    - Alterar: `src/test/groovy/CicloMatchSpec.groovy`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test --tests CicloMatchSpec` e `.\gradlew.bat test`.

- [x] **6. Endpoints REST para Recrutador, edição de vagas e avaliação/rejeitados**
  - **Camada:** Back-end (API REST)
  - **O que será feito:**
    - Criar `RecrutadorController` (`/recrutadores`: `GET` por `idEmpresa`, `POST`, `PUT`, `DELETE`) usando `ApiServlet` e retornando apenas campos não sensíveis.
    - Atualizar `VagaController` para suportar `PUT /vagas` (validando vínculo com a empresa do recrutador, quando informado).
    - Criar `MatchController` (ou endpoints de avaliação: interessados anônimos, curtida com `idRecrutador`, rejeição, listagem anônima de rejeitados e matches por visão).
    - Registrar os novos servlets em `Main.groovy`.
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Criar: `src/main/groovy/controller/RecrutadorController.groovy`
    - Criar: `src/main/groovy/controller/MatchController.groovy`
    - Alterar: `src/main/groovy/controller/VagaController.groovy`
    - Alterar: `src/main/groovy/Main.groovy`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test`, subir a API com `.\gradlew.bat run` e validar `GET/POST/PUT/DELETE /recrutadores`, `PUT /vagas` e rotas de match/rejeitados via `curl.exe` ou Postman (conferindo ausência de `cpf`, `dataNascimento` e `senha` nas respostas).

- [x] **7. Fluxo CLI para Recrutador, gestão pela Empresa e lista de rejeitados**
  - **Camada:** Back-end (CLI)
  - **O que será feito:**
    - Atualizar `Menu.groovy`, `LinketinderController.groovy` e `MainTerminal.groovy` para incluir: CRUD de recrutadores pela empresa, atuação do recrutador (criar/editar vagas da sua empresa, avaliar candidatos interessados, consultar lista anônima de rejeitados e ver matches).
    - Garantir tratamento de erros via `AvisoErros` e leitura validada via `LeitorConsole`.
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/groovy/view/Menu.groovy`
    - Alterar: `src/main/groovy/controller/LinketinderController.groovy`
    - Alterar: `src/main/groovy/MainTerminal.groovy`
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Executar `.\gradlew.bat test` e testar manualmente os novos fluxos no `MainTerminal` (incluindo verificação do Princípio do Anonimato na avaliação e na lista de rejeitados).

---

## Front-end

- [x] **8. Modelo de dados, `localStorage` e login de Recrutador**
  - **Camada:** Front-end
  - **O que será feito:**
    - Atualizar `storage.ts` com a interface `Recrutador`, armazenamento de recrutadores e rejeições (`rejeicoes`) no `localStorage`, além de recrutadores fictícios iniciais para teste.
    - Atualizar `acesso.ts` para autenticar também recrutadores por e-mail/senha e redirecionar para `painel_recrutador.html`.
    - Compilar TypeScript para JS (`npx tsc`).
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/frontend/ts/storage.ts`
    - Alterar: `src/main/resources/frontend/ts/acesso.ts`
    - Atualizar (gerados pelo `tsc`): `src/main/resources/frontend/js/storage.js`, `src/main/resources/frontend/js/acesso.js` (e `.d.ts`/`.map`)
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Rodar `npx tsc` em `src/main/resources/frontend` sem erros de compilação e testar login na página `acessar.html` servida via `npx serve src/main/resources`.

- [x] **9. Gestão de Recrutadores e lista de rejeitados no Painel da Empresa**
  - **Camada:** Front-end
  - **O que será feito:**
    - Atualizar `painel_empresa.html`, `painel_empresa_style.css` e `painel_empresa.ts` para permitir à empresa autenticada cadastrar, listar, editar e remover seus recrutadores (validando e-mail, CPF e data `dd-mm-aaaa` ≥ 18 anos, sem exibir dados sensíveis na listagem).
    - Atualizar a ação "Não Interessado" para registrar na lista de rejeitados e exibir uma seção/aba de candidatos rejeitados (mantendo anonimato).
    - Compilar TypeScript para JS (`npx tsc`).
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Alterar: `src/main/resources/frontend/html/painel_empresa.html`
    - Alterar: `src/main/resources/frontend/css/painel_empresa_style.css`
    - Alterar: `src/main/resources/frontend/ts/painel_empresa.ts`
    - Atualizar (gerados pelo `tsc`): `src/main/resources/frontend/js/painel_empresa.js` (e `.d.ts`/`.map`)
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Rodar `npx tsc`, abrir `painel_empresa.html` via servidor local e testar CRUD de recrutadores, rejeição de candidato e consulta à lista de rejeitados.

- [ ] **10. Painel do Recrutador e validação final**
  - **Camada:** Front-end
  - **O que será feito:**
    - Criar `painel_recrutador.html`, `painel_recrutador_style.css` e `painel_recrutador.ts` permitindo ao recrutador logado: visualizar vagas da sua empresa, criar/editar vagas, avaliar candidatos interessados (curtir gerando match ou rejeitar), consultar rejeitados e ver matches (preservando estritamente o Princípio do Anonimato).
    - Compilar TypeScript para JS (`npx tsc`).
    - Atualizar `CHANGELOG.md` (seção `[Não lançado]`).
  - **Arquivos criados ou alterados:**
    - Criar: `src/main/resources/frontend/html/painel_recrutador.html`
    - Criar: `src/main/resources/frontend/css/painel_recrutador_style.css`
    - Criar: `src/main/resources/frontend/ts/painel_recrutador.ts`
    - Criar (gerados pelo `tsc`): `src/main/resources/frontend/js/painel_recrutador.js` (e `.d.ts`/`.map`)
    - Alterar: `CHANGELOG.md`
  - **Como validar:**
    - Rodar `npx tsc`, testar todo o fluxo do recrutador no navegador (`npx serve src/main/resources`), rodar `.\gradlew.bat test` e executar o checklist completo da Etapa 6 da skill (critérios de aceite, Princípio do Anonimato, `CHANGELOG.md` e regressão).
