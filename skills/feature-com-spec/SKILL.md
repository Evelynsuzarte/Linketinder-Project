---
name: feature-com-spec
description: Conduz o desenvolvimento de features no Linketinder seguindo o fluxo SDD (Spec-Driven Development) — contexto (AGENTS.md) → especificação (docs/specs/<nome-da-feature>/spec.md) → plano (docs/specs/<nome-da-feature>/plano.md) → implementação incremental com atualização do CHANGELOG.md → validação (incluindo Princípio do Anonimato).
---

# Skill: Feature com Spec — Linketinder (SDD)

## Objetivo

Implementar uma nova feature no **Linketinder** seguindo rigorosamente o fluxo **spec → plano → implementação → validação**, garantindo alinhamento de requisitos antes de escrever código, rastreabilidade por tarefas pequenas, atualização contínua do `CHANGELOG.md`, preservação das funcionalidades existentes (CLI, API RESTful e front-end) e respeito ao **Princípio do Anonimato**.

---

## Entradas

- **Nome e descrição da feature:** identificador em kebab-case (`<nome-da-feature>`), problema a resolver, comportamento esperado, camada(s) afetada(s) (CLI, API REST, banco de dados, front-end) e restrições.
- **Projeto / Repositório:** repositório Linketinder (com `AGENTS.md` e `CHANGELOG.md` na raiz).

---

## Regras Inegociáveis

1. **Não escrever código antes da spec aprovada:** nenhum arquivo de produção ou teste deve ser criado/alterado antes da aprovação explícita de `docs/specs/<nome-da-feature>/spec.md`.
2. **Uma tarefa por vez:** implementar apenas uma tarefa de `docs/specs/<nome-da-feature>/plano.md` por ciclo, aguardando validação antes de avançar para a próxima.
3. **Atualizar o `CHANGELOG.md` a cada tarefa:** toda tarefa concluída deve registrar suas mudanças na seção `[Não lançado]` do `CHANGELOG.md` (agrupadas em `Adicionado`, `Modificado`, `Corrigido` ou `Removido`).
4. **Um commit por tarefa:** cada tarefa concluída e validada deve gerar seu próprio commit com mensagem clara.
5. **Não alterar o que estiver fora do escopo:** preservar integralmente funcionalidades, contratos em `model/repository/` e fluxos existentes (CLI, API REST e front-end) que não façam parte da feature.
6. **Spec sempre à frente do código:** se durante a implementação for descoberta uma lacuna ou erro de requisito, atualizar `docs/specs/<nome-da-feature>/spec.md` primeiro e só depois ajustar o código.

---

## Passo a Passo (Etapas 2 a 6)

### Etapa 2: Contexto para o agente (`AGENTS.md`)

**O que gerar:**
Consulte `AGENTS.md` na raiz do projeto (e atualize-o se alguma mudança estrutural recente ainda não estiver refletida), garantindo que contenha:
- **Visão geral:** API RESTful (`Main.groovy`), CLI (`MainTerminal.groovy`) e front-end web estático (`src/main/resources/frontend/`).
- **Stack e estrutura:** Groovy, Gradle, PostgreSQL/H2, Tomcat embutido, Spock/JUnit e TypeScript/HTML/CSS.
- **Como executar e testar:** `.\gradlew.bat test` / `./gradlew test`, `.\gradlew.bat run` / `./gradlew run`, execução de `MainTerminal` e instruções do front-end.
- **Convenções:** camadas (`model/entity`, `model/repository`, `db/dao`, `service`, `controller`, `view`), nomenclatura e boas práticas (Clean Code / SOLID).
- **Domínio, regras de negócio e Princípio do Anonimato.**
- **Restrições:** não alterar funcionalidades existentes fora do escopo da feature.

**O que revisar:**
- Validar se caminhos de arquivos, comandos de execução/teste e convenções refletem fielmente o repositório atual.

---

### Etapa 3: Especificação (`docs/specs/<nome-da-feature>/spec.md`)

**O que gerar:**
1. **Antes de escrever o documento:** analise a descrição da feature frente ao `AGENTS.md` e ao código existente e **faça perguntas objetivas** ao usuário para esclarecer ambiguidades, casos de borda, impacto no banco (`src/main/resources/db/modelagem.sql`) e impacto no anonimato.
2. **Após as respostas:** crie `docs/specs/<nome-da-feature>/spec.md` usando o modelo da seção **Modelos**, contendo:
   - **Contexto e objetivo:** qual problema a feature resolve no Linketinder.
   - **Regras de negócio:** comportamento esperado, validações, regras de curtida/match e preservação do Princípio do Anonimato.
   - **Critérios de aceite:** cenários testáveis no formato **Dado / Quando / Então**.
   - **Impacto no que já existe:** módulos, endpoints, tabelas ou fluxos atuais (CLI, API, front-end) que podem ser afetados.
   - **Fora do escopo:** o que esta feature explicitamente não fará.

**O que revisar:**
- Verificar se não há contradições com o domínio do Linketinder nem exposição indevida de dados antes do match.
- Ajustar a spec até aprovação explícita e **fazer o commit da spec aprovada** antes de iniciar o plano/código.

---

### Etapa 4: Plano de implementação (`docs/specs/<nome-da-feature>/plano.md`)

**O que gerar:**
Leia `docs/specs/<nome-da-feature>/spec.md` e `AGENTS.md` e proponha um plano dividido em **tarefas pequenas e numeradas** (com checkboxes `- [ ]` em `docs/specs/<nome-da-feature>/plano.md`), indicando para cada tarefa:
- **O que será feito** (incluindo atualização do `CHANGELOG.md`).
- **Quais arquivos serão criados ou alterados** (incluindo `CHANGELOG.md`).
- **Como validar** que a tarefa está pronta (ex.: `.\gradlew.bat test` com Spock/H2 e/ou conferência manual).

**O que revisar:**
- Avaliar a ordem lógica das tarefas (entidades/repositórios/DAOs/serviços antes de controllers/UI, testes Spock contemplados).
- Verificar o tamanho de cada tarefa; se alguma estiver grande demais ou misturar responsabilidades, dividi-la antes de aprovar e gerar `docs/specs/<nome-da-feature>/plano.md`.

---

### Etapa 5: Implementação incremental

**O que gerar:**
Implemente **uma tarefa por vez**, sempre referenciando `docs/specs/<nome-da-feature>/spec.md` e `docs/specs/<nome-da-feature>/plano.md`. A cada tarefa:
1. Aplicar somente as alterações previstas na tarefa atual.
2. Atualizar o `CHANGELOG.md` na seção `[Não lançado]` (em `Adicionado`, `Modificado`, `Corrigido` ou `Removido`).
3. Apresentar o resumo das mudanças / diff gerado e sugerir uma mensagem de commit clara.
4. Executar a compilação e os testes aplicáveis (`.\gradlew.bat test` / `./gradlew test`).
5. Aguardar a revisão do usuário antes de marcar `- [x]` em `docs/specs/<nome-da-feature>/plano.md` e seguir para a próxima tarefa.

**O que revisar (a cada tarefa):**
- Ler o diff gerado e entender exatamente o que mudou.
- Confirmar que o `CHANGELOG.md` foi atualizado corretamente no padrão Keep a Changelog.
- Rodar a aplicação e os testes.
- Marcar a tarefa como concluída (`- [x]`) em `docs/specs/<nome-da-feature>/plano.md` e fazer **um commit por tarefa** com mensagem clara.

---

### Etapa 6: Validação

**O que gerar / executar:**
1. **Testes automatizados (Spock + H2):** garantir que existam testes Spock cobrindo a feature e executar `.\gradlew.bat test` (ou `./gradlew test`) verificando que toda a suíte passa sem falhas nem assertions relaxadas.
2. **Checklist de validação do Linketinder:**
   - [ ] **Auditoria de critérios de aceite:** todos os cenários **Dado / Quando / Então** de `docs/specs/<nome-da-feature>/spec.md` foram atendidos (distinguindo o que foi validado por teste automatizado do que exige verificação manual).
   - [ ] **Princípio do Anonimato:**
     - Antes do match, a **empresa** visualiza apenas a **descrição e as competências** do candidato (sem expor nome, sobrenome, CPF ou e-mail).
     - Antes do match, o **candidato** visualiza apenas o **nome da vaga, competências e local** (sem expor a empresa durante a avaliação).
     - Após o match, os dados revelados seguem exatamente o permitido por visão (candidato: vaga, empresa e local; empresa: vaga, nome, sobrenome e e-mail do candidato).
   - [ ] **Atualização do `CHANGELOG.md`:** todas as tarefas concluídas estão registradas na seção `[Não lançado]` do `CHANGELOG.md`.
   - [ ] **Regressão manual e escopo:** funcionalidades existentes (CLI, API REST e front-end) não foram quebradas nem alteradas fora do escopo, e todas as tarefas de `docs/specs/<nome-da-feature>/plano.md` estão marcadas como `[x]`.

---

## Modelos

### Modelo esperado de `docs/specs/<nome-da-feature>/spec.md`

```markdown
# Especificação Técnica: <Nome da Feature>

## 1. Contexto e Objetivo
Descreva o problema atual e o objetivo da feature no Linketinder.

## 2. Regras de Negócio
1. Regra principal...
2. Princípio do Anonimato: como a feature preserva os dados ocultos antes do match e o que revela após o match.
3. Casos de borda:
   - Cenário de erro / indisponibilidade / entrada inválida...

## 3. Critérios de Aceite
- **Cenário 1: <Título>**
  - **Dado** <estado inicial>
  - **Quando** <ação executada>
  - **Então** <resultado esperado>

## 4. Impacto no que já existe
- `<arquivo ou módulo>`: o que muda e cuidados para não quebrar fluxos atuais.

## 5. Fora do Escopo
- Itens que não serão implementados nesta feature.
```

### Modelo esperado de `docs/specs/<nome-da-feature>/plano.md`

```markdown
# Plano de Implementação

Este plano divide a implementação de [spec.md](spec.md) em tarefas pequenas e numeradas, respeitando [AGENTS.md](../../../AGENTS.md).

- [ ] **1. <Título curto da tarefa>**
  - **O que será feito:** <descrição objetiva>
  - **Arquivos criados ou alterados:**
    - Criar/Alterar: `<caminho/do/arquivo>`
    - Alterar: `CHANGELOG.md` (seção `[Não lançado]`)
  - **Como validar:**
    - <comando de teste automatizado (ex.: `.\gradlew.bat test`) e/ou passos de conferência manual>

- [ ] **2. <Título curto da tarefa>**
  - **O que será feito:** <descrição objetiva>
  - **Arquivos criados ou alterados:**
    - Criar/Alterar: `<caminho/do/arquivo>`
    - Alterar: `CHANGELOG.md` (seção `[Não lançado]`)
  - **Como validar:**
    - <comando de teste automatizado e/ou passos de conferência manual>
```
