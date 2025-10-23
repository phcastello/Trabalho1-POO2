import { http } from "./http";

export interface Prova {
  id: number;
  departamentoId: number;
  titulo: string;
  data: string;
  descricao: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ProvaPayload {
  departamentoId: number;
  titulo: string;
  data: string;
  descricao?: string | null;
}

export async function listProvas() {
  const { data } = await http.get<Prova[]>("/provas");
  return data;
}

export async function createProva(payload: ProvaPayload) {
  const { data } = await http.post<Prova>("/provas", payload);
  return data;
}

export async function updateProva(id: number, payload: ProvaPayload) {
  const { data } = await http.put<Prova>(`/provas/${id}`, payload);
  return data;
}

export async function deleteProva(id: number) {
  await http.delete(`/provas/${id}`);
}

