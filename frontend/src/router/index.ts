import { createRouter, createWebHistory } from "vue-router";
import LoginView from "@/views/LoginView.vue";
import DashboardView from "@/views/DashboardView.vue";
import AlunosView from "@/views/AlunosView.vue";
import DepartamentosView from "@/views/DepartamentosView.vue";
import ProvasView from "@/views/ProvasView.vue";
import NotasView from "@/views/NotasView.vue";
import ConsultasAvancadasView from "@/views/ConsultasAvancadasView.vue";
import { useAuth } from "@/store/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", component: LoginView },
    { path: "/dashboard", component: DashboardView },
    { path: "/alunos", component: AlunosView },
    { path: "/departamentos", component: DepartamentosView },
    { path: "/provas", component: ProvasView },
    { path: "/notas", component: NotasView },
    { path: "/consultas", name: "consultas", component: ConsultasAvancadasView },
    { path: "/", redirect: "/dashboard" }
  ],
});

router.beforeEach(async (to) => {
  const auth = useAuth();
  if (auth.user === null) await auth.hydrate();
  if (to.path !== "/login" && !auth.user) return "/login";
  if (to.path === "/login" && auth.user) return "/dashboard";
});

export default router;
