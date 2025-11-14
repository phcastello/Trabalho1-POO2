# Registro de Refactors

Dropdowns Unificados e UX
- Criação do componente `SearchableDropdown.vue`, com tipagens compartilhadas em `dropdown.types.ts`, para padronizar selects com busca incremental, navegação por teclado e estados visuais únicos.
- Cadastros de alunos e provas passaram a consumir o componente e o store de departamentos, garantindo carregamento único, mensagens consistentes e eliminação da digitação manual de IDs.
- A tela de notas reutiliza o dropdown nos filtros e no formulário, permitindo busca por nome/RA ou título/data, além de limpar seleção rapidamente e manter comportamento uniforme.
- Estilos e mensagens de suporte foram centralizados nos formulários, mantendo feedbacks de carregamento/erro iguais em todos os CRUDs que dependem de dados auxiliares.


Backend Refactors Realizados
    
* **Controllers mais enxutos e padronizados**

  * `Aluno/Departamento/Prova/NotaController` agora herdam `BaseCrudController`, usando `createdUri`, `requireFound` e `deleteOrNotFound` para padronizar `201`, `404` e `204`.
  * DTOs viraram `record`s em `poo.controller.dto`, com mapeamento para entidade centralizado em `Aluno/Departamento/Nota/ProvaMapper` + `MapperUtils` para `trimToNull`, normalização de e-mail e `uppercaseOrNull`.

* **DAOs JDBC com metadados reutilizáveis**

  * DAOs de `Aluno`, `Departamento`, `Nota` e `Prova` agora usam `JdbcTableMetadata` para definir tabela, colunas (incluindo auditoria), `ORDER BY` padrão e gerar SQL (`SELECT`, `INSERT ... RETURNING`, `UPDATE ... RETURNING`, `DELETE`) sem copy-paste.
  * `populateAuditColumns` concentra o preenchimento de `created_at`/`updated_at` nos modelos.

* **Serviços com tratamento de erro consistente**

  * `Aluno/Departamento/Nota/ProvaServiceImpl` delegam para `CrudServiceSupport` a tradução de `DataAccessException` em `ResponseStatusException` (409 para duplicado, 400 para integridade) via `UpsertErrorDescriptor`.
  * Updates passaram a usar `support.updater(...)` para validar IDs não nulos e copiar campos de forma uniforme, reduzindo código repetido.

* **Cobertura mínima de testes das abstrações**

  * `CrudServiceSupportTest` garante o comportamento do builder de update e da tradução de erros, dando uma camada de segurança para as novas abstrações compartilhadas.
