import { obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas, salvarUsuarios, salvarVagas } from "./storage.js";
// ==========================================
// USUÁRIO LOGADO (só candidato pode ver esta página)
// ==========================================
function voltarParaLogin() {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como candidato.");
}
const dadosLogado = localStorage.getItem('linketinder_usuario_logado');
const logado = dadosLogado ? JSON.parse(dadosLogado) : null;
if (logado === null || logado.tipo !== 'candidato') {
    voltarParaLogin();
}
// ==========================================
// LISTAS EM MEMÓRIA
// ==========================================
const candidatos = obterTodosCandidatos();
const empresas = obterTodasEmpresas();
const vagas = obterTodasVagas();
// Candidato atual, como está na lista (versão mais atualizada)
const candidatoAtual = candidatos.find(c => c.id === logado.id) ?? voltarParaLogin();
// Protege contra dados antigos do navegador, que não tinham este campo
if (!Array.isArray(candidatoAtual.vagasInteresse)) {
    candidatoAtual.vagasInteresse = [];
}
// Sincroniza com dados antigos: se o candidato consta em vaga.interessados,
// o id dessa vaga precisa estar em vagasInteresse
let precisaSalvar = false;
for (const vaga of vagas) {
    const constaNaVaga = vaga.interessados.some(c => c.id === candidatoAtual.id);
    if (constaNaVaga && !candidatoAtual.vagasInteresse.includes(vaga.id)) {
        candidatoAtual.vagasInteresse.push(vaga.id);
        precisaSalvar = true;
    }
}
if (precisaSalvar) {
    salvarUsuarios([...candidatos, ...empresas]);
}
console.log('Candidato:', candidatoAtual.nome, '| vagas no sistema:', vagas.length, '| interesses:', candidatoAtual.vagasInteresse);
// ==========================================
// ELEMENTOS DA PÁGINA
// ==========================================
const selectFiltro = document.querySelector('#filtro-vagas');
const corpoTabela = document.querySelector('#lista-vagas-disponiveis');
// ==========================================
// FUNÇÕES AUXILIARES
// ==========================================
// A lista vagasInteresse do candidato é a fonte de verdade
function estaInscrito(vaga) {
    return candidatoAtual.vagasInteresse.includes(vaga.id);
}
function filtrarVagas(filtro) {
    return filtro === 'cadastradas'
        ? vagas.filter(estaInscrito)
        : vagas.filter(v => !estaInscrito(v));
}
function criarCelula(classe, texto) {
    const td = document.createElement('td');
    td.className = classe;
    td.textContent = texto; // textContent evita injetar HTML vindo dos dados
    return td;
}
// ==========================================
// RENDERIZAÇÃO DA TABELA
// ==========================================
function renderizarVagas() {
    const filtro = selectFiltro.value;
    const lista = filtrarVagas(filtro);
    corpoTabela.innerHTML = '';
    if (lista.length === 0) {
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.colSpan = 4;
        td.textContent = filtro === 'cadastradas'
            ? 'Você ainda não se inscreveu em nenhuma vaga.'
            : 'Não há novas vagas disponíveis no momento.';
        tr.appendChild(td);
        corpoTabela.appendChild(tr);
        return;
    }
    for (const vaga of lista) {
        const tr = document.createElement('tr');
        tr.appendChild(criarCelula('vaga-titulo', vaga.nome));
        tr.appendChild(criarCelula('vaga-descricao', vaga.descricao));
        tr.appendChild(criarCelula('vaga-requisitos', vaga.competencias.join(', ')));
        const tdAcoes = document.createElement('td');
        tdAcoes.className = 'acoes-tabela';
        const botao = document.createElement('button');
        botao.dataset.id = String(vaga.id);
        // Em "cadastradas" aparece só o cancelar; em "novas", só o inscrever
        if (filtro === 'cadastradas') {
            botao.className = 'btn-cancelar';
            botao.dataset.acao = 'cancelar';
            botao.textContent = 'Cancelar interesse';
        }
        else {
            botao.className = 'btn-inscrever';
            botao.dataset.acao = 'candidatar';
            botao.textContent = 'Inscrever-se';
        }
        tdAcoes.appendChild(botao);
        tr.appendChild(tdAcoes);
        corpoTabela.appendChild(tr);
    }
}
// ==========================================
// AÇÕES: INSCREVER-SE / CANCELAR INTERESSE
// ==========================================
function salvarTudo() {
    salvarVagas(vagas);
    salvarUsuarios([...candidatos, ...empresas]);
}
function candidatar(idVaga) {
    const vaga = vagas.find(v => v.id === idVaga);
    if (!vaga || candidatoAtual.vagasInteresse.includes(idVaga))
        return;
    candidatoAtual.vagasInteresse.push(idVaga);
    if (!vaga.interessados.some(c => c.id === candidatoAtual.id)) {
        vaga.interessados.push(candidatoAtual);
    }
    salvarTudo();
}
function cancelarInteresse(idVaga) {
    const vaga = vagas.find(v => v.id === idVaga);
    if (!vaga)
        return;
    candidatoAtual.vagasInteresse = candidatoAtual.vagasInteresse.filter(id => id !== idVaga);
    vaga.interessados = vaga.interessados.filter(c => c.id !== candidatoAtual.id);
    salvarTudo();
}
// Um único ouvinte no tbody atende todos os botões (inclusive os criados depois)
corpoTabela.addEventListener('click', (event) => {
    const botao = event.target.closest('button');
    if (!botao)
        return;
    const idVaga = Number(botao.dataset.id);
    if (botao.dataset.acao === 'candidatar') {
        candidatar(idVaga);
    }
    else if (botao.dataset.acao === 'cancelar') {
        cancelarInteresse(idVaga);
    }
    renderizarVagas();
});
// ==========================================
// FILTRO
// ==========================================
selectFiltro.addEventListener('change', renderizarVagas);
renderizarVagas(); // desenha a lista ao abrir a página
//# sourceMappingURL=painel_candidato.js.map