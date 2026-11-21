import type { Candidato, Empresa, Recrutador, Rejeicao, Vaga } from "./storage.js";
import {
    obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas,
    obterTodosRecrutadores, obterTodasRejeicoes,
    salvarUsuarios, salvarVagas, salvarRecrutadores, salvarRejeicoes
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
let recrutadores: Recrutador[] = obterTodosRecrutadores();
let rejeicoes: Rejeicao[] = obterTodasRejeicoes();

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
const listaRecrutadores = document.querySelector('#lista-recrutadores') as HTMLTableSectionElement;
const listaRejeitados = document.querySelector('#lista-rejeitados') as HTMLTableSectionElement;
const formRecrutador = document.querySelector('#form-recrutador') as HTMLFormElement;
const inputRecrutadorId = document.querySelector('#recrutador-id') as HTMLInputElement;
const inputRecrutadorNome = document.querySelector('#recrutador-nome') as HTMLInputElement;
const inputRecrutadorArea = document.querySelector('#recrutador-area') as HTMLInputElement;
const inputRecrutadorDescricao = document.querySelector('#recrutador-descricao') as HTMLInputElement;
const inputRecrutadorEmail = document.querySelector('#recrutador-email') as HTMLInputElement;
const inputRecrutadorCpf = document.querySelector('#recrutador-cpf') as HTMLInputElement;
const inputRecrutadorNascimento = document.querySelector('#recrutador-nascimento') as HTMLInputElement;
const inputRecrutadorSenha = document.querySelector('#recrutador-senha') as HTMLInputElement;
const btnCancelarEdicao = document.querySelector('#btn-cancelar-edicao') as HTMLButtonElement;


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
    salvarRecrutadores(recrutadores);
    salvarRejeicoes(rejeicoes);
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

function renderizarRecrutadores(): void {
    listaRecrutadores.innerHTML = '';
    const daEmpresa = recrutadores.filter(r => r.idEmpresa === empresaAtual.id);

    if (daEmpresa.length === 0) {
        const tr = criarElemento('tr');
        const td = criarElemento('td', '', 'Nenhum recrutador cadastrado.');
        td.colSpan = 5;
        tr.appendChild(td);
        listaRecrutadores.appendChild(tr);
        return;
    }

    for (const rec of daEmpresa) {
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
    desenharGrafico();
    renderizarRecrutadores();
    renderizarRejeitados();
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

    const jaRejeitado = rejeicoes.some(
        r => r.idEmpresa === empresaAtual.id && r.idCandidato === idCandidato && r.idVaga === idVaga
    );
    if (!jaRejeitado) {
        rejeicoes.push({ idEmpresa: empresaAtual.id, idCandidato, idVaga });
    }

    salvarTudo();
}

function validarEmail(email: string): boolean {
    return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email.trim());
}

function validarCpf(cpf: string): string | null {
    const digitos = cpf.replace(/\D/g, '');
    if (digitos.length !== 11 || /^(\d)\1{10}$/.test(digitos)) return null;

    const calcDigito = (base: number): number => {
        let soma = 0;
        for (let i = 0; i < base; i++) {
            soma += Number(digitos.charAt(i)) * (base + 1 - i);
        }
        const resto = (soma * 10) % 11;
        return resto === 10 ? 0 : resto;
    };

    if (Number(digitos.charAt(9)) !== calcDigito(9) || Number(digitos.charAt(10)) !== calcDigito(10)) {
        return null;
    }
    return digitos;
}

function validarNascimentoMaior18(texto: string): boolean {
    const match = /^(\d{2})-(\d{2})-(\d{4})$/.exec(texto.trim());
    if (!match) return false;

    const dia = Number(match[1]);
    const mes = Number(match[2]);
    const ano = Number(match[3]);
    const data = new Date(ano, mes - 1, dia);
    if (data.getFullYear() !== ano || data.getMonth() !== mes - 1 || data.getDate() !== dia) {
        return false;
    }

    const hoje = new Date();
    let idade = hoje.getFullYear() - ano;
    const aniversarioPassou = hoje.getMonth() + 1 > mes || (hoje.getMonth() + 1 === mes && hoje.getDate() >= dia);
    if (!aniversarioPassou) idade--;
    return idade >= 18;
}

function limparFormularioRecrutador(): void {
    formRecrutador.reset();
    inputRecrutadorId.value = '';
    btnCancelarEdicao.classList.add('escondido');
}

formRecrutador.addEventListener('submit', (e: Event) => {
    e.preventDefault();
    const idEditando = inputRecrutadorId.value ? Number(inputRecrutadorId.value) : 0;
    const nome = inputRecrutadorNome.value.trim();
    const area = inputRecrutadorArea.value.trim();
    const descricao = inputRecrutadorDescricao.value.trim();
    const email = inputRecrutadorEmail.value.trim();
    const cpfLimpo = validarCpf(inputRecrutadorCpf.value);
    const dataNascimento = inputRecrutadorNascimento.value.trim();
    const senha = inputRecrutadorSenha.value;

    if (!nome || !area) {
        alert('Nome e área de recrutamento são obrigatórios.');
        return;
    }
    if (!validarEmail(email)) {
        alert('E-mail inválido.');
        return;
    }
    if (!cpfLimpo) {
        alert('CPF inválido.');
        return;
    }
    if (!validarNascimentoMaior18(dataNascimento)) {
        alert('Data de nascimento inválida (use dd-mm-aaaa e idade mínima de 18 anos).');
        return;
    }
    if (recrutadores.some(r => r.email === email && r.id !== idEditando)) {
        alert('Já existe um recrutador cadastrado com este e-mail.');
        return;
    }
    if (recrutadores.some(r => r.cpf.replace(/\D/g, '') === cpfLimpo && r.id !== idEditando)) {
        alert('Já existe um recrutador cadastrado com este CPF.');
        return;
    }

    if (idEditando > 0) {
        const atual = recrutadores.find(r => r.id === idEditando && r.idEmpresa === empresaAtual.id);
        if (!atual) return;
        atual.nome = nome;
        atual.areaRecrutamento = area;
        atual.descricao = descricao;
        atual.email = email;
        atual.cpf = cpfLimpo;
        atual.dataNascimento = dataNascimento;
        if (senha.trim() !== '') {
            atual.senha = senha;
        }
    } else {
        if (senha.trim() === '') {
            alert('A senha é obrigatória no cadastro.');
            return;
        }
        const proximoId = recrutadores.reduce((max, r) => Math.max(max, r.id), 100) + 1;
        recrutadores.push({
            id: proximoId,
            idEmpresa: empresaAtual.id,
            nome,
            areaRecrutamento: area,
            descricao,
            email,
            cpf: cpfLimpo,
            dataNascimento,
            senha,
            tipo: 'recrutador'
        });
    }

    salvarTudo();
    limparFormularioRecrutador();
    renderizarRecrutadores();
});

btnCancelarEdicao.addEventListener('click', limparFormularioRecrutador);

listaRecrutadores.addEventListener('click', (event: Event) => {
    const botao = (event.target as HTMLElement).closest('button');
    if (!botao) return;

    const id = Number(botao.dataset.id);
    if (botao.dataset.acao === 'editar-recrutador') {
        const rec = recrutadores.find(r => r.id === id && r.idEmpresa === empresaAtual.id);
        if (!rec) return;
        inputRecrutadorId.value = String(rec.id);
        inputRecrutadorNome.value = rec.nome;
        inputRecrutadorArea.value = rec.areaRecrutamento;
        inputRecrutadorDescricao.value = rec.descricao;
        inputRecrutadorEmail.value = rec.email;
        inputRecrutadorCpf.value = rec.cpf;
        inputRecrutadorNascimento.value = rec.dataNascimento;
        inputRecrutadorSenha.value = '';
        btnCancelarEdicao.classList.remove('escondido');
    } else if (botao.dataset.acao === 'remover-recrutador') {
        recrutadores = recrutadores.filter(r => !(r.id === id && r.idEmpresa === empresaAtual.id));
        salvarTudo();
        renderizarRecrutadores();
    }
});

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