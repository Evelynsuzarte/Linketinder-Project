# Especificação Técnica: Integração do Frontend e Backend

## 1. Contexto e Objetivo
Atualmente no Linketinder, o **back-end** expõe uma API RESTful em Groovy sobre Apache Tomcat embutido na porta `8080` (`Main.groovy`, com endpoints `/candidatos`, `/empresas`, `/vagas`, `/recrutadores` e `/matches`) conectada ao PostgreSQL, enquanto o **front-end** (`src/main/resources/frontend/`) opera de forma isolada e fictícia utilizando apenas o `localStorage` do navegador (`storage.ts`).

O objetivo desta feature é **incorporar o front-end ao back-end** via API REST, **sem uso de frameworks**, garantindo que:
- Toda comunicação com o back-end seja realizada exclusivamente pela camada de visualização (front-end) por meio de um cliente HTTP em TypeScript (explorando uma biblioteca de HTTP client como `typescript-http-client` ou abstração equivalente sem framework);
- No mínimo as operações de **cadastro** e **login** de usuários estejam integradas ao banco de dados via API;
- Os dados persistidos no banco PostgreSQL (incluindo senhas com hash `PBKDF2WithHmacSHA256`) sejam utilizados de ponta a ponta, substituindo a dependência exclusiva do `localStorage` como fonte de verdade;
- Sejam adicionados os suportes necessários na API REST para essa integração (como CORS, autenticação/login e endpoints de competências, quando aplicáveis).

---

## 2. Regras de Negócio

1. **Acesso exclusivo pela View (Front-end) via HTTP Client:**
   - O front-end não deve acessar diretamente banco de dados nem depender de frameworks SPA (React, Angular, Vue etc.).
   - As chamadas à API REST devem ser centralizadas em um módulo cliente HTTP em TypeScript (utilizando `typescript-http-client` ou wrapper compatível em ES Modules no navegador), enviando e recebendo JSON em UTF-8.
   - A API REST (`ApiServlet`) deve responder com cabeçalhos **CORS** adequados (incluindo `OPTIONS` preflight) caso o front-end seja servido em origem/porta distinta de `http://localhost:8080`, ou o front-end pode ser servido pelo próprio Tomcat/servidor local.

2. **Cadastro de Usuários (`Candidato` e `Empresa`):**
   - Na tela de cadastro (`cadastro.html` / `cadastro.ts`), o usuário seleciona o perfil (**Candidato** ou **Empresa**), preenche os campos do formulário e os submete.
   - O front-end encapsula os dados no payload JSON esperado pela API e envia `POST /candidatos` (obrigatórios: `cpf`, `nome`, `email`, `senha`) ou `POST /empresas` (obrigatórios: `cnpj`, `nome`, `email`, `senha`).
   - O controller no back-end valida os dados, aplica hash seguro na senha (`SegurancaSenha`), persiste no banco via `Service`/`DAO` e retorna **`201 Created`** com o `id` gerado.
   - Em caso de erro de validação ou duplicidade (ex.: e-mail/CPF/CNPJ já existente), a API retorna `400 Bad Request` com `{ "erro": "..." }` e o front-end exibe a mensagem ao usuário sem redirecionar.

3. **Login de Usuários (`Candidato`, `Empresa` e `Recrutador`):**
   - Na tela inicial de acesso (`index.html` / `acesso.ts`), o usuário informa `email` e `senha`.
   - O front-end envia os dados para o endpoint de autenticação no back-end (ex.: `POST /login`), que verifica o e-mail e valida a senha contra o hash `PBKDF2WithHmacSHA256` armazenado no banco.
   - Em caso de sucesso (`200 OK`), o back-end retorna os dados seguros da sessão (sem expor `senha`, `cpf` ou `dataNascimento` sensíveis), o front-end armazena o contexto do usuário autenticado para navegação entre páginas e redireciona para o painel correspondente (`painel_candidato.html`, `painel_empresa.html` ou `painel_recrutador.html`).
   - Em caso de credenciais inválidas (`401 Unauthorized` ou `400 Bad Request`), o front-end exibe feedback claro de erro.

4. **Competências e Fluxo Pós-Cadastro (Caso de Referência):**
   - O sistema deve permitir consultar competências já cadastradas (`GET /competencias`) e vincular/inserir competências do usuário (`POST /competencias` ou envio integrado ao perfil), retornando **`201 Created`** ao persistir novas competências no banco de dados.
   - Após o cadastro e definição de competências, o candidato pode prosseguir para a visualização de vagas (`painel_candidato.html`).

5. **Princípio do Anonimato:**
   - A integração entre front-end e back-end deve preservar estritamente o Princípio do Anonimato:
     - **Antes do match (visão da empresa):** respostas `GET` e telas do painel exibem apenas `descricao` e `competencias` do candidato (nunca `nome`, `sobrenome`, `cpf`, `email` ou `senha`).
     - **Antes do match (visão do recrutador):** funciona de forma idêntica à visão da empresa — o recrutador visualiza apenas `descricao` e `competencias` do candidato (tanto na avaliação quanto na lista de rejeitados), sem expor `nome`, `sobrenome`, `cpf`, `email` ou `senha`.
     - **Antes do match (visão do candidato):** na avaliação de vagas, exibem-se apenas `nome` da vaga, `competencias` e `local` (sem expor a empresa durante a avaliação).
     - **Após o match:** visão do candidato exibe vaga, empresa e local (sem expor dados individuais do recrutador); visão da empresa e do recrutador exibe vaga, nome, sobrenome e e-mail do candidato.
   - Dados sensíveis (`senha`, `cpf` de recrutador, `dataNascimento`) **nunca** devem trafegar em respostas `GET` ou de login para o front-end.

6. **Casos de Borda:**
   - **Back-end indisponível / erro de rede:** o front-end deve capturar falhas de conexão no HTTP client e informar ao usuário que não foi possível se comunicar com o servidor, sem quebrar a interface.
   - **JSON inválido ou campos obrigatórios ausentes:** a API retorna `400 Bad Request` com mensagem descritiva, e o front-end exibe o erro retornado.
   - **Sessão ausente ao acessar painéis:** se não houver usuário autenticado no contexto de sessão do navegador, o painel deve redirecionar para `index.html`.

---

## 3. Critérios de Aceite

- **Cenário 1: Cadastro de candidato com sucesso (Caso Sandubinha)**
  - **Dado** que o servidor back-end está em execução na porta `8080` e o usuário acessa a página de cadastro a partir da home page
  - **Quando** seleciona a opção de candidato, preenche os dados obrigatórios (`cpf`, `nome`, `email`, `senha` e demais campos) e submete o formulário
  - **Então** o front-end envia `POST /candidatos` em JSON via HTTP client, o back-end persiste o candidato no banco com senha em hash e retorna `201 Created` com o `id` criado, e o usuário é notificado do sucesso para prosseguir.

- **Cenário 2: Cadastro de empresa com sucesso**
  - **Dado** que o usuário está na tela de cadastro com o back-end ativo
  - **Quando** seleciona a opção de empresa, preenche `cnpj`, `nome`, `email`, `senha` e demais campos e submete o formulário
  - **Então** o front-end envia `POST /empresas` em JSON, o back-end persiste a empresa no banco e retorna `201 Created`, exibindo confirmação ao usuário.

- **Cenário 3: Cadastro com dados inválidos ou duplicados**
  - **Dado** que já existe um usuário cadastrado com o e-mail ou CPF/CNPJ informado, ou algum campo obrigatório está vazio
  - **Quando** o usuário submete o formulário de cadastro
  - **Então** o back-end retorna status `400 Bad Request` com mensagem de erro e o front-end exibe o erro na interface sem concluir o cadastro.

- **Cenário 4: Login com credenciais válidas**
  - **Dado** que existe um candidato, empresa ou recrutador cadastrado no banco de dados com senha armazenada em hash `PBKDF2WithHmacSHA256`
  - **Quando** o usuário informa `email` e `senha` corretos na home page (`index.html`) e submete o formulário
  - **Então** o front-end realiza requisição ao endpoint de login no back-end, recebe `200 OK` com os dados seguros do perfil (sem senha) e redireciona para o painel correspondente (`painel_candidato.html`, `painel_empresa.html` ou `painel_recrutador.html`).

- **Cenário 5: Login com credenciais inválidas**
  - **Dado** que o usuário está na tela de login (`index.html`)
  - **Quando** informa um e-mail inexistente ou senha incorreta
  - **Então** o back-end retorna erro de autenticação (`401 Unauthorized` ou `400 Bad Request`) e o front-end exibe alerta de credenciais inválidas sem redirecionar.

- **Cenário 6: Consulta e inserção de competências via API**
  - **Dado** que o candidato (ex.: Sandubinha) concluiu o cadastro e está na etapa/tela de competências
  - **Quando** a tela carrega e o candidato seleciona ou insere novas competências e confirma
  - **Então** o front-end consulta as competências existentes via `GET /competencias`, envia as competências selecionadas/inseridas via `POST /competencias` associadas ao usuário, recebe `201 Created` e permite prosseguir para a visualização de vagas.

- **Cenário 7: Preservação do Princípio do Anonimato na comunicação REST**
  - **Dado** que há vagas, candidatos e interações cadastradas no banco
  - **Quando** o front-end consome os endpoints da API para exibir vagas ao candidato ou candidatos interessados à empresa/recrutador antes do match
  - **Então** nenhum dado identificável proibido pelo Princípio do Anonimato é retornado pela API ou exibido na tela antes do match mútuo.

---

## 4. Impacto no que já existe
- **`src/main/groovy/controller/api/ApiServlet.groovy` e `Main.groovy`:** adição de suporte a CORS (`Access-Control-Allow-Origin`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers` e `doOptions`) e registro de novos servlets necessários (como `/login` e `/competencias`), sem quebrar os endpoints `/candidatos`, `/empresas`, `/vagas`, `/recrutadores` e `/matches`.
- **`src/main/groovy/service/` e `db/dao/`:** criação/exposição de serviço de autenticação (`LoginService` ou métodos de autenticação validando `SegurancaSenha.verificarSenha`) e endpoints/serviços de `Competencia` (`GET` e `POST`), mantendo as interfaces de `model/repository/` compatíveis.
- **`src/main/resources/frontend/package.json` e `tsconfig.json`:** eventual inclusão da biblioteca HTTP client (ex.: `typescript-http-client`) e ajuste de build para gerar módulos compatíveis com os `<script type="module">` dos HTMLs.
- **`src/main/resources/frontend/ts/` (`acesso.ts`, `cadastro.ts`, `storage.ts` e painéis):** substituição gradual das leituras/escritas em `localStorage` por chamadas HTTP via cliente TypeScript para o back-end, mantendo o `localStorage`/`sessionStorage` apenas para estado de sessão do usuário autenticado, se necessário.

---

## 5. Fora do Escopo
- **Não alterar o modo Terminal (CLI):** `MainTerminal.groovy`, `LinketinderController.groovy`, `Menu.groovy`, `LeitorConsole.groovy` e `AvisoErros.groovy` não devem ser modificados; a integração ocorre exclusivamente entre o front-end web e o back-end via API REST.
- **Não manipular ou alterar a lógica de negócio existente:** não modificar regras de negócio, fluxos de domínio, validações ou contratos já consolidados nos services/DAOs/repositórios; o foco é utilizar as APIs existentes para conectar e disponibilizar as funções para leitura e consumo no front-end.
- **Não adotar frameworks front-end:** não migrar o front-end para React, Angular, Vue, Next.js ou similares; manter HTML5, CSS3 e TypeScript puro compilado para ES Modules.
- **Não alterar regras de negócio de match no CLI ou versão legada `Main_semDB.groovy`.**
