import { defineStore } from "pinia";
import { login as apiLogin, me as apiMe, logout as apiLogout } from "@/api/auth";

export const useAuth = defineStore("auth", {
  state: () => ({ user: null as null | { id: number; nome: string } }),
  actions: {
    async hydrate() {
      try { this.user = await apiMe(); } catch { this.user = null; }
    },
    async login(username: string, password: string) {
      const u = await apiLogin(username, password);
      this.user = { id: u.id, nome: u.nome };
    },
    async logout() {
      await apiLogout();
      this.user = null;
    }
  }
});
