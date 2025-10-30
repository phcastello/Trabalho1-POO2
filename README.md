# Trabalho Final - Banco de Dados 2

Este repositorio consolida a solucao full-stack usada como projeto final da disciplina de Banco de Dados 2. O foco e demonstrar modelagem relacional, integridade transacional e automatizacao do ciclo de vida do banco de dados, enquanto o backend e o frontend funcionam como camadas de apoio para exercitar consultas e cenarios de uso.

## Visao Geral da Solucao
- `backend/`: API REST Spring Boot com camada de servico e DAOs JDBC. As migrations Flyway versionam o schema e garantem reproducao do estado do banco.
- `frontend/`: SPA criada com Vue 3 + Pinia que consome a API e permite validar regras de negocio, fluxos de cadastro e consultas.
- `infra/`: orquestracao Docker Compose para provisionar Postgres e demais dependencias de banco.
- `docs/`: diagramas UML aproveitados na disciplina para justificar escolhas de arquitetura e fluxo de dados.

## Objetivo Academico e Entregaveis
- Modelagem conceitual e logica das entidades academicas (departamento, aluno, prova, nota e usuario).
- Aplicacao de restricoes, referencias e gatilhos que asseguram integridade entre dominios.
- Scripts reproduziveis (Flyway + Docker) para criar, popular e observar o banco de dados.
- Endpoints REST e interface web que expõem cenarios de consultas, escritas concorrentes e politicas de autenticacao.
- Guia de validacao com comandos SQL e testes automatizados que suportam a avaliacao da disciplina.

## Destaques de Banco de Dados
- Schema normalizado com indices pensados para consultas por departamento, email e composicao aluno-prova.
- Gatilhos em PL/pgSQL (`set_updated_at`, `check_nota_mesmo_departamento`) que registram auditoria e impedem inconsistencias entre departamentos.
- Uso de `pgcrypto` para hash de senha, demonstrando boas praticas de armazenamento seguro.
- Seeds controlados por Flyway que criam um conjunto minimamente realista de departamentos, alunos, provas e notas para experimentos.
- Estrutura preparada para testar funcoes agregadas, janelas e visoes (ver instrucoes em `backend/src/main/resources/db/migration` para adicionar novas versoes Flyway).

## Arquitetura e Modelagem

### MVC + DAO (backend)
![Diagrama MVC e DAO](docs/uml/mvc-dao.png)

O desenho destaca a separacao de responsabilidades adotada no backend. Controllers recebem as chamadas HTTP e delegam regras para services, enquanto os DAOs concentram acesso JDBC e consultas SQL parametrizadas. Essa estrutura facilita mapear as operacoes CRUD para comandos SQL versionados nas migrations.

### Sequencia de criacao de aluno
![Diagrama de sequencia - Criar Aluno](docs/uml/sequence-criar-aluno.png)

O diagrama acompanha o fluxo completo de cadastro: do formulario no frontend ao commit da transacao SQL. Ele evidencia os pontos em que validacoes de negocio cruzam com restricoes do banco (unicidade de RA, verificacao de departamento e gatilho de consistencia).

## Scripts de Migracao (Flyway)
- `V1__init.sql`: cria funcoes auxiliares, tabelas `departamento` e `aluno`, alem de indices e gatilhos de auditoria.
- `V2__provas_notas.sql`: adiciona `prova` e `nota`, assegurando integridade com verificacao de departamento e restricoes de dominio.
- `V3__auth_usuario.sql`: habilita `pgcrypto` e cria a tabela `usuario` com hash de senha Bcrypt.
- `V4__seed_dados_iniciais.sql`: popula dados de referencia para execucao de consultas, relatorios e testes de escrita.

Para evoluir o schema, adicione novos arquivos numerados em `backend/src/main/resources/db/migration`. Cada script sera aplicado automaticamente na inicializacao do backend ou via `mvn flyway:migrate`.

## Requisitos de Ambiente
- Java 21 e Maven
- Node.js 18+ e npm
- Docker e Docker Compose
- Opcional: psql ou outro client SQL para inspecao manual

## Preparacao do Ambiente

### Execucao completa com Docker
Com Docker instalado, basta rodar:
```bash
docker compose up --build
```
Esse comando sobe Postgres, backend e frontend. A API fica em `http://localhost:8080/api` e a SPA em `http://localhost:5173`. Para desligar, use `docker compose down`.


### Execucao manual passo a passo

1. Banco de dados
   ```bash
   docker compose -f infra/docker-compose-db.yml up -d
   ```
   O Postgres fica exposto em `localhost:55432` (`poo_user` / `poo_pass`). Seeds sao aplicadas nas migrations do backend. Para acessar o banco diretamente:
   ```bash
   docker exec -it poo_postgres psql -U poo_user -d poo
   ```

2. Backend
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   A API estara disponivel em `http://localhost:8080/api`. Endpoints principais:
   - `GET /api/health` para verificar disponibilidade
   - `POST /api/auth/login` para autenticar (usuario `admin` / `senha123`)
   - CRUDs relacionados a `alunos`, `departamentos`, `provas` e `notas`

3. Frontend
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Acesse `http://localhost:5173`. Ajuste `VITE_API_BASE_URL` no `.env.local` para apontar para a API quando necessario.
