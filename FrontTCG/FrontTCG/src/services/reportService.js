import api from './api';

const logSuccess = (message) => {
  console.log('%c✅ ' + message, 'color: green; font-weight: bold;');
};

const logError = (error) => {
  console.log('%c❌ Erro: ' + (error.response?.data?.message || error.message), 'color: red; font-weight: bold;');
};

const logInfo = (message) => {
  console.log('%cℹ️ ' + message, 'color: blue; font-weight: bold;');
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
      
      const response = await api.post('/relatorios', relatorioData, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      // Converter base64 para blob e fazer download
      const base64 = response.data.imagemBase64;
      const byteCharacters = atob(base64);
      const byteNumbers = new Array(byteCharacters.length);
      
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: 'image/png' });
      
      // Criar URL do blob e fazer download
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `relatorio-torneio-${relatorioData.dataTorneio}.png`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      logSuccess('Relatório criado com sucesso!');
      logSuccess(`Relatório baixado como: relatorio-torneio-${relatorioData.dataTorneio}.png`);
      
      return response.data;
    } catch (error) {
      logError(error);
      throw error;
    }
  },

  async getUltimosReports() {
    try {
      const response = await fetch('http://localhost:8080/api/relatorios/ultimos', {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error('Erro ao buscar relatórios');
      }

      const data = await response.json();
      return data;
    } catch (error) {
      console.error('Erro ao buscar relatórios:', error);
      throw error;
    }
  },
}; 