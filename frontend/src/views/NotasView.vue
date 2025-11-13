<script setup lang="ts">
import { computed, reactive, ref, watch, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useNotas } from "@/store/notas";
import { useAlunos } from "@/store/alunos";
import { useProvas } from "@/store/provas";
import SearchableDropdown from "@/components/SearchableDropdown.vue";
import type { SearchableDropdownOption } from "@/components/dropdown.types";
import type { Nota } from "@/api/notas";

const notasStore = useNotas();
const alunosStore = useAlunos();
const provasStore = useProvas();

const { items, loading } = storeToRefs(notasStore);
const {
  items: alunos,
  loading: alunosLoading,
  lastError: alunosError,
} = storeToRefs(alunosStore);
const {
  items: provas,
  loading: provasLoading,
  lastError: provasError,
} = storeToRefs(provasStore);

const pageError = ref("");
const formMode = ref<"create" | "edit">("create");
const editingKey = ref<{ alunoId: number; provaId: number } | null>(null);
const saving = ref(false);
const removingKey = ref<{ alunoId: number; provaId: number } | null>(null);

const feedback = reactive({ success: "", error: "" });
const form = reactive({
  alunoId: "",
  provaId: "",
  valor: "",
  observacao: "",
});

const filters = reactive({ alunoId: "", provaId: "" });

const hasNotas = computed(() => items.value.length > 0);
const tableBusy = computed(() => loading.value && !hasNotas.value);
const formTitle = computed(() =>
  formMode.value === "edit" ? "Editar nota" : "Lançar nota"
);
const submitLabel = computed(() =>
  formMode.value === "edit" ? "Salvar alterações" : "Lançar nota"
);

function keywordList(...values: (string | number | null | undefined)[]) {
  return values
    .filter((value): value is string | number => value !== null && value !== undefined)
    .map((value) => String(value).trim())
    .filter((value) => value.length > 0);
}

const alunoOptions = computed<SearchableDropdownOption[]>(() =>
  alunos.value.map((aluno) => ({
    value: String(aluno.id),
    label: aluno.nome,
    description: `RA: ${aluno.ra}`,
    keywords: keywordList(aluno.ra, aluno.nome),
  }))
);
const provaOptions = computed<SearchableDropdownOption[]>(() =>
  provas.value.map((prova) => {
    const description = provaLabel(prova.id);
    return {
      value: String(prova.id),
      label: prova.titulo,
      description,
      keywords: keywordList(prova.titulo, description, formatDate(prova.data) || null, String(prova.id)),
    };
  })
);

function clearFeedback() {
  feedback.success = "";
  feedback.error = "";
  notasStore.clearError();
}

function resetForm() {
  form.alunoId = "";
  form.provaId = "";
  form.valor = "";
  form.observacao = "";
}

function startCreate() {
  formMode.value = "create";
  editingKey.value = null;
  clearFeedback();
  resetForm();
}

function startEdit(nota: Nota) {
  formMode.value = "edit";
  editingKey.value = { alunoId: nota.alunoId, provaId: nota.provaId };
  clearFeedback();
  form.alunoId = String(nota.alunoId);
  form.provaId = String(nota.provaId);
  form.valor = nota.valor.toFixed(2);
  form.observacao = nota.observacao ?? "";
}

function extractMessage(error: unknown) {
  if (error && typeof error === "object" && "response" in error) {
    const response = (error as any).response;
    return (
      response?.data?.message ||
      response?.data?.error ||
      `Requisição falhou (${response?.status ?? "erro"})`
    );
  }
  if (error instanceof Error) return error.message;
  return "Não foi possível concluir a operação.";
}

function normalizeObservacao(value: string | number | null | undefined) {
  const trimmed = String(value ?? "").trim();
  return trimmed.length > 0 ? trimmed : null;
}

function parseNotaValor(value: string | number | null | undefined) {
  const normalized = String(value ?? "").trim().replace(",", ".");
  if (normalized.length === 0) {
    throw new Error("Informe uma nota válida.");
  }
  const parsed = Number(normalized);
  if (Number.isNaN(parsed)) {
    throw new Error("Informe uma nota válida.");
  }
  return parsed;
}

function currentFilters() {
  const query: { alunoId?: number; provaId?: number } = {};
  const alunoIdNumber = Number(filters.alunoId);
  const provaIdNumber = Number(filters.provaId);
  if (!Number.isNaN(alunoIdNumber) && filters.alunoId) {
    query.alunoId = alunoIdNumber;
  }
  if (!Number.isNaN(provaIdNumber) && filters.provaId) {
    query.provaId = provaIdNumber;
  }
  return query;
}

async function refresh(force = true) {
  pageError.value = "";
  try {
    await notasStore.fetch(currentFilters(), force);
  } catch (error) {
    pageError.value = extractMessage(error);
  }
}

function ensureAlunos() {
  if (!alunosStore.initialized) {
    alunosStore.fetch().catch((error) => {
      console.error("Falha ao carregar alunos:", error);
    });
  }
}

function ensureProvas() {
  if (!provasStore.initialized) {
    provasStore.fetch().catch((error) => {
      console.error("Falha ao carregar provas:", error);
    });
  }
}

onMounted(() => {
  ensureAlunos();
  ensureProvas();
  if (!notasStore.initialized) {
    refresh(false);
  }
});

watch(
  () => [filters.alunoId, filters.provaId],
  () => {
    refresh(true);
  }
);

function buildCreatePayload() {
  const alunoId = Number(form.alunoId);
  if (!Number.isInteger(alunoId)) {
    throw new Error("Selecione um aluno válido.");
  }
  const provaId = Number(form.provaId);
  if (!Number.isInteger(provaId)) {
    throw new Error("Selecione uma prova válida.");
  }
  const valor = parseNotaValor(form.valor);

  if (valor < 0 || valor > 10) {
    throw new Error("A nota deve estar entre 0 e 10.");
  }

  return {
    alunoId,
    provaId,
    valor,
    observacao: normalizeObservacao(form.observacao),
  };
}

function buildUpdatePayload() {
  const valor = parseNotaValor(form.valor);
  if (valor < 0 || valor > 10) {
    throw new Error("A nota deve estar entre 0 e 10.");
  }
  return {
    valor,
    observacao: normalizeObservacao(form.observacao),
  };
}

async function submit() {
  clearFeedback();

  saving.value = true;
  try {
    if (formMode.value === "edit") {
      if (!editingKey.value) {
        throw new Error("Seleção inválida da nota.");
      }
      const payload = buildUpdatePayload();
      await notasStore.update(editingKey.value.alunoId, editingKey.value.provaId, payload);
      feedback.success = "Nota atualizada com sucesso.";
    } else {
      const payload = buildCreatePayload();
      await notasStore.create(payload);
      feedback.success = "Nota lançada com sucesso.";
      resetForm();
    }
  } catch (error) {
    feedback.error = notasStore.lastError ?? extractMessage(error);
  } finally {
    saving.value = false;
  }
}

async function removeNota(nota: Nota) {
  const confirmed = window.confirm("Remover esta nota? Esta ação não pode ser desfeita.");
  if (!confirmed) return;

  clearFeedback();
  removingKey.value = { alunoId: nota.alunoId, provaId: nota.provaId };
  try {
    await notasStore.remove(nota.alunoId, nota.provaId);
    if (editingKey.value && nota.alunoId === editingKey.value.alunoId && nota.provaId === editingKey.value.provaId) {
      startCreate();
    }
    feedback.success = "Nota removida com sucesso.";
  } catch (error) {
    feedback.error = notasStore.lastError ?? extractMessage(error);
  } finally {
    removingKey.value = null;
  }
}

function alunoLabel(id: number) {
  const aluno = alunos.value.find((item) => item.id === id);
  if (!aluno) return `Aluno #${id}`;
  return `${aluno.nome} (RA: ${aluno.ra})`;
}

function provaLabel(id: number) {
  const prova = provas.value.find((item) => item.id === id);
  if (!prova) return `Prova #${id}`;
  const data = formatDate(prova.data);
  return data ? `${prova.titulo} (${data})` : prova.titulo;
}

function formatDate(value: string) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
}

function formatValor(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value);
}

function formatDateTime(value: string) {
  if (!value) return "--";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "--";
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}
</script>

<template>
  <section>
    <span class="pill">Módulo</span>
    <h1 class="page-title">Notas</h1>
    <p class="page-subtitle">
      Lançamento de resultados e acompanhamento de médias dos alunos.
    </p>

    <div class="notas-layout">
      <div class="card notas-card">
        <header class="card-header">
          <h2>Notas lançadas</h2>
          <button type="button" class="secondary-button" @click="refresh(true)" :disabled="loading">
            Atualizar
          </button>
        </header>

        <div class="filters">
          <div class="form-field">
            <label for="filtro-aluno">Filtrar por aluno</label>
            <SearchableDropdown
              id="filtro-aluno"
              v-model="filters.alunoId"
              :items="alunoOptions"
              placeholder="Digite nome ou RA"
              clearable
              :loading="alunosLoading"
            />
          </div>

          <div class="form-field">
            <label for="filtro-prova">Filtrar por prova</label>
            <SearchableDropdown
              id="filtro-prova"
              v-model="filters.provaId"
              :items="provaOptions"
              placeholder="Digite título ou data"
              clearable
              :loading="provasLoading"
            />
          </div>
        </div>

        <p v-if="pageError" class="status status-error">{{ pageError }}</p>
        <p v-else-if="tableBusy" class="status">Carregando notas...</p>
        <p v-else-if="!hasNotas" class="status">Nenhuma nota lançada com os filtros atuais.</p>

        <div v-else class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>Aluno</th>
                <th>Prova</th>
                <th>Nota</th>
                <th>Observação</th>
                <th>Atualizada em</th>
                <th class="actions-col">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="nota in items" :key="`${nota.alunoId}-${nota.provaId}`">
                <td>{{ alunoLabel(nota.alunoId) }}</td>
                <td>{{ provaLabel(nota.provaId) }}</td>
                <td>{{ formatValor(nota.valor) }}</td>
                <td>{{ nota.observacao ?? "--" }}</td>
                <td>{{ formatDateTime(nota.updatedAt) }}</td>
                <td class="actions-col">
                  <button type="button" class="link-button" @click="startEdit(nota)">
                    Editar
                  </button>
                  <button
                    type="button"
                    class="link-button danger"
                    @click="removeNota(nota)"
                    :disabled="Boolean(
                      removingKey &&
                        removingKey.alunoId === nota.alunoId &&
                        removingKey.provaId === nota.provaId
                    )"
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card notas-card">
        <header class="card-header">
          <h2>{{ formTitle }}</h2>
          <button
            v-if="formMode === 'edit'"
            type="button"
            class="secondary-button"
            @click="startCreate"
            :disabled="saving"
          >
            Cancelar edição
          </button>
        </header>

        <form class="nota-form" @submit.prevent="submit">
          <div class="form-row">
            <div class="form-field">
              <label for="nota-aluno">Aluno *</label>
              <SearchableDropdown
                id="nota-aluno"
                v-model="form.alunoId"
                :items="alunoOptions"
                placeholder="Digite nome ou RA do aluno"
                :disabled="formMode === 'edit' || alunosLoading"
                :loading="alunosLoading"
              />
            </div>

            <div class="form-field">
              <label for="nota-prova">Prova *</label>
              <SearchableDropdown
                id="nota-prova"
                v-model="form.provaId"
                :items="provaOptions"
                placeholder="Digite título ou data da prova"
                :disabled="formMode === 'edit' || provasLoading"
                :loading="provasLoading"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-field">
              <label for="nota-valor">Nota (0 a 10) *</label>
              <input
                id="nota-valor"
                v-model="form.valor"
                type="number"
                step="0.01"
                min="0"
                max="10"
                placeholder="Ex.: 8.75"
              />
            </div>
            <div class="form-field">
              <label for="nota-observacao">Observação</label>
              <input
                id="nota-observacao"
                v-model="form.observacao"
                placeholder="Comentário opcional sobre o desempenho."
              />
            </div>
          </div>

          <p v-if="alunosLoading || provasLoading" class="status">
            Carregando dados auxiliares...
          </p>
          <p v-else-if="alunosError" class="status status-error">{{ alunosError }}</p>
          <p v-else-if="provasError" class="status status-error">{{ provasError }}</p>

          <p v-if="feedback.error" class="status status-error">{{ feedback.error }}</p>
          <p v-if="feedback.success" class="status status-success">{{ feedback.success }}</p>

          <button
            type="submit"
            :disabled="saving || alunosLoading || provasLoading"
          >
            {{ submitLabel }}
          </button>
        </form>
      </div>
    </div>
  </section>
</template>

<style scoped>
.notas-layout {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: minmax(0, 1.9fr) minmax(0, 1fr);
  align-items: start;
}

.notas-card {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.secondary-button {
  background: rgba(59, 130, 246, 0.12);
  color: var(--brand-600);
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow: none;
  padding: 0.5rem 1.1rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.secondary-button:hover {
  filter: none;
  transform: none;
  background: rgba(59, 130, 246, 0.2);
}

.secondary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  filter: none;
  transform: none;
}

.filters {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  min-width: 700px;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 0.75rem 0.9rem;
  text-align: left;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  font-size: 0.9rem;
}

.data-table th {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-muted);
}

.data-table tbody tr:hover {
  background: rgba(59, 130, 246, 0.08);
}

.actions-col {
  white-space: nowrap;
  text-align: right;
}

.link-button {
  background: none;
  border: none;
  color: var(--brand-600);
  padding: 0;
  font-size: 0.85rem;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  display: inline;
}

.link-button + .link-button {
  margin-left: 0.75rem;
}

.link-button:hover {
  color: var(--brand-500);
  text-decoration: underline;
}

.link-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  text-decoration: none;
}

.link-button.danger {
  color: var(--danger);
}

.nota-form {
  display: flex;
  flex-direction: column;
}

.form-row {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.status {
  margin: 0;
  font-size: 0.9rem;
  color: var(--text-muted);
}

.status-error {
  color: var(--danger);
}

.status-success {
  color: var(--brand-600);
}

@media (max-width: 1120px) {
  .notas-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .filters,
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
