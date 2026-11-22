import { HttpError, cadastrarVagaApi, obterSessao } from "./api.js";

function voltarParaLogin(): never {
    window.location.href = "acessar.html";
    throw new Error("Sessão inválida: faça login como empresa ou recrutador.");
}

const logado = obterSessao();
if (logado === null || (logado.tipo !== 'empresa' && logado.tipo !== 'recrutador')) {
    voltarParaLogin();
}

const idEmpresa = logado.tipo === 'empresa' ? logado.id : logado.idEmpresa;
const nomeEmpresa = logado.tipo === 'empresa' ? logado.nome : (logado.nomeEmpresa ?? `Empresa #${logado.idEmpresa}`);
const paginaPainel = logado.tipo === 'empresa' ? "painel_empresa.html" : "painel_recrutador.html";

const linkVoltar = document.querySelector('#link-voltar') as HTMLAnchorElement;
const btnCancelar = document.querySelector('#btn-cancelar-vaga') as HTMLAnchorElement;
const infoEmpresa = document.querySelector('#empresa-vinculada') as HTMLParagraphElement;
const formCriarVaga = document.querySelector('#form-criar-vaga') as HTMLFormElement;
const inputNome = document.querySelector('#vaga-nome') as HTMLInputElement;
const inputDescricao = document.querySelector('#vaga-descricao') as HTMLInputElement;
const inputCompetencias = document.querySelector('#vaga-competencias') as HTMLInputElement;

linkVoltar.href = paginaPainel;
btnCancelar.href = paginaPainel;
infoEmpresa.textContent = `Empresa vinculada: ${nomeEmpresa}`;

formCriarVaga.addEventListener('submit', async (e: Event) => {
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

    try {
        await cadastrarVagaApi({
            nome,
            descricao,
            local: 'Remoto',
            idEmpresa,
            competencias
        });
        alert('Vaga cadastrada com sucesso!');
        window.location.href = paginaPainel;
    } catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao cadastrar vaga.');
    }
});
