import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const http = axios.create({
  baseURL,
  timeout: 10000,
  withCredentials: true, // envia/recebe cookie de sessão
});
