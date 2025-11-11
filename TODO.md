# TODO – Pendências do Trabalho Integrado

## 1. Consultas Avançadas (Requisito 7)
- **Situação:** Falta documentar e versionar as três consultas SQL exigidas (agregação, operador de conjunto e junção externa).
- **Plano breve:** 
  1. Definir cenários de negócio que justifiquem cada consulta (ex.: ranking de médias, alunos sem notas, interseção entre departamentos).
  2. Escrever os SQLs diretamente em um novo arquivo `docs/sql/consultas_avancadas.sql` ou em uma migration ilustrativa para fins de documentação.
  3. Validar os resultados executando contra o Postgres provisionado via `infra/docker-compose-db.yml` (ou `mvn flyway:migrate` + `psql`).
  4. Incluir a descrição e a saída esperada no README ou em um anexo do relatório para comprovar o requisito.

## 2. Evidências do Plano de Teste para as Consultas
- **Situação:** Ao adicionar as consultas, será necessário mostrar como foram verificadas.
- **Plano breve:** 
  1. Registrar no README (seção BD2) os comandos utilizados para validar cada consulta.
  2. Se possível, adicionar capturas (`psql` ou `DBeaver`) em `docs/queries/`.
