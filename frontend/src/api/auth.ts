import { http } from "./http";

export async function login(username: string, password: string) {
  const { data } = await http.post("/auth/login", { username, password });
  return data as { id: number; username: string; nome: string };
}

export async function me() {
  const { data } = await http.get("/auth/me");
  return data as { id: number; nome: string };
}

export async function logout() {
  await http.post("/auth/logout");
}
