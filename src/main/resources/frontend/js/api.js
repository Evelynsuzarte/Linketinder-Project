export const API_BASE_URL = 'http://localhost:8080';
const CHAVE_SESSAO = 'linketinder_usuario_logado';
export class HttpError extends Error {
    status;
    dados;
    constructor(mensagem, status, dados) {
        super(mensagem);
        this.name = 'HttpError';
        this.status = status;
        this.dados = dados;
    }
}
export class HttpClient {
    baseUrl;
    constructor(baseUrl = API_BASE_URL) {
        this.baseUrl = baseUrl.replace(/\/+$/, '');
    }
    async get(caminho) {
        return this.requisitar('GET', caminho);
    }
    async post(caminho, corpo) {
        return this.requisitar('POST', caminho, corpo);
    }
    async put(caminho, corpo) {
        return this.requisitar('PUT', caminho, corpo);
    }
    async delete(caminho, corpo) {
        return this.requisitar('DELETE', caminho, corpo);
    }
    async requisitar(metodo, caminho, corpo) {
        const opcoes = {
            method: metodo,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        if (corpo !== undefined) {
            opcoes.body = JSON.stringify(corpo);
        }
        let resposta;
        try {
            resposta = await fetch(`${this.baseUrl}${caminho}`, opcoes);
        }
        catch {
            throw new HttpError('Não foi possível conectar ao servidor. Verifique se o back-end está em execução.', 0);
        }
        const texto = await resposta.text();
        let dados = null;
        if (texto) {
            try {
                dados = JSON.parse(texto);
            }
            catch {
                dados = texto;
            }
        }
        if (!resposta.ok) {
            const mensagemErro = typeof dados === 'object' && dados !== null && 'erro' in dados
                ? String(dados.erro)
                : `Erro HTTP ${resposta.status}`;
            throw new HttpError(mensagemErro, resposta.status, dados);
        }
        return dados;
    }
}
export const apiClient = new HttpClient(API_BASE_URL);
// ==========================================
// SESSÃO NO NAVEGADOR
// ==========================================
export function salvarSessao(usuario) {
    localStorage.setItem(CHAVE_SESSAO, JSON.stringify(usuario));
}
export function obterSessao() {
    const bruto = localStorage.getItem(CHAVE_SESSAO);
    if (!bruto) {
        return null;
    }
    try {
        return JSON.parse(bruto);
    }
    catch {
        return null;
    }
}
export function limparSessao() {
    localStorage.removeItem(CHAVE_SESSAO);
}
// ==========================================
// CHAMADAS À API
// ==========================================
export function loginApi(email, senha) {
    return apiClient.post('/login', { email, senha });
}
export function cadastrarCandidatoApi(payload) {
    return apiClient.post('/candidatos', payload);
}
export function cadastrarEmpresaApi(payload) {
    return apiClient.post('/empresas', payload);
}
export function listarCompetenciasApi() {
    return apiClient.get('/competencias');
}
export function salvarCompetenciasCandidatoApi(idCandidato, competencias) {
    return apiClient.post('/competencias', { idCandidato, competencias });
}
export function listarVagasNaoCurtidasApi(idCandidato) {
    return apiClient.get(`/vagas?idCandidato=${idCandidato}`);
}
export function listarVagasPorEmpresaApi(idEmpresa) {
    return apiClient.get(`/vagas?idEmpresa=${idEmpresa}`);
}
export function cadastrarVagaApi(payload) {
    return apiClient.post('/vagas', payload);
}
export function atualizarVagaApi(payload) {
    return apiClient.put('/vagas', payload);
}
export function removerVagaApi(id, idRecrutador) {
    const query = idRecrutador !== undefined ? `?id=${id}&idRecrutador=${idRecrutador}` : `?id=${id}`;
    return apiClient.delete(`/vagas${query}`);
}
export function listarRecrutadoresApi(idEmpresa) {
    return apiClient.get(`/recrutadores?idEmpresa=${idEmpresa}`);
}
export function cadastrarRecrutadorApi(payload) {
    return apiClient.post('/recrutadores', payload);
}
export function atualizarRecrutadorApi(payload) {
    return apiClient.put('/recrutadores', payload);
}
export function removerRecrutadorApi(id) {
    return apiClient.delete(`/recrutadores?id=${id}`);
}
export function listarInteressadosApi(idEmpresa) {
    return apiClient.get(`/matches?tipo=interessados&idEmpresa=${idEmpresa}`);
}
export function listarRejeitadosApi(idEmpresa) {
    return apiClient.get(`/matches?tipo=rejeitados&idEmpresa=${idEmpresa}`);
}
export function listarMatchesCandidatoApi(idCandidato) {
    return apiClient.get(`/matches?tipo=candidato&idCandidato=${idCandidato}`);
}
export function listarMatchesEmpresaApi(idEmpresa) {
    return apiClient.get(`/matches?tipo=empresa&idEmpresa=${idEmpresa}`);
}
export function curtirVagaApi(idCandidato, idVaga) {
    return apiClient.post('/matches', {
        acao: 'curtirVaga',
        idCandidato,
        idVaga
    });
}
export function curtirCandidatoApi(idEmpresa, idCandidato, idVaga, idRecrutador) {
    return apiClient.post('/matches', {
        acao: 'curtirCandidato',
        idEmpresa,
        idCandidato,
        idVaga,
        idRecrutador
    });
}
export function rejeitarCandidatoApi(idEmpresa, idCandidato, idVaga, idRecrutador) {
    return apiClient.post('/matches', {
        acao: 'rejeitarCandidato',
        idEmpresa,
        idCandidato,
        idVaga,
        idRecrutador
    });
}
//# sourceMappingURL=api.js.map