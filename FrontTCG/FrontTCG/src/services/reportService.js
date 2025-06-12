import { api } from './api';

const logInfo = (message, ...args) => {
  console.log(`%c📝 ${message}`, 'color: #4CAF50; font-weight: bold;', ...args);
};

const logError = (error) => {
  console.error('%c❌ Erro:', 'color: #f44336; font-weight: bold;', error);
  if (error.response) {
    console.error('%c👉 Resposta do servidor:', 'color: #ff9800;', error.response.data);
  }
};

const logSuccess = (message) => {
  console.log(`%c✅ ${message}`, 'color: #2196F3; font-weight: bold;');
};

export const reportService = {
  // Buscar tipos de torneio
  getTiposTorneio: async () => {
    try {
      logInfo('Buscando tipos de torneio...');
      const response = await api.get('/tipos-torneio');
      logSuccess(`${response.data.length} tipos de torneio carregados`);
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  // Buscar locais
  getLocais: async () => {
    try {
      logInfo('Buscando locais...');
      const response = await api.get('/locais');
      logSuccess(`${response.data.length} locais carregados`);
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  // Buscar decks
  getDecks: async () => {
    try {
      logInfo('Buscando decks...');
      const response = await api.get('/decks');
      logSuccess(`${response.data.length} decks carregados`);
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  // Enviar relatório
  createRelatorio: async (relatorioData) => {
    try {
      logInfo('Enviando relatório...');
      logInfo(`Data do torneio: ${relatorioData.dataTorneio}`);
      logInfo(`Número de participantes: ${relatorioData.numeroParticipantes}`);
      logInfo(`Número de jogadores registrados: ${relatorioData.jogadores.length}`);
      
      const response = await api.post('/relatorios', relatorioData);
      logSuccess('Relatório criado com sucesso!');
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  async getNextReport(lastLoadedId = null) {
    try {
      logInfo(`Buscando próximo relatório${lastLoadedId ? ` após ID ${lastLoadedId}` : ''}`);
      
      const url = lastLoadedId 
        ? `/relatorios/sequencial?lastLoadedId=${lastLoadedId}`
        : '/relatorios/sequencial';
      
      const response = await api.get(url);
      
      if (!response.data) {
        logInfo('Nenhum relatório encontrado');
        return null;
      }
      
      logSuccess(`Relatório carregado - ID: ${response.data.id}`);
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  async getHighQualityImage(reportId) {
    try {
      logInfo(`Buscando imagem em alta qualidade para relatório ${reportId}`);
      const response = await api.get(`/relatorios/${reportId}/high-quality`);
      logSuccess('Imagem em alta qualidade carregada');
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  async getAllReports() {
    try {
      logInfo('Buscando todos os relatórios');
      const response = await api.get('/relatorios/todos');
      logSuccess(`${response.data.length} relatórios carregados`);
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  async downloadHighQualityImage(reportId) {
    try {
      logInfo(`Baixando imagem em alta qualidade para relatório ${reportId}`);
      const response = await api.get(`/relatorios/${reportId}/high-quality`, { responseType: 'blob' });
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  }
}; 