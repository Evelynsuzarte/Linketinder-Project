# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

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

### Modificado
- Instruções de teste da API RESTful adicionadas em `AGENTS.md`.
- Modelagem completa em `src/main/resources/db/modelagem.sql` atualizada para incluir `recrutadores`, `rejeicao_empresa_candidato` e `id_recrutador` em `curtida_empresa_candidato` e `match`.
- `Validador` atualizado com validações de e-mail, CPF e data de nascimento (`dd-mm-aaaa` com idade mínima de 18 anos).
- `VagaController` atualizado com filtro `?idEmpresa` e suporte a `PUT /vagas`; novos servlets registrados em `Main.groovy`.
- CLI (`Menu`, `LinketinderController` e `MainTerminal`) atualizado com gestão de recrutadores pela empresa, criação/edição de vagas e avaliação de candidatos pelo recrutador, e consulta anônima de candidatos rejeitados.
- Front-end (`storage.ts` e `acesso.ts`) atualizado com modelo e persistência de `Recrutador` e `Rejeicao` no `localStorage`, além de login de recrutador redirecionando para `painel_recrutador.html`.
- Painel da Empresa (`painel_empresa.html`, `painel_empresa_style.css`, `painel_empresa.ts`) atualizado com gestão de recrutadores (CRUD com validações) e lista anônima de candidatos rejeitados.

### Corrigido

### Removido
