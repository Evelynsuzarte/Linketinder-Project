-- Script de migração incremental para a feature Recrutador
-- Seguro para executar em bancos PostgreSQL já existentes com dados.

CREATE TABLE IF NOT EXISTS recrutadores (
    id SERIAL PRIMARY KEY NOT NULL,
    id_empresa INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    area_recrutamento VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    email VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    senha VARCHAR(255) NOT NULL,

    FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rejeicao_empresa_candidato (
    id_empresa INT NOT NULL,
    id_candidato INT NOT NULL,
    id_vaga INT NOT NULL,
    id_recrutador INT,

    PRIMARY KEY (id_empresa, id_candidato, id_vaga),
    FOREIGN KEY (id_candidato) REFERENCES candidatos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_vaga) REFERENCES vagas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_empresa) REFERENCES empresas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_recrutador) REFERENCES recrutadores(id) ON DELETE SET NULL
);

ALTER TABLE curtida_empresa_candidato
    ADD COLUMN IF NOT EXISTS id_recrutador INT REFERENCES recrutadores(id) ON DELETE SET NULL;

ALTER TABLE match
    ADD COLUMN IF NOT EXISTS id_recrutador INT REFERENCES recrutadores(id) ON DELETE SET NULL;

-- Dados iniciais de exemplo (inseridos apenas se as empresas existirem e ainda não houver conflito)
INSERT INTO recrutadores
    (id_empresa, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha)
SELECT 1, 'Fernanda Lima', 'TI', 'Tech Recruiter focada em backend e dados', 'fernanda@pastelsoft.com', '666.666.666-66', '1995-03-12', '123456'
WHERE EXISTS (SELECT 1 FROM empresas WHERE id = 1)
ON CONFLICT DO NOTHING;

INSERT INTO recrutadores
    (id_empresa, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha)
SELECT 2, 'Rafael Mendes', 'RH', 'Recrutador de talentos de tecnologia', 'rafael@techbahia.com', '777.777.777-77', '1992-09-20', '123456'
WHERE EXISTS (SELECT 1 FROM empresas WHERE id = 2)
ON CONFLICT DO NOTHING;
