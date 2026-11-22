# Roteiro de Teste Manual — Integração Frontend e Backend

Este roteiro cobre todos os cenários da [spec.md](spec.md) e o ciclo completo entre **Front-end**, **API REST** e **PostgreSQL**.

---

## 1. Pré-requisitos e Inicialização

1. **Banco de dados PostgreSQL:**
   - Certifique-se de que o banco `linketinder` está criado e populado com:
     - `src/main/resources/db/modelagem.sql`
     - `src/main/resources/db/migracao_recrutador.sql` *(caso o banco seja anterior à feature de recrutador)*
   - Variáveis de ambiente opcionais (se diferentes do padrão): `DB_URL`, `DB_USER` (padrão `postgres`), `DB_PASSWORD` (padrão `1234`).

2. **Testes automatizados e compilação (opcional antes do teste manual):**
   ```powershell
   .\gradlew.bat test
   node src/main/resources/frontend/ts/node_modules/typescript/bin/tsc -p src/main/resources/frontend/tsconfig.json
   ```

3. **Subir a aplicação:**
   ```powershell
   .\gradlew.bat run
   ```
   - Aguarde a mensagem: `Servidor Tomcat iniciado em: http://localhost:8080`.
   - Acesse no navegador: **`http://localhost:8080/`** *(redireciona automaticamente para `/frontend/html/index.html`)*.

---

## 2. Acessos Iniciais do Banco SQL (`modelagem.sql`)

Todos os usuários iniciais do banco utilizam a senha **`123456`** (armazenada com hash `PBKDF2WithHmacSHA256`).

### Candidatos
- `sandubinha@email.com` — senha `123456` (Sandubinha Silva)
- `carlos@email.com` — senha `123456` (Carlos Santos)
- `mariana@email.com` — senha `123456` (Mariana Oliveira)
- `joao@email.com` — senha `123456` (Joao Costa)
- `ana@email.com` — senha `123456` (Ana Souza)

### Empresas
- `contato@pastelsoft.com` — senha `123456` (Pastelsoft, id `1`)
- `contato@techbahia.com` — senha `123456` (Tech Bahia, id `2`)
- `contato@datasolutions.com` — senha `123456` (Data Solutions, id `3`)
- `contato@codemais.com` — senha `123456` (CodeMais, id `4`)
- `contato@inovatech.com` — senha `123456` (InovaTech, id `5`)

### Recrutadores
- `fernanda@pastelsoft.com` — senha `123456` (Fernanda Lima — empresa Pastelsoft)
- `rafael@techbahia.com` — senha `123456` (Rafael Mendes — empresa Tech Bahia)

---

## 3. Script de Teste Manual (Todos os Cenários)

> **Dica:** mantenha o **DevTools (`F12` → aba Network)** aberto para conferir os status HTTP (`200`, `201`, `400`, `401`) e os payloads JSON sem exposição de dados sensíveis.

### Cenário 1 — Cadastro de Candidato + Competências (Caso Sandubinha)
1. Em `http://localhost:8080/`, clique em **Cadastrar / Criar conta** (`novo_cadastro.html`).
2. Selecione o perfil **Candidato**.
3. Preencha dados inéditos (ex.: Nome `Novo Candidato`, Email `novo.candidato@email.com`, CPF `88888888888`, Idade `25`, CEP `40000000`, Estado `BA`, Descrição `Dev Fullstack`, Senha `123456`) e clique em **Cadastrar**.
4. **Resultado esperado:**
   - Na aba Network: `POST /candidatos` retorna **`201 Created`** com `{ "mensagem": "...", "id": <novoId> }`.
   - Exibe alerta de sucesso e abre a seção **Suas Competências**.
   - `GET /competencias` retorna **`200 OK`** listando as competências já existentes no banco em checkboxes.
5. Marque algumas competências existentes (ex.: `Groovy`, `SQL`) e digite uma nova no campo manual (ex.: `TypeScript, Docker`), depois clique em **Salvar competências e ver vagas**.
6. **Resultado esperado:**
   - `POST /competencias` retorna **`201 Created`**.
   - Redireciona para `painel_candidato.html`.

### Cenário 2 — Cadastro de Empresa
1. Acesse `novo_cadastro.html` e selecione **Empresa**.
2. Preencha dados inéditos (ex.: Nome `Empresa Teste`, Email `rh@empresateste.com`, CNPJ `99999999000199`, País `Brasil`, Estado `SP`, CEP `01001000`, Descrição `Tecnologia`, Senha `123456`) e clique em **Cadastrar**.
3. **Resultado esperado:**
   - `POST /empresas` retorna **`201 Created`**.
   - Alerta de sucesso e formulário limpo.

### Cenário 3 — Cadastro com Dados Inválidos ou Duplicados
1. Ainda em `novo_cadastro.html`, tente cadastrar um candidato com um e-mail já existente (ex.: `sandubinha@email.com`) ou deixando CPF/Nome vazios.
2. **Resultado esperado:**
   - O front-end bloqueia campos obrigatórios vazios antes do envio ou o back-end retorna **`400 Bad Request`** com `{ "erro": "..." }`, exibido em alerta sem concluir o cadastro.

### Cenário 4 — Login com Credenciais Inválidas
1. Acesse a tela de login (`acessar.html` pela home).
2. Informe `sandubinha@email.com` e senha incorreta `errada`, depois clique em entrar.
3. **Resultado esperado:**
   - `POST /login` retorna **`401 Unauthorized`** com `{ "erro": "E-mail ou senha inválidos." }`.
   - Alerta exibido e usuário permanece na tela de login.
4. Repita com e-mail inexistente `naoexiste@email.com` → mesmo comportamento (`401`).

### Cenário 5 — Login Válido (Candidato) e Avaliação Anônima de Vagas
1. Faça login com `novo.candidato@email.com` (ou `sandubinha@email.com`) e senha `123456`.
2. **Resultado esperado:**
   - `POST /login` retorna **`200 OK`** sem campo `senha` nem `cpf` no JSON.
   - Redireciona para `painel_candidato.html`.
   - `GET /vagas?idCandidato=<id>` retorna **`200 OK`** apenas com `id`, `nome`, `descricao`, `local` e `competencias` (**sem expor o nome da empresa**).
3. Clique em **Inscrever-se** em uma das vagas disponíveis.
4. **Resultado esperado:**
   - `POST /matches` com `acao: "curtirVaga"` retorna **`201 Created`**.
   - A vaga sai da lista de novas vagas disponíveis.

### Cenário 6 — Login Válido (Empresa), Anonimato, CRUD de Recrutadores e Gráfico
1. Faça logoff e entre como empresa: `contato@pastelsoft.com` / senha `123456`.
2. **Resultado esperado:**
   - `POST /login` retorna **`200 OK`** (sem `senha`) e redireciona para `painel_empresa.html`.
   - O gráfico Chart.js é renderizado com as competências das vagas da Pastelsoft.
   - Na tabela de **Candidatos Aplicados**, confira o **Princípio do Anonimato**: aparecem apenas `Candidato #<id>`, descrição e competências (sem nome, sobrenome, CPF ou e-mail).
3. **CRUD de Recrutadores:**
   - Cadastre um novo recrutador no formulário (ex.: Nome `Lucas Andrade`, Área `TI`, Descrição `Tech Recruiter`, Email `lucas@pastelsoft.com`, CPF válido, Data de Nascimento `10-05-1995`, Senha `123456`).
   - Confira `POST /recrutadores` (`201 Created`) e a listagem em `GET /recrutadores?idEmpresa=1` (sem `cpf`, `dataNascimento` ou `senha`).
   - Teste **Editar** e **Remover** no recrutador criado (`PUT` / `DELETE /recrutadores`).
4. **Criar Vaga:**
   - Acesse `criar-vaga.html` pelo painel, crie uma vaga (`POST /vagas` → `201 Created`) e confirme que retorna ao painel com a vaga listada.

### Cenário 7 — Login Válido (Recrutador), Rejeição, Match e Revelação Pós-Match
1. Faça logoff e entre como recrutador: `fernanda@pastelsoft.com` / senha `123456`.
2. **Resultado esperado:**
   - `POST /login` retorna **`200 OK`** (sem `senha`, `cpf` ou `dataNascimento`) e redireciona para `painel_recrutador.html`.
   - Exibe vagas da empresa Pastelsoft (`GET /vagas?idEmpresa=1`).
   - Candidatos interessados aparecem **anônimos** (mesma regra da empresa).
3. **Testar criação/edição/remoção de vaga pelo recrutador:**
   - Crie uma vaga pelo formulário do painel (`POST /vagas`), edite-a (`PUT /vagas` com `idRecrutador`) e remova uma vaga de teste (`DELETE /vagas`).
4. **Testar Rejeição (`❌ Não Interessado`):**
   - Clique em **Não Interessado** em um candidato pendente (`POST /matches` com `acao: "rejeitarCandidato"`).
   - Confira que ele sai da fila e aparece na tabela de **Candidatos Rejeitados** mantendo o anonimato (`Candidato #<id>`, descrição, competências e vaga).
5. **Testar Match (`❤️ Match`):**
   - Clique em **Match** em um candidato pendente (`POST /matches` com `acao: "curtirCandidato"`).
   - Confira na tabela de **Matches Confirmados** que agora aparecem: nome da vaga, **nome e sobrenome do candidato** e **e-mail**.
6. **Confirmar visão pós-match do candidato:**
   - Faça logoff, entre novamente com o candidato que recebeu o match e mude o filtro em `painel_candidato.html` para **Minhas vagas inscritas**.
   - Confira que o match aparece exibindo: **nome da vaga, nome da empresa e local** (sem expor dados individuais do recrutador).

### Cenário 8 — Proteção de Sessão e Erro de Conexão
1. Limpe o `localStorage` (`linketinder_usuario_logado`) no DevTools e tente acessar `http://localhost:8080/frontend/html/painel_empresa.html` diretamente.
   - **Resultado esperado:** redireciona automaticamente para `acessar.html`.
2. Pare o servidor (`Ctrl+C` no terminal do Gradle) e tente fazer login em `acessar.html`.
   - **Resultado esperado:** exibe alerta amigável informando que não foi possível conectar ao servidor.
