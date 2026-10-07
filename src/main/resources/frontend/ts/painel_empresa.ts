import type { Candidato, Empresa, Vaga } from "./storage.js";
import {
    obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas,
    salvarUsuarios, salvarVagas
} from "./storage.js";

declare const Chart: any;

// USUÁRIO LOGADO (só empresa pode ver esta página)

function voltarParaLogin(): never {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como empresa.");
}

const dadosLogado = localStorage.getItem('linketinder_usuario_logado');
const logado = dadosLogado ? JSON.parse(dadosLogado) : null;

if (logado === null || logado.tipo !== 'empresa') {
    voltarParaLogin();
}


const candidatos: Candidato[] = obterTodosCandidatos();
const empresas: Empresa[] = obterTodasEmpresas();
const vagas: Vaga[] = obterTodasVagas();

const empresaAtual: Empresa = empresas.find(e => e.id === logado.id) ?? voltarParaLogin();

function vagasDaEmpresa(): Vaga[] {
    return vagas.filter(v => v.empresa.id === empresaAtual.id);
}

if (!Array.isArray(empresaAtual.vagas)) {
    empresaAtual.vagas = [];
}

let precisaSalvar = false;
for (const vaga of vagasDaEmpresa()) {
    if (!empresaAtual.vagas.includes(vaga.id)) {
        empresaAtual.vagas.push(vaga.id);
        precisaSalvar = true;
    }
}
if (precisaSalvar) {
    salvarUsuarios([...candidatos, ...empresas]);
}


const listaVagas = document.querySelector('#lista-vagas') as HTMLDivElement;
const canvasGrafico = document.querySelector('#graficoCompetencias') as HTMLCanvasElement;


function criarElemento<K extends keyof HTMLElementTagNameMap>(
    tag: K, classe?: string, texto?: string
): HTMLElementTagNameMap[K] {
    const el = document.createElement(tag);
    if (classe) el.className = classe;
    if (texto !== undefined) el.textContent = texto; // textContent evita injetar HTML vindo dos dados
    return el;
}

function buscarCandidato(interessado: Candidato): Candidato {
    return candidatos.find(c => c.id === interessado.id) ?? interessado;
}

function salvarTudo(): void {
    salvarVagas(vagas);
    salvarUsuarios([...candidatos, ...empresas]);
}


let grafico: any = null;

function desenharGrafico(): void {
    if (typeof Chart === 'undefined') {
        console.warn('Chart.js não foi carregado. Confira a tag <script> do HTML.');
        return;
    }

    // Para cada competência exigida nas vagas da empresa, conta candidatos distintos inscritos
    const contagem = new Map<string, Set<number>>();

    for (const vaga of vagasDaEmpresa()) {
        for (const competencia of vaga.competencias) {
            if (!contagem.has(competencia)) {
                contagem.set(competencia, new Set<number>());
            }
            const conjunto = contagem.get(competencia) as Set<number>;
            vaga.interessados.forEach(c => conjunto.add(c.id));
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


function criarLinhaCandidato(vaga: Vaga, interessado: Candidato): HTMLTableRowElement {
    const candidato = buscarCandidato(interessado);
    const jaTemMatch = (vaga.matches ?? []).includes(candidato.id);

    const tr = criarElemento('tr');

    // mostra só o número do candidato, sem nome
    tr.appendChild(criarElemento('td', 'candidato-id', `Candidato #${candidato.id}`));
    tr.appendChild(criarElemento('td', 'candidato-skills', candidato.descricao));
    tr.appendChild(criarElemento('td', 'candidato-formacao', 'Não informada'));

    const tdAcoes = criarElemento('td', 'acoes-tabela');

    const btnMatch = criarElemento('button', 'btn-match', jaTemMatch ? '✅ Match feito' : '❤️ Match');
    btnMatch.dataset.acao = 'match';
    btnMatch.dataset.vaga = String(vaga.id);
    btnMatch.dataset.candidato = String(candidato.id);
    btnMatch.disabled = jaTemMatch;

    const btnRecusar = criarElemento('button', 'btn-recusar', '❌ Não Interessado');
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

    const detalhes = criarElemento('div', 'vaga-detalhes');
    detalhes.appendChild(criarElemento('h2', 'vaga-titulo', vaga.nome));

    const pDescricao = criarElemento('p', '', 'Descrição: ');
    pDescricao.appendChild(criarElemento('span', 'vaga-descricao', vaga.descricao));
    detalhes.appendChild(pDescricao);

    const pStatus = criarElemento('p', '', 'Status: ');
    pStatus.appendChild(criarElemento('span', 'vaga-status', 'Aberta'));
    detalhes.appendChild(pStatus);

    const areaCandidatos = criarElemento('div', 'vaga-candidatos');
    areaCandidatos.appendChild(criarElemento('p', '', 'Candidatos Aplicados'));

    const tabela = criarElemento('table', 'tabela-candidatos');

    const thead = criarElemento('thead');
    const trCabecalho = criarElemento('tr');
    ['Candidato', 'Skills (Competências)', 'Formação', 'Ações']
        .forEach(titulo => trCabecalho.appendChild(criarElemento('th', '', titulo)));
    thead.appendChild(trCabecalho);

    const tbody = criarElemento('tbody');

    if (vaga.interessados.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum candidato inscrito ainda.');
        td.colSpan = 4;
        tr.appendChild(td);
        tbody.appendChild(tr);
    } else {
        for (const interessado of vaga.interessados) {
            tbody.appendChild(criarLinhaCandidato(vaga, interessado));
        }
    }

    tabela.appendChild(thead);
    tabela.appendChild(tbody);
    areaCandidatos.appendChild(tabela);

    card.appendChild(detalhes);
    card.appendChild(areaCandidatos);

    return card;
}

function renderizarVagas(): void {
    listaVagas.innerHTML = '';

    const minhasVagas = vagasDaEmpresa();

    if (minhasVagas.length === 0) {
        listaVagas.appendChild(criarElemento('p', '', 'Você ainda não criou nenhuma vaga.'));
        return;
    }

    for (const vaga of minhasVagas) {
        listaVagas.appendChild(criarCardVaga(vaga));
    }
}

function renderizarTudo(): void {
    renderizarVagas();
    desenharGrafico();
}


function darMatch(idVaga: number, idCandidato: number): void {
    const vaga = vagas.find(v => v.id === idVaga);
    if (!vaga) return;

    const matches = vaga.matches ?? [];
    if (!matches.includes(idCandidato)) {
        matches.push(idCandidato);
    }
    vaga.matches = matches;

    salvarTudo();
}

function recusarCandidato(idVaga: number, idCandidato: number): void {
    const vaga = vagas.find(v => v.id === idVaga);
    if (!vaga) return;

    // Tira o candidato da vaga (e o match, se houver)
    vaga.interessados = vaga.interessados.filter(c => c.id !== idCandidato);
    vaga.matches = (vaga.matches ?? []).filter(id => id !== idCandidato);

    // Tira a vaga da lista de interesses do candidato
    const candidato = candidatos.find(c => c.id === idCandidato);
    if (candidato) {
        candidato.vagasInteresse = (candidato.vagasInteresse ?? []).filter(id => id !== idVaga);
    }

    salvarTudo();
}

// Um único ouvinte no container atende todos os botões (inclusive os criados depois)
listaVagas.addEventListener('click', (event: Event) => {
    const botao = (event.target as HTMLElement).closest('button');
    if (!botao || botao.disabled) return;

    const idVaga = Number(botao.dataset.vaga);
    const idCandidato = Number(botao.dataset.candidato);

    if (botao.dataset.acao === 'match') {
        darMatch(idVaga, idCandidato);
    } else if (botao.dataset.acao === 'recusar') {
        recusarCandidato(idVaga, idCandidato);
    }

    renderizarTudo();
});

renderizarTudo();