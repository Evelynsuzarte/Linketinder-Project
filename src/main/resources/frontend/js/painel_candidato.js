import { HttpError, curtirVagaApi, listarMatchesCandidatoApi, listarVagasNaoCurtidasApi, obterSessao } from "./api.js";
function voltarParaLogin() {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como candidato.");
}
const logado = obterSessao();
if (logado === null || logado.tipo !== 'candidato') {
    voltarParaLogin();
}
const candidatoAtual = logado;
const selectFiltro = document.querySelector('#filtro-vagas');
const corpoTabela = document.querySelector('#lista-vagas-disponiveis');
function criarCelula(classe, texto) {
    const td = document.createElement('td');
    td.className = classe;
    td.textContent = texto;
    return td;
}
function renderizarMensagemTabela(mensagem) {
    corpoTabela.innerHTML = '';
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = 4;
    td.textContent = mensagem;
    tr.appendChild(td);
    corpoTabela.appendChild(tr);
}
async function renderizarVagas() {
    const filtro = selectFiltro.value;
    corpoTabela.innerHTML = '';
    try {
        if (filtro === 'cadastradas') {
            const matches = await listarMatchesCandidatoApi(candidatoAtual.id);
            if (matches.length === 0) {
                renderizarMensagemTabela('Você ainda não possui matches confirmados.');
                return;
            }
            for (const match of matches) {
                const tr = document.createElement('tr');
                tr.appendChild(criarCelula('vaga-titulo', match.nomeVaga));
                tr.appendChild(criarCelula('vaga-descricao', `Empresa: ${match.nomeEmpresa}`));
                tr.appendChild(criarCelula('vaga-requisitos', `Local: ${match.localVaga || '-'}`));
                tr.appendChild(criarCelula('acoes-tabela', '❤️ Match confirmado'));
                corpoTabela.appendChild(tr);
            }
            return;
        }
        const vagas = await listarVagasNaoCurtidasApi(candidatoAtual.id);
        if (vagas.length === 0) {
            renderizarMensagemTabela('Não há novas vagas disponíveis no momento.');
            return;
        }
        for (const vaga of vagas) {
            const tr = document.createElement('tr');
            tr.appendChild(criarCelula('vaga-titulo', vaga.nome));
            tr.appendChild(criarCelula('vaga-descricao', `${vaga.descricao} (${vaga.local || 'Não informado'})`));
            tr.appendChild(criarCelula('vaga-requisitos', (vaga.competencias ?? []).join(', ')));
            const tdAcoes = document.createElement('td');
            tdAcoes.className = 'acoes-tabela';
            const botao = document.createElement('button');
            botao.dataset.id = String(vaga.id);
            botao.className = 'btn-inscrever';
            botao.dataset.acao = 'candidatar';
            botao.textContent = 'Inscrever-se';
            tdAcoes.appendChild(botao);
            tr.appendChild(tdAcoes);
            corpoTabela.appendChild(tr);
        }
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            renderizarMensagemTabela(`Erro ao carregar dados: ${erro.message}`);
            return;
        }
        renderizarMensagemTabela('Erro inesperado ao carregar vagas.');
    }
}
corpoTabela.addEventListener('click', async (event) => {
    const botao = event.target.closest('button');
    if (!botao || botao.dataset.acao !== 'candidatar') {
        return;
    }
    const idVaga = Number(botao.dataset.id);
    try {
        await curtirVagaApi(candidatoAtual.id, idVaga);
        alert('Interesse registrado na vaga!');
        await renderizarVagas();
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao registrar interesse.');
    }
});
selectFiltro.addEventListener('change', () => {
    void renderizarVagas();
});
void renderizarVagas();
//# sourceMappingURL=painel_candidato.js.map