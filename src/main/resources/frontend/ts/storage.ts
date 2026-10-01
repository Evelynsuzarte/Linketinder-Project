// ==========================================
// 1. CONTRATOS DE DADOS (INTERFACES)
// ==========================================
export interface Usuario {
    id: number;
    nome: string;
    email: string;
    descricao: string;
    cep: string;
    estado: string;
    tipo: 'candidato' | 'empresa';
    senha: string;
}

export interface Candidato extends Usuario {
    cpf: string;
    idade: number;
    vagasInteresse: number[];     // ids das vagas em que demonstrou interesse
}

export interface Empresa extends Usuario {
    cnpj: string;
    pais: string;
    vagas: number[];              // ids das vagas criadas pela empresa
}

export interface Vaga {
    id: number;
    nome: string;                 // Nome/Título da vaga
    interessados: Candidato[];    // Lista de objetos do tipo Candidato
    empresa: Empresa;             // Objeto completo da Empresa criadora
    descricao: string;
    competencias: string[];       // Array de Strings
    matches?: number[];           // ids dos candidatos que receberam match (opcional)
}

// ==========================================
// 2. DADOS FICTÍCIOS (CARGA INICIAL)
// ==========================================

// Candidatos
const cand1: Candidato = { id: 1, nome: "Gabriel Silva", email: "gabriel@email.com", descricao: "Dev Frontend.", cep: "01001-000", estado: "SP", tipo: "candidato", senha: "123", cpf: "12345678901", idade: 24, vagasInteresse: [1, 5] };
const cand2: Candidato = { id: 2, nome: "Mariana Souza", email: "mariana@email.com", descricao: "Designer UX/UI.", cep: "40020-000", estado: "BA", tipo: "candidato", senha: "abc", cpf: "98765432100", idade: 28, vagasInteresse: [3, 4] };
const cand3: Candidato = { id: 3, nome: "Carlos Eduardo", email: "carlos@email.com", descricao: "Dev Backend.", cep: "30140-010", estado: "MG", tipo: "candidato", senha: "456", cpf: "45612378922", idade: 31, vagasInteresse: [5, 8] };

// Empresas
const emp1: Empresa = { id: 11, nome: "Tech Solutions", email: "vagas@techsolutions.com", descricao: "Softwares.", cep: "20040-002", estado: "RJ", tipo: "empresa", senha: "admin", cnpj: "12345678000199", pais: "Brasil", vagas: [1, 2, 3] };
const emp2: Empresa = { id: 22, nome: "Inova Studio", email: "rh@inovastudio.com", descricao: "Marketing.", cep: "04538-132", estado: "SP", tipo: "empresa", senha: "inova", cnpj: "98765432000188", pais: "Brasil", vagas: [4, 5, 6] };
const emp3: Empresa = { id: 33, nome: "SoftGlobal Inc.", email: "careers@softglobal.com", descricao: "Cloud.", cep: "99999-999", estado: "SP", tipo: "empresa", senha: "cloud", cnpj: "55544433000122", pais: "Estados Unidos", vagas: [7, 8, 9] };

const candidatosIniciais: Candidato[] = [cand1, cand2, cand3];
const empresasIniciais: Empresa[] = [emp1, emp2, emp3];

const vagasIniciais: Vaga[] = [
    // --- TECH SOLUTIONS (emp1) ---
    { id: 1, nome: "Dev Frontend Júnior", interessados: [cand1], empresa: emp1, descricao: "Atuar com HTML/CSS.", competencias: ["HTML", "CSS", "TypeScript"] },
    { id: 2, nome: "Dev Backend Node.js", interessados: [], empresa: emp1, descricao: "APIs RESTful.", competencias: ["Node.js", "Express", "SQL"] },
    { id: 3, nome: "QA Engineer", interessados: [cand2], empresa: emp1, descricao: "Testes automatizados.", competencias: ["Cypress", "JavaScript"] },

    // --- INOVA STUDIO (emp2) ---
    { id: 4, nome: "Designer UX/UI Pleno", interessados: [cand2], empresa: emp2, descricao: "Pesquisas com usuários.", competencias: ["Figma", "UI Design"] },
    { id: 5, nome: "Dev Mobile React Native", interessados: [cand1, cand3], empresa: emp2, descricao: "Apps Android e iOS.", competencias: ["React Native", "TypeScript"] },
    { id: 6, nome: "Product Owner (PO)", interessados: [], empresa: emp2, descricao: "Gerenciar backlog.", competencias: ["Scrum", "Agile"] },

    // --- SOFTGLOBAL INC (emp3) ---
    { id: 7, nome: "Cloud Solutions Architect", interessados: [], empresa: emp3, descricao: "Desenhar arquiteturas cloud.", competencias: ["AWS", "Terraform", "Docker"] },
    { id: 8, nome: "DevOps Engineer Sênior", interessados: [cand3], empresa: emp3, descricao: "Estruturar CI/CD.", competencias: ["Kubernetes", "Jenkins", "Linux"] },
    { id: 9, nome: "Analista de Segurança", interessados: [], empresa: emp3, descricao: "Monitoramento de redes.", competencias: ["Cybersecurity", "Firewalls"] }
];

// ==========================================
// 3. CARGA INICIAL NO LOCALSTORAGE
// ==========================================

// Grava os dados fictícios apenas se as chaves estiverem vazias
function inicializarLocalStorage(): void {
    const usuariosSalvos = localStorage.getItem('linketinder_usuarios');
    const vagasSalvas = localStorage.getItem('linketinder_vagas');

    if (usuariosSalvos === null || usuariosSalvos.trim() === "") {
        const todosUsuarios = [...candidatosIniciais, ...empresasIniciais];
        localStorage.setItem('linketinder_usuarios', JSON.stringify(todosUsuarios));
    }

    if (vagasSalvas === null || vagasSalvas.trim() === "") {
        localStorage.setItem('linketinder_vagas', JSON.stringify(vagasIniciais));
    }
}

// ==========================================
// 4. MÉTODOS DE LEITURA DO LOCALSTORAGE
// ==========================================

/** Lê e retorna todos os usuários (Candidatos e Empresas juntos) */
export function obterTodosUsuarios(): (Candidato | Empresa)[] {
    const dados = localStorage.getItem('linketinder_usuarios');
    if (dados !== null && dados.trim() !== "") {
        return JSON.parse(dados) as (Candidato | Empresa)[];
    }
    return [];
}

/** Retorna estritamente a lista de Candidatos */
export function obterTodosCandidatos(): Candidato[] {
    const todos = obterTodosUsuarios();
    const candidatos = todos.filter(usuario => usuario.tipo === 'candidato');
    return candidatos as Candidato[];
}

/** Retorna estritamente a lista de Empresas */
export function obterTodasEmpresas(): Empresa[] {
    const todos = obterTodosUsuarios();
    const empresas = todos.filter(usuario => usuario.tipo === 'empresa');
    return empresas as Empresa[];
}

/** Lê e retorna todas as Vagas salvas no sistema */
export function obterTodasVagas(): Vaga[] {
    const dados = localStorage.getItem('linketinder_vagas');
    if (dados !== null && dados.trim() !== "") {
        return JSON.parse(dados) as Vaga[];
    }
    return [];
}

// ==========================================
// 5. MÉTODOS DE ESCRITA NO LOCALSTORAGE
// ==========================================

export function salvarUsuarios(usuarios: (Candidato | Empresa)[]): void {
    localStorage.setItem('linketinder_usuarios', JSON.stringify(usuarios));
}

export function salvarVagas(vagas: Vaga[]): void {
    localStorage.setItem('linketinder_vagas', JSON.stringify(vagas));
}

// Executa a carga inicial sempre que qualquer página importar este módulo
inicializarLocalStorage();