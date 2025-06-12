import axios from 'axios';

// Criar uma instância do axios com configuração personalizada
export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  // Não fazer requisições automáticas
  validateStatus: status => status >= 200 && status < 300,
  // Timeout de 30 segundos
  timeout: 30000
}); 