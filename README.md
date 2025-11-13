# Trabalho Final Integrado - Programação Orientada a Objetos 2 (POO2) e Banco de Dados 2 (BD2)

Este repositório reúne a solução full-stack desenvolvida para os trabalhos finais integrados das disciplinas de Programação Orientada a Objetos 2 e Banco de Dados 2. A aplicação simula um portal acadêmico simplificado e serve como laboratório unificado para praticar arquitetura em camadas, encapsulamento de regras de negócio e integração com um banco relacional que acompanha o relatório de BD2.

Na dimensão de POO2, a avaliação enfatiza boas práticas de OO: as entidades e DTOs representam o domínio, enquanto controllers, services e DAOs demonstram como aplicar SOLID em um projeto realista. Pelo lado de BD2, destacamos modelagem relacional normalizada, versionamento do schema com Flyway, uso de SQL explícito para consultas críticas e documentação dos artefatos de banco presentes em `docs/` e na pasta `infra/`.

## Visão Geral da Solução
- `backend/`: API REST com Spring Boot. Controllers expõem endpoints HTTP, services concentram regras e DAOs JDBC executam SQL versionado por Flyway.
- `frontend/`: SPA Vue 3 + Pinia usada para testar fluxos de cadastro, listagem e autenticação simulada.
- `infra/`: definições Docker Compose que sobem Postgres, migrações dedicadas ao trabalho de BD2 e demais dependências locais.
- `docs/`: diagramas UML, DER lógico/físico, relatório de BD2 e referências utilizadas durante as disciplinas.

## Objetivos de Aprendizagem
- Consolidar princípios de OO (encapsulamento, responsabilidade única, inversão de dependência) aplicados a um domínio universitário.
- Implementar MVC + DAO manualmente, explorando interfaces e injeção de dependências do Spring em vez de abstrair tudo com ORMs.
- Demonstrar como regras de negócio e validações residem em services, separando-as das camadas de transporte e persistência.
- Entregar uma API REST e uma SPA que permitam testar as funcionalidades e observar o comportamento das camadas.

## Objetivos Complementares de BD2
- Evidenciar o processo completo de modelagem: DER conceitual, normalização e mapeamento para PostgreSQL.
- Versionar o schema com Flyway, garantindo reprodutibilidade e rastreabilidade das entregas de banco de dados.
- Criar consultas SQL otimizadas, índices e gatilhos que suportem os casos de uso da aplicação.
- Documentar scripts de carga, políticas de backup/restauração e procedimentos usados para validar integridade referencial.

## Conceitos e Técnicas de POO aplicados
- Camadas `Controller -> Service -> DAO` desacopladas por interfaces (`AlunoService`, `AlunoDao`, etc.) para facilitar testes e evolução.
- DAOs implementados com `JdbcTemplate` e `RowMapper`, demonstrando o padrão DAO em cima de SQL parametrizado.
- Services encapsulam regras (unicidade de RA/email, proteção contra exclusões com dependências) e traduzem exceções de infraestrutura para respostas HTTP coesas.
- DTOs com Bean Validation garantem pré-condições antes de delegar para o domínio.
- Uso de `HttpSession` para ilustrar autenticação baseada em sessão e como o estado do usuário pode ser consumido pelos controllers.
- Documentação automática com Swagger/OpenAPI para reforçar contratos e facilitar exploração dos endpoints.

## Componentes e Técnicas de BD2 aplicados
- Migrations Flyway organizam a evolução do schema (`backend/src/main/resources/db/migration` e `infra/db/migrations`) mantendo histórico auditável.
- Índices e constraints garantem unicidade de RA/email e preservam referências entre departamentos, provas e notas.
- Funções SQL e gatilhos registram auditoria e automatizam regras que precisam residir no banco.
- Seeds reproduzem cenários de teste e sustentam os relatórios e consultas analisadas na disciplina de BD2.

## Domínio e Funcionalidades
- Entidades principais: `departamento`, `aluno`, `prova`, `nota` e `usuario`.
- CRUD completo para departamentos, alunos, provas e notas, expondo validações como RA único, relação aluno-departamento e consistência entre provas/notas.
- Endpoint `GET /api/health` para verificação rápida do backend.
- Fluxo simples de autenticação (`/api/auth/login`, `/api/auth/me`, `/api/auth/logout`) que demonstra como persistir informações do usuário na sessão.

## Arquitetura e Diagramas

### MVC + DAO (backend)
![Diagrama MVC e DAO](docs/uml/mvc-dao.png)

O diagrama realça a divisão em camadas pedida na disciplina: controllers tratam HTTP, services isolam regras de negócio e DAOs controlam o acesso a dados. Essa separação facilita evoluir regras de OO sem acoplar transporte e persistência.

### Sequência de criação de aluno
![Diagrama de sequência - Criar Aluno](docs/uml/sequence-criar-aluno.png)

O fluxo destaca a conversa entre frontend, controller, service e DAO na inserção de um aluno. O objetivo é evidenciar os pontos em que validações de domínio acontecem antes de efetivar a transação no banco.

## Estado da Autenticação
- As rotas de negócio (`/api/alunos`, `/api/departamentos`, etc.) não verificam a sessão e podem ser chamadas sem realizar login.
- O login em `/api/auth/login` apenas grava `userId`/`userName` na sessão para que `/api/auth/me` consiga identificar o usuário atual.
- O projeto não inclui Spring Security ou interceptadores; por isso, o fluxo de autenticação serve apenas como mock para a disciplina.
- Para tornar o login obrigatório, adicione um mecanismo de segurança real (Spring Security, JWT ou um `HandlerInterceptor` que rejeite requisições sem `userId`).

## Scripts de Migração (Flyway)
Por se tratar de um trabalho conjunto de POO2 e BD2, o schema do Postgres permanece versionado para garantir reprodutibilidade, análise de desempenho e rastreabilidade das decisões tomadas no relatório de banco de dados.

- `V1__init.sql`: funções auxiliares, tabelas `departamento` e `aluno`, índices e gatilhos de auditoria.
- `V2__provas_notas.sql`: cria `prova` e `nota`, assegurando integridade entre departamentos.
- `V3__auth_usuario.sql`: habilita `pgcrypto` e registra `usuario` com hash BCrypt.
- `V4__seed_dados_iniciais.sql`: popula dados de referência para testar os fluxos.

Novas evoluções devem ser adicionadas em `backend/src/main/resources/db/migration` ou `infra/db/migrations`. As migrations são aplicadas automaticamente ao subir o backend ou via `mvn flyway:migrate`, servindo como base oficial para as entregas de BD2.

## Consultas Avançadas (Requisito 7)
As três consultas solicitadas por BD2 estão versionadas em `docs/sql/consultas_avancadas.sql` e expostas pelo endpoint `GET /api/consultas-avancadas`, consumido pela SPA na rota `/consultas`. O comando abaixo executa todas elas diretamente no Postgres provisionado via `infra/docker-compose-db.yml`:

```bash
PGPASSWORD=poo_pass psql -h localhost -p 55432 -U poo_user -d poo -f docs/sql/consultas_avancadas.sql
```

### 1. Ranking de desempenho por departamento (funções agregadas)
- **Objetivo:** priorizar os departamentos com melhor média, maior amplitude de notas e maior volume de lançamentos para relatórios de desempenho.
- **SQL:** primeira consulta do arquivo `docs/sql/consultas_avancadas.sql` e campo `rankingDepartamentos` da API.
- **Saída esperada (seed atual):**

```
 departamento_id |   departamento_nome    | media_notas | menor_nota | maior_nota | alunos_avaliados | notas_lancadas 
-----------------+------------------------+-------------+------------+------------+------------------+----------------
               3 | Matematica Aplicada    |        9.10 |       9.10 |       9.10 |                1 |              1
               1 | Ciencias da Computacao |        8.45 |       7.20 |       9.30 |                2 |              4
               2 | Engenharia Eletrica    |        7.20 |       6.50 |       7.90 |                2 |              2
```

### 2. Alunos com entrega equilibrada (operador de conjunto `INTERSECT`)
- **Objetivo:** identificar estudantes que receberam notas tanto em provas tradicionais (títulos contendo “Prova”) quanto em projetos práticos, elegíveis a monitorias.
- **SQL:** segunda consulta do arquivo e campo `alunosModalidades`.
- **Saída esperada (seed atual):**

```
 aluno_id |   ra    |    nome     |   departamento_nome    | avaliacoes_prova | projetos_entregues 
----------+---------+-------------+------------------------+------------------+--------------------
        1 | 2023001 | Ana Pereira | Ciencias da Computacao |                1 |                  1
        2 | 2023002 | Bruno Lima  | Ciencias da Computacao |                1 |                  1
```

### 3. Cobertura de notas por aluno (junção externa `LEFT JOIN`)
- **Objetivo:** listar todos os alunos, inclusive quem ainda não recebeu notas, destacando a quantidade de avaliações e a média parcial de cada um.
- **SQL:** terceira consulta do arquivo e campo `coberturaNotas`.
- **Saída esperada (seed atual + cadastros feitos na disciplina):**

```
 aluno_id |     ra      |     nome      |   departamento_nome    | provas_avaliadas | media_notas | sem_notas 
----------+-------------+---------------+------------------------+------------------+-------------+-----------
       39 | 57024740011 | ADdasd        | Ciencias da Computacao |                0 |        0.00 | t
        1 | 2023001     | Ana Pereira   | Ciencias da Computacao |                2 |        8.90 | f
        2 | 2023002     | Bruno Lima    | Ciencias da Computacao |                2 |        8.00 | f
        3 | 2023101     | Carla Menezes | Engenharia Eletrica    |                1 |        7.90 | f
        4 | 2023102     | Diego Santos  | Engenharia Eletrica    |                1 |        6.50 | f
        5 | 2023201     | Eva Martins   | Matematica Aplicada    |                1 |        9.10 | f
```

### Visualização no frontend
- Após autenticar na SPA, acesse o menu **Consultas** para ver gráficos e tabelas com os mesmos dados retornados pela API, incluindo estados de carregamento/erro e botões de atualização.

## Requisitos de Ambiente
- Java 21 e Maven
- Node.js 18+ e npm
- Docker e Docker Compose
- Opcional: psql ou outro cliente SQL para inspeção manual

## Preparação do Ambiente

### Execução completa com Docker

#### Ambiente de desenvolvimento (build local)
```bash
docker compose up --build
```
O Compose utiliza os `Dockerfile` do backend e do frontend para gerar as imagens locais. A API responde em `http://localhost:8080/api` e a SPA em `http://localhost:5173`. Para desligar, execute `docker compose down`.

#### Consumir imagens publicadas (sem build)
Disponibilizamos imagens prontas no GitHub Container Registry. Utilize `docker-compose.images.yml` para baixá-las e subir o ambiente sem build local.

#### Baixar apenas o compose de imagens
Para usar somente as imagens publicadas, clique no link abaixo e baixe o arquivo:
- [docker-compose.images.yml](https://github.com/phcastello/Trabalho1-POO2/blob/main/docker-compose.images.yml)

Para subir:
```bash
docker compose -f docker-compose.images.yml up -d
```

Para desligar:
```bash
docker compose -f docker-compose.images.yml down
```

### Execução manual passo a passo

1. Banco de dados
   ```bash
    
   ```
   O Postgres fica exposto em `localhost:55432` (`poo_user` / `poo_pass`). Seeds são aplicadas pelas migrations do backend. Para acessar diretamente:
   ```bash
   docker exec -it poo_postgres psql -U poo_user -d poo
   ```

2. Backend
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   A API fica disponível em `http://localhost:8080/api`. Endpoints principais:
   - `GET /api/health` para verificar disponibilidade
   - `POST /api/auth/login` para autenticar (usuário `admin` / `senha123`)
   - CRUD de `alunos`, `departamentos`, `provas` e `notas`
   - Swagger UI em `http://localhost:8080/swagger-ui.html`

3. Frontend
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Acesse `http://localhost:5173`. Ajuste `VITE_API_BASE_URL` no `.env.local` caso a API esteja em outro host.
