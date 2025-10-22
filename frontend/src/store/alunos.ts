import { defineStore } from "pinia";
import {
  type Aluno,
  type AlunoPayload,
  listAlunos,
  createAluno,
  updateAluno,
  deleteAluno,
} from "@/api/alunos";

function parseError(error: unknown): string {
  if (error && typeof error === "object" && "response" in error) {
    const response = (error as any).response;
    return (
      response?.data?.message ||
      response?.data?.error ||
      `Requisição falhou (${response?.status ?? "erro"})`
    );
  }
  if (error instanceof Error) return error.message;
  return "Erro inesperado.";
}

export const useAlunos = defineStore("alunos", {
  state: () => ({
    items: [] as Aluno[],
    loading: false,
    initialized: false,
    lastError: null as string | null,
  }),
  getters: {
    total: (state) => state.items.length,
  },
  actions: {
    sortItems() {
      this.items.sort((a, b) => a.nome.localeCompare(b.nome, "pt-BR", { sensitivity: "base" }));
    },
    async fetch(force = false) {
      if (this.loading) return;
      if (this.initialized && !force) return;
      this.loading = true;
      try {
        const data = await listAlunos();
        this.items = data.slice();
        this.sortItems();
        this.initialized = true;
        this.lastError = null;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async create(payload: AlunoPayload) {
      try {
        const created = await createAluno(payload);
        this.items.push(created);
        this.sortItems();
        return created;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async update(id: number, payload: AlunoPayload) {
      try {
        const updated = await updateAluno(id, payload);
        const idx = this.items.findIndex((a) => a.id === id);
        if (idx >= 0) {
          this.items[idx] = updated;
          this.sortItems();
        } else {
          this.items.push(updated);
          this.sortItems();
        }
        return updated;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async remove(id: number) {
      try {
        await deleteAluno(id);
        this.items = this.items.filter((aluno) => aluno.id !== id);
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    clearError() {
      this.lastError = null;
    },
  },
});

