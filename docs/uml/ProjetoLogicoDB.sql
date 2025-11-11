CREATE TABLE departamento (
  id           BIGINT      NOT NULL,
  nome         VARCHAR(255) NOT NULL,
  sigla        VARCHAR(10),
  created_at   TIMESTAMP   NOT NULL,
  updated_at   TIMESTAMP   NOT NULL,

  CONSTRAINT pk_departamento PRIMARY KEY (id),
  CONSTRAINT uq_departamento_nome UNIQUE (nome),
  CONSTRAINT ck_departamento_sigla_len
    CHECK (sigla IS NULL OR (CHAR_LENGTH(sigla) BETWEEN 1 AND 10))
);

CREATE TABLE usuario (
  id             BIGINT        NOT NULL,
  username       VARCHAR(100)  NOT NULL,
  nome           VARCHAR(255)  NOT NULL,
  password_hash  VARCHAR(255)  NOT NULL,
  created_at     TIMESTAMP     NOT NULL,

  CONSTRAINT pk_usuario PRIMARY KEY (id),
  CONSTRAINT uq_usuario_username UNIQUE (username)
);

CREATE TABLE aluno (
  id                BIGINT        NOT NULL,
  ra                VARCHAR(20)   NOT NULL,
  nome              VARCHAR(255)  NOT NULL,
  email             VARCHAR(320),           -- UNIQUE lógica; ver observação abaixo
  departamento_id   BIGINT        NOT NULL,
  data_nascimento   DATE,
  created_at        TIMESTAMP     NOT NULL,
  updated_at        TIMESTAMP     NOT NULL,

  CONSTRAINT pk_aluno PRIMARY KEY (id),
  CONSTRAINT uq_aluno_ra UNIQUE (ra),
  CONSTRAINT uq_aluno_email UNIQUE (email),   -- Requisito lógico de unicidade
  CONSTRAINT fk_aluno_departamento
    FOREIGN KEY (departamento_id)
    REFERENCES departamento (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);

CREATE TABLE prova (
  id               BIGINT        NOT NULL,
  departamento_id  BIGINT        NOT NULL,
  titulo           VARCHAR(255)  NOT NULL,
  data             DATE          NOT NULL,
  descricao        VARCHAR(2000),
  created_at       TIMESTAMP     NOT NULL,
  updated_at       TIMESTAMP     NOT NULL,

  CONSTRAINT pk_prova PRIMARY KEY (id),
  CONSTRAINT fk_prova_departamento
    FOREIGN KEY (departamento_id)
    REFERENCES departamento (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT,
  CONSTRAINT uq_prova_unica_por_dep_dia
    UNIQUE (departamento_id, titulo, data)
);

CREATE TABLE nota (
  aluno_id     BIGINT       NOT NULL,
  prova_id     BIGINT       NOT NULL,
  valor        NUMERIC(5,2) NOT NULL,
  observacao   VARCHAR(2000),
  created_at   TIMESTAMP    NOT NULL,
  updated_at   TIMESTAMP    NOT NULL,

  CONSTRAINT pk_nota PRIMARY KEY (aluno_id, prova_id),

  CONSTRAINT ck_nota_valor
    CHECK (valor BETWEEN 0 AND 10),

  CONSTRAINT fk_nota_aluno
    FOREIGN KEY (aluno_id)
    REFERENCES aluno (id)
      ON UPDATE CASCADE
      ON DELETE CASCADE,

  CONSTRAINT fk_nota_prova
    FOREIGN KEY (prova_id)
    REFERENCES prova (id)
      ON UPDATE CASCADE
      ON DELETE CASCADE
);

CREATE ASSERTION as_nota_mesmo_departamento
CHECK (
  NOT EXISTS (
    SELECT 1
    FROM nota n
    JOIN aluno a ON a.id = n.aluno_id
    JOIN prova p ON p.id = n.prova_id
    WHERE a.departamento_id <> p.departamento_id
  )
);
