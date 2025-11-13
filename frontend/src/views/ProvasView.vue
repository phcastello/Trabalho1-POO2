<script setup lang="ts">
import { computed, reactive, ref, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useProvas } from "@/store/provas";
import { useDepartamentos } from "@/store/departamentos";
import SearchableDropdown from "@/components/SearchableDropdown.vue";
import type { SearchableDropdownOption } from "@/components/dropdown.types";
import type { Prova } from "@/api/provas";

const provasStore = useProvas();
const departamentosStore = useDepartamentos();
const { items, loading } = storeToRefs(provasStore);
const {
  items: departamentos,
  loading: departamentosLoading,
  lastError: departamentosError,
} = storeToRefs(departamentosStore);

const pageError = ref("");
const formMode = ref<"create" | "edit">("create");
const editingId = ref<number | null>(null);
const saving = ref(false);
const removingId = ref<number | null>(null);

const feedback = reactive({ success: "", error: "" });
const form = reactive({
  departamentoId: "",
  titulo: "",
  data: "",
  descricao: "",
});

const hasProvas = computed(() => items.value.length > 0);
const tableBusy = computed(() => loading.value && !hasProvas.value);
const formTitle = computed(() => (formMode.value === "edit" ? "Editar prova" : "Cadastrar prova"));
const submitLabel = computed(() => (formMode.value === "edit" ? "Salvar alterações" : "Cadastrar prova"));
const departamentosCarregados = computed(() => departamentos.value.length > 0);
const departamentoOptions = computed<SearchableDropdownOption[]>(() =>
  departamentos.value.map((departamento) => ({
    value: String(departamento.id),
    label: departamento.nome,
    description: departamento.sigla ? `Sigla: ${departamento.sigla}` : `ID interno: ${departamento.id}`,
    keywords: departamento.sigla ? [departamento.sigla, String(departamento.id)] : [String(departamento.id)],
  }))
);

function clearFeedback() {
  feedback.success = "";
  feedback.error = "";
  provasStore.clearError();
}

function resetForm() {
  form.departamentoId = "";
  form.titulo = "";
  form.data = "";
  form.descricao = "";
}

function startCreate() {
  formMode.value = "create";
  editingId.value = null;
  clearFeedback();
  resetForm();
}

function startEdit(prova: Prova) {
  formMode.value = "edit";
  editingId.value = prova.id;
  clearFeedback();
  form.departamentoId = prova.departamentoId ? String(prova.departamentoId) : "";
  form.titulo = prova.titulo;
  form.data = prova.data;
  form.descricao = prova.descricao ?? "";
}

function normalizeDescricao(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
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

async function refresh(force = true) {
  pageError.value = "";
  try {
    await provasStore.fetch(force);
  } catch (error) {
    pageError.value = extractMessage(error);
  }
}

async function ensureDepartamentos() {
  try {
    await departamentosStore.fetch();
  } catch (error) {
    console.error("Falha ao carregar departamentos:", error);
  }
}

onMounted(() => {
  ensureDepartamentos();
  if (!provasStore.initialized) {
    refresh(false);
  }
});

function buildPayload() {
  const departamentoIdNumber = Number(form.departamentoId);
  if (Number.isNaN(departamentoIdNumber)) {
    throw new Error("Selecione um departamento válido.");
  }
  if (!form.titulo.trim()) {
    throw new Error("Informe o título da prova.");
  }
  if (!form.data) {
    throw new Error("Informe a data da prova.");
  }

  return {
    departamentoId: departamentoIdNumber,
    titulo: form.titulo.trim(),
    data: form.data,
    descricao: normalizeDescricao(form.descricao),
  };
}

async function submit() {
  clearFeedback();

  let payload;
  try {
    payload = buildPayload();
  } catch (error) {
    feedback.error = extractMessage(error);
    return;
  }

  saving.value = true;
  try {
    if (formMode.value === "edit" && editingId.value !== null) {
      await provasStore.update(editingId.value, payload);
      feedback.success = "Prova atualizada com sucesso.";
    } else {
      await provasStore.create(payload);
      feedback.success = "Prova cadastrada com sucesso.";
      resetForm();
    }
  } catch (error) {
    feedback.error = provasStore.lastError ?? extractMessage(error);
  } finally {
    saving.value = false;
  }
}

async function removeProva(prova: Prova) {
  const confirmed = window.confirm(`Deseja realmente remover a prova "${prova.titulo}"?`);
  if (!confirmed) return;

  clearFeedback();
  removingId.value = prova.id;
  try {
    await provasStore.remove(prova.id);
    if (editingId.value === prova.id) {
      startCreate();
    }
    feedback.success = "Prova removida com sucesso.";
  } catch (error) {
    feedback.error = provasStore.lastError ?? extractMessage(error);
  } finally {
    removingId.value = null;
  }
}

function departamentoNome(id: number) {
  const departamento = departamentos.value.find((item) => item.id === id);
  return departamento ? departamento.nome : `Departamento #${id}`;
}

function formatDate(value: string) {
  if (!value) return "--";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
}
</script>

<template>
  <section>
    <span class="pill">Módulo</span>
    <h1 class="page-title">Provas</h1>
    <p class="page-subtitle">
      Planeje avaliações, defina pesos e acompanhe o andamento das disciplinas.
    </p>

    <div class="provas-layout">
      <div class="card provas-card">
        <header class="card-header">
          <h2>Lista de provas</h2>
          <button type="button" class="secondary-button" @click="refresh()" :disabled="loading">
            Atualizar
          </button>
        </header>

        <p v-if="pageError" class="status status-error">{{ pageError }}</p>
        <p v-else-if="tableBusy" class="status">Carregando provas...</p>
        <p v-else-if="!hasProvas" class="status">Nenhuma prova cadastrada.</p>

        <div v-else class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>Título</th>
                <th>Departamento</th>
                <th>Data</th>
                <th>Descrição</th>
                <th class="actions-col">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="prova in items" :key="prova.id">
                <td>{{ prova.titulo }}</td>
                <td>{{ departamentoNome(prova.departamentoId) }}</td>
                <td>{{ formatDate(prova.data) }}</td>
                <td>{{ prova.descricao ?? "--" }}</td>
                <td class="actions-col">
                  <button type="button" class="link-button" @click="startEdit(prova)">
                    Editar
                  </button>
                  <button
                    type="button"
                    class="link-button danger"
                    @click="removeProva(prova)"
                    :disabled="removingId === prova.id"
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card provas-card">
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

        <form class="prova-form" @submit.prevent="submit">
          <div class="form-row">
            <div class="form-field">
              <label for="prova-departamento">Departamento *</label>
              <SearchableDropdown
                id="prova-departamento"
                v-model="form.departamentoId"
                :items="departamentoOptions"
                placeholder="Digite o nome ou sigla do departamento"
                :disabled="departamentosLoading || !departamentosCarregados"
                :loading="departamentosLoading"
              />
            </div>

            <div class="form-field">
              <label for="prova-data">Data *</label>
              <input id="prova-data" v-model="form.data" type="date" />
            </div>
          </div>

          <div class="form-field">
            <label for="prova-titulo">Título *</label>
            <input id="prova-titulo" v-model="form.titulo" placeholder="Ex.: Avaliação intermediária de POO" />
          </div>

          <div class="form-field">
            <label for="prova-descricao">Descrição</label>
            <textarea
              id="prova-descricao"
              v-model="form.descricao"
              rows="3"
              placeholder="Detalhes adicionais, peso da prova ou orientações."
            ></textarea>
          </div>

          <p v-if="departamentosLoading" class="status">Carregando departamentos...</p>
          <p v-else-if="departamentosError" class="status status-error">{{ departamentosError }}</p>
          <p v-else-if="!departamentosCarregados" class="status">
            Cadastre um departamento antes de lançar provas.
          </p>
          <p v-if="feedback.error" class="status status-error">{{ feedback.error }}</p>
          <p v-if="feedback.success" class="status status-success">{{ feedback.success }}</p>

          <button type="submit" :disabled="saving || departamentosLoading || !departamentosCarregados">
            {{ submitLabel }}
          </button>
        </form>
      </div>
    </div>
  </section>
</template>

<style scoped>
.provas-layout {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: minmax(0, 1.7fr) minmax(0, 1fr);
  align-items: start;
}

.provas-card {
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

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  min-width: 580px;
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

.prova-form {
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

textarea {
  min-height: 90px;
  resize: vertical;
}

@media (max-width: 1024px) {
  .provas-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
