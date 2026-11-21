import type { Candidato, Empresa, Recrutador, Rejeicao, Vaga } from "./storage.js";
import {
    obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas,
    obterTodosRecrutadores, obterTodasRejeicoes,
    salvarUsuarios, salvarVagas, salvarRejeicoes
} from "./storage.js";

function voltarParaLogin(): never {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como recrutador.");
}

const dadosLogado = localStorage.getItem('linketinder_usuario_logado');
const logado = dadosLogado ? JSON.parse(dadosLogado) : null;

if (logado === null || logado.tipo !== 'recrutador') {
    voltarParaLogin();
}

const candidatos: Candidato[] = obterTodosCandidatos();
const empresas: Empresa[] = obterTodasEmpresas();
const recrutadores: Recrutador[] = obterTodosRecrutadores();
const vagas: Vaga[] = obterTodasVagas();
const rejeicoes: Rejeicao[] = obterTodasRejeicoes();

const recrutadorAtual: Recrutador = recrutadores.find(r => r.id === logado.id) ?? voltarParaLogin();
const empresaAtual: Empresa = empresas.find(e => e.id === recrutadorAtual.idEmpresa) ?? voltarParaLogin();

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
subtituloRecrutador.textContent = `Empresa: ${empresaAtual.nome} | Área: ${recrutadorAtual.areaRecrutamento}`;

function criarElemento<K extends keyof HTMLElementTagNameMap>(
    tag: K, classe?: string, texto?: string
): HTMLElementTagNameMap[K] {
    const el = document.createElement(tag);
    if (classe) el.className = classe;
    if (texto !== undefined) el.textContent = texto;
    return el;
}

function vagasDaEmpresa(): Vaga[] {
    return vagas.filter(v => v.empresa.id === empresaAtual.id);
}

function salvarTudo(): void {
    salvarVagas(vagas);
    salvarUsuarios([...candidatos, ...empresas]);
    salvarRejeicoes(rejeicoes);
}

function buscarCandidato(interessado: Candidato): Candidato {
    return candidatos.find(c => c.id === interessado.id) ?? interessado;
}

function criarLinhaCandidato(vaga: Vaga, interessado: Candidato): HTMLTableRowElement {
    const candidato = buscarCandidato(interessado);
    const jaTemMatch = (vaga.matches ?? []).includes(candidato.id);
    const tr = criarElemento('tr');

    // Princípio do Anonimato: exibe apenas identificador anônimo e descrição
    tr.appendChild(criarElemento('td', '', `Candidato #${candidato.id}`));
    tr.appendChild(criarElemento('td', '', candidato.descricao));

    const tdAcoes = criarElemento('td');
    const btnMatch = criarElemento('button', 'btn-match', jaTemMatch ? '✅ Match feito' : '❤️ Match');
    btnMatch.type = 'button';
    btnMatch.dataset.acao = 'match';
    btnMatch.dataset.vaga = String(vaga.id);
    btnMatch.dataset.candidato = String(candidato.id);
    btnMatch.disabled = jaTemMatch;

    const btnRecusar = criarElemento('button', 'btn-recusar', '❌ Não Interessado');
    btnRecusar.type = 'button';
    btnRecusar.dataset.acao = 'recusar';
    btnRecusar.dataset.vaga = String(vaga.id);
    btnRecusar.dataset.candidato = String(candidato.id);

    tdAcoes.appendChild(btnMatch);
    tdAcoes.appendChild(btnRecusar);
    tr.appendChild(tdAcoes);
    return tr;
}

function criarCardVaga(vaga: Vaga): HTMLElement {
    const card = criarElemento('article', 'vaga-card');

    const cabecalho = criarElemento('div', 'vaga-cabecalho');
    cabecalho.appendChild(criarElemento('h2', '', vaga.nome));

    const btnEditar = criarElemento('button', 'btn-editar', 'Editar Vaga');
    btnEditar.type = 'button';
    btnEditar.dataset.acao = 'editar-vaga';
    btnEditar.dataset.vaga = String(vaga.id);
    cabecalho.appendChild(btnEditar);
    card.appendChild(cabecalho);

    card.appendChild(criarElemento('p', '', `Descrição: ${vaga.descricao}`));
    card.appendChild(criarElemento('p', '', `Competências: ${vaga.competencias.join(', ')}`));

    const tabela = criarElemento('table');
    const thead = criarElemento('thead');
    const trCab = criarElemento('tr');
    ['Candidato', 'Descrição / Perfil', 'Ações'].forEach(t => trCab.appendChild(criarElemento('th', '', t)));
    thead.appendChild(trCab);

    const tbody = criarElemento('tbody');
    if (vaga.interessados.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum candidato interessado no momento.');
        td.colSpan = 3;
        tr.appendChild(td);
        tbody.appendChild(tr);
    } else {
        vaga.interessados.forEach(i => tbody.appendChild(criarLinhaCandidato(vaga, i)));
    }

    tabela.appendChild(thead);
    tabela.appendChild(tbody);
    card.appendChild(tabela);
    return card;
}

function renderizarVagas(): void {
    listaVagas.innerHTML = '';
    const minhasVagas = vagasDaEmpresa();
    if (minhasVagas.length === 0) {
        listaVagas.appendChild(criarElemento('p', '', 'Nenhuma vaga cadastrada para esta empresa.'));
        return;
    }
    minhasVagas.forEach(v => listaVagas.appendChild(criarCardVaga(v)));
}

function renderizarMatches(): void {
    listaMatches.innerHTML = '';
    const linhas: { vaga: string; nome: string; email: string }[] = [];

    for (const vaga of vagasDaEmpresa()) {
        for (const idCand of (vaga.matches ?? [])) {
            const cand = candidatos.find(c => c.id === idCand);
            if (cand) {
                linhas.push({ vaga: vaga.nome, nome: cand.nome, email: cand.email });
            }
        }
    }

    if (linhas.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum match confirmado ainda.');
        td.colSpan = 3;
        tr.appendChild(td);
        listaMatches.appendChild(tr);
        return;
    }

    for (const m of linhas) {
        const tr = criarElemento('tr');
        tr.appendChild(criarElemento('td', '', m.vaga));
        tr.appendChild(criarElemento('td', '', m.nome));
        tr.appendChild(criarElemento('td', '', m.email));
        listaMatches.appendChild(tr);
    }
}

function renderizarRejeitados(): void {
    listaRejeitados.innerHTML = '';
    const daEmpresa = rejeicoes.filter(r => r.idEmpresa === empresaAtual.id);

    if (daEmpresa.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum candidato rejeitado.');
        td.colSpan = 3;
        tr.appendChild(td);
        listaRejeitados.appendChild(tr);
        return;
    }

    for (const rej of daEmpresa) {
        const vaga = vagas.find(v => v.id === rej.idVaga);
        const cand = candidatos.find(c => c.id === rej.idCandidato);
        const tr = criarElemento('tr');
        tr.appendChild(criarElemento('td', '', vaga ? vaga.nome : `Vaga #${rej.idVaga}`));
        tr.appendChild(criarElemento('td', '', `Candidato #${rej.idCandidato}`));
        tr.appendChild(criarElemento('td', '', cand ? cand.descricao : 'Descrição não disponível'));
        listaRejeitados.appendChild(tr);
    }
}

function renderizarTudo(): void {
    renderizarVagas();
    renderizarMatches();
    renderizarRejeitados();
}

function limparFormularioVaga(): void {
    formVaga.reset();
    inputVagaId.value = '';
    btnCancelarVaga.classList.add('escondido');
}

formVaga.addEventListener('submit', (e: Event) => {
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

    if (idEditando > 0) {
        const vaga = vagas.find(v => v.id === idEditando && v.empresa.id === empresaAtual.id);
        if (!vaga) {
            alert('Você só pode editar vagas da sua empresa.');
            return;
        }
        vaga.nome = nome;
        vaga.descricao = descricao;
        vaga.competencias = competencias;
    } else {
        const proximoId = vagas.reduce((max, v) => Math.max(max, v.id), 0) + 1;
        vagas.push({
            id: proximoId,
            nome,
            descricao,
            competencias,
            empresa: empresaAtual,
            interessados: [],
            matches: []
        });
        if (!empresaAtual.vagas.includes(proximoId)) {
            empresaAtual.vagas.push(proximoId);
        }
    }

    salvarTudo();
    limparFormularioVaga();
    renderizarTudo();
});

btnCancelarVaga.addEventListener('click', limparFormularioVaga);

listaVagas.addEventListener('click', (event: Event) => {
    const botao = (event.target as HTMLElement).closest('button');
    if (!botao || botao.disabled) return;

    const acao = botao.dataset.acao;
    const idVaga = Number(botao.dataset.vaga);

    if (acao === 'editar-vaga') {
        const vaga = vagas.find(v => v.id === idVaga && v.empresa.id === empresaAtual.id);
        if (!vaga) return;
        inputVagaId.value = String(vaga.id);
        inputVagaNome.value = vaga.nome;
        inputVagaDescricao.value = vaga.descricao;
        inputVagaCompetencias.value = vaga.competencias.join(', ');
        btnCancelarVaga.classList.remove('escondido');
        return;
    }

    const idCandidato = Number(botao.dataset.candidato);
    const vaga = vagas.find(v => v.id === idVaga && v.empresa.id === empresaAtual.id);
    if (!vaga) return;

    if (acao === 'match') {
        const matches = vaga.matches ?? [];
        if (!matches.includes(idCandidato)) {
            matches.push(idCandidato);
        }
        vaga.matches = matches;
        salvarTudo();
    } else if (acao === 'recusar') {
        vaga.interessados = vaga.interessados.filter(c => c.id !== idCandidato);
        vaga.matches = (vaga.matches ?? []).filter(id => id !== idCandidato);

        const cand = candidatos.find(c => c.id === idCandidato);
        if (cand) {
            cand.vagasInteresse = (cand.vagasInteresse ?? []).filter(id => id !== idVaga);
        }

        const jaRejeitado = rejeicoes.some(
            r => r.idEmpresa === empresaAtual.id && r.idCandidato === idCandidato && r.idVaga === idVaga
        );
        if (!jaRejeitado) {
            rejeicoes.push({
                idEmpresa: empresaAtual.id,
                idCandidato,
                idVaga,
                idRecrutador: recrutadorAtual.id
            });
        }
        salvarTudo();
    }

    renderizarTudo();
});

renderizarTudo();
