import { HttpError, cadastrarCandidatoApi, cadastrarEmpresaApi, listarCompetenciasApi, salvarCompetenciasCandidatoApi, salvarSessao } from "./api.js";
// Mapeamento dos botões de rádio e seções por classe
const radioCandidato = document.querySelector('.classe-radio-candidato');
const radioEmpresa = document.querySelector('.classe-radio-empresa');
const secaoCandidato = document.querySelector('.cadastro-candidato');
const secaoEmpresa = document.querySelector('.cadastro-empresa');
const formulario = document.querySelector('.formulario-adicionar-conta');
// Mapeamento dos inputs
const inputNome = document.querySelector('.form-nome input');
const inputEmail = document.querySelector('.form-email input');
const inputDescricao = document.querySelector('.form-descricao textarea');
const inputCep = document.querySelector('.form-cep input');
const selectEstado = document.querySelector('.form-estado select');
const inputCpf = document.querySelector('.form-cpf input');
const inputIdade = document.querySelector('.form-idade input');
const inputCnpj = document.querySelector('.form-cnpj input');
const inputPais = document.querySelector('.form-pais input');
const inputSenha = document.querySelector('.form-senha input');
const inputSenhaEmpresa = document.querySelector('.form-senha-empresa input');
// Seção de competências pós-cadastro do candidato
const secaoCompetencias = document.querySelector('.secao-competencias');
const listaCompetenciasExistentes = document.querySelector('.lista-competencias-existentes');
const inputNovasCompetencias = document.querySelector('#input-novas-competencias');
const btnSalvarCompetencias = document.querySelector('.btn-salvar-competencias');
const btnPularCompetencias = document.querySelector('.btn-pular-competencias');
let candidatoRecemCadastrado = null;
function alternarCampos() {
    if (radioCandidato.checked) {
        secaoCandidato.style.display = 'block';
        secaoEmpresa.style.display = 'none';
        inputCnpj.value = '';
        inputPais.value = '';
    }
    else if (radioEmpresa.checked) {
        secaoCandidato.style.display = 'none';
        secaoEmpresa.style.display = 'block';
        inputCpf.value = '';
        inputIdade.value = '';
    }
    else {
        secaoCandidato.style.display = 'none';
        secaoEmpresa.style.display = 'none';
        inputCpf.value = '';
        inputIdade.value = '';
        inputCnpj.value = '';
        inputPais.value = '';
    }
}
async function exibirEtapaCompetencias(candidato) {
    candidatoRecemCadastrado = candidato;
    salvarSessao(candidato);
    formulario.style.display = 'none';
    secaoCompetencias.style.display = 'block';
    listaCompetenciasExistentes.innerHTML = '<span>Carregando competências...</span>';
    try {
        const competencias = await listarCompetenciasApi();
        listaCompetenciasExistentes.innerHTML = '';
        if (competencias.length === 0) {
            listaCompetenciasExistentes.innerHTML = '<span>Nenhuma competência cadastrada ainda.</span>';
            return;
        }
        competencias.forEach((comp) => {
            const label = document.createElement('label');
            label.style.display = 'inline-flex';
            label.style.alignItems = 'center';
            label.style.gap = '6px';
            label.style.cursor = 'pointer';
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.value = comp;
            checkbox.className = 'checkbox-competencia';
            label.appendChild(checkbox);
            label.appendChild(document.createTextNode(comp));
            listaCompetenciasExistentes.appendChild(label);
        });
    }
    catch (erro) {
        listaCompetenciasExistentes.innerHTML = '';
        if (erro instanceof HttpError) {
            alert(`Não foi possível carregar competências: ${erro.message}`);
        }
    }
}
async function salvarCompetencias() {
    if (!candidatoRecemCadastrado) {
        return;
    }
    const selecionadas = Array.from(document.querySelectorAll('.checkbox-competencia:checked')).map((cb) => cb.value.trim());
    const digitadas = inputNovasCompetencias.value
        .split(',')
        .map((c) => c.trim())
        .filter((c) => c.length > 0);
    const todas = Array.from(new Set([...selecionadas, ...digitadas]));
    if (todas.length === 0) {
        alert('Selecione ou digite ao menos uma competência.');
        return;
    }
    try {
        const resposta = await salvarCompetenciasCandidatoApi(candidatoRecemCadastrado.id, todas);
        candidatoRecemCadastrado = {
            ...candidatoRecemCadastrado,
            competencias: resposta.competencias
        };
        salvarSessao(candidatoRecemCadastrado);
        alert('Competências salvas com sucesso!');
        window.location.href = 'painel_candidato.html';
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao salvar competências.');
    }
}
radioCandidato.addEventListener('change', alternarCampos);
radioEmpresa.addEventListener('change', alternarCampos);
btnSalvarCompetencias.addEventListener('click', () => {
    void salvarCompetencias();
});
btnPularCompetencias.addEventListener('click', () => {
    window.location.href = 'painel_candidato.html';
});
formulario.addEventListener('submit', async (event) => {
    event.preventDefault();
    const nome = inputNome.value.trim();
    const email = inputEmail.value.trim();
    const descricao = inputDescricao.value.trim();
    const cep = inputCep.value.trim();
    const estado = selectEstado.value;
    if (!nome || !email) {
        alert('Por favor, preencha o Nome e o Email.');
        return;
    }
    try {
        if (radioCandidato.checked) {
            const cpf = inputCpf.value.trim();
            const idadeValor = inputIdade.value.trim();
            const senha = inputSenha.value;
            if (!cpf || !idadeValor || !senha) {
                alert('Por favor, preencha CPF, Idade e Senha.');
                return;
            }
            const idade = parseInt(idadeValor, 10);
            const resposta = await cadastrarCandidatoApi({
                cpf,
                nome,
                idade: Number.isNaN(idade) ? null : idade,
                email,
                estado,
                cep,
                descricao,
                senha,
                competencias: []
            });
            alert('Cadastro de candidato realizado com sucesso! Agora informe suas competências.');
            await exibirEtapaCompetencias({
                tipo: 'candidato',
                id: resposta.id ?? 0,
                nome,
                email,
                idade,
                estado,
                cep,
                descricao,
                competencias: []
            });
            return;
        }
        if (radioEmpresa.checked) {
            const cnpj = inputCnpj.value.trim();
            const pais = inputPais.value.trim();
            const senha = inputSenhaEmpresa.value;
            if (!cnpj || !pais || !senha) {
                alert('Por favor, preencha CNPJ, País e Senha.');
                return;
            }
            await cadastrarEmpresaApi({
                cnpj,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao,
                senha
            });
            alert('Cadastro de empresa realizado com sucesso!');
            formulario.reset();
            alternarCampos();
            return;
        }
        alert('Selecione se você é candidato ou empresa.');
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert('Erro inesperado ao realizar cadastro.');
    }
});
alternarCampos();
//# sourceMappingURL=cadastro.js.map