<script setup lang="ts">
import { computed, onMounted } from "vue";
import { RouterLink } from "vue-router";
import { storeToRefs } from "pinia";
import { useAuth } from "@/store/auth";
import { useAlunos } from "@/store/alunos";
import { useDepartamentos } from "@/store/departamentos";
import { useProvas } from "@/store/provas";
import { useNotas } from "@/store/notas";
import { useConsultas } from "@/store/consultas";

const auth = useAuth();
const alunosStore = useAlunos();
const {
  items: alunosItems,
  total: alunosTotal,
  loading: alunosLoading,
  initialized: alunosInitialized,
  lastError: alunosError,
} = storeToRefs(alunosStore);
const departamentosStore = useDepartamentos();
const {
  items: departamentosItems,
  total: departamentosTotal,
  loading: departamentosLoading,
  initialized: departamentosInitialized,
  lastError: departamentosError,
} = storeToRefs(departamentosStore);
const provasStore = useProvas();
const {
  items: provasItems,
  total: provasTotal,
  loading: provasLoading,
  initialized: provasInitialized,
  lastError: provasError,
} = storeToRefs(provasStore);
const notasStore = useNotas();
const {
  items: notasItems,
  total: notasTotal,
  loading: notasLoading,
  initialized: notasInitialized,
  lastError: notasError,
} = storeToRefs(notasStore);
const consultasStore = useConsultas();
const {
  resumo: consultasResumo,
  loading: consultasLoading,
  lastError: consultasError,
  initialized: consultasInitialized,
} = storeToRefs(consultasStore);

const firstName = computed(() => {
  const fullName = auth.user?.nome ?? "coordenador";
  return fullName.split(" ")[0];
});

onMounted(() => {
  alunosStore.fetch().catch(() => undefined);
  departamentosStore.fetch().catch(() => undefined);
  provasStore.fetch().catch(() => undefined);
  notasStore.fetch({}).catch(() => undefined);
  consultasStore.fetch().catch(() => undefined);
});

const alunosCardValue = computed(() => {
  if (alunosError.value) return "--";
  if (!alunosInitialized.value && alunosLoading.value) return "...";
  return String(alunosTotal.value);
});

const alunosCardNote = computed(() => {
  if (alunosError.value) return "Não foi possível sincronizar agora. Tente novamente em instantes.";
  if (!alunosInitialized.value) return "Sincronize com o backend para enxergar matrículas em tempo real.";
  if (alunosLoading.value) return "Atualizando dados em segundo plano.";
  return "Contagem atualizada com base nos cadastros do sistema.";
});

const departamentosCardValue = computed(() => {
  if (departamentosError.value) return "--";
  if (!departamentosInitialized.value && departamentosLoading.value) return "...";
  return String(departamentosTotal.value);
});

const departamentosCardNote = computed(() => {
  if (departamentosError.value) return "Não foi possível obter os departamentos no momento.";
  if (!departamentosInitialized.value) return "Cadastre uma coordenação para organizar cursos e turmas.";
  if (departamentosLoading.value) return "Atualizando dados em segundo plano.";
  return "Departamentos disponíveis para vincular alunos e provas.";
});

const provasCardValue = computed(() => {
  if (provasError.value) return "--";
  if (!provasInitialized.value && provasLoading.value) return "...";
  return String(provasTotal.value);
});

const provasCardNote = computed(() => {
  if (provasError.value) return "Falha ao sincronizar provas. Tente novamente em instantes.";
  if (!provasInitialized.value) return "Sincronize avaliações com o backend para planejar o semestre.";
  if (provasLoading.value) return "Atualizando dados em segundo plano.";
  return "Total de avaliações cadastradas para os departamentos.";
});

const notasCardValue = computed(() => {
  if (notasError.value) return "--";
  if (!notasInitialized.value && notasLoading.value) return "...";
  return String(notasTotal.value);
});

const notasCardNote = computed(() => {
  if (notasError.value) return "Não foi possível carregar as notas neste momento.";
  if (!notasInitialized.value) return "Lance as notas após as provas para acompanhar o desempenho.";
  if (notasLoading.value) return "Atualizando dados em segundo plano.";
  return "Contagem de notas lançadas pelos docentes.";
});

function formatNotaValor(valor: number) {
  return valor.toLocaleString("pt-BR", { minimumFractionDigits: 1, maximumFractionDigits: 1 });
}

function pluralize(valor: number, singular: string, plural?: string) {
  const termoPlural = plural ?? `${singular}s`;
  return `${valor} ${valor === 1 ? singular : termoPlural}`;
}

function clampPercentage(valor: number) {
  if (!Number.isFinite(valor)) return 0;
  if (valor < 0) return 0;
  if (valor > 100) return 100;
  return valor;
}

function formatShortDate(dateString: string) {
  const parsed = new Date(`${dateString}T00:00:00`);
  return parsed
    .toLocaleDateString("pt-BR", { day: "2-digit", month: "short" })
    .replace(".", "");
}

function formatRelativeDays(days: number) {
  if (days === 0) return "Hoje";
  if (days === 1) return "Amanhã";
  return `Em ${days} dias`;
}

const consultasHighlights = computed(() => {
  if (!consultasResumo.value) return null;
  const [topDepartamento] = consultasResumo.value.rankingDepartamentos;
  const alunosEquilibrados = consultasResumo.value.alunosModalidades.length;
  const alunosSemNotas = consultasResumo.value.coberturaNotas.filter((aluno) => aluno.semNotas).length;

  return {
    topDepartamento,
    alunosEquilibrados,
    alunosSemNotas,
  };
});

const analyticsError = computed(
  () => alunosError.value ?? departamentosError.value ?? provasError.value ?? notasError.value
);

const analyticsDependenciesReady = computed(
  () => alunosInitialized.value && departamentosInitialized.value && provasInitialized.value && notasInitialized.value
);

const analyticsStatus = computed<"loading" | "ready" | "error">(() => {
  if (analyticsError.value) return "error";
  if (!analyticsDependenciesReady.value) return "loading";
  return "ready";
});

const analytics = computed(() => {
  if (analyticsStatus.value !== "ready") return null;

  const alunosList = alunosItems.value;
  const departamentosList = departamentosItems.value;
  const provasList = provasItems.value;
  const notasList = notasItems.value;

  const alunoMap = new Map(alunosList.map((aluno) => [aluno.id, aluno]));
  const departamentoMap = new Map(departamentosList.map((departamento) => [departamento.id, departamento]));
  const provaMap = new Map(provasList.map((prova) => [prova.id, prova]));

  const alunosCountByDepartamento = new Map<number, number>();
  for (const aluno of alunosList) {
    alunosCountByDepartamento.set(
      aluno.departamentoId,
      (alunosCountByDepartamento.get(aluno.departamentoId) ?? 0) + 1
    );
  }

  const provasCountByDepartamento = new Map<number, number>();
  for (const prova of provasList) {
    provasCountByDepartamento.set(
      prova.departamentoId,
      (provasCountByDepartamento.get(prova.departamentoId) ?? 0) + 1
    );
  }

  const departamentoStats = new Map<number, { sum: number; count: number }>();
  const studentStats = new Map<number, { sum: number; count: number }>();
  const provaStats = new Map<number, { sum: number; count: number }>();

  let notasSum = 0;

  for (const nota of notasList) {
    notasSum += nota.valor;

    const alunoEntry = studentStats.get(nota.alunoId) ?? { sum: 0, count: 0 };
    alunoEntry.sum += nota.valor;
    alunoEntry.count += 1;
    studentStats.set(nota.alunoId, alunoEntry);

    const prova = provaMap.get(nota.provaId);
    if (prova) {
      const provaEntry = provaStats.get(prova.id) ?? { sum: 0, count: 0 };
      provaEntry.sum += nota.valor;
      provaEntry.count += 1;
      provaStats.set(prova.id, provaEntry);

      const departamentoEntry = departamentoStats.get(prova.departamentoId) ?? { sum: 0, count: 0 };
      departamentoEntry.sum += nota.valor;
      departamentoEntry.count += 1;
      departamentoStats.set(prova.departamentoId, departamentoEntry);
    }
  }

  const overallAverage = notasList.length ? notasSum / notasList.length : null;

  const studentRanking = Array.from(studentStats.entries())
    .map(([alunoId, stats]) => {
      const aluno = alunoMap.get(alunoId);
      const media = stats.sum / stats.count;
      return {
        alunoId,
        nome: aluno?.nome ?? `Aluno ${alunoId}`,
        media,
        mediaLabel: formatNotaValor(media),
        avaliacoes: stats.count,
        subtitle: pluralize(stats.count, "avaliação", "avaliações"),
      };
    })
    .sort((a, b) => {
      if (b.media !== a.media) return b.media - a.media;
      if (b.avaliacoes !== a.avaliacoes) return b.avaliacoes - a.avaliacoes;
      return a.nome.localeCompare(b.nome, "pt-BR", { sensitivity: "base" });
    });

  const gradeBuckets = [
    { id: "9-10", label: "9.0 - 10.0", min: 9, max: 10.01 },
    { id: "7-9", label: "7.0 - 8.9", min: 7, max: 9 },
    { id: "5-7", label: "5.0 - 6.9", min: 5, max: 7 },
    { id: "0-5", label: "0 - 4.9", min: 0, max: 5 },
  ] as const;

  const bucketCounters = gradeBuckets.map(() => 0);
  for (const nota of notasList) {
    for (let i = 0; i < gradeBuckets.length; i += 1) {
      const bucket = gradeBuckets[i];
      if (nota.valor >= bucket.min && nota.valor < bucket.max) {
        bucketCounters[i] += 1;
        break;
      }
    }
  }

  const gradeDistribution = gradeBuckets.map((bucket, index) => {
    const count = bucketCounters[index];
    const percentage = notasList.length ? (count / notasList.length) * 100 : 0;
    return {
      id: bucket.id,
      label: bucket.label,
      count,
      percentage,
      percentageLabel: `${Math.round(percentage)}%`,
      meta: count ? pluralize(count, "nota", "notas") : "Sem registros",
      progress: clampPercentage(percentage),
    };
  });

  const departamentoResumo = departamentosList
    .map((departamento) => {
      const departamentoDados = departamentoStats.get(departamento.id);
      const media = departamentoDados ? departamentoDados.sum / departamentoDados.count : null;
      const totalAlunos = alunosCountByDepartamento.get(departamento.id) ?? 0;
      const totalProvas = provasCountByDepartamento.get(departamento.id) ?? 0;
      const subtitleParts = [
        pluralize(totalAlunos, "aluno", "alunos"),
        pluralize(totalProvas, "prova", "provas"),
      ];
      if (departamentoDados?.count) {
        subtitleParts.push(pluralize(departamentoDados.count, "nota", "notas"));
      } else {
        subtitleParts.push("Sem notas registradas");
      }
      return {
        id: departamento.id,
        nome: departamento.nome,
        sigla: departamento.sigla,
        media,
        mediaLabel: media != null ? formatNotaValor(media) : "--",
        avaliacoes: departamentoDados?.count ?? 0,
        totalAlunos,
        totalProvas,
        subtitle: subtitleParts.join(" • "),
        progress: media != null ? clampPercentage((media / 10) * 100) : 0,
      };
    })
    .sort((a, b) => {
      if (a.media == null && b.media == null) {
        return a.nome.localeCompare(b.nome, "pt-BR", { sensitivity: "base" });
      }
      if (a.media == null) return 1;
      if (b.media == null) return -1;
      if (b.media !== a.media) return b.media - a.media;
      return a.nome.localeCompare(b.nome, "pt-BR", { sensitivity: "base" });
    });

  const today = new Date();
  const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());

  const upcomingProvas = provasList
    .map((prova) => {
      const provaDate = new Date(`${prova.data}T00:00:00`);
      return { prova, provaDate };
    })
    .filter(({ provaDate }) => provaDate >= todayStart)
    .sort((a, b) => a.provaDate.getTime() - b.provaDate.getTime());

  const nextExamEntry = upcomingProvas[0];
  const nextExam = nextExamEntry
    ? {
        prova: nextExamEntry.prova,
        departamento: departamentoMap.get(nextExamEntry.prova.departamentoId) ?? null,
        daysUntil: Math.max(
          0,
          Math.round((nextExamEntry.provaDate.getTime() - todayStart.getTime()) / 86400000)
        ),
      }
    : null;

  const nextExamWithMeta = nextExam
    ? {
        ...nextExam,
        formattedDate: formatShortDate(nextExam.prova.data),
        relativeText: formatRelativeDays(nextExam.daysUntil),
      }
    : null;

  const bestDepartment = departamentoResumo.find((item) => item.media != null) ?? null;

  return {
    totalNotas: notasList.length,
    overallAverage,
    bestStudent: studentRanking[0] ?? null,
    bestDepartment,
    departamentoResumo,
    topStudents: studentRanking.slice(0, 5),
    gradeDistribution,
    nextExam: nextExamWithMeta,
  };
});

const hasNotas = computed(() => (analytics.value?.totalNotas ?? 0) > 0);

const dashboardInsights = computed(() => {
  if (analyticsStatus.value === "loading") {
    return [
      {
        key: "average",
        title: "Média geral das notas",
        value: "...",
        note: "Carregando dados analíticos...",
      },
      {
        key: "student",
        title: "Aluno destaque",
        value: "...",
        note: "Carregando dados analíticos...",
      },
      {
        key: "department",
        title: "Departamento com melhor média",
        value: "...",
        note: "Carregando dados analíticos...",
      },
      {
        key: "exam",
        title: "Próxima prova agendada",
        value: "...",
        note: "Carregando dados analíticos...",
      },
    ];
  }

  if (analyticsStatus.value === "error") {
    const errorMessage =
      analyticsError.value ?? "Não foi possível carregar os dados analíticos no momento.";
    return [
      {
        key: "average",
        title: "Média geral das notas",
        value: "--",
        note: errorMessage,
      },
      {
        key: "student",
        title: "Aluno destaque",
        value: "--",
        note: errorMessage,
      },
      {
        key: "department",
        title: "Departamento com melhor média",
        value: "--",
        note: errorMessage,
      },
      {
        key: "exam",
        title: "Próxima prova agendada",
        value: "--",
        note: "Consulte o backend para detalhes.",
      },
    ];
  }

  const data = analytics.value;
  if (!data) {
    return [
      {
        key: "average",
        title: "Média geral das notas",
        value: "--",
        note: "Sincronize os dados para visualizar os insights.",
      },
      {
        key: "student",
        title: "Aluno destaque",
        value: "--",
        note: "Sincronize os dados para visualizar os insights.",
      },
      {
        key: "department",
        title: "Departamento com melhor média",
        value: "--",
        note: "Sincronize os dados para visualizar os insights.",
      },
      {
        key: "exam",
        title: "Próxima prova agendada",
        value: "--",
        note: "Sincronize os dados para visualizar os insights.",
      },
    ];
  }

  const insights = [];

  insights.push({
    key: "average",
    title: "Média geral das notas",
    value: data.totalNotas ? formatNotaValor(data.overallAverage ?? 0) : "--",
    note: data.totalNotas
      ? `Com base em ${pluralize(data.totalNotas, "avaliação", "avaliações")}.`
      : "Cadastre notas para visualizar a média geral.",
  });

  insights.push({
    key: "student",
    title: "Aluno destaque",
    value: data.bestStudent ? data.bestStudent.nome : "--",
    note: data.bestStudent
      ? `Média ${formatNotaValor(data.bestStudent.media)} em ${pluralize(
          data.bestStudent.avaliacoes,
          "avaliação",
          "avaliações"
        )}.`
      : "Lançamentos de notas exibem o ranking de alunos automaticamente.",
  });

  insights.push({
    key: "department",
    title: "Departamento com melhor média",
    value: data.bestDepartment ? data.bestDepartment.nome : "--",
    note: data.bestDepartment
      ? `Média ${data.bestDepartment.mediaLabel} com ${data.bestDepartment.subtitle}.`
      : "Ainda não há notas suficientes para comparar departamentos.",
  });

  insights.push({
    key: "exam",
    title: "Próxima prova agendada",
    value: data.nextExam ? data.nextExam.prova.titulo : "Sem avaliações futuras",
    note: data.nextExam
      ? `${data.nextExam.formattedDate} • ${data.nextExam.relativeText}${
          data.nextExam.departamento
            ? ` • ${data.nextExam.departamento.sigla ?? data.nextExam.departamento.nome}`
            : ""
        }`
      : "Cadastre novas provas para planejar o cronograma.",
  });

  return insights;
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
        <span class="card-title">Departamentos ativos</span>
        <p class="card-value">{{ departamentosCardValue }}</p>
        <p class="card-note">{{ departamentosCardNote }}</p>
      </article>

      <article class="card">
        <span class="card-title">Alunos ativos</span>
        <p class="card-value">{{ alunosCardValue }}</p>
        <p class="card-note">{{ alunosCardNote }}</p>
      </article>

      <article class="card">
        <span class="card-title">Provas lançadas</span>
        <p class="card-value">{{ provasCardValue }}</p>
        <p class="card-note">{{ provasCardNote }}</p>
      </article>

      <article class="card">
        <span class="card-title">Notas registradas</span>
        <p class="card-value">{{ notasCardValue }}</p>
        <p class="card-note">{{ notasCardNote }}</p>
      </article>
    </div>

    <div class="quick-actions">
      <RouterLink to="/consultas" class="quick-action">
        <span>Consultas avançadas</span>
        <p>Visualize as consultas de BD2 com agregações, INTERSECT e junções externas.</p>
      </RouterLink>

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

    <section class="analytics-section">
      <header class="analytics-header">
        <span class="pill">Recursos analíticos</span>
        <h2 class="analytics-title">Insights do semestre</h2>
        <p class="analytics-subtitle">
          Utilize os lançamentos para acompanhar o desempenho das turmas e planejar as próximas ações.
        </p>
      </header>

      <div class="analytics-insights">
        <article v-for="insight in dashboardInsights" :key="insight.key" class="insight-card">
          <span class="insight-title">{{ insight.title }}</span>
          <p class="insight-value">{{ insight.value }}</p>
          <p class="insight-note">{{ insight.note }}</p>
        </article>
      </div>

      <div v-if="analyticsStatus === 'loading'" class="analytics-empty">
        Carregando dados analíticos...
      </div>
      <div v-else-if="analyticsStatus === 'error'" class="analytics-empty">
        {{ analyticsError }}
      </div>
      <div v-else-if="!hasNotas" class="analytics-empty">
        Lançamentos de notas aparecerão aqui assim que forem registrados.
      </div>
      <div v-else class="analytics-panels">
        <article class="analytics-card">
          <header class="analytics-card-header">
            <h3>Desempenho por departamento</h3>
            <span>{{ analytics?.departamentoResumo.length }} departamentos</span>
          </header>

          <ul class="department-list">
            <li v-for="departamento in analytics?.departamentoResumo" :key="departamento.id" class="department-row">
              <div class="department-row-header">
                <div class="department-identity">
                  <span class="department-name">{{ departamento.nome }}</span>
                  <span v-if="departamento.sigla" class="department-pill">{{ departamento.sigla }}</span>
                </div>
                <span class="department-average">{{ departamento.mediaLabel }}</span>
              </div>
              <div class="metric-bar">
                <div class="metric-bar-fill" :style="{ width: departamento.progress + '%' }"></div>
              </div>
              <div class="department-meta">{{ departamento.subtitle }}</div>
            </li>
          </ul>
        </article>

        <article class="analytics-card">
          <header class="analytics-card-header">
            <h3>Ranking de alunos</h3>
            <span>{{ analytics?.topStudents.length ?? 0 }} destacados</span>
          </header>

          <ul class="ranking-list">
            <li
              v-for="(student, index) in analytics?.topStudents"
              :key="student.alunoId"
              class="ranking-row"
            >
              <span class="ranking-position">#{{ index + 1 }}</span>
              <div class="ranking-info">
                <span class="ranking-name">{{ student.nome }}</span>
                <span class="ranking-meta">{{ student.subtitle }}</span>
              </div>
              <span class="ranking-average">{{ student.mediaLabel }}</span>
            </li>
          </ul>

          <div class="analytics-divider"></div>

          <div class="distribution-header">
            <h4>Distribuição de notas</h4>
            <span>{{ analytics?.totalNotas }} registros</span>
          </div>
          <ul class="distribution-list">
            <li v-for="bucket in analytics?.gradeDistribution" :key="bucket.id" class="distribution-row">
              <div class="distribution-info">
                <span class="distribution-label">{{ bucket.label }}</span>
                <span class="distribution-meta">{{ bucket.meta }}</span>
              </div>
              <div class="metric-bar">
                <div class="metric-bar-fill" :style="{ width: bucket.progress + '%' }"></div>
              </div>
              <span class="distribution-percentage">{{ bucket.percentageLabel }}</span>
            </li>
          </ul>
        </article>
      </div>
    </section>

    <section class="consultas-preview">
      <header class="consultas-preview-header">
        <div>
          <span class="pill">Consultas avançadas</span>
          <h2>Resumo rápido do requisito 7</h2>
          <p>
            Veja quais departamentos lideram o ranking, quantos alunos equilibram provas e projetos e quem ainda está sem
            notas. Abra o painel completo para explorar cada tabela em detalhes.
          </p>
        </div>
        <RouterLink to="/consultas" class="button-link">Abrir painel completo</RouterLink>
      </header>

      <div v-if="consultasError" class="alert alert-error compact">
        <strong>Consultas indisponíveis.</strong>
        <span>{{ consultasError }}</span>
      </div>
      <div v-else-if="!consultasInitialized || consultasLoading" class="consulta-preview-loader">
        Carregando consultas avançadas...
      </div>
      <div v-else class="consultas-preview-grid">
        <article class="consultas-preview-card">
          <p class="preview-label">Departamento destaque</p>
          <h3>{{ consultasHighlights?.topDepartamento?.departamentoNome ?? "Sem dados" }}</h3>
          <p class="preview-note" v-if="consultasHighlights?.topDepartamento">
            Média {{ formatNotaValor(consultasHighlights.topDepartamento.mediaNotas) }} com
            {{ pluralize(consultasHighlights.topDepartamento.notasLancadas, "lançamento", "lançamentos") }}.
          </p>
          <p v-else class="preview-note">Cadastre notas para gerar o ranking.</p>
        </article>

        <article class="consultas-preview-card">
          <p class="preview-label">Alunos equilibrados</p>
          <h3>{{ consultasHighlights?.alunosEquilibrados ?? 0 }}</h3>
          <p class="preview-note">Participaram de provas e projetos (consulta com INTERSECT).</p>
        </article>

        <article class="consultas-preview-card">
          <p class="preview-label">Alunos sem notas</p>
          <h3>{{ consultasHighlights?.alunosSemNotas ?? 0 }}</h3>
          <p class="preview-note">Detectados via LEFT JOIN para priorizar cobranças.</p>
        </article>
      </div>
    </section>
  </section>
</template>
