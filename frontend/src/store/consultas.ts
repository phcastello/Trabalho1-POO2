import { defineStore } from "pinia";
import {
  type ConsultasAvancadasResumo,
  fetchConsultasAvancadas,
} from "@/api/consultas";

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

export const useConsultas = defineStore("consultas", {
  state: () => ({
    resumo: null as ConsultasAvancadasResumo | null,
    loading: false,
    initialized: false,
    lastError: null as string | null,
  }),
  getters: {
    ranking: (state) => state.resumo?.rankingDepartamentos ?? [],
    alunosModalidades: (state) => state.resumo?.alunosModalidades ?? [],
    coberturaNotas: (state) => state.resumo?.coberturaNotas ?? [],
  },
  actions: {
    async fetch(force = false) {
      if (this.loading) return;
      if (this.initialized && !force) return;
      this.loading = true;
      try {
        this.resumo = await fetchConsultasAvancadas();
        this.initialized = true;
        this.lastError = null;
      } catch (error) {
        this.lastError = parseError(error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async refresh() {
      return this.fetch(true);
    },
    clearError() {
      this.lastError = null;
    },
  },
});
