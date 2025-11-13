# Registro de Refactors

## 2025-11-13 — Dropdowns Unificados e UX
- Criação do componente `SearchableDropdown.vue`, com tipagens compartilhadas em `dropdown.types.ts`, para padronizar selects com busca incremental, navegação por teclado e estados visuais únicos.
- Cadastros de alunos e provas passaram a consumir o componente e o store de departamentos, garantindo carregamento único, mensagens consistentes e eliminação da digitação manual de IDs.
- A tela de notas reutiliza o dropdown nos filtros e no formulário, permitindo busca por nome/RA ou título/data, além de limpar seleção rapidamente e manter comportamento uniforme.
- Estilos e mensagens de suporte foram centralizados nos formulários, mantendo feedbacks de carregamento/erro iguais em todos os CRUDs que dependem de dados auxiliares.
