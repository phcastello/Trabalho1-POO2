import { http } from "./http";

export interface DepartamentoDesempenho {
  departamentoId: number;
  departamentoNome: string;
  mediaNotas: number;
  menorNota: number;
  maiorNota: number;
  alunosAvaliados: number;
  notasLancadas: number;
}

export interface AlunoModalidadeEquilibrada {
  alunoId: number;
  ra: string;
  nome: string;
  departamentoNome: string;
  avaliacoesProva: number;
  projetosEntregues: number;
}

export interface AlunoCoberturaNotas {
  alunoId: number;
  ra: string;
  nome: string;
  departamentoNome: string;
  provasAvaliadas: number;
  mediaNotas: number;
  semNotas: boolean;
}

export interface ConsultasAvancadasResumo {
  rankingDepartamentos: DepartamentoDesempenho[];
  alunosModalidades: AlunoModalidadeEquilibrada[];
  coberturaNotas: AlunoCoberturaNotas[];
}

export async function fetchConsultasAvancadas() {
  const { data } = await http.get<ConsultasAvancadasResumo>("/consultas-avancadas");
  return data;
}
