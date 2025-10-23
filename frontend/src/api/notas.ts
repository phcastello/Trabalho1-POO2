import { http } from "./http";

export interface Nota {
  alunoId: number;
  provaId: number;
  valor: number;
  observacao: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface NotaPayload {
  alunoId: number;
  provaId: number;
  valor: number;
  observacao?: string | null;
}

export interface NotaUpdatePayload {
  valor: number;
  observacao?: string | null;
}

export interface NotaQuery {
  alunoId?: number;
  provaId?: number;
}

export async function listNotas(params: NotaQuery = {}) {
  const { data } = await http.get<Nota[]>("/notas", { params });
  return data;
}

export async function createNota(payload: NotaPayload) {
  const { data } = await http.post<Nota>("/notas", payload);
  return data;
}

export async function updateNota(alunoId: number, provaId: number, payload: NotaUpdatePayload) {
  const { data } = await http.put<Nota>(`/notas/${alunoId}/${provaId}`, payload);
  return data;
}

export async function deleteNota(alunoId: number, provaId: number) {
  await http.delete(`/notas/${alunoId}/${provaId}`);
}

