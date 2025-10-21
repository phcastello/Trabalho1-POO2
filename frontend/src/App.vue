<script setup lang="ts">
import { computed } from "vue";
import { RouterLink, RouterView, useRoute, useRouter } from "vue-router";
import { useAuth } from "@/store/auth";

const auth = useAuth();
const route = useRoute();
const router = useRouter();

const showShell = computed(() => route.path !== "/login" && !!auth.user);

async function logout() {
  await auth.logout();
  router.push("/login");
}
</script>

<template>
  <div class="app-shell">
    <header v-if="showShell" class="app-header">
      <div class="app-brand">
        <div class="app-brand-icon">🎓</div>
        <span>POO2 • Gestão Acadêmica</span>
      </div>

      <nav class="app-nav">
        <RouterLink to="/dashboard" class="nav-link">Dashboard</RouterLink>
        <RouterLink to="/alunos" class="nav-link">Alunos</RouterLink>
        <RouterLink to="/departamentos" class="nav-link">Departamentos</RouterLink>
        <RouterLink to="/provas" class="nav-link">Provas</RouterLink>
        <RouterLink to="/notas" class="nav-link">Notas</RouterLink>
      </nav>

      <div class="user-chip">
        <span>Olá, <strong>{{ auth.user?.nome }}</strong></span>
        <button type="button" @click="logout">Sair</button>
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>
