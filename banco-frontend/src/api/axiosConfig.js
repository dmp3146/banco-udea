import axios from "axios";

// Cambia esta URL si tu backend corre en otro host/puerto.
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
