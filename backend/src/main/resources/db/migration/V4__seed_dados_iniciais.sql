-- V4__seed_dados_iniciais.sql

-- Departamentos base para referencia cruzada
INSERT INTO departamento (nome, sigla)
VALUES
  ('Ciencias da Computacao', 'CIC'),
  ('Engenharia Eletrica', 'EEL'),
  ('Matematica Aplicada', 'MAT')
ON CONFLICT (nome) DO NOTHING;

-- Alunos por departamento
INSERT INTO aluno (ra, nome, email, departamento_id, data_nascimento)
VALUES
  ('2023001', 'Ana Pereira', 'ana.pereira@uni.test', (SELECT id FROM departamento WHERE sigla = 'CIC'), '2002-05-14'),
  ('2023002', 'Bruno Lima', 'bruno.lima@uni.test', (SELECT id FROM departamento WHERE sigla = 'CIC'), '2001-11-02'),
  ('2023101', 'Carla Menezes', 'carla.menezes@uni.test', (SELECT id FROM departamento WHERE sigla = 'EEL'), '2000-12-29'),
  ('2023102', 'Diego Santos', 'diego.santos@uni.test', (SELECT id FROM departamento WHERE sigla = 'EEL'), '1999-07-01'),
  ('2023201', 'Eva Martins', 'eva.martins@uni.test', (SELECT id FROM departamento WHERE sigla = 'MAT'), '2003-03-19'),
  ('2023202', 'Felipe Nunes', 'felipe.nunes@uni.test', (SELECT id FROM departamento WHERE sigla = 'MAT'), '2002-09-25')
ON CONFLICT (ra) DO NOTHING;

-- Provas planejadas por departamento
INSERT INTO prova (departamento_id, titulo, data, descricao)
VALUES
  ((SELECT id FROM departamento WHERE sigla = 'CIC'), 'POO - Prova 1', '2024-03-15', 'Primeira avaliacao de programacao orientada a objetos.'),
  ((SELECT id FROM departamento WHERE sigla = 'CIC'), 'Banco de Dados - Projeto Final', '2024-04-10', 'Entrega do projeto final de bancos de dados.'),
  ((SELECT id FROM departamento WHERE sigla = 'EEL'), 'Circuitos I - Prova 1', '2024-03-18', 'Prova teorica de circuitos eletricos.'),
  ((SELECT id FROM departamento WHERE sigla = 'MAT'), 'Calculo II - Prova 1', '2024-03-22', 'Avaliacao de derivadas e integrais.')
ON CONFLICT ON CONSTRAINT uq_prova_unica_por_dep_dia DO NOTHING;

-- Notas dos alunos nas provas (mesmo departamento garantido por trigger)
INSERT INTO nota (aluno_id, prova_id, valor, observacao)
VALUES
  ((SELECT id FROM aluno WHERE ra = '2023001'), (SELECT id FROM prova WHERE titulo = 'POO - Prova 1' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'CIC')), 8.5, 'Participacao ativa nas aulas.'),
  ((SELECT id FROM aluno WHERE ra = '2023002'), (SELECT id FROM prova WHERE titulo = 'POO - Prova 1' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'CIC')), 7.2, NULL),
  ((SELECT id FROM aluno WHERE ra = '2023001'), (SELECT id FROM prova WHERE titulo = 'Banco de Dados - Projeto Final' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'CIC')), 9.3, 'Projeto entregue com testes automatizados.'),
  ((SELECT id FROM aluno WHERE ra = '2023002'), (SELECT id FROM prova WHERE titulo = 'Banco de Dados - Projeto Final' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'CIC')), 8.8, 'Documentacao consistente.'),
  ((SELECT id FROM aluno WHERE ra = '2023101'), (SELECT id FROM prova WHERE titulo = 'Circuitos I - Prova 1' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'EEL')), 7.9, 'Boa resolucao dos exercicios.'),
  ((SELECT id FROM aluno WHERE ra = '2023102'), (SELECT id FROM prova WHERE titulo = 'Circuitos I - Prova 1' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'EEL')), 6.5, NULL),
  ((SELECT id FROM aluno WHERE ra = '2023201'), (SELECT id FROM prova WHERE titulo = 'Calculo II - Prova 1' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'MAT')), 9.1, 'Acertos completos nas questoes discursivas.'),
  ((SELECT id FROM aluno WHERE ra = '2023202'), (SELECT id FROM prova WHERE titulo = 'Calculo II - Prova 1' AND departamento_id = (SELECT id FROM departamento WHERE sigla = 'MAT')), 7.4, 'Recomendada revisao de integrais.')
ON CONFLICT (aluno_id, prova_id) DO NOTHING;
