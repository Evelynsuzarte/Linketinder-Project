// ==========================================
// 2. DADOS FICTÍCIOS CONFORME A SUA ESTRUTURA
// ==========================================
// ==========================================
// DADOS FICTÍCIOS CORRIGIDOS (SEM ERROS DE TIPAGEM)
// ==========================================
// ==========================================
// DADOS FICTÍCIOS 100% CORRIGIDOS DE ACORDO COM A INTERFACE VAGA
// ==========================================
// ==========================================
// 2. OBJETOS DECLARADOS INDIVIDUALMENTE (EVITA UNDEFINED)
// ==========================================
// Candidatos Individuais
const cand1 = { id: 1, nome: "Gabriel Silva", email: "gabriel@email.com", descricao: "Dev Frontend.", cep: "01001-000", estado: "SP", tipo: "candidato", senha: "123", cpf: "12345678901", idade: 24 };
const cand2 = { id: 2, nome: "Mariana Souza", email: "mariana@email.com", descricao: "Designer UX/UI.", cep: "40020-000", estado: "BA", tipo: "candidato", senha: "abc", cpf: "98765432100", idade: 28 };
const cand3 = { id: 3, nome: "Carlos Eduardo", email: "carlos@email.com", descricao: "Dev Backend.", cep: "30140-010", estado: "MG", tipo: "candidato", senha: "456", cpf: "45612378922", idade: 31 };
// Empresas Individuais
const emp1 = { id: 11, nome: "Tech Solutions", email: "vagas@techsolutions.com", descricao: "Softwares.", cep: "20040-002", estado: "RJ", tipo: "empresa", senha: "admin", cnpj: "12345678000199", pais: "Brasil" };
const emp2 = { id: 22, nome: "Inova Studio", email: "rh@inovastudio.com", descricao: "Marketing.", cep: "04538-132", estado: "SP", tipo: "empresa", senha: "inova", cnpj: "98765432000188", pais: "Brasil" };
const emp3 = { id: 33, nome: "SoftGlobal Inc.", email: "careers@softglobal.com", descricao: "Cloud.", cep: "99999-999", estado: "SP", tipo: "empresa", senha: "cloud", cnpj: "55544433000122", pais: "Estados Unidos" };
// Arrays agrupados para uso na inicialização do LocalStorage
const candidatosIniciais = [cand1, cand2, cand3];
const empresasIniciais = [emp1, emp2, emp3];
// Vagas referenciando diretamente as constantes seguras (0 Erros)
const vagasIniciais = [
    // --- VAGAS TECH SOLUTIONS (emp1) ---
    { id: 1, nome: "Dev Frontend Júnior", interessados: [cand1], empresa: emp1, descricao: "Atuar com HTML/CSS.", competencias: ["HTML", "CSS", "TypeScript"] },
    { id: 2, nome: "Dev Backend Node.js", interessados: [], empresa: emp1, descricao: "APIs RESTful.", competencias: ["Node.js", "Express", "SQL"] },
    { id: 3, nome: "QA Engineer", interessados: [cand2], empresa: emp1, descricao: "Testes automatizados.", competencias: ["Cypress", "JavaScript"] },
    // --- VAGAS INOVA STUDIO (emp2) ---
    { id: 4, nome: "Designer UX/UI Pleno", interessados: [cand2], empresa: emp2, descricao: "Pesquisas com usuários.", competencias: ["Figma", "UI Design"] },
    { id: 5, nome: "Dev Mobile React Native", interessados: [cand1, cand3], empresa: emp2, descricao: "Apps Android e iOS.", competencias: ["React Native", "TypeScript"] },
    { id: 6, nome: "Product Owner (PO)", interessados: [], empresa: emp2, descricao: "Gerenciar backlog.", competencias: ["Scrum", "Agile"] },
    // --- VAGAS SOFTGLOBAL INC (emp3) ---
    { id: 7, nome: "Cloud Solutions Architect", interessados: [], empresa: emp3, descricao: "Desenhar arquiteturas cloud.", competencias: ["AWS", "Terraform", "Docker"] },
    { id: 8, nome: "DevOps Engineer Sênior", interessados: [cand3], empresa: emp3, descricao: "Estruturar CI/CD.", competencias: ["Kubernetes", "Jenkins", "Linux"] },
    { id: 9, nome: "Analista de Segurança", interessados: [], empresa: emp3, descricao: "Monitoramento de redes.", competencias: ["Cybersecurity", "Firewalls"] }
];
// ==========================================
// 3. CARGA E LEITURA NO LOCALSTORAGE
// ==========================================
// Inicializa as chaves caso estejam vazias no navegador
function inicializarLocalStorage() {
    const usuariosSalvos = localStorage.getItem('linketinder_usuarios');
    const vagasSalvas = localStorage.getItem('linketinder_vagas');
    if (usuariosSalvos === null || usuariosSalvos.trim() === "") {
        // Junta os arrays iniciais de Candidatos e Empresas em uma única lista de usuários
        const todosUsuarios = [...candidatosIniciais, ...empresasIniciais];
        localStorage.setItem('linketinder_usuarios', JSON.stringify(todosUsuarios));
    }
    if (vagasSalvas === null || vagasSalvas.trim() === "") {
        localStorage.setItem('linketinder_vagas', JSON.stringify(vagasIniciais));
    }
}
// Funções de Leitura (Retornam os dados atuais do LocalStorage)
function obterTodosUsuarios() {
    const dados = localStorage.getItem('linketinder_usuarios');
    return dados ? JSON.parse(dados) : [];
}
function obterTodasVagas() {
    const dados = localStorage.getItem('linketinder_vagas');
    return dados ? JSON.parse(dados) : [];
}
// Executa a injeção automática de dados fictícios
inicializarLocalStorage();
export {};
//# sourceMappingURL=index.js.map