<script setup lang="ts">
import { ref } from "vue";
import { useAuth } from "@/store/auth";
import { useRouter } from "vue-router";

const username = ref("");
const password = ref("");
const error = ref<string | null>(null);
const loading = ref(false);

const auth = useAuth();
const router = useRouter();

async function onSubmit() {
  error.value = null;
  loading.value = true;
  try {
    await auth.login(username.value, password.value);
    router.push("/dashboard");
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? "Falha no login";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="auth-wrapper">
    <section class="auth-card">
      <header class="auth-header">
        <span class="pill">Bem-vindo de volta</span>
        <h1 class="auth-title">Acesse o painel acadêmico</h1>
        <p class="auth-subtitle">
          Faça login com suas credenciais para gerir alunos, provas e notas.
        </p>
      </header>

      <form @submit.prevent="onSubmit">
        <div class="form-field">
          <label for="username">Usuário</label>
          <input
            id="username"
            v-model="username"
            type="text"
            required
            autocomplete="username"
            placeholder="ex: coordenacao"
          />
        </div>

        <div class="form-field">
          <label for="password">Senha</label>
          <input
            id="password"
            v-model="password"
            type="password"
            required
            autocomplete="current-password"
            placeholder="Digite sua senha"
          />
        </div>

        <button :disabled="loading">
          {{ loading ? "Entrando..." : "Entrar no sistema" }}
        </button>

        <p v-if="error" class="form-error">{{ error }}</p>
      </form>

      <footer class="auth-footer">
        Acesso restrito à equipe. Entre em contato com a coordenação se precisar de ajuda.
      </footer>
    </section>
  </div>
</template>
