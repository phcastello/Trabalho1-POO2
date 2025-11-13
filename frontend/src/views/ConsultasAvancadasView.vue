<script setup lang="ts">
import { computed, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useConsultas } from "@/store/consultas";

const consultas = useConsultas();
const {
  loading,
  lastError,
  initialized,
  ranking,
  alunosModalidades,
  coberturaNotas,
} = storeToRefs(consultas);

const carregandoInicial = computed(() => loading.value && !initialized.value);
const hasRanking = computed(() => ranking.value.length > 0);
const hasModalidades = computed(() => alunosModalidades.value.length > 0);
const hasCobertura = computed(() => coberturaNotas.value.length > 0);

function formatNota(valor: number) {
  return valor.toLocaleString("pt-BR", { minimumFractionDigits: 1, maximumFractionDigits: 1 });
}

function plural(valor: number, texto: string, pluralTexto?: string) {
  const pluralizado = pluralTexto ?? `${texto}s`;
  return `${valor} ${valor === 1 ? texto : pluralizado}`;
}

function refresh() {
  consultas.refresh().catch(() => undefined);
}

onMounted(() => {
  if (!consultas.initialized) {
    consultas.fetch().catch(() => undefined);
  }
});
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">Consultas avançadas</h1>
        <p class="page-subtitle">
          Relatórios oficiais do requisito 7 (BD2) com agregações, operador de conjunto e junção externa.
        </p>
      </div>
      <div class="page-actions">
        <button type="button" :disabled="loading" @click="refresh">
          {{ loading ? "Atualizando..." : "Atualizar agora" }}
        </button>
      </div>
    </div>

    <div v-if="lastError" class="alert alert-error">
      <strong>Não foi possível sincronizar.</strong>
      <span>{{ lastError }}</span>
    </div>

    <div v-if="carregandoInicial" class="consulta-loader">
      <span>Carregando cenários de BD2...</span>
    </div>

    <section v-else class="consulta-section">
      <header class="consulta-headline">
        <div>
          <p class="consulta-label">Consulta 1 · Funções agregadas</p>
          <h2>Ranking de desempenho por departamento</h2>
          <p>
            Cruzamos notas, alunos e departamentos para identificar as coordenações com melhor média, maior volume de
            avaliações e amplitude de desempenho da turma.
          </p>
        </div>
      </header>

      <div v-if="!hasRanking" class="consulta-empty">
        Ainda não há notas suficientes para calcular o ranking. Lance novas notas e atualize este painel.
      </div>

      <div v-else class="consulta-table-wrapper">
        <table class="consulta-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Departamento</th>
              <th>Média</th>
              <th>Melhor nota</th>
              <th>Menor nota</th>
              <th>Alunos avaliados</th>
              <th>Notas lançadas</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in ranking" :key="item.departamentoId">
              <td>{{ idx + 1 }}</td>
              <td>{{ item.departamentoNome }}</td>
              <td class="text-strong">{{ formatNota(item.mediaNotas) }}</td>
              <td>{{ formatNota(item.maiorNota) }}</td>
              <td>{{ formatNota(item.menorNota) }}</td>
              <td>{{ plural(item.alunosAvaliados, "aluno", "alunos") }}</td>
              <td>{{ plural(item.notasLancadas, "lançamento", "lançamentos") }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="consulta-section">
      <header class="consulta-headline">
        <div>
          <p class="consulta-label">Consulta 2 · Operador de conjunto (INTERSECT)</p>
          <h2>Alunos com entrega equilibrada (provas + projetos)</h2>
          <p>
            O operador INTERSECT cruza os conjuntos de alunos com avaliações tradicionais e entregas práticas para
            evidenciar quem manteve desempenho nas duas modalidades.
          </p>
        </div>
      </header>

      <div v-if="!hasModalidades" class="consulta-empty">
        Nenhum aluno participou simultaneamente de provas e projetos até o momento.
      </div>

      <div v-else class="consulta-table-wrapper">
        <table class="consulta-table">
          <thead>
            <tr>
              <th>Aluno</th>
              <th>Departamento</th>
              <th>Provas realizadas</th>
              <th>Projetos entregues</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in alunosModalidades" :key="item.alunoId">
              <td>
                <strong>{{ item.nome }}</strong>
                <span class="muted">· RA {{ item.ra }}</span>
              </td>
              <td>{{ item.departamentoNome }}</td>
              <td>{{ plural(item.avaliacoesProva, "prova") }}</td>
              <td>{{ plural(item.projetosEntregues, "projeto") }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="consulta-section">
      <header class="consulta-headline">
        <div>
          <p class="consulta-label">Consulta 3 · Junção externa (LEFT JOIN)</p>
          <h2>Cobertura de notas por aluno</h2>
          <p>
            A junção externa mantém alunos sem lançamentos, permitindo descobrir quem ainda aguarda notas e monitorar a
            média atual de cada estudante.
          </p>
        </div>
      </header>

      <div v-if="!hasCobertura" class="consulta-empty">
        Nenhum aluno cadastrado até o momento.
      </div>

      <div v-else class="consulta-table-wrapper">
        <table class="consulta-table">
          <thead>
            <tr>
              <th>Aluno</th>
              <th>Departamento</th>
              <th>Provas avaliadas</th>
              <th>Média atual</th>
              <th>Situação</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in coberturaNotas" :key="item.alunoId">
              <td>
                <strong>{{ item.nome }}</strong>
                <span class="muted">· RA {{ item.ra }}</span>
              </td>
              <td>{{ item.departamentoNome }}</td>
              <td>{{ plural(item.provasAvaliadas, "lançamento", "lançamentos") }}</td>
              <td>{{ formatNota(item.mediaNotas) }}</td>
              <td>
                <span :class="['tag', item.semNotas ? 'tag-warning' : 'tag-success']">
                  {{ item.semNotas ? "Sem notas lançadas" : "Notas atualizadas" }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>
