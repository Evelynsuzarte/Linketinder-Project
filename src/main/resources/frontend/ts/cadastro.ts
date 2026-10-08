import type { Candidato, Empresa, Vaga } from "./storage.js";
import {
    obterTodosUsuarios, obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas,
    salvarUsuarios, salvarVagas
} from "./storage.js";


// Mapeamento dos botões de rádio e seções por classe
const radioCandidato = document.querySelector('.classe-radio-candidato') as HTMLInputElement;
const radioEmpresa = document.querySelector('.classe-radio-empresa') as HTMLInputElement;
const secaoCandidato = document.querySelector('.cadastro-candidato') as HTMLDivElement;
const secaoEmpresa = document.querySelector('.cadastro-empresa') as HTMLDivElement;
const formulario = document.querySelector('.formulario-adicionar-conta') as HTMLFormElement;

// Mapeamento dos inputs baseado nas classes dos seus blocos
const inputNome = document.querySelector('.form-nome input') as HTMLInputElement;
const inputEmail = document.querySelector('.form-email input') as HTMLInputElement;
const inputDescricao = document.querySelector('.form-descricao textarea') as HTMLTextAreaElement;
const inputCep = document.querySelector('.form-cep input') as HTMLInputElement;
const selectEstado = document.querySelector('.form-estado select') as HTMLSelectElement;

// Inputs específicos por classe
const inputCpf = document.querySelector('.form-cpf input') as HTMLInputElement;
const inputIdade = document.querySelector('.form-idade input') as HTMLInputElement;
const inputCnpj = document.querySelector('.form-cnpj input') as HTMLInputElement;
const inputPais = document.querySelector('.form-pais input') as HTMLInputElement;
const inputSenha = document.querySelector('.form-senha input') as HTMLInputElement;
///////////// adicionar o input da senha da empresa /////////////////

// Função para alternar a exibição dos blocos de campos
function alternarCampos(): void {
    if (radioCandidato.checked) {
        secaoCandidato.style.display = 'block';
        secaoEmpresa.style.display = 'none';

        inputCnpj.value = '';
        inputPais.value = '';
    } else if (radioEmpresa.checked) {
        secaoCandidato.style.display = 'none';
        secaoEmpresa.style.display = 'block';

        inputCpf.value = '';
        inputIdade.value = '';
    } else {
        secaoCandidato.style.display = 'none';
        secaoEmpresa.style.display = 'none';

        inputCpf.value = '';
        inputIdade.value = '';
        inputCnpj.value = '';
        inputPais.value = '';
    }
}

// Gera o próximo id livre
function gerarNovoId(usuarios: (Candidato | Empresa)[]): number {
    const maiorId = usuarios.reduce((maior, u) => Math.max(maior, u.id), 0);
    return maiorId + 1;
}

// Ouvintes de evento para os botões de rádio
radioCandidato.addEventListener('change', alternarCampos);
radioEmpresa.addEventListener('change', alternarCampos);

// Evento de envio do formulário
formulario.addEventListener('submit', (event: Event) => {
    event.preventDefault();

    const usuarios = obterTodosUsuarios();

    const emailJaExiste = usuarios.some(u => u.email === inputEmail.value);
    if (emailJaExiste) {
        alert('Já existe uma conta com este e-mail.');
        return;
    }

    const dadosBase = {
        id: gerarNovoId(usuarios),
        nome: inputNome.value,
        email: inputEmail.value,
        descricao: inputDescricao.value,
        cep: inputCep.value,
        estado: selectEstado.value,
        senha: inputSenha.value
    };

    if (radioCandidato.checked) {
        if (!inputCpf.value || !inputIdade.value) {
            alert('Por favor, preencha o CPF e a Idade.');
            return;
        }

        const novoCandidato: Candidato = {
            ...dadosBase,
            tipo: 'candidato',
            cpf: inputCpf.value,
            idade: parseInt(inputIdade.value),
            vagasInteresse: []
        };

        usuarios.push(novoCandidato);

    } else if (radioEmpresa.checked) {
        if (!inputCnpj.value || !inputPais.value) {
            alert('Por favor, preencha o CNPJ e o País.');
            return;
        }

        const novaEmpresa: Empresa = {
            ...dadosBase,
            tipo: 'empresa',
            cnpj: inputCnpj.value,
            pais: inputPais.value,
            vagas: []
        };

        usuarios.push(novaEmpresa);

    } else {
        alert('Selecione se você é candidato ou empresa.');
        return;
    }

    salvarUsuarios(usuarios);

    alert('Cadastro realizado com sucesso!');
    formulario.reset();
    alternarCampos(); // Mantém a interface limpa e correta após o reset
});

alternarCampos();