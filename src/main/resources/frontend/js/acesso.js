import { HttpError, loginApi, salvarSessao } from "./api.js";
const inputEmail = document.querySelector('.form-email input');
const inputSenha = document.querySelector('.form-senha input');
const form = document.querySelector('#meuFormulario');
async function fazerLogin() {
    const email = inputEmail.value.trim();
    const senha = inputSenha.value;
    if (!email || !senha) {
        alert("Informe e-mail e senha para entrar.");
        return;
    }
    try {
        const usuarioAutenticado = await loginApi(email, senha);
        salvarSessao(usuarioAutenticado);
        if (usuarioAutenticado.tipo === 'candidato') {
            window.location.href = "painel_candidato.html";
        }
        else if (usuarioAutenticado.tipo === 'empresa') {
            window.location.href = "painel_empresa.html";
        }
        else if (usuarioAutenticado.tipo === 'recrutador') {
            window.location.href = "painel_recrutador.html";
        }
    }
    catch (erro) {
        if (erro instanceof HttpError) {
            alert(erro.message);
            return;
        }
        alert("Erro inesperado ao tentar realizar login.");
    }
}
form.addEventListener('submit', (e) => {
    e.preventDefault();
    void fazerLogin();
});
//# sourceMappingURL=acesso.js.map