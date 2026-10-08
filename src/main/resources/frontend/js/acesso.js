import { obterTodosUsuarios, obterTodosCandidatos, obterTodasEmpresas, obterTodasVagas, salvarUsuarios, salvarVagas } from "./storage.js";
let candidatos = obterTodosCandidatos();
let empresas = obterTodasEmpresas();
let vagas = obterTodasVagas();
function salvarTudo() {
    salvarUsuarios([...candidatos, ...empresas]);
    salvarVagas(vagas);
}
// LOGIN
const inputEmail = document.querySelector('.form-email input');
const inputSenha = document.querySelector('.form-senha input');
function buscarSenha(email) {
    const usuarios = obterTodosUsuarios();
    return usuarios.find(u => u.email === email);
}
function fazerLogin() {
    const usuarioEncontrado = buscarSenha(inputEmail.value);
    if (usuarioEncontrado) {
        if (usuarioEncontrado.senha === inputSenha.value) {
            console.log("Login efetuado com sucesso!");
            localStorage.setItem('linketinder_usuario_logado', JSON.stringify(usuarioEncontrado));
            if (usuarioEncontrado.tipo === 'candidato') {
                window.location.href = "painel_candidato.html";
            }
            else if (usuarioEncontrado.tipo === 'empresa') {
                window.location.href = "painel_empresa.html";
            }
        }
        else {
            alert("Senha incorreta!");
        }
    }
    else {
        alert("Usuário não encontrado!");
    }
}
const form = document.querySelector('#meuFormulario');
form.addEventListener('submit', (e) => {
    e.preventDefault();
    fazerLogin();
});
//# sourceMappingURL=acesso.js.map