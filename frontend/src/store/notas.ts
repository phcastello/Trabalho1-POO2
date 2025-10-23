import { defineStore } from "pinia";
import {
  type Nota,
  type NotaPayload,
  type NotaQuery,
  type NotaUpdatePayload,
  listNotas,
  createNota,
  updateNota,
  deleteNota,
} from "@/api/notas";

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

function matchesFilters(nota: Nota, filters: NotaQuery) {
  if (filters.alunoId != null && nota.alunoId !== filters.alunoId) return false;
  if (filters.provaId != null && nota.provaId !== filters.provaId) return false;
  return true;
}

export const useNotas = defineStore("notas", {
  state: () => ({
    items: [] as Nota[],
    loading: false,
    initialized: false,
    lastError: null as string | null,
    lastFilters: {} as NotaQuery,
  }),
  getters: {
    total: (state) => state.items.length,
  },
  actions: {
    sortItems() {
      this.items.sort((a, b) => {
        if (a.provaId === b.provaId) {
          return a.alunoId - b.alunoId;
        }
        return b.provaId - a.provaId;
      });
    },
    async fetch(filters: NotaQuery = {}, force = false) {
      if (this.loading) return;
      const sameFilters =
        !force &&
        this.initialized &&
        this.lastFilters.alunoId === filters.alunoId &&
        this.lastFilters.provaId === filters.provaId;
      if (sameFilters) return;

      this.loading = true;
      try {
        const data = await listNotas(filters);
        this.items = data.slice();
        this.sortItems();
        this.initialized = true;
        this.lastFilters = { ...filters };
        this.lastError = null;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async create(payload: NotaPayload) {
      try {
        const created = await createNota(payload);
        if (matchesFilters(created, this.lastFilters)) {
          this.items.push(created);
          this.sortItems();
        }
        return created;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async update(alunoId: number, provaId: number, payload: NotaUpdatePayload) {
      try {
        const updated = await updateNota(alunoId, provaId, payload);
        const idx = this.items.findIndex(
          (nota) => nota.alunoId === alunoId && nota.provaId === provaId
        );
        if (idx >= 0) {
          this.items[idx] = updated;
          this.sortItems();
        } else if (matchesFilters(updated, this.lastFilters)) {
          this.items.push(updated);
          this.sortItems();
        }
        return updated;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    async remove(alunoId: number, provaId: number) {
      try {
        await deleteNota(alunoId, provaId);
        this.items = this.items.filter(
          (nota) => !(nota.alunoId === alunoId && nota.provaId === provaId)
        );
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      }
    },
    clearError() {
      this.lastError = null;
    },
    setFilters(filters: NotaQuery) {
      this.lastFilters = { ...filters };
    },
  },
});

