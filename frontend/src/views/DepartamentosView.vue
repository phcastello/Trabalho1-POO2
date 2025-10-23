<script setup lang="ts">
import { computed, reactive, ref, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useDepartamentos } from "@/store/departamentos";
import type { Departamento } from "@/api/departamentos";

const departamentosStore = useDepartamentos();
const { items, loading } = storeToRefs(departamentosStore);

const pageError = ref("");
const formMode = ref<"create" | "edit">("create");
const editingId = ref<number | null>(null);
const saving = ref(false);
const removingId = ref<number | null>(null);

const feedback = reactive({ success: "", error: "" });
const form = reactive({ nome: "", sigla: "" });

const hasDepartamentos = computed(() => items.value.length > 0);
const tableBusy = computed(() => loading.value && !hasDepartamentos.value);
const formTitle = computed(() =>
  formMode.value === "edit" ? "Editar departamento" : "Cadastrar departamento"
);
const submitLabel = computed(() =>
  formMode.value === "edit" ? "Salvar alterações" : "Cadastrar departamento"
);

function clearFeedback() {
  feedback.success = "";
  feedback.error = "";
  departamentosStore.clearError();
}

function resetForm() {
  form.nome = "";
  form.sigla = "";
}

function startCreate() {
  formMode.value = "create";
  editingId.value = null;
  clearFeedback();
  resetForm();
}

function startEdit(departamento: Departamento) {
  formMode.value = "edit";
  editingId.value = departamento.id;
  clearFeedback();
  form.nome = departamento.nome;
  form.sigla = departamento.sigla ?? "";
}

function normalizeSigla(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed.toUpperCase() : null;
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
    await departamentosStore.fetch(force);
  } catch (error) {
    pageError.value = extractMessage(error);
  }
}

onMounted(() => {
  if (!departamentosStore.initialized) {
    refresh(false);
  }
});

function buildPayload() {
  if (!form.nome.trim()) {
    throw new Error("Informe o nome do departamento.");
  }
  return {
    nome: form.nome.trim(),
    sigla: form.sigla ? normalizeSigla(form.sigla) : null,
  };
}

async function submit() {
  clearFeedback();

  if (!form.nome.trim()) {
    feedback.error = "Preencha o nome do departamento.";
    return;
  }

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
      await departamentosStore.update(editingId.value, payload);
      feedback.success = "Departamento atualizado com sucesso.";
    } else {
      await departamentosStore.create(payload);
      feedback.success = "Departamento cadastrado com sucesso.";
      resetForm();
    }
  } catch (error) {
    feedback.error = departamentosStore.lastError ?? extractMessage(error);
  } finally {
    saving.value = false;
  }
}

async function removeDepartamento(departamento: Departamento) {
  const confirmed = window.confirm(`Deseja realmente remover ${departamento.nome}?`);
  if (!confirmed) return;

  clearFeedback();
  removingId.value = departamento.id;
  try {
    await departamentosStore.remove(departamento.id);
    if (editingId.value === departamento.id) {
      startCreate();
    }
    feedback.success = "Departamento removido com sucesso.";
  } catch (error) {
    feedback.error = departamentosStore.lastError ?? extractMessage(error);
  } finally {
    removingId.value = null;
  }
}

function formatSigla(value: string | null) {
  return value && value.trim().length > 0 ? value : "--";
}

function formatDate(value: string) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "--";
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
    <h1 class="page-title">Departamentos</h1>
    <p class="page-subtitle">
      Organize coordenações, cursos e fluxos acadêmicos em um único lugar.
    </p>

    <div class="departamentos-layout">
      <div class="card departamentos-card">
        <header class="card-header">
          <h2>Lista de departamentos</h2>
          <button type="button" class="secondary-button" @click="refresh()" :disabled="loading">
            Atualizar
          </button>
        </header>

        <p v-if="pageError" class="status status-error">{{ pageError }}</p>
        <p v-else-if="tableBusy" class="status">Carregando departamentos...</p>
        <p v-else-if="!hasDepartamentos" class="status">Nenhum departamento cadastrado.</p>

        <div v-else class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Sigla</th>
                <th>Última atualização</th>
                <th class="actions-col">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="departamento in items" :key="departamento.id">
                <td>{{ departamento.nome }}</td>
                <td>{{ formatSigla(departamento.sigla) }}</td>
                <td>{{ formatDate(departamento.updatedAt) }}</td>
                <td class="actions-col">
                  <button type="button" class="link-button" @click="startEdit(departamento)">
                    Editar
                  </button>
                  <button
                    type="button"
                    class="link-button danger"
                    @click="removeDepartamento(departamento)"
                    :disabled="removingId === departamento.id"
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card departamentos-card">
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

        <form class="departamento-form" @submit.prevent="submit">
          <div class="form-field">
            <label for="departamento-nome">Nome *</label>
            <input id="departamento-nome" v-model="form.nome" placeholder="Ex.: Engenharia de Software" />
          </div>

          <div class="form-field">
            <label for="departamento-sigla">Sigla</label>
            <input
              id="departamento-sigla"
              v-model="form.sigla"
              maxlength="10"
              placeholder="Ex.: ENSW"
            />
            <small>Limite de 10 caracteres. Será salvo em letras maiúsculas.</small>
          </div>

          <p v-if="feedback.error" class="status status-error">{{ feedback.error }}</p>
          <p v-if="feedback.success" class="status status-success">{{ feedback.success }}</p>

          <button type="submit" :disabled="saving">
            {{ submitLabel }}
          </button>
        </form>
      </div>
    </div>
  </section>
</template>

<style scoped>
.departamentos-layout {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: minmax(0, 1.6fr) minmax(0, 1fr);
  align-items: start;
}

.departamentos-card {
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
  min-width: 520px;
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

.departamento-form {
  display: flex;
  flex-direction: column;
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

small {
  color: var(--text-muted);
}

@media (max-width: 1024px) {
  .departamentos-layout {
    grid-template-columns: 1fr;
  }
}
</style>
