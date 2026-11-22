import { obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas, obterTodosRecrutadores, salvarUsuarios, salvarVagas } from "./storage.js";
function voltarParaLogin() {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como empresa ou recrutador.");
}
const dadosLogado = localStorage.getItem('linketinder_usuario_logado');
const logado = dadosLogado ? JSON.parse(dadosLogado) : null;
if (logado === null || (logado.tipo !== 'empresa' && logado.tipo !== 'recrutador')) {
    voltarParaLogin();
}
const candidatos = obterTodosCandidatos();
const empresas = obterTodasEmpresas();
const recrutadores = obterTodosRecrutadores();
const vagas = obterTodasVagas();
let empresaAtual;
let paginaPainel = "painel_empresa.html";
if (logado.tipo === 'empresa') {
    empresaAtual = empresas.find(e => e.id === logado.id);
    paginaPainel = "painel_empresa.html";
}
else {
    const rec = recrutadores.find(r => r.id === logado.id);
    if (rec) {
        empresaAtual = empresas.find(e => e.id === rec.idEmpresa);
    }
    paginaPainel = "painel_recrutador.html";
}
if (!empresaAtual) {
    voltarParaLogin();
}
const empresaConfirmada = empresaAtual;
const linkVoltar = document.querySelector('#link-voltar');
const btnCancelar = document.querySelector('#btn-cancelar-vaga');
const infoEmpresa = document.querySelector('#empresa-vinculada');
const formCriarVaga = document.querySelector('#form-criar-vaga');
const inputNome = document.querySelector('#vaga-nome');
const inputDescricao = document.querySelector('#vaga-descricao');
const inputCompetencias = document.querySelector('#vaga-competencias');
linkVoltar.href = paginaPainel;
btnCancelar.href = paginaPainel;
infoEmpresa.textContent = `Empresa vinculada: ${empresaConfirmada.nome}`;
formCriarVaga.addEventListener('submit', (e) => {
    e.preventDefault();
    const nome = inputNome.value.trim();
    const descricao = inputDescricao.value.trim();
    const competencias = inputCompetencias.value
        .split(',')
        .map(c => c.trim())
        .filter(c => c.length > 0);
    if (!nome || !descricao) {
        alert('Nome e descrição da vaga são obrigatórios.');
        return;
    }
    const proximoId = vagas.reduce((max, v) => Math.max(max, v.id), 0) + 1;
    vagas.push({
        id: proximoId,
        nome,
        descricao,
        competencias,
        empresa: empresaConfirmada,
        interessados: [],
        matches: []
    });
    if (!Array.isArray(empresaConfirmada.vagas)) {
        empresaConfirmada.vagas = [];
    }
    empresaConfirmada.vagas.push(proximoId);
    salvarVagas(vagas);
    salvarUsuarios([...candidatos, ...empresas]);
    window.location.href = paginaPainel;
});
//# sourceMappingURL=criar_vaga.js.map