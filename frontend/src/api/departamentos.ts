import { http } from "./http";

export interface Departamento {
  id: number;
  nome: string;
  sigla: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface DepartamentoPayload {
  nome: string;
  sigla?: string | null;
}

export async function listDepartamentos() {
  const { data } = await http.get<Departamento[]>("/departamentos");
  return data;
}

export async function createDepartamento(payload: DepartamentoPayload) {
  const { data } = await http.post<Departamento>("/departamentos", payload);
  return data;
}

export async function updateDepartamento(id: number, payload: DepartamentoPayload) {
  const { data } = await http.put<Departamento>(`/departamentos/${id}`, payload);
  return data;
}

export async function deleteDepartamento(id: number) {
  await http.delete(`/departamentos/${id}`);
}

