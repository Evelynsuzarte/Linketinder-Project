import { obterTodosUsuarios, obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas, salvarUsuarios, salvarVagas } from "./storage.js";
// Mapeamento dos botões de rádio e seções por classe
const radioCandidato = document.querySelector('.classe-radio-candidato');
const radioEmpresa = document.querySelector('.classe-radio-empresa');
const secaoCandidato = document.querySelector('.cadastro-candidato');
const secaoEmpresa = document.querySelector('.cadastro-empresa');
const formulario = document.querySelector('.formulario-adicionar-conta');
// Mapeamento dos inputs baseado nas classes dos seus blocos
const inputNome = document.querySelector('.form-nome input');
const inputEmail = document.querySelector('.form-email input');
const inputDescricao = document.querySelector('.form-descricao textarea');
const inputCep = document.querySelector('.form-cep input');
const selectEstado = document.querySelector('.form-estado select');
// Inputs específicos por classe
const inputCpf = document.querySelector('.form-cpf input');
const inputIdade = document.querySelector('.form-idade input');
const inputCnpj = document.querySelector('.form-cnpj input');
const inputPais = document.querySelector('.form-pais input');
const inputSenha = document.querySelector('.form-senha input');
// Função para alternar a exibição dos blocos de campos
function alternarCampos() {
    if (radioCandidato.checked) {
        secaoCandidato.style.display = 'block';
        secaoEmpresa.style.display = 'none';
        // Limpa os dados do tipo oposto
        inputCnpj.value = '';
        inputPais.value = '';
    }
    else if (radioEmpresa.checked) {
        secaoCandidato.style.display = 'none';
        secaoEmpresa.style.display = 'block';
        // Limpa os dados do tipo oposto
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
// Gera o próximo id livre (maior id existente + 1), em vez de Math.random()
function gerarNovoId(usuarios) {
    const maiorId = usuarios.reduce((maior, u) => Math.max(maior, u.id), 0);
    return maiorId + 1;
}
// Ouvintes de evento para os botões de rádio
radioCandidato.addEventListener('change', alternarCampos);
radioEmpresa.addEventListener('change', alternarCampos);
// Evento de envio do formulário
formulario.addEventListener('submit', (event) => {
    event.preventDefault();
    // Lista atual de usuários (vinda do storage)
    const usuarios = obterTodosUsuarios();
    // O login busca por e-mail, então não pode haver e-mails repetidos
    const emailJaExiste = usuarios.some(u => u.email === inputEmail.value);
    if (emailJaExiste) {
        alert('Já existe uma conta com este e-mail.');
        return;
    }
    // Coleta dados base comuns
    const dadosBase = {
        id: gerarNovoId(usuarios),
        nome: inputNome.value,
        email: inputEmail.value,
        descricao: inputDescricao.value,
        cep: inputCep.value,
        estado: selectEstado.value,
        senha: inputSenha.value
    };
    // Validação e persistência condicional
    if (radioCandidato.checked) {
        if (!inputCpf.value || !inputIdade.value) {
            alert('Por favor, preencha o CPF e a Idade.');
            return;
        }
        const novoCandidato = {
            ...dadosBase,
            tipo: 'candidato',
            cpf: inputCpf.value,
            idade: parseInt(inputIdade.value),
            vagasInteresse: []
        };
        usuarios.push(novoCandidato);
    }
    else if (radioEmpresa.checked) {
        if (!inputCnpj.value || !inputPais.value) {
            alert('Por favor, preencha o CNPJ e o País.');
            return;
        }
        const novaEmpresa = {
            ...dadosBase,
            tipo: 'empresa',
            cnpj: inputCnpj.value,
            pais: inputPais.value,
            vagas: []
        };
        usuarios.push(novaEmpresa);
    }
    else {
        alert('Selecione se você é candidato ou empresa.');
        return;
    }
    salvarUsuarios(usuarios);
    alert('Cadastro realizado com sucesso!');
    formulario.reset();
    alternarCampos(); // Mantém a interface limpa e correta após o reset
});
alternarCampos();
