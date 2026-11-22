# Prompts Usados — Integração Frontend e Backend

Registro cronológico dos prompts utilizados durante o desenvolvimento da feature de integração entre o front-end e o back-end do Linketinder seguindo o fluxo da skill `feature-com-spec`.

1. **Alinhamento inicial de contexto (`AGENTS.md`):**
   > `agora ou trabalhar na integração de front e backend, leia o arquivo agent para poder continuar`

2. **Criação da especificação técnica e abertura do `CHANGELOG.md`:**
   > `utilizando a @[skills] , crie um docs/specs/<nome-da-feature>/ e atualizar o CHANGELOG.md par a sessão de [Integração do frontend e backend], os requisitos são: O acesso ao backend será realizado exclusivamente pela view da aplicação (frontend). Então, é interessante explorar alguma biblioteca de HTTP client; Integrar o frontend do linketinder ao backend sem uso de framework; Nó mínimo operações de cadastro e login de usuários devem estar integradas; caso de exemplo: Sandubinha realiza cadastro, insere competências e prossegue para visualização de vagas... Com a ajuda do agente, escreva a especificação da feature antes de qualquer código. Ela deve conter: Contexto e objetivo, Critérios de aceite (Dado / Quando / Então) e Fora do escopo (nao mexer no terminal, apenas conectar o front e back por meio da api).`

3. **Confirmação da arquitetura sem framework:**
   > `uma pergunta, ele está sendo feito sem framework?`

4. **Refinamento da spec (Fora de escopo e Princípio do Anonimato do Recrutador):**
   > `Adicione também fora de escopo: manipular a lógica de qualquer coisa, apenas utilizar as APIs existentes para poder manipular as funções para que sejam lidas no frontend. Coloque também na regra do anonimato também a visão do recrutador, que funciona assim como igual a da empresa.`

5. **Aprovação da spec para geração do `plano.md`:**
   > `sim`

6. **Aprovação do plano e início da implementação incremental (uma tarefa por vez com confirmação e commit):**
   > `sim, e lembre sempre de marcar como concluida e commitar depois que eu confirmar`

7. **Confirmação da Tarefa 1 (CORS) e avanço para Tarefa 2 (`POST /login` e sanitização de `GET`s):**
   > `sim`

8. **Confirmação da Tarefa 2 e avanço para Tarefa 3 (`GET`/`POST /competencias`):**
   > `sim`

9. **Confirmação da Tarefa 3 e avanço para Tarefa 4 (`GET /vagas?idCandidato=` anônimo):**
   > `sim`

10. **Confirmação da Tarefa 4 e avanço para Tarefa 5 (camada HTTP Client em TypeScript):**
    > `sim`

11. **Ajuste no comando de compilação TypeScript a partir da raiz e aprovação para seguir para a Tarefa 6 (Login em `acesso.ts`):**
    > `<erro MODULE_NOT_FOUND ao executar tsc da raiz>` → `pode seguir`

12. **Confirmação da Tarefa 6 e avanço para Tarefa 7 (Cadastro e Competências em `novo_cadastro.html` / `cadastro.ts`):**
    > `sim`

13. **Confirmação da Tarefa 8 (Painéis e Criação de Vagas) e encerramento da suíte:**
    > `sim`

14. **Geração de texto para Pull Request:**
    > `gere o texto para a pull request`

15. **Melhoria de acesso direto ao front-end via Tomcat na raiz `/`:**
    > `quando sobe o servidor num da pra só ir direto pra o link sem ter q acessar o index?` → `faça pra eu testar` → `faça o commit e atualize em algum documento qcaso necessario`

16. **Geração deste histórico de prompts e do roteiro de teste manual:**
    > `agora crie um arquivo de prompts usados e um arquivo com um script q posso seguir para o teste manual com todos os cenários e também com os acessos necessarios, me mostre antes de commitar`
