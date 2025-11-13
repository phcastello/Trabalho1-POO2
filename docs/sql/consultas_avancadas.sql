-- ============================================================
-- Consultas avançadas documentadas para o Requisito 7 (BD2)
-- ============================================================


-- -----------------------------------------------------------------
-- Consulta 1 - Ranking de desempenho por departamento (agregações)
-- -----------------------------------------------------------------
SELECT
  d.id AS departamento_id,
  d.nome AS departamento_nome,
  ROUND(AVG(n.valor)::numeric, 2) AS media_notas,
  MIN(n.valor) AS menor_nota,
  MAX(n.valor) AS maior_nota,
  COUNT(DISTINCT n.aluno_id) AS alunos_avaliados,
  COUNT(*) AS notas_lancadas
FROM departamento d
JOIN aluno a ON a.departamento_id = d.id
JOIN nota n ON n.aluno_id = a.id
GROUP BY d.id, d.nome
ORDER BY media_notas DESC, notas_lancadas DESC;

-- -----------------------------------------------------------------
-- Consulta 2 - Alunos com entrega equilibrada (operador de conjunto)
-- -----------------------------------------------------------------
WITH alunos_prova AS (
  SELECT n.aluno_id, COUNT(DISTINCT n.prova_id) AS avaliacoes_prova
  FROM nota n
  JOIN prova p ON p.id = n.prova_id
  WHERE p.titulo ILIKE '%prova%'
  GROUP BY n.aluno_id
),
alunos_projeto AS (
  SELECT n.aluno_id, COUNT(DISTINCT n.prova_id) AS projetos_entregues
  FROM nota n
  JOIN prova p ON p.id = n.prova_id
  WHERE p.titulo ILIKE '%projeto%'
  GROUP BY n.aluno_id
),
intersecao AS (
  SELECT aluno_id FROM alunos_prova
  INTERSECT
  SELECT aluno_id FROM alunos_projeto
)
SELECT
  a.id AS aluno_id,
  a.ra,
  a.nome,
  d.nome AS departamento_nome,
  ap.avaliacoes_prova,
  apr.projetos_entregues
FROM intersecao i
JOIN aluno a ON a.id = i.aluno_id
JOIN departamento d ON d.id = a.departamento_id
JOIN alunos_prova ap ON ap.aluno_id = i.aluno_id
JOIN alunos_projeto apr ON apr.aluno_id = i.aluno_id
ORDER BY a.nome;

-- -----------------------------------------------------------------
-- Consulta 3 - Cobertura de notas por aluno (junção externa)
-- -----------------------------------------------------------------
SELECT
  a.id AS aluno_id,
  a.ra,
  a.nome,
  d.nome AS departamento_nome,
  COUNT(n.prova_id) AS provas_avaliadas,
  ROUND(COALESCE(AVG(n.valor), 0)::numeric, 2) AS media_notas,
  CASE WHEN COUNT(n.prova_id) = 0 THEN TRUE ELSE FALSE END AS sem_notas
FROM aluno a
LEFT JOIN nota n ON n.aluno_id = a.id
JOIN departamento d ON d.id = a.departamento_id
GROUP BY a.id, a.ra, a.nome, d.nome
ORDER BY sem_notas DESC, a.nome;
