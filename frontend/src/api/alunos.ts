import { http } from "./http";

export interface Aluno {
  id: number;
  ra: string;
  nome: string;
  email: string | null;
  departamentoId: number;
  dataNascimento: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AlunoPayload {
  ra: string;
  nome: string;
  email?: string | null;
  departamentoId: number;
  dataNascimento?: string | null;
}

export async function listAlunos() {
  const { data } = await http.get<Aluno[]>("/alunos");
  return data;
}

export async function createAluno(payload: AlunoPayload) {
  const { data } = await http.post<Aluno>("/alunos", payload);
  return data;
}

export async function updateAluno(id: number, payload: AlunoPayload) {
  const { data } = await http.put<Aluno>(`/alunos/${id}`, payload);
  return data;
}

export async function deleteAluno(id: number) {
  await http.delete(`/alunos/${id}`);
}

