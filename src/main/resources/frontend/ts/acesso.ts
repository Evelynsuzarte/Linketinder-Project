import type { Candidato, Empresa, Recrutador, Vaga } from "./storage.js";
import {
    obterTodosUsuarios, obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas,
    obterTodosRecrutadores, salvarUsuarios, salvarVagas
} from "./storage.js";


let candidatos: Candidato[] = obterTodosCandidatos();
let empresas: Empresa[] = obterTodasEmpresas();
let vagas: Vaga[] = obterTodasVagas();

function salvarTudo(): void {
    salvarUsuarios([...candidatos, ...empresas]);
    salvarVagas(vagas);
}


// LOGIN
const inputEmail = document.querySelector('.form-email input') as HTMLInputElement;
const inputSenha = document.querySelector('.form-senha input') as HTMLInputElement;

function buscarSenha(email: string): Candidato | Empresa | Recrutador | undefined {
    const usuarios = obterTodosUsuarios();
    const recrutadores = obterTodosRecrutadores();
    return [...usuarios, ...recrutadores].find(u => u.email === email);
}

function fazerLogin(): void {
    const usuarioEncontrado = buscarSenha(inputEmail.value);

    if (usuarioEncontrado) {
        if (usuarioEncontrado.senha === inputSenha.value) {
            console.log("Login efetuado com sucesso!");
            localStorage.setItem('linketinder_usuario_logado', JSON.stringify(usuarioEncontrado));
            if (usuarioEncontrado.tipo === 'candidato') {
                window.location.href = "painel_candidato.html";
            } else if (usuarioEncontrado.tipo === 'empresa') {
                window.location.href = "painel_empresa.html";
            } else if (usuarioEncontrado.tipo === 'recrutador') {
                window.location.href = "painel_recrutador.html";
            }
        } else {
            alert("Senha incorreta!");
        }
    } else {
        alert("Usuário não encontrado!");
    }
}

const form = document.querySelector('#meuFormulario') as HTMLFormElement;
form.addEventListener('submit', (e) => {
    e.preventDefault();
    fazerLogin();
});