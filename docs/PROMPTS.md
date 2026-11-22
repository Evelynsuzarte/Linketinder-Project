# Registro de Prompts — Feature Recrutador (SDD)

Este documento registra os prompts e etapas conduzidas no desenvolvimento orientado por especificação (**Spec-Driven Development**) da feature **Recrutador** no **Linketinder**.

---

## 1. Contexto Inicial (`AGENTS.md`)
- **Objetivo:** Explorar o repositório e gerar o arquivo `AGENTS.md` na raiz do projeto com visão geral, stack e estrutura, comandos de execução e testes (incluindo front-end e API REST), convenções, restrições, domínio, regras de negócio, Princípio do Anonimato e banco de dados.
- **Resultado:** Criação e atualização de `AGENTS.md`.

## 2. Adaptação da Skill SDD e `CHANGELOG.md`
- **Objetivo:** Adaptar `skills/feature-com-spec/SKILL.md` para o Linketinder (specs em `docs/specs/<nome-da-feature>/`, validação do Princípio do Anonimato no checklist e atualização obrigatória do `CHANGELOG.md` a cada tarefa) e criar `CHANGELOG.md` no padrão Keep a Changelog 1.1.0.
- **Resultado:** Atualização de `skills/feature-com-spec/SKILL.md` e criação de `CHANGELOG.md`.

## 3. Especificação Técnica (`docs/specs/recrutador/spec.md`)
- **Objetivo:** Levantar dúvidas técnicas e de negócio antes de escrever a spec, consolidar decisões (CRUD de recrutadores pela empresa, atuação do recrutador em todas as vagas da empresa, criação/edição/remoção de vagas, curtida gerando match com `id_recrutador`, lista consultável de rejeitados, preservação do anonimato, validações de e-mail/CPF/data `dd-mm-aaaa` ≥ 18 anos e script incremental `migracao_recrutador.sql`).
- **Resultado:** Criação de `docs/specs/recrutador/spec.md`.

## 4. Plano de Implementação (`docs/specs/recrutador/plano.md`)
- **Objetivo:** Quebrar a spec em tarefas pequenas e numeradas na ordem **banco de dados → back-end com testes → front-end**, cada uma com arquivos envolvidos, atualização do `CHANGELOG.md`, validação e commit individual após revisão.
- **Resultado:** Criação de `docs/specs/recrutador/plano.md` (Tarefas 1 a 10, posteriormente expandidas para 11, 12 e 13).

## 5. Implementação Incremental e Evoluções
- **Tarefas 1 a 10:** Modelagem e migração SQL (`modelagem.sql`, `migracao_recrutador.sql`), entidade e repositório (`Recrutador`, `RecrutadorRepository`, `Validador`), `RecrutadorDao` + H2, `RecrutadorService` com projeção segura, `VagaService`/`MatchDao`/`MatchService`, API REST (`/recrutadores`, `/matches`, `PUT /vagas`), CLI (`Menu`, `LinketinderController`, `MainTerminal`) e Front-end (`storage.ts`, `acesso.ts`, `painel_empresa.*`, `painel_recrutador.*`).
- **Tarefa 11 (Criação de vaga no painel da empresa):** Criação de `criar-vaga.html` e `criar_vaga.ts` para o botão `+ Criar Nova Vaga`, com atualização da spec e do plano.
- **Tarefa 12 (Remoção de vagas):** Implementação da exclusão de vagas no back-end (`VagaRepository`, `VagaDao`, `VagaService`), API (`DELETE /vagas`), CLI e front-end (`painel_empresa.ts`, `painel_recrutador.ts`), com atualização da spec e do plano.
- **Tarefa 13 (Segurança de senhas):** Substituição do armazenamento em texto puro por hash `PBKDF2WithHmacSHA256` + salt aleatório de 16 bytes (`SegurancaSenha`, `SegurancaSenhaSpec`) para `Recrutador`, `Candidato` e `Empresa`, ampliação das colunas `senha` para `VARCHAR(255)` e documentação das senhas iniciais de teste em `AGENTS.md` e `spec.md`.

## 6. Migração do Banco Local e Pull Request
- **Objetivo:** Executar `src/main/resources/db/migracao_recrutador.sql` no PostgreSQL local (`linketinder`) e gerar a descrição do Pull Request vinculada à issue (`Closes #1`), detalhando alterações, testes, critérios de aceite e garantia do Princípio do Anonimato.
