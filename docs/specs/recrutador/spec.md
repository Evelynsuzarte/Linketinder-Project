# Especificação Técnica: Recrutador

## 1. Contexto e Objetivo
Atualmente no Linketinder, a entidade `Empresa` é quem avalia candidatos interessados, registra curtidas e visualiza matches. Na prática, essa atuação é feita por colaboradores da empresa — os **recrutadores**.

Esta feature introduz a entidade e o perfil **Recrutador**, vinculado a uma `Empresa`, permitindo:
- Gestão completa (CRUD) de recrutadores pela empresa;
- Criação e edição de vagas da empresa pelo recrutador;
- Análise de perfis interessados e registro de curtidas (que geram match em nome da empresa) ou rejeições (armazenadas em lista consultável de rejeitados);
- Aplicação em todas as camadas existentes: **Banco de Dados**, **API RESTful**, **CLI** e **Front-end Web**.

---

## 2. Decisões Tomadas
Consolidação das decisões alinhadas antes da implementação:

1. **Escopo de camadas:** A feature será aplicada no **Banco + API REST + CLI + Front-end (`localStorage`)**.
2. **Operações de CRUD:** Inclui **cadastrar, listar, editar e remover** recrutadores.
3. **Quem gerencia recrutadores:** A **empresa**. No front-end utiliza a autenticação já existente da empresa; no CLI e na API REST não há autenticação de sessão, sendo o vínculo informado/validado pela empresa (`idEmpresa` na API / nome da empresa no CLI).
4. **Vínculo com empresa:** Relacionamento `1:N` obrigatório (`id_empresa NOT NULL`). Ao remover a empresa, seus recrutadores são removidos em cascata (`ON DELETE CASCADE`).
5. **Escopo de vagas do recrutador:** O recrutador visualiza **todas as vagas da empresa** à qual pertence (sem filtro por área) e **pode criar, editar e remover vagas** dessa empresa, além de analisar vagas e perfis interessados.
6. **Curtida e match:** A curtida de um recrutador conta como curtida da empresa e gera o match imediatamente. O **primeiro recrutador** da empresa que curtir o candidato para aquela vaga gera o match, e o candidato sai da fila de avaliação dos demais recrutadores daquela empresa.
7. **Rastreabilidade:** As tabelas `curtida_empresa_candidato` e `match` passam a registrar `id_recrutador` (opcional/nullable para preservar compatibilidade com ações diretas da empresa) para identificar qual recrutador efetuou a curtida/match.
8. **Rejeição de candidato:** Quando o recrutador (ou empresa) recusa um candidato (`n`), a rejeição passa a ser gravada em uma **lista de rejeitados** (removendo-o da fila principal de avaliação), e essa lista pode ser **consultada** posteriormente.
9. **Atuação antes do match:** O recrutador atua de forma idêntica à empresa hoje, visualizando apenas `descricao` e `competencias` do candidato (sem criar novas tabelas de formação/experiência nesta feature).
10. **Visão do candidato após o match:** Mantida exatamente como antes (`vaga`, `empresa` e `local`), sem expor dados individuais do recrutador.
11. **Dados sensíveis:** `cpf`, `data_nascimento` e `senha` do recrutador **nunca** são expostos em respostas `GET` da API, listagens do CLI, telas de match ou painéis do front-end.
12. **Validações e senha:**
    - **E-mail:** formato válido e único entre recrutadores.
    - **CPF:** formato/dígitos válidos e único entre recrutadores.
    - **Data de nascimento:** formato `dd-mm-aaaa` e idade mínima de 18 anos.
    - **Senha:** sem regras de complexidade mínima, porém **armazenada com hash seguro (`PBKDF2WithHmacSHA256` + salt aleatório de 16 bytes, 65.536 iterações, formato `<saltBase64>:<hashBase64>`)** no back-end para recrutadores, candidatos e empresas (nunca em texto puro no banco e nunca exposta em retornos). As senhas originais de teste dos registros iniciais ficam documentadas em `AGENTS.md` e nesta spec para consulta.

---

## 3. Modelo de Dados

### Nova Entidade: `Recrutador` (`model/entity/Recrutador.groovy` e `storage.ts`)
- `id` (`int` / `number`)
- `nome` (`String`)
- `areaRecrutamento` (`String` — ex.: `TI`, `RH`, `Marketing`, `Segurança da Informação`)
- `descricao` (`String`)
- `email` (`String` — único)
- `cpf` (`String` — único, sensível)
- `dataNascimento` (`LocalDate` / `String` no formato `dd-mm-aaaa`, sensível)
- `senha` (`String` — sensível, nunca exposta)
- `empresa` / `idEmpresa` (referência obrigatória à `Empresa`)

### Alterações no Banco de Dados (`src/main/resources/db/modelagem.sql`)
1. **Nova tabela `recrutadores`:**
   - `id SERIAL PRIMARY KEY NOT NULL`
   - `id_empresa INT NOT NULL` (`FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE`)
   - `nome VARCHAR(100) NOT NULL`
   - `area_recrutamento VARCHAR(50) NOT NULL`
   - `descricao VARCHAR(255)`
   - `email VARCHAR(100) UNIQUE NOT NULL`
   - `cpf VARCHAR(14) UNIQUE NOT NULL`
   - `data_nascimento DATE NOT NULL`
   - `senha VARCHAR(255) NOT NULL`
2. **Nova tabela `rejeicao_empresa_candidato`:**
   - `id_empresa INT NOT NULL`
   - `id_candidato INT NOT NULL`
   - `id_vaga INT NOT NULL`
   - `id_recrutador INT` (nullable, referência a `recrutadores(id) ON DELETE SET NULL`)
   - `PRIMARY KEY (id_empresa, id_candidato, id_vaga)`
   - FKs para `empresas(id)`, `candidatos(id)` e `vagas(id)` com `ON DELETE CASCADE`.
3. **Ajustes em `curtida_empresa_candidato` e `match`:**
   - Adicionar coluna `id_recrutador INT` (nullable, `FOREIGN KEY (id_recrutador) REFERENCES recrutadores(id) ON DELETE SET NULL`).
4. **Script de migração incremental (`src/main/resources/db/migracao_recrutador.sql`):**
   - Para atualizar bancos PostgreSQL já existentes sem perda de dados, usar `CREATE TABLE IF NOT EXISTS` (`recrutadores`, `rejeicao_empresa_candidato`), `ALTER TABLE ... ADD COLUMN IF NOT EXISTS id_recrutador` em `curtida_empresa_candidato` e `match`, e `INSERT`s iniciais opcionais (`ON CONFLICT DO NOTHING`).

---

## 4. Regras de Negócio

1. **Gestão de Recrutadores:**
   - A empresa pode cadastrar, listar, editar e remover seus recrutadores.
   - Não é permitido cadastrar ou atualizar recrutador com `id_empresa` inexistente, e-mail já utilizado por outro recrutador ou CPF já utilizado por outro recrutador.
   - O campo `data_nascimento` deve obedecer ao formato `dd-mm-aaaa`, representar uma data calendárica válida e corresponder a idade ≥ 18 anos na data atual.
   - A senha é obrigatória no cadastro e deve ser convertida em hash `PBKDF2WithHmacSHA256` antes da persistência; na edição, caso omitida/vazia, mantém o hash atual.
   - **Senhas de teste iniciais (banco SQL):** todos os registros iniciais em `modelagem.sql` e `migracao_recrutador.sql` (`candidatos`, `empresas` e `recrutadores`) utilizam a senha original **`123456`** (armazenada como hash no banco).
2. **Gestão de Vagas pelo Recrutador:**
   - Um recrutador autenticado/identificado pode criar novas vagas vinculadas à sua empresa, editar e remover vagas pertencentes à sua empresa.
   - Um recrutador **não** pode criar, editar ou remover vagas de outra empresa.
3. **Avaliação, Curtida, Rejeição e Match:**
   - A fila de candidatos interessados exibida ao recrutador considera todas as vagas da sua empresa cujo candidato ainda **não** foi curtido (`curtida_empresa_candidato`) nem rejeitado (`rejeicao_empresa_candidato`) para aquela vaga.
   - **Curtida (`s`):** registra `curtida_empresa_candidato` e `match` com `id_empresa`, `id_candidato`, `id_vaga` e `id_recrutador`. Uma vez gerado o match, o par `(candidato, vaga)` não aparece mais para outros recrutadores da mesma empresa.
   - **Rejeição (`n`):** registra em `rejeicao_empresa_candidato` e remove o par `(candidato, vaga)` da fila principal de avaliação da empresa.
   - **Consulta de Rejeitados:** a empresa e seus recrutadores podem listar os candidatos rejeitados nas vagas da empresa (mantendo o anonimato do candidato!).
4. **Serialização e Privacidade:**
   - Nenhuma listagem ou resposta JSON de recrutador deve incluir `senha`, `cpf` ou `dataNascimento` (retornar DTO/mapa seguro com `id`, `idEmpresa`, `nome`, `areaRecrutamento`, `descricao`, `email`).

---

## 5. Princípio do Anonimato

O recrutador segue exatamente as mesmas regras de anonimato da empresa:

| Perspectiva | Antes do Match (Avaliação e Lista de Rejeitados) | Depois do Match |
|---|---|---|
| **Recrutador / Empresa** | Visualiza apenas **nome da vaga de interesse, descrição e competências** do candidato. Nome, sobrenome, CPF, e-mail e demais dados pessoais permanecem ocultos. | Visualiza **nome da vaga, nome e sobrenome do candidato e e-mail**. |
| **Candidato** | Visualiza apenas **nome da vaga, competências e local**. Empresa e recrutador permanecem ocultos. | Visualiza **nome da vaga, nome da empresa e local** (sem expor dados individuais do recrutador). |

**Dados pessoais sensíveis do Recrutador (`cpf`, `data_nascimento`, `senha`):** nunca são exibidos a candidatos, em endpoints públicos de listagem, no CLI ou no front-end.

---

## 6. Critérios de Aceite

- **Cenário 1: Cadastro de recrutador válido**
  - **Dado** uma empresa existente no banco
  - **Quando** a empresa cadastra um recrutador com nome, área, descrição, e-mail único válido, CPF único válido, data de nascimento `dd-mm-aaaa` (≥ 18 anos) e senha
  - **Então** o recrutador é salvo vinculado à empresa, a senha é armazenada como hash `<saltBase64>:<hashBase64>` (nunca em texto puro) e o retorno não expõe `senha`, `cpf` nem `data_nascimento`.

- **Cenário 2: Validações de cadastro/edição de recrutador**
  - **Dado** dados inválidos (e-mail duplicado/inválido, CPF duplicado/inválido, data fora de `dd-mm-aaaa` ou menor de 18 anos, ou empresa inexistente)
  - **Quando** for tentado o cadastro ou edição
  - **Então** a operação é rejeitada com erro claro (`400 Bad Request` na API / mensagem de erro no CLI e front-end) sem persistir alterações.

- **Cenário 3: Edição e remoção de recrutador**
  - **Dado** um recrutador vinculado a uma empresa
  - **Quando** a empresa edita seus dados ou o remove
  - **Então** as alterações são persistidas (ou o registro é removido), e ao remover a empresa pai seus recrutadores são removidos em cascata.

- **Cenário 4: Criação e edição de vagas pelo recrutador**
  - **Dado** um recrutador vinculado à Empresa A
  - **Quando** ele cria uma nova vaga ou edita uma vaga da Empresa A
  - **Então** a vaga é salva/atualizada com sucesso; se tentar editar vaga da Empresa B, a operação é bloqueada.

- **Cenário 5: Anonimato e Match pelo recrutador**
  - **Dado** um candidato que curtiu uma vaga da Empresa A
  - **Quando** um recrutador da Empresa A visualiza os interessados e curte o candidato
  - **Então** antes da curtida ele vê apenas descrição, competências e nome da vaga; após a curtida, o match é gravado com `id_recrutador` e o candidato sai da fila dos demais recrutadores da Empresa A.

- **Cenário 6: Rejeição e consulta à lista de rejeitados**
  - **Dado** um candidato interessado em uma vaga da Empresa A
  - **Quando** o recrutador recusa o candidato
  - **Então** o par `(candidato, vaga)` sai da fila de avaliação e passa a constar na lista de rejeitados da empresa, exibindo apenas dados anônimos (descrição, competências e vaga).

- **Cenário 7: Visão do candidato após o match**
  - **Dado** um match gerado por um recrutador da Empresa A
  - **Quando** o candidato consulta seus matches
  - **Então** ele visualiza apenas o nome da vaga, o nome da empresa e o local, sem exposição de dados do recrutador.

---

## 7. Impacto no que já existe
- **`src/main/resources/db/modelagem.sql` e `src/main/resources/db/migracao_recrutador.sql`:** inclusão das tabelas `recrutadores` e `rejeicao_empresa_candidato`, além da coluna `id_recrutador` em `curtida_empresa_candidato` e `match` (tanto no script completo quanto no script incremental de atualização de bancos existentes).
- **Camada `model` / `service` / `db/dao`:** novas interfaces/implementações para `Recrutador` e validação de CPF/e-mail/data (`dd-mm-aaaa`), além de métodos em `VagaRepository`/`VagaDao` (edição e remoção) e `MatchRepository`/`MatchDao` (rejeição, listagem de rejeitados e `id_recrutador`).
- **API REST (`Main.groovy` e `controller/`):** novo servlet `/recrutadores` (`GET`, `POST`, `PUT`, `DELETE`), suporte a `PUT /vagas` e `DELETE /vagas`, e endpoints/parâmetros para curtida/rejeição/rejeitados.
- **CLI (`MainTerminal.groovy`, `Menu.groovy`, `LinketinderController.groovy`):** novas opções de menu para gestão de recrutadores, atuação como recrutador (criar/editar/remover vagas, avaliar candidatos, ver rejeitados).
- **Front-end (`src/main/resources/frontend/`):** adaptação de `storage.ts`, `acesso.ts`, `painel_empresa.ts` (gerenciar recrutadores, remover vagas + lista de rejeitados), criação do painel do recrutador (`painel_recrutador.*`, com criação/edição/remoção de vagas) e criação da tela `criar-vaga.html` (`criar_vaga.ts`) para cadastro de novas vagas por empresa ou recrutador, mantendo a compilação via `tsc`.

---

## 8. Fora do Escopo
- Integração HTTP entre o front-end estático e a API REST (o front-end continua usando `localStorage`).
- Criação de tabelas/campos de formações acadêmicas e experiências profissionais para candidatos.
- Autenticação por token/sessão (JWT/cookies) na API REST ou login no CLI.
- Regras de complexidade mínima de senha.
