import { HttpError, atualizarRecrutadorApi, cadastrarRecrutadorApi, curtirCandidatoApi, listarInteressadosApi, listarRecrutadoresApi, listarRejeitadosApi, listarVagasPorEmpresaApi, obterSessao, rejeitarCandidatoApi, removerRecrutadorApi, removerVagaApi } from "./api.js";
function voltarParaLogin() {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como empresa.");
}
const logado = obterSessao();
if (logado === null || logado.tipo !== 'empresa') {
    voltarParaLogin();
}
const empresaAtual = logado;
const listaVagas = document.querySelector('#lista-vagas');
const canvasGrafico = document.querySelector('#graficoCompetencias');
const listaRecrutadores = document.querySelector('#lista-recrutadores');
const listaRejeitados = document.querySelector('#lista-rejeitados');
const formRecrutador = document.querySelector('#form-recrutador');
const inputRecrutadorId = document.querySelector('#recrutador-id');
const inputRecrutadorNome = document.querySelector('#recrutador-nome');
const inputRecrutadorArea = document.querySelector('#recrutador-area');
const inputRecrutadorDescricao = document.querySelector('#recrutador-descricao');
const inputRecrutadorEmail = document.querySelector('#recrutador-email');
const inputRecrutadorCpf = document.querySelector('#recrutador-cpf');
const inputRecrutadorNascimento = document.querySelector('#recrutador-nascimento');
const inputRecrutadorSenha = document.querySelector('#recrutador-senha');
const btnCancelarEdicao = document.querySelector('#btn-cancelar-edicao');
let vagas = [];
let interessados = [];
let rejeitados = [];
let recrutadores = [];
let grafico = null;
function criarElemento(tag, classe, texto) {
    const el = document.createElement(tag);
    if (classe)
        el.className = classe;
    if (texto !== undefined)
        el.textContent = texto;
    return el;
}
function desenharGrafico() {
    if (typeof Chart === 'undefined') {
        console.warn('Chart.js não foi carregado. Confira a tag <script> do HTML.');
        return;
    }
    const contagem = new Map();
    for (const vaga of vagas) {
        const interessadosDaVaga = interessados.filter(i => i.idVaga === vaga.id);
        for (const competencia of (vaga.competencias ?? [])) {
            if (!contagem.has(competencia)) {
                contagem.set(competencia, new Set());
            }
            const conjunto = contagem.get(competencia);
            interessadosDaVaga.forEach(i => conjunto.add(i.idCandidato));
        }
    }
    const dados = Array.from(contagem.entries())
        .map(([nome, conjunto]) => ({ nome, total: conjunto.size }))
        .sort((a, b) => b.total - a.total || a.nome.localeCompare(b.nome));
    if (grafico) {
        grafico.destroy();
    }
    grafico = new Chart(canvasGrafico, {
        type: 'bar',
        data: {
            labels: dados.map(d => d.nome),
            datasets: [{
                    label: 'Candidatos',
                    data: dados.map(d => d.total)
                }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}
function criarLinhaCandidato(vaga, item) {
    const tr = criarElemento('tr');
    // Princípio do Anonimato: exibe apenas identificador anônimo, descrição e competências
    tr.appendChild(criarElemento('td', 'candidato-id', `Candidato #${item.idCandidato}`));
    tr.appendChild(criarElemento('td', 'candidato-skills', `${item.descricaoCandidato || '-'} (${(item.competencias ?? []).join(', ')})`));
    tr.appendChild(criarElemento('td', 'candidato-formacao', 'Anônimo'));
    const tdAcoes = criarElemento('td', 'acoes-tabela');
    const btnMatch = criarElemento('button', 'btn-match', '❤️ Match');
    btnMatch.dataset.acao = 'match';
    btnMatch.dataset.vaga = String(vaga.id);
    btnMatch.dataset.candidato = String(item.idCandidato);
    const btnRecusar = criarElemento('button', 'btn-recusar', '❌ Não Interessado');
    btnRecusar.dataset.acao = 'recusar';
    btnRecusar.dataset.vaga = String(vaga.id);
    btnRecusar.dataset.candidato = String(item.idCandidato);
    tdAcoes.appendChild(btnMatch);
    tdAcoes.appendChild(btnRecusar);
    tr.appendChild(tdAcoes);
    return tr;
}
function criarCardVaga(vaga) {
    const card = criarElemento('article', 'vaga-card');
    const detalhes = criarElemento('div', 'vaga-detalhes');
    detalhes.appendChild(criarElemento('h2', 'vaga-titulo', vaga.nome));
    const btnRemoverVaga = criarElemento('button', 'btn-remover', 'Remover Vaga');
    btnRemoverVaga.type = 'button';
    btnRemoverVaga.dataset.acao = 'remover-vaga';
    btnRemoverVaga.dataset.vaga = String(vaga.id);
    detalhes.appendChild(btnRemoverVaga);
    const pDescricao = criarElemento('p', '', 'Descrição: ');
    pDescricao.appendChild(criarElemento('span', 'vaga-descricao', vaga.descricao));
    detalhes.appendChild(pDescricao);
    const pStatus = criarElemento('p', '', 'Local: ');
    pStatus.appendChild(criarElemento('span', 'vaga-status', vaga.local || 'Não informado'));
    detalhes.appendChild(pStatus);
    const areaCandidatos = criarElemento('div', 'vaga-candidatos');
    areaCandidatos.appendChild(criarElemento('p', '', 'Candidatos Aplicados'));
    const tabela = criarElemento('table', 'tabela-candidatos');
    const thead = criarElemento('thead');
    const trCabecalho = criarElemento('tr');
    ['Candidato', 'Descrição e Competências', 'Perfil', 'Ações']
        .forEach(titulo => trCabecalho.appendChild(criarElemento('th', '', titulo)));
    thead.appendChild(trCabecalho);
    const tbody = criarElemento('tbody');
    const interessadosDaVaga = interessados.filter(i => i.idVaga === vaga.id);
    if (interessadosDaVaga.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum candidato pendente de avaliação.');
        td.colSpan = 4;
        tr.appendChild(td);
        tbody.appendChild(tr);
    }
    else {
        for (const item of interessadosDaVaga) {
            tbody.appendChild(criarLinhaCandidato(vaga, item));
        }
    }
    tabela.appendChild(thead);
    tabela.appendChild(tbody);
    areaCandidatos.appendChild(tabela);
    card.appendChild(detalhes);
    card.appendChild(areaCandidatos);
    return card;
}
function renderizarVagas() {
    listaVagas.innerHTML = '';
    if (vagas.length === 0) {
        listaVagas.appendChild(criarElemento('p', '', 'Você ainda não criou nenhuma vaga.'));
        return;
    }
    for (const vaga of vagas) {
        listaVagas.appendChild(criarCardVaga(vaga));
    }
}
function renderizarRecrutadores() {
    listaRecrutadores.innerHTML = '';
    if (recrutadores.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum recrutador cadastrado.');
        td.colSpan = 5;
        tr.appendChild(td);
        listaRecrutadores.appendChild(tr);
        return;
    }
    for (const rec of recrutadores) {
        const tr = criarElemento('tr');
        tr.appendChild(criarElemento('td', '', rec.nome));
        tr.appendChild(criarElemento('td', '', rec.areaRecrutamento));
        tr.appendChild(criarElemento('td', '', rec.email));
        tr.appendChild(criarElemento('td', '', rec.descricao || '-'));
        const tdAcoes = criarElemento('td');
        const btnEditar = criarElemento('button', 'btn-editar', 'Editar');
        btnEditar.type = 'button';
        btnEditar.dataset.acao = 'editar-recrutador';
        btnEditar.dataset.id = String(rec.id);
        const btnRemover = criarElemento('button', 'btn-remover', 'Remover');
        btnRemover.type = 'button';
        btnRemover.dataset.acao = 'remover-recrutador';
        btnRemover.dataset.id = String(rec.id);
        tdAcoes.appendChild(btnEditar);
        tdAcoes.appendChild(btnRemover);
        tr.appendChild(tdAcoes);
        listaRecrutadores.appendChild(tr);
    }
}
function renderizarRejeitados() {
    listaRejeitados.innerHTML = '';
    if (rejeitados.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum candidato rejeitado.');
        td.colSpan = 3;
        tr.appendChild(td);
        listaRejeitados.appendChild(tr);
        return;
    }
    for (const rej of rejeitados) {
        const tr = criarElemento('tr');
        tr.appendChild(criarElemento('td', '', `Candidato #${rej.idCandidato}`));
        tr.appendChild(criarElemento('td', '', `${rej.descricaoCandidato || '-'} (${(rej.competencias ?? []).join(', ')})`));
        tr.appendChild(criarElemento('td', '', rej.nomeVaga));
        listaRejeitados.appendChild(tr);
    }
}
function limparFormularioRecrutador() {
    formRecrutador.reset();
    inputRecrutadorId.value = '';
    btnCancelarEdicao.classList.add('escondido');
}
async function carregarPainel() {
    try {
        [vagas, interessados, rejeitados, recrutadores] = await Promise.all([
            listarVagasPorEmpresaApi(empresaAtual.id),
            listarInteressadosApi(empresaAtual.id),
            listarRejeitadosApi(empresaAtual.id),
            listarRecrutadoresApi(empresaAtual.id)
        ]);
        renderizarVagas();
        desenharGrafico();
        renderizarRecrutadores();
        renderizarRejeitados();
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(`Erro ao carregar painel: ${erro.message}`);
            return;
        }
        alert('Erro inesperado ao carregar painel da empresa.');
    }
}
formRecrutador.addEventListener('submit', async (event) => {
    event.preventDefault();
    const idEditando = inputRecrutadorId.value ? Number(inputRecrutadorId.value) : 0;
    const nome = inputRecrutadorNome.value.trim();
    const areaRecrutamento = inputRecrutadorArea.value.trim();
    const descricao = inputRecrutadorDescricao.value.trim();
    const email = inputRecrutadorEmail.value.trim();
    const cpf = inputRecrutadorCpf.value.trim();
    const dataNascimento = inputRecrutadorNascimento.value.trim();
    const senha = inputRecrutadorSenha.value;
    try {
        if (idEditando > 0) {
            const payload = {
                id: idEditando,
                idEmpresa: empresaAtual.id,
                nome,
                areaRecrutamento,
                descricao,
                email,
                cpf,
                dataNascimento
            };
            if (senha.trim() !== '') {
                payload.senha = senha;
            }
            await atualizarRecrutadorApi(payload);
        }
        else {
            await cadastrarRecrutadorApi({
                idEmpresa: empresaAtual.id,
                nome,
                areaRecrutamento,
                descricao,
                email,
                cpf,
                dataNascimento,
                senha
            });
        }
        limparFormularioRecrutador();
        await carregarPainel();
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao salvar recrutador.');
    }
});
btnCancelarEdicao.addEventListener('click', limparFormularioRecrutador);
listaRecrutadores.addEventListener('click', async (event) => {
    const botao = event.target.closest('button');
    if (!botao)
        return;
    const id = Number(botao.dataset.id);
    if (botao.dataset.acao === 'editar-recrutador') {
        const rec = recrutadores.find(r => r.id === id);
        if (!rec)
            return;
        inputRecrutadorId.value = String(rec.id);
        inputRecrutadorNome.value = rec.nome;
        inputRecrutadorArea.value = rec.areaRecrutamento;
        inputRecrutadorDescricao.value = rec.descricao ?? '';
        inputRecrutadorEmail.value = rec.email;
        inputRecrutadorCpf.value = '';
        inputRecrutadorNascimento.value = '';
        inputRecrutadorSenha.value = '';
        btnCancelarEdicao.classList.remove('escondido');
    }
    else if (botao.dataset.acao === 'remover-recrutador') {
        try {
            await removerRecrutadorApi(id);
            await carregarPainel();
        }
        catch (erro) {
            if (erro instanceof HttpError) {
                alert(erro.message);
                return;
            }
            alert('Erro inesperado ao remover recrutador.');
        }
    }
});
listaVagas.addEventListener('click', async (event) => {
    const botao = event.target.closest('button');
    if (!botao || botao.disabled)
        return;
    const idVaga = Number(botao.dataset.vaga);
    const idCandidato = Number(botao.dataset.candidato);
    try {
        if (botao.dataset.acao === 'remover-vaga') {
            await removerVagaApi(idVaga);
        }
        else if (botao.dataset.acao === 'match') {
            await curtirCandidatoApi(empresaAtual.id, idCandidato, idVaga);
        }
        else if (botao.dataset.acao === 'recusar') {
            await rejeitarCandidatoApi(empresaAtual.id, idCandidato, idVaga);
        }
        await carregarPainel();
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao processar ação.');
    }
});
void carregarPainel();
//# sourceMappingURL=painel_empresa.js.map