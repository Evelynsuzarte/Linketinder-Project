import {
    HttpError,
    atualizarVagaApi,
    cadastrarVagaApi,
    curtirCandidatoApi,
    listarInteressadosApi,
    listarMatchesEmpresaApi,
    listarRejeitadosApi,
    listarVagasPorEmpresaApi,
    obterSessao,
    rejeitarCandidatoApi,
    removerVagaApi
} from "./api.js";
import type {
    CandidatoInteressadoDto,
    MatchEmpresaDto,
    VagaEmpresaDto
} from "./api.js";

function voltarParaLogin(): never {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como recrutador.");
}

const logado = obterSessao();
if (logado === null || logado.tipo !== 'recrutador') {
    voltarParaLogin();
}
const recrutadorAtual = logado;

const tituloRecrutador = document.querySelector('#titulo-recrutador') as HTMLHeadingElement;
const subtituloRecrutador = document.querySelector('#subtitulo-recrutador') as HTMLParagraphElement;
const listaVagas = document.querySelector('#lista-vagas') as HTMLDivElement;
const listaMatches = document.querySelector('#lista-matches') as HTMLTableSectionElement;
const listaRejeitados = document.querySelector('#lista-rejeitados') as HTMLTableSectionElement;

const formVaga = document.querySelector('#form-vaga') as HTMLFormElement;
const inputVagaId = document.querySelector('#vaga-id') as HTMLInputElement;
const inputVagaNome = document.querySelector('#vaga-nome') as HTMLInputElement;
const inputVagaDescricao = document.querySelector('#vaga-descricao') as HTMLInputElement;
const inputVagaCompetencias = document.querySelector('#vaga-competencias') as HTMLInputElement;
const btnCancelarVaga = document.querySelector('#btn-cancelar-vaga') as HTMLButtonElement;

tituloRecrutador.textContent = `Painel do Recrutador — ${recrutadorAtual.nome}`;
subtituloRecrutador.textContent = `Empresa: ${recrutadorAtual.nomeEmpresa ?? `#${recrutadorAtual.idEmpresa}`} | Área: ${recrutadorAtual.areaRecrutamento}`;

let vagas: VagaEmpresaDto[] = [];
let interessados: CandidatoInteressadoDto[] = [];
let rejeitados: CandidatoInteressadoDto[] = [];
let matches: MatchEmpresaDto[] = [];

function criarElemento<K extends keyof HTMLElementTagNameMap>(
    tag: K, classe?: string, texto?: string
): HTMLElementTagNameMap[K] {
    const el = document.createElement(tag);
    if (classe) el.className = classe;
    if (texto !== undefined) el.textContent = texto;
    return el;
}

function criarLinhaCandidato(vaga: VagaEmpresaDto, item: CandidatoInteressadoDto): HTMLTableRowElement {
    const tr = criarElemento('tr');

    // Princípio do Anonimato (idêntico à visão da empresa): apenas descrição e competências antes do match
    tr.appendChild(criarElemento('td', '', `Candidato #${item.idCandidato}`));
    tr.appendChild(criarElemento('td', '', `${item.descricaoCandidato || '-'} (${(item.competencias ?? []).join(', ')})`));

    const tdAcoes = criarElemento('td');
    const btnMatch = criarElemento('button', 'btn-match', '❤️ Match');
    btnMatch.type = 'button';
    btnMatch.dataset.acao = 'match';
    btnMatch.dataset.vaga = String(vaga.id);
    btnMatch.dataset.candidato = String(item.idCandidato);

    const btnRecusar = criarElemento('button', 'btn-recusar', '❌ Não Interessado');
    btnRecusar.type = 'button';
    btnRecusar.dataset.acao = 'recusar';
    btnRecusar.dataset.vaga = String(vaga.id);
    btnRecusar.dataset.candidato = String(item.idCandidato);

    tdAcoes.appendChild(btnMatch);
    tdAcoes.appendChild(btnRecusar);
    tr.appendChild(tdAcoes);
    return tr;
}

function criarCardVaga(vaga: VagaEmpresaDto): HTMLElement {
    const card = criarElemento('article', 'vaga-card');

    const cabecalho = criarElemento('div', 'vaga-cabecalho');
    cabecalho.appendChild(criarElemento('h2', '', vaga.nome));

    const acoesVaga = criarElemento('div');
    const btnEditar = criarElemento('button', 'btn-editar', 'Editar Vaga');
    btnEditar.type = 'button';
    btnEditar.dataset.acao = 'editar-vaga';
    btnEditar.dataset.vaga = String(vaga.id);

    const btnRemover = criarElemento('button', 'btn-remover', 'Remover Vaga');
    btnRemover.type = 'button';
    btnRemover.dataset.acao = 'remover-vaga';
    btnRemover.dataset.vaga = String(vaga.id);

    acoesVaga.appendChild(btnEditar);
    acoesVaga.appendChild(btnRemover);
    cabecalho.appendChild(acoesVaga);
    card.appendChild(cabecalho);

    card.appendChild(criarElemento('p', '', `Descrição: ${vaga.descricao}`));
    card.appendChild(criarElemento('p', '', `Competências: ${(vaga.competencias ?? []).join(', ')}`));

    const tabela = criarElemento('table', 'tabela-candidatos');
    const thead = criarElemento('thead');
    const trCabecalho = criarElemento('tr');
    ['Candidato', 'Descrição e Competências', 'Ações']
        .forEach(t => trCabecalho.appendChild(criarElemento('th', '', t)));
    thead.appendChild(trCabecalho);

    const tbody = criarElemento('tbody');
    const interessadosDaVaga = interessados.filter(i => i.idVaga === vaga.id);

    if (interessadosDaVaga.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum candidato pendente de avaliação.');
        td.colSpan = 3;
        tr.appendChild(td);
        tbody.appendChild(tr);
    } else {
        for (const item of interessadosDaVaga) {
            tbody.appendChild(criarLinhaCandidato(vaga, item));
        }
    }

    tabela.appendChild(thead);
    tabela.appendChild(tbody);
    card.appendChild(tabela);
    return card;
}

function renderizarVagas(): void {
    listaVagas.innerHTML = '';
    if (vagas.length === 0) {
        listaVagas.appendChild(criarElemento('p', '', 'A empresa ainda não possui vagas cadastradas.'));
        return;
    }
    for (const vaga of vagas) {
        listaVagas.appendChild(criarCardVaga(vaga));
    }
}

function renderizarMatches(): void {
    listaMatches.innerHTML = '';
    if (matches.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum match confirmado ainda.');
        td.colSpan = 3;
        tr.appendChild(td);
        listaMatches.appendChild(tr);
        return;
    }

    for (const m of matches) {
        const tr = criarElemento('tr');
        const nomeCompleto = m.sobrenomeCandidato ? `${m.nomeCandidato} ${m.sobrenomeCandidato}` : m.nomeCandidato;
        tr.appendChild(criarElemento('td', '', m.nomeVaga));
        tr.appendChild(criarElemento('td', '', nomeCompleto));
        tr.appendChild(criarElemento('td', '', m.emailCandidato));
        listaMatches.appendChild(tr);
    }
}

function renderizarRejeitados(): void {
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

function limparFormularioVaga(): void {
    formVaga.reset();
    inputVagaId.value = '';
    btnCancelarVaga.classList.add('escondido');
}

async function carregarPainel(): Promise<void> {
    try {
        [vagas, interessados, rejeitados, matches] = await Promise.all([
            listarVagasPorEmpresaApi(recrutadorAtual.idEmpresa),
            listarInteressadosApi(recrutadorAtual.idEmpresa),
            listarRejeitadosApi(recrutadorAtual.idEmpresa),
            listarMatchesEmpresaApi(recrutadorAtual.idEmpresa)
        ]);
        renderizarVagas();
        renderizarMatches();
        renderizarRejeitados();
    } catch (erro) {
        if (erro instanceof HttpError) {
            alert(`Erro ao carregar painel: ${erro.message}`);
            return;
        }
        alert('Erro inesperado ao carregar painel do recrutador.');
    }
}

formVaga.addEventListener('submit', async (e: Event) => {
    e.preventDefault();
    const idEditando = inputVagaId.value ? Number(inputVagaId.value) : 0;
    const nome = inputVagaNome.value.trim();
    const descricao = inputVagaDescricao.value.trim();
    const competencias = inputVagaCompetencias.value
        .split(',')
        .map(c => c.trim())
        .filter(c => c.length > 0);

    if (!nome || !descricao) {
        alert('Nome e descrição da vaga são obrigatórios.');
        return;
    }

    try {
        if (idEditando > 0) {
            await atualizarVagaApi({
                id: idEditando,
                nome,
                descricao,
                local: 'Remoto',
                idEmpresa: recrutadorAtual.idEmpresa,
                competencias,
                idRecrutador: recrutadorAtual.id
            });
        } else {
            await cadastrarVagaApi({
                nome,
                descricao,
                local: 'Remoto',
                idEmpresa: recrutadorAtual.idEmpresa,
                competencias
            });
        }

        limparFormularioVaga();
        await carregarPainel();
    } catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao salvar vaga.');
    }
});

btnCancelarVaga.addEventListener('click', limparFormularioVaga);

listaVagas.addEventListener('click', async (event: Event) => {
    const botao = (event.target as HTMLElement).closest('button');
    if (!botao || botao.disabled) return;

    const acao = botao.dataset.acao;
    const idVaga = Number(botao.dataset.vaga);

    if (acao === 'editar-vaga') {
        const vaga = vagas.find(v => v.id === idVaga);
        if (!vaga) return;
        inputVagaId.value = String(vaga.id);
        inputVagaNome.value = vaga.nome;
        inputVagaDescricao.value = vaga.descricao;
        inputVagaCompetencias.value = (vaga.competencias ?? []).join(', ');
        btnCancelarVaga.classList.remove('escondido');
        return;
    }

    const idCandidato = Number(botao.dataset.candidato);

    try {
        if (acao === 'remover-vaga') {
            await removerVagaApi(idVaga, recrutadorAtual.id);
        } else if (acao === 'match') {
            await curtirCandidatoApi(recrutadorAtual.idEmpresa, idCandidato, idVaga, recrutadorAtual.id);
        } else if (acao === 'recusar') {
            await rejeitarCandidatoApi(recrutadorAtual.idEmpresa, idCandidato, idVaga, recrutadorAtual.id);
        }
        await carregarPainel();
    } catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao processar ação.');
    }
});

void carregarPainel();
