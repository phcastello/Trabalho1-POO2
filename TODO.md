# TODO – Pendências do Trabalho Integrado

- [x] **Consultas Avançadas (Requisito 7)**
  - **Situação:** Falta documentar e versionar as três consultas SQL exigidas (1 usando funções agregadas, 1 usando operador de conjunto, 1 usando junção externa).
  - **Checklist:**
    - [x] Definir cenários de negócio que justifiquem cada consulta (ex.: ranking de médias, alunos sem notas, interseção entre departamentos).
    - [x] Escrever os SQLs diretamente em `docs/sql/consultas_avancadas.sql` ou em uma migration ilustrativa para fins de documentação.
    - [x] Validar os resultados executando contra o Postgres provisionado via `infra/docker-compose-db.yml` (ou `mvn flyway:migrate` + `psql`).
    - [x] Incluir a descrição e a saída esperada no README ou em um anexo do relatório para comprovar o requisito.

- [x] **Cadastro de Alunos – Departamento por Nome**
  - **Situação:** O formulário ainda consome o `id` do departamento e exibe o mesmo identificador na listagem, o que atrapalha o entendimento do usuário final.
  - **Checklist:**
    - [x] Trocar o campo atual por um dropdown com busca pelo nome (typeahead) que só aparece após digitar as primeiras letras.
    - [x] Ajustar o payload do cadastro/edição para enviar o `id` correto a partir da seleção do nome.
    - [x] Atualizar a listagem dos alunos para renderizar o nome do departamento.

- [x] **Comportamento Padrão dos Dropdowns nos CRUDs**
  - **Situação:** Cada tela trata o carregamento dos dropdowns de forma diferente, gerando inconsistências de usabilidade.
  - **Checklist:**
    - [x] Definir um componente reutilizável de dropdown com busca incremental que seja aplicado a todos os CRUDs.
    - [x] Substituir os dropdowns atuais pelo novo componente garantindo que só abra após o usuário digitar parte do termo.
    - [x] Validar se o comportamento ficou uniforme em alunos, professores, turmas etc.

- [x] **Estilização Consistente dos Dropdowns**
  - **Situação:** Além do comportamento desigual, os dropdowns carecem de padronização visual.
  - **Checklist:**
    - [x] Adotar um guideline (spacing, cores, ícones) e aplicar via CSS/tema compartilhado.
    - [x] Revisar estados de foco, hover, vazio e preenchido para manter acessibilidade.
    - [x] Garantir responsividade em telas menores.

- [ ] **Login com Validação Real**
  - **Situação:** A tela de login não autentica de fato contra o backend, permitindo acesso sem credenciais válidas.
  - **Checklist:**
    - [ ] Integrar o formulário ao endpoint de autenticação e tratar respostas de erro.
    - [ ] Armazenar o token/sessão no fluxo esperado (ex.: `localStorage` + interceptors).
    - [ ] Bloquear rotas protegidas para usuários não autenticados e criar testes manuais cobrindo o fluxo.

- [ ] **Oportunidades de Refatoração no Backend**
  - **Situação:** Há trechos do backend crescendo em complexidade sem uma análise estruturada de melhorias.
  - **Checklist:**
    - [ ] Mapear ao menos quatro alvos (métodos, controllers, services ou models) que se beneficiariam de refatoração.
    - [ ] Documentar para cada alvo o problema atual (dívida técnica, duplicação, falta de testes, etc.).
    - [ ] Priorizar os alvos e propor o escopo inicial de cada refactor (ex.: extrair serviço, dividir responsabilidades, adicionar testes).
