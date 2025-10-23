import { defineStore } from "pinia";
import {
  type Departamento,
  type DepartamentoPayload,
  listDepartamentos,
  createDepartamento,
  updateDepartamento,
  deleteDepartamento,
} from "@/api/departamentos";

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

export const useDepartamentos = defineStore("departamentos", {
  state: () => ({
    items: [] as Departamento[],
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
        const data = await listDepartamentos();
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
    async create(payload: DepartamentoPayload) {
      try {
        const created = await createDepartamento(payload);
        this.items.push(created);
        this.sortItems();
        return created;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async update(id: number, payload: DepartamentoPayload) {
      try {
        const updated = await updateDepartamento(id, payload);
        const idx = this.items.findIndex((d) => d.id === id);
        if (idx >= 0) {
          this.items[idx] = updated;
        } else {
          this.items.push(updated);
        }
        this.sortItems();
        return updated;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async remove(id: number) {
      try {
        await deleteDepartamento(id);
        this.items = this.items.filter((d) => d.id !== id);
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

