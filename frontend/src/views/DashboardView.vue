<script setup lang="ts">
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { useAuth } from "@/store/auth";

const auth = useAuth();

const firstName = computed(() => {
  const fullName = auth.user?.nome ?? "coordenador";
  return fullName.split(" ")[0];
});
</script>

<template>
  <section>
    <span class="pill">Painel geral</span>
    <h1 class="page-title">Bem-vindo, {{ firstName }} 👋</h1>
    <p class="page-subtitle">
      Consolide as informações acadêmicas da instituição, acompanhe desempenho
      das turmas e organize os próximos passos do semestre.
    </p>

    <div class="card-grid">
      <article class="card">
        <span class="card-title">Alunos ativos</span>
        <p class="card-value">--</p>
        <p class="card-note">Sincronize com o backend para enxergar matrículas em tempo real.</p>
      </article>

      <article class="card">
        <span class="card-title">Provas lançadas</span>
        <p class="card-value">--</p>
        <p class="card-note">Cadastre avaliações e distribua pesos por disciplina.</p>
      </article>

      <article class="card">
        <span class="card-title">Notas registradas</span>
        <p class="card-value">--</p>
        <p class="card-note">Após lançar as provas, atualize as notas dos alunos.</p>
      </article>
    </div>

    <div class="quick-actions">
      <RouterLink to="/alunos" class="quick-action">
        <span>Gerenciar alunos</span>
        <p>Cadastre novos discentes, organize turmas e ajuste dados básicos.</p>
      </RouterLink>

      <RouterLink to="/provas" class="quick-action">
        <span>Planejar provas</span>
        <p>Estruture avaliações, defina pesos e acompanhe entregas.</p>
      </RouterLink>

      <RouterLink to="/notas" class="quick-action">
        <span>Lançar notas</span>
        <p>Registre resultados e obtenha rapidamente médias e estatísticas.</p>
      </RouterLink>

      <RouterLink to="/departamentos" class="quick-action">
        <span>Organizar departamentos</span>
        <p>Centralize cursos, docentes e coordenações dentro da instituição.</p>
      </RouterLink>
    </div>

    <div class="empty-state">
      Recursos analíticos e dashboards detalhados chegam em breve. Enquanto isso,
      utilize o backend para alimentar os módulos principais e vá conferindo o fluxo.
    </div>
  </section>
</template>
