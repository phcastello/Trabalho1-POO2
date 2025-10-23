import { defineStore } from "pinia";
import {
  type Prova,
  type ProvaPayload,
  listProvas,
  createProva,
  updateProva,
  deleteProva,
} from "@/api/provas";

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

export const useProvas = defineStore("provas", {
  state: () => ({
    items: [] as Prova[],
    loading: false,
    initialized: false,
    lastError: null as string | null,
  }),
  getters: {
    total: (state) => state.items.length,
  },
  actions: {
    sortItems() {
      this.items.sort((a, b) => {
        if (a.data === b.data) {
          return a.titulo.localeCompare(b.titulo, "pt-BR", { sensitivity: "base" });
        }
        return a.data < b.data ? 1 : -1;
      });
    },
    async fetch(force = false) {
      if (this.loading) return;
      if (this.initialized && !force) return;
      this.loading = true;
      try {
        const data = await listProvas();
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
    async create(payload: ProvaPayload) {
      try {
        const created = await createProva(payload);
        this.items.push(created);
        this.sortItems();
        return created;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async update(id: number, payload: ProvaPayload) {
      try {
        const updated = await updateProva(id, payload);
        const idx = this.items.findIndex((prova) => prova.id === id);
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
        await deleteProva(id);
        this.items = this.items.filter((prova) => prova.id !== id);
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

