
-------------------------- CRIAÇÕES DE TABELAS
CREATE TABLE candidatos (
    id SERIAL PRIMARY KEY NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    nome VARCHAR(20),
    sobrenome VARCHAR(20),
    ---data_nascimento DATE,
    idade int,
    email VARCHAR(50),
    pais VARCHAR(20),
    cep VARCHAR(10),
    descricao VARCHAR(100),
    senha VARCHAR(20),
    estado VARCHAR(20)
);

CREATE TABLE empresas (
    id SERIAL PRIMARY KEY NOT NULL,
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    nome VARCHAR(50),
    email VARCHAR(50),
    pais VARCHAR(20),
    cep VARCHAR(10),
    descricao VARCHAR(100),
    senha VARCHAR(20),
    estado VARCHAR(20)
);

CREATE TABLE vagas (
    id SERIAL PRIMARY KEY NOT NULL,
    nome VARCHAR(20),
    descricao VARCHAR(100),
    local_vaga VARCHAR(20),
    id_empresa INT NOT NULL,

    FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE
);

CREATE TABLE competencias (
    id SERIAL PRIMARY KEY NOT NULL,
    nome VARCHAR(50) UNIQUE NOT NULL
);


CREATE TABLE candidato_competencia (
    id_candidato INT NOT NULL,
    id_competencia INT NOT NULL,

    PRIMARY KEY (id_candidato, id_competencia),
    FOREIGN KEY (id_candidato) REFERENCES candidatos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencias(id) ON DELETE CASCADE
);


CREATE TABLE vaga_competencia (
    id_vaga INT NOT NULL,
    id_competencia INT NOT NULL,

    PRIMARY KEY (id_vaga, id_competencia),
    FOREIGN KEY (id_vaga) REFERENCES vagas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencias(id) ON DELETE CASCADE
);

CREATE TABLE curtida_candidato_vaga (
    id_candidato INT,
    id_vaga INT,

    PRIMARY KEY (id_candidato, id_vaga),
    FOREIGN KEY (id_candidato) REFERENCES candidatos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga) REFERENCES vagas(id) ON DELETE CASCADE
);

CREATE TABLE curtida_empresa_candidato (
    id_empresa INT,
    id_candidato INT,
    id_vaga INT,

    PRIMARY KEY (id_empresa, id_candidato, id_vaga),
    FOREIGN KEY (id_candidato) REFERENCES candidatos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga) REFERENCES vagas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE
);

CREATE TABLE match (
    id SERIAL PRIMARY KEY,
    id_empresa INT,
    id_candidato INT,
    id_vaga INT,

    UNIQUE (id_empresa, id_candidato, id_vaga),
    FOREIGN KEY (id_candidato) REFERENCES candidatos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga) REFERENCES vagas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE
);


--------------------------------- INSERÇÕES

---- CANDIDATOS
INSERT INTO candidatos
(cpf, nome, sobrenome, idade, email, pais, cep, descricao, senha, estado)
VALUES
    ('111.111.111-11', 'Sandubinha', 'Silva', 26, 'sandubinha@email.com', 'Brasil', '40000-000', 'Desenvolvedor backend', '123456', 'Bahia'),
    ('222.222.222-22', 'Carlos', 'Santos', 27, 'carlos@email.com', 'Brasil', '40100-000', 'Desenvolvedor fullstack', '123456', 'São Paulo'),
    ('333.333.333-33', 'Mariana', 'Oliveira', 25, 'mariana@email.com', 'Brasil', '40200-000', 'Analista de dados', '123456', 'Pernambuco'),
    ('444.444.444-44', 'Joao', 'Costa', 27, 'joao@email.com', 'Brasil', '40300-000', 'Desenvolvedor Java', '123456','Santa Catarina'),
    ('555.555.555-55', 'Ana', 'Souza', 24, 'ana@email.com', 'Brasil', '40400-000', 'Desenvolvedora frontend', '123456', 'Rio de Janeiro');

-- INSERT INTO candidatos
--     (cpf, nome, sobrenome, data_nascimento, email, pais, cep, descricao, senha)
-- VALUES
--     ('111.111.111-11', 'Sandubinha', 'Silva', '2000-05-15', 'sandubinha@email.com', 'Brasil', '40000-000', 'Desenvolvedor backend', '123456'),
--     ('222.222.222-22', 'Carlos', 'Santos', '1999-08-20', 'carlos@email.com', 'Brasil', '40100-000', 'Desenvolvedor fullstack', '123456'),
--     ('333.333.333-33', 'Mariana', 'Oliveira', '2001-02-10', 'mariana@email.com', 'Brasil', '40200-000', 'Analista de dados', '123456'),
--     ('444.444.444-44', 'Joao', 'Costa', '1998-11-25', 'joao@email.com', 'Brasil', '40300-000', 'Desenvolvedor Java', '123456'),
--     ('555.555.555-55', 'Ana', 'Souza', '2002-07-30', 'ana@email.com', 'Brasil', '40400-000', 'Desenvolvedora frontend', '123456');


---- EMPRESAS
INSERT INTO empresas
    (cnpj, nome, email, pais, cep, descricao, senha)
VALUES
    ('11.111.111/0001-11', 'Pastelsoft', 'contato@pastelsoft.com', 'Brasil', '40000-001', 'Empresa de desenvolvimento de software', '123456'),
    ('22.222.222/0001-22', 'Tech Bahia', 'contato@techbahia.com', 'Brasil', '40100-001', 'Empresa de tecnologia', '123456'),
    ('33.333.333/0001-33', 'Data Solutions', 'contato@datasolutions.com', 'Brasil', '40200-001', 'Empresa de dados e analytics', '123456'),
    ('44.444.444/0001-44', 'CodeMais', 'contato@codemais.com', 'Brasil', '40300-001', 'Empresa de desenvolvimento web', '123456'),
    ('55.555.555/0001-55', 'InovaTech', 'contato@inovatech.com', 'Brasil', '40400-001', 'Empresa de inovação e tecnologia', '123456');

---- COMPETENCIAS
INSERT INTO competencias 
    (nome)
VALUES
    ('Python'),
    ('Java'),
    ('Groovy'),
    ('SQL'),
    ('JavaScript'),
    ('HTML'),
    ('CSS'),
    ('Angular'),
    ('Spring'),
    ('PostgreSQL'),
    ('Git'),
    ('Docker');


---- VAGAS
INSERT INTO vagas
    (nome, descricao, local_vaga, id_empresa)
VALUES
    ('Backend Python', 'Desenvolver APIs', 'Salvador', 1),
    ('Java Backend', 'Desenvolver sistemas', 'Feira de Santana', 1),
    ('Analista Dados', 'Analisar dados', 'Salvador', 2),
    ('Frontend Angular', 'Criar interfaces', 'Sao Paulo', 3),
    ('Dev Fullstack', 'Desenvolver aplicacoes', 'Salvador', 4),
    ('Dev Java', 'Desenvolver APIs', 'Recife', 5);

---- COMPETENCIAS DOS CANDIDATOS
INSERT INTO candidato_competencia
    (id_candidato, id_competencia)
VALUES
    -- Sandubinha
    (1, 1),  -- Python
    (1, 4),  -- SQL
    (1, 10), -- PostgreSQL
    (1, 11), -- Git

    -- Carlos
    (2, 2),  -- Java
    (2, 9),  -- Spring
    (2, 4),  -- SQL
    (2, 11), -- Git

    -- Mariana
    (3, 1),  -- Python
    (3, 4),  -- SQL
    (3, 10), -- PostgreSQL

    -- Joao
    (4, 2),  -- Java
    (4, 3),  -- Groovy
    (4, 9),  -- Spring
    (4, 11), -- Git

    -- Ana
    (5, 5),  -- JavaScript
    (5, 6),  -- HTML
    (5, 7),  -- CSS
    (5, 8);  -- Angular

---- COMPETENCIAS DAS VAGAS
INSERT INTO vaga_competencia
    (id_vaga, id_competencia)
VALUES
    -- Vaga 1 - Backend Python
    (1, 1),  -- Python
    (1, 4),  -- SQL
    (1, 10), -- PostgreSQL
    (1, 11), -- Git

    -- Vaga 2 - Java Backend
    (2, 2),  -- Java
    (2, 9),  -- Spring
    (2, 4),  -- SQL
    (2, 11), -- Git

    -- Vaga 3 - Analista Dados
    (3, 1),  -- Python
    (3, 4),  -- SQL
    (3, 10), -- PostgreSQL

    -- Vaga 4 - Frontend Angular
    (4, 5),  -- JavaScript
    (4, 6),  -- HTML
    (4, 7),  -- CSS
    (4, 8),  -- Angular

    -- Vaga 5 - Dev Fullstack
    (5, 5),  -- JavaScript
    (5, 1),  -- Python
    (5, 4),  -- SQL
    (5, 11), -- Git

    -- Vaga 6 - Dev Java
    (6, 2),  -- Java
    (6, 9),  -- Spring
    (6, 11); -- Git