<script setup lang="ts">
import { computed, reactive, ref, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useAlunos } from "@/store/alunos";
import { useDepartamentos } from "@/store/departamentos";
import SearchableDropdown from "@/components/SearchableDropdown.vue";
import type { SearchableDropdownOption } from "@/components/dropdown.types";
import type { Aluno } from "@/api/alunos";

const alunosStore = useAlunos();
const departamentosStore = useDepartamentos();
const { items, loading } = storeToRefs(alunosStore);
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
  ra: "",
  nome: "",
  email: "",
  departamentoId: "",
  dataNascimento: "",
});

const hasAlunos = computed(() => items.value.length > 0);
const tableBusy = computed(() => loading.value && !hasAlunos.value);
const formTitle = computed(() => (formMode.value === "edit" ? "Editar aluno" : "Cadastrar aluno"));
const submitLabel = computed(() => (formMode.value === "edit" ? "Salvar alterações" : "Cadastrar aluno"));
const departamentosCarregados = computed(() => departamentos.value.length > 0);
const departamentoOptions = computed<SearchableDropdownOption[]>(() =>
  departamentos.value.map((departamento) => ({
    value: String(departamento.id),
    label: departamento.nome,
    description: departamento.sigla ? `Sigla: ${departamento.sigla}` : `ID interno: ${departamento.id}`,
    keywords: departamento.sigla ? [departamento.sigla, String(departamento.id)] : [String(departamento.id)],
  }))
);

const formatDate = (value: string | null) => {
  if (!value) return "--";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("pt-BR").format(date);
};

const formatEmail = (value: string | null) => (value && value.trim().length > 0 ? value : "--");

function clearFeedback() {
  feedback.success = "";
  feedback.error = "";
  alunosStore.clearError();
}

function resetForm() {
  form.ra = "";
  form.nome = "";
  form.email = "";
  form.departamentoId = "";
  form.dataNascimento = "";
}

function startCreate() {
  formMode.value = "create";
  editingId.value = null;
  clearFeedback();
  resetForm();
}

function startEdit(aluno: Aluno) {
  formMode.value = "edit";
  editingId.value = aluno.id;
  clearFeedback();
  form.ra = aluno.ra;
  form.nome = aluno.nome;
  form.email = aluno.email ?? "";
  form.departamentoId = aluno.departamentoId ? String(aluno.departamentoId) : "";
  form.dataNascimento = aluno.dataNascimento ?? "";
}

function normalizeEmail(value: string) {
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

async function refresh() {
  pageError.value = "";
  try {
    await alunosStore.fetch(true);
    await departamentosStore.fetch(true);
  } catch (error) {
    pageError.value = extractMessage(error);
  }
}

onMounted(() => {
  if (!departamentosStore.initialized) {
    departamentosStore.fetch().catch((error) => {
      console.error("Falha ao carregar departamentos:", error);
    });
  }
  if (!alunosStore.initialized) {
    refresh();
  }
});

function buildPayload() {
  const departamentoIdNumber = Number(form.departamentoId);
  if (Number.isNaN(departamentoIdNumber)) {
    throw new Error("Informe um departamento válido.");
  }
  return {
    ra: form.ra.trim(),
    nome: form.nome.trim(),
    email: normalizeEmail(form.email),
    departamentoId: departamentoIdNumber,
    dataNascimento: form.dataNascimento || null,
  };
}

async function submit() {
  clearFeedback();

  if (!form.ra.trim() || !form.nome.trim() || !form.departamentoId) {
    feedback.error = "Preencha RA, nome e departamento.";
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
      await alunosStore.update(editingId.value, payload);
      feedback.success = "Aluno atualizado com sucesso.";
    } else {
      await alunosStore.create(payload);
      feedback.success = "Aluno cadastrado com sucesso.";
      resetForm();
    }
  } catch (error) {
    feedback.error = alunosStore.lastError ?? extractMessage(error);
  } finally {
    saving.value = false;
  }
}

async function removeAluno(aluno: Aluno) {
  const confirmed = window.confirm(`Deseja realmente remover ${aluno.nome}?`);
  if (!confirmed) return;

  clearFeedback();
  removingId.value = aluno.id;
  try {
    await alunosStore.remove(aluno.id);
    if (editingId.value === aluno.id) {
      startCreate();
    }
    feedback.success = "Aluno removido com sucesso.";
  } catch (error) {
    feedback.error = alunosStore.lastError ?? extractMessage(error);
  } finally {
    removingId.value = null;
  }
}

function departamentoNome(id: number) {
  const departamento = departamentos.value.find((item) => item.id === id);
  return departamento ? departamento.nome : `Departamento #${id}`;
}
</script>

<template>
  <section>
    <span class="pill">Módulo</span>
    <h1 class="page-title">Alunos</h1>
    <p class="page-subtitle">
      Cadastre novos estudantes, acompanhe matrículas e mantenha dados atualizados.
    </p>

    <div class="alunos-layout">
      <div class="card alunos-card">
        <header class="card-header">
          <h2>Lista de alunos</h2>
          <button type="button" class="secondary-button" @click="refresh" :disabled="loading">
            Atualizar
          </button>
        </header>

        <p v-if="pageError" class="status status-error">{{ pageError }}</p>
        <p v-else-if="tableBusy" class="status">Carregando alunos...</p>
        <p v-else-if="!hasAlunos" class="status">Nenhum aluno cadastrado até o momento.</p>

        <div v-else class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>RA</th>
                <th>Nome</th>
                <th>E-mail</th>
                <th>Departamento</th>
                <th>Data de nascimento</th>
                <th class="actions-col">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="aluno in items" :key="aluno.id">
                <td>{{ aluno.ra }}</td>
                <td>{{ aluno.nome }}</td>
                <td>{{ formatEmail(aluno.email) }}</td>
                <td>{{ departamentoNome(aluno.departamentoId) }}</td>
                <td>{{ formatDate(aluno.dataNascimento) }}</td>
                <td class="actions-col">
                  <button type="button" class="link-button" @click="startEdit(aluno)">
                    Editar
                  </button>
                  <button
                    type="button"
                    class="link-button danger"
                    @click="removeAluno(aluno)"
                    :disabled="removingId === aluno.id"
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card alunos-card">
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

        <form class="aluno-form" @submit.prevent="submit">
          <div class="form-row">
            <div class="form-field">
              <label for="aluno-ra">RA *</label>
              <input id="aluno-ra" v-model="form.ra" placeholder="Ex.: 2023001" />
            </div>
            <div class="form-field">
              <label for="aluno-departamento">Departamento *</label>
              <SearchableDropdown
                id="aluno-departamento"
                v-model="form.departamentoId"
                :items="departamentoOptions"
                placeholder="Digite o nome do departamento"
                :disabled="departamentosLoading || !departamentosCarregados"
                :loading="departamentosLoading"
              />
            </div>
          </div>

          <div class="form-field">
            <label for="aluno-nome">Nome completo *</label>
            <input id="aluno-nome" v-model="form.nome" placeholder="Digite o nome do aluno" />
          </div>

          <div class="form-field">
            <label for="aluno-email">E-mail institucional</label>
            <input id="aluno-email" v-model="form.email" type="email" placeholder="nome.sobrenome@universidade.br" />
          </div>

          <div class="form-field">
            <label for="aluno-data-nascimento">Data de nascimento</label>
            <input id="aluno-data-nascimento" v-model="form.dataNascimento" type="date" />
          </div>

          <p v-if="departamentosLoading" class="status">Carregando departamentos...</p>
          <p v-else-if="departamentosError" class="status status-error">{{ departamentosError }}</p>
          <p v-else-if="!departamentosCarregados" class="status status-error">
            Cadastre um departamento antes de adicionar alunos.
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
.alunos-layout {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: minmax(0, 1.75fr) minmax(0, 1fr);
  align-items: start;
}

.alunos-card {
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
  min-width: 560px;
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

.aluno-form {
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

@media (max-width: 1024px) {
  .alunos-layout {
    grid-template-columns: 1fr;
  }

  .table-wrapper {
    overflow-x: auto;
  }
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
