# Trabalho 1 - POO2

Aplicacao full-stack para gestao academica composta por um backend Spring Boot (REST + DAO/JDBC), um frontend Vue 3 com Pinia e uma infra Docker para o banco Postgres.

- `backend/`: servico REST exposto em `http://localhost:8080/api`, autenticacao via sessao HTTP e migrations Flyway.
- `frontend/`: SPA criada com Vite, configura a URL base da API via `VITE_API_BASE_URL`.
- `infra/`: `docker-compose` para subir o banco Postgres com credenciais de desenvolvimento (`poo_user` / `poo_pass`).

## Requisitos

- Java 21 e Maven
- Node.js 18+ e npm
- Docker e Docker Compose

## Subindo o ambiente

1. Banco de dados  
   ```
   docker compose -f infra/docker-compose-db.yml up -d
   ```
   O container expoe o Postgres em `localhost:55432` e as migrations Flyway rodam automaticamente quando o backend inicia. Seeds criam dados iniciais de alunos, provas, notas e um usuario `admin` com senha `senha123`.

2. Backend  
   ```
   cd backend
   mvn spring-boot:run
   ```
   A API ficara disponivel em `http://localhost:8080/api`. Endpoints principais:
   - `GET /api/health` - verificacao basica
   - `POST /api/auth/login` - autentica e cria sessao (usa cookie)
   - `GET /api/auth/me` - retorna o usuario autenticado
   - `POST /api/auth/logout` - encerra a sessao

3. Frontend  
   ```
   cd frontend
   npm install
   npm run dev
   ```
   Acesse `http://localhost:5173`. O login padrao e `admin` / `senha123`. Ajuste `VITE_API_BASE_URL` no arquivo `.env.local` se necessario.

## Testes e utilidades

- Backend: `mvn test`
- Frontend: `npm run check`
- Para resetar o banco, derrube o container (`docker compose down -v`) e suba novamente.
