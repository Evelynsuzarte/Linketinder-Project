export const API_BASE_URL = 'http://localhost:8080';
const CHAVE_SESSAO = 'linketinder_usuario_logado';

export class HttpError extends Error {
    readonly status: number;
    readonly dados: unknown;

    constructor(mensagem: string, status: number, dados?: unknown) {
        super(mensagem);
        this.name = 'HttpError';
        this.status = status;
        this.dados = dados;
    }
}

export class HttpClient {
    private readonly baseUrl: string;

    constructor(baseUrl: string = API_BASE_URL) {
        this.baseUrl = baseUrl.replace(/\/+$/, '');
    }

    async get<T>(caminho: string): Promise<T> {
        return this.requisitar<T>('GET', caminho);
    }

    async post<T>(caminho: string, corpo: unknown): Promise<T> {
        return this.requisitar<T>('POST', caminho, corpo);
    }

    async put<T>(caminho: string, corpo: unknown): Promise<T> {
        return this.requisitar<T>('PUT', caminho, corpo);
    }

    async delete<T>(caminho: string, corpo?: unknown): Promise<T> {
        return this.requisitar<T>('DELETE', caminho, corpo);
    }

    private async requisitar<T>(metodo: string, caminho: string, corpo?: unknown): Promise<T> {
        const opcoes: RequestInit = {
            method: metodo,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (corpo !== undefined) {
            opcoes.body = JSON.stringify(corpo);
        }

        let resposta: Response;
        try {
            resposta = await fetch(`${this.baseUrl}${caminho}`, opcoes);
        } catch {
            throw new HttpError('Não foi possível conectar ao servidor. Verifique se o back-end está em execução.', 0);
        }

        const texto = await resposta.text();
        let dados: unknown = null;
        if (texto) {
            try {
                dados = JSON.parse(texto);
            } catch {
                dados = texto;
            }
        }

        if (!resposta.ok) {
            const mensagemErro =
                typeof dados === 'object' && dados !== null && 'erro' in dados
                    ? String((dados as { erro: unknown }).erro)
                    : `Erro HTTP ${resposta.status}`;
            throw new HttpError(mensagemErro, resposta.status, dados);
        }

        return dados as T;
    }
}

export const apiClient = new HttpClient(API_BASE_URL);

// ==========================================
// CONTRATOS DA API REST
// ==========================================

export interface SessaoCandidato {
    tipo: 'candidato';
    id: number;
    nome: string;
    sobrenome?: string;
    email: string;
    idade?: number;
    pais?: string;
    estado?: string;
    cep?: string;
    descricao?: string;
    competencias?: string[];
}

export interface SessaoEmpresa {
    tipo: 'empresa';
    id: number;
    nome: string;
    email: string;
    pais?: string;
    estado?: string;
    cep?: string;
    descricao?: string;
}

export interface SessaoRecrutador {
    tipo: 'recrutador';
    id: number;
    idEmpresa: number;
    nomeEmpresa?: string;
    nome: string;
    areaRecrutamento: string;
    descricao?: string;
    email: string;
}

export type UsuarioSessao = SessaoCandidato | SessaoEmpresa | SessaoRecrutador;

export interface CadastroCandidatoPayload {
    cpf: string;
    nome: string;
    sobrenome?: string;
    idade?: number | null;
    email: string;
    pais?: string;
    estado?: string;
    cep?: string;
    descricao?: string;
    senha: string;
    competencias?: string[];
}

export interface CadastroEmpresaPayload {
    cnpj: string;
    nome: string;
    email: string;
    pais?: string;
    estado?: string;
    cep?: string;
    descricao?: string;
    senha: string;
}

export interface RespostaCriacao {
    mensagem: string;
    id?: number;
}

export interface VagaAnonimaDto {
    id: number;
    nome: string;
    descricao: string;
    local: string;
    competencias: string[];
}

export interface VagaEmpresaDto {
    id: number;
    nome: string;
    descricao: string;
    local: string;
    competencias: string[];
    empresa?: {
        id: number;
        nome?: string;
    };
}

export interface RecrutadorDto {
    id: number;
    idEmpresa: number;
    nomeEmpresa?: string;
    nome: string;
    areaRecrutamento: string;
    descricao?: string;
    email: string;
}

export interface CandidatoInteressadoDto {
    idCandidato: number;
    idVaga: number;
    nomeVaga: string;
    descricaoCandidato: string;
    competencias: string[];
}

export interface MatchCandidatoDto {
    nomeVaga: string;
    nomeEmpresa: string;
    localVaga: string;
}

export interface MatchEmpresaDto {
    nomeVaga: string;
    nomeCandidato: string;
    sobrenomeCandidato?: string;
    emailCandidato: string;
}

// ==========================================
// SESSÃO NO NAVEGADOR
// ==========================================

export function salvarSessao(usuario: UsuarioSessao): void {
    localStorage.setItem(CHAVE_SESSAO, JSON.stringify(usuario));
}

export function obterSessao(): UsuarioSessao | null {
    const bruto = localStorage.getItem(CHAVE_SESSAO);
    if (!bruto) {
        return null;
    }
    try {
        return JSON.parse(bruto) as UsuarioSessao;
    } catch {
        return null;
    }
}

export function limparSessao(): void {
    localStorage.removeItem(CHAVE_SESSAO);
}

// ==========================================
// CHAMADAS À API
// ==========================================

export function loginApi(email: string, senha: string): Promise<UsuarioSessao> {
    return apiClient.post<UsuarioSessao>('/login', { email, senha });
}

export function cadastrarCandidatoApi(payload: CadastroCandidatoPayload): Promise<RespostaCriacao> {
    return apiClient.post<RespostaCriacao>('/candidatos', payload);
}

export function cadastrarEmpresaApi(payload: CadastroEmpresaPayload): Promise<RespostaCriacao> {
    return apiClient.post<RespostaCriacao>('/empresas', payload);
}

export function listarCompetenciasApi(): Promise<string[]> {
    return apiClient.get<string[]>('/competencias');
}

export function salvarCompetenciasCandidatoApi(
    idCandidato: number,
    competencias: string[]
): Promise<{ mensagem: string; idCandidato: number; competencias: string[] }> {
    return apiClient.post('/competencias', { idCandidato, competencias });
}

export function listarVagasNaoCurtidasApi(idCandidato: number): Promise<VagaAnonimaDto[]> {
    return apiClient.get<VagaAnonimaDto[]>(`/vagas?idCandidato=${idCandidato}`);
}

export function listarVagasPorEmpresaApi(idEmpresa: number): Promise<VagaEmpresaDto[]> {
    return apiClient.get<VagaEmpresaDto[]>(`/vagas?idEmpresa=${idEmpresa}`);
}

export function cadastrarVagaApi(payload: {
    nome: string;
    descricao: string;
    local: string;
    idEmpresa: number;
    competencias: string[];
}): Promise<RespostaCriacao> {
    return apiClient.post<RespostaCriacao>('/vagas', payload);
}

export function atualizarVagaApi(payload: {
    id: number;
    nome: string;
    descricao: string;
    local: string;
    idEmpresa: number;
    competencias: string[];
    idRecrutador?: number;
}): Promise<RespostaCriacao> {
    return apiClient.put<RespostaCriacao>('/vagas', payload);
}

export function removerVagaApi(id: number, idRecrutador?: number): Promise<{ mensagem: string }> {
    const query = idRecrutador !== undefined ? `?id=${id}&idRecrutador=${idRecrutador}` : `?id=${id}`;
    return apiClient.delete<{ mensagem: string }>(`/vagas${query}`);
}

export function listarRecrutadoresApi(idEmpresa: number): Promise<RecrutadorDto[]> {
    return apiClient.get<RecrutadorDto[]>(`/recrutadores?idEmpresa=${idEmpresa}`);
}

export function cadastrarRecrutadorApi(payload: {
    idEmpresa: number;
    nome: string;
    areaRecrutamento: string;
    descricao: string;
    email: string;
    cpf: string;
    dataNascimento: string;
    senha: string;
}): Promise<RespostaCriacao> {
    return apiClient.post<RespostaCriacao>('/recrutadores', payload);
}

export function atualizarRecrutadorApi(payload: {
    id: number;
    idEmpresa: number;
    nome: string;
    areaRecrutamento: string;
    descricao: string;
    email: string;
    cpf: string;
    dataNascimento: string;
    senha?: string;
}): Promise<RespostaCriacao> {
    return apiClient.put<RespostaCriacao>('/recrutadores', payload);
}

export function removerRecrutadorApi(id: number): Promise<{ mensagem: string }> {
    return apiClient.delete<{ mensagem: string }>(`/recrutadores?id=${id}`);
}

export function listarInteressadosApi(idEmpresa: number): Promise<CandidatoInteressadoDto[]> {
    return apiClient.get<CandidatoInteressadoDto[]>(`/matches?tipo=interessados&idEmpresa=${idEmpresa}`);
}

export function listarRejeitadosApi(idEmpresa: number): Promise<CandidatoInteressadoDto[]> {
    return apiClient.get<CandidatoInteressadoDto[]>(`/matches?tipo=rejeitados&idEmpresa=${idEmpresa}`);
}

export function listarMatchesCandidatoApi(idCandidato: number): Promise<MatchCandidatoDto[]> {
    return apiClient.get<MatchCandidatoDto[]>(`/matches?tipo=candidato&idCandidato=${idCandidato}`);
}

export function listarMatchesEmpresaApi(idEmpresa: number): Promise<MatchEmpresaDto[]> {
    return apiClient.get<MatchEmpresaDto[]>(`/matches?tipo=empresa&idEmpresa=${idEmpresa}`);
}

export function curtirVagaApi(idCandidato: number, idVaga: number): Promise<{ mensagem: string }> {
    return apiClient.post<{ mensagem: string }>('/matches', {
        acao: 'curtirVaga',
        idCandidato,
        idVaga
    });
}

export function curtirCandidatoApi(
    idEmpresa: number,
    idCandidato: number,
    idVaga: number,
    idRecrutador?: number
): Promise<{ mensagem: string }> {
    return apiClient.post<{ mensagem: string }>('/matches', {
        acao: 'curtirCandidato',
        idEmpresa,
        idCandidato,
        idVaga,
        idRecrutador
    });
}

export function rejeitarCandidatoApi(
    idEmpresa: number,
    idCandidato: number,
    idVaga: number,
    idRecrutador?: number
): Promise<{ mensagem: string }> {
    return apiClient.post<{ mensagem: string }>('/matches', {
        acao: 'rejeitarCandidato',
        idEmpresa,
        idCandidato,
        idVaga,
        idRecrutador
    });
}
