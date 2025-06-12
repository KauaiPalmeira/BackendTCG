import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import { format } from 'date-fns';
import { reportService } from '../services/reportService';
import './ReportForm.css';

export default function ReportForm() {
  const [selectedDate, setSelectedDate] = useState(null);
  const [tiposTorneio, setTiposTorneio] = useState([]);
  const [locais, setLocais] = useState([]);
  const [decks, setDecks] = useState([]);
  const [formData, setFormData] = useState({
    tournament: '',
    location: '',
    players: '',
    player1: '',
    deck1: '',
    player2: '',
    deck2: '',
    player3: '',
    deck3: '',
    player4: '',
    deck4: '',
    player5: '',
    deck5: '',
    player6: '',
    deck6: '',
    player7: '',
    deck7: '',
    player8: '',
    deck8: '',
  });

  // Carregar dados iniciais
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [tiposTorneioData, locaisData, decksData] = await Promise.all([
          reportService.getTiposTorneio(),
          reportService.getLocais(),
          reportService.getDecks()
        ]);

        setTiposTorneio(tiposTorneioData);
        setLocais(locaisData);
        setDecks(decksData);
      } catch (error) {
        console.log('%c❌ Erro ao carregar dados iniciais', 'color: red; font-weight: bold;');
        console.log('%c👉 Verifique se o backend está rodando em localhost:8080', 'color: orange;');
      }
    };

    fetchData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      // Validações básicas
      if (!selectedDate) {
        console.log('%c❌ Erro: Data do torneio não selecionada', 'color: red; font-weight: bold;');
        return;
      }

      if (!formData.tournament) {
        console.log('%c❌ Erro: Tipo de torneio não selecionado', 'color: red; font-weight: bold;');
        return;
      }

      if (!formData.location) {
        console.log('%c❌ Erro: Local não selecionado', 'color: red; font-weight: bold;');
        return;
      }

      if (!formData.players || parseInt(formData.players) <= 0) {
        console.log('%c❌ Erro: Número de participantes inválido', 'color: red; font-weight: bold;');
        return;
      }

      // Preparar os jogadores
      const jogadores = [];
      let jogadoresIncompletos = false;

      for (let i = 1; i <= 8; i++) {
        const nomeJogador = formData[`player${i}`];
        const deckId = parseInt(formData[`deck${i}`]);
        
        if (!nomeJogador || !deckId) {
          console.log(`%c❌ Erro: Jogador ${i} incompleto. Nome e deck são obrigatórios.`, 'color: red; font-weight: bold;');
          jogadoresIncompletos = true;
          continue;
        }

        jogadores.push({
          nomeJogador,
          deckId,
          posicao: i
        });
      }

      if (jogadoresIncompletos || jogadores.length !== 8) {
        console.log('%c❌ Erro: Todos os 8 jogadores devem ser preenchidos com nome e deck', 'color: red; font-weight: bold;');
        return;
      }

      // Preparar o payload no formato esperado pelo backend
      const payload = {
        tipoTorneioId: parseInt(formData.tournament),
        localId: parseInt(formData.location),
        dataTorneio: format(selectedDate, 'yyyy-MM-dd'),
        numeroParticipantes: parseInt(formData.players),
        jogadores
      };

      // Enviar para o backend
      const created = await reportService.createRelatorio(payload);

      // Baixar imagem em alta qualidade automaticamente
      if (created && created.id) {
        try {
          const imageResponse = await reportService.getHighQualityImage(created.id);
          const base64 = imageResponse.imagemBase64;
          if (base64) {
            const byteCharacters = atob(base64);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: 'image/png' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'relatorio.png');
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
          }
        } catch (e) {
          console.log('%c❌ Erro ao baixar imagem do relatório', 'color: red; font-weight: bold;');
        }
      }

      // Limpar formulário após sucesso
      setSelectedDate(null);
      setFormData({
        tournament: '',
        location: '',
        players: '',
        player1: '',
        deck1: '',
        player2: '',
        deck2: '',
        player3: '',
        deck3: '',
        player4: '',
        deck4: '',
        player5: '',
        deck5: '',
        player6: '',
        deck6: '',
        player7: '',
        deck7: '',
        player8: '',
        deck8: '',
      });
      
    } catch (error) {
      console.log('%c❌ Erro ao enviar relatório', 'color: red; font-weight: bold;');
      console.log('%c👉 Detalhes:', 'color: orange;', error.message);
    }
  };

  return (
    <div className="report-form-container">
      <form onSubmit={handleSubmit} className="report-form">
        <h2 className="report-form-title">REPORTAR TORNEIO</h2>
        
        <div className="form-grid">
          <div className="form-section">
            <label className="form-label">TORNEIO</label>
            <select
              name="tournament"
              value={formData.tournament}
              onChange={handleInputChange}
              className="form-select"
            >
              <option value="">Selecione um torneio</option>
              {tiposTorneio.map(tipo => (
                <option key={tipo.id} value={tipo.id}>
                  {tipo.nome}
                </option>
              ))}
            </select>
          </div>

          <div className="form-section">
            <label className="form-label">LOCAL</label>
            <select
              name="location"
              value={formData.location}
              onChange={handleInputChange}
              className="form-select"
            >
              <option value="">Selecione um local</option>
              {locais.map(local => (
                <option key={local.id} value={local.id}>
                  {local.nome}
                </option>
              ))}
            </select>
          </div>

          <div className="form-section">
            <label className="form-label">DIA DO TORNEIO</label>
            <DatePicker
              selected={selectedDate}
              onChange={(date) => setSelectedDate(date)}
              dateFormat="dd/MM/yyyy"
              className="form-input"
            />
          </div>

          <div className="form-section">
            <label className="form-label">Qº DE PLAYERS</label>
            <input
              type="number"
              name="players"
              value={formData.players}
              onChange={handleInputChange}
              className="form-input"
              min="8"
              max="999"
            />
          </div>
        </div>

        <div className="players-grid">
          {[...Array(8)].map((_, index) => {
            const playerNum = index + 1;
            return (
              <div key={playerNum} className="player-section">
                <div>
                  <label className="form-label">PLAYER {playerNum}º</label>
                  <input
                    type="text"
                    name={`player${playerNum}`}
                    value={formData[`player${playerNum}`]}
                    onChange={handleInputChange}
                    className="form-input"
                    placeholder="Nome do jogador"
                  />
                </div>
                <div>
                  <label className="form-label">DECK</label>
                  <select
                    name={`deck${playerNum}`}
                    value={formData[`deck${playerNum}`]}
                    onChange={handleInputChange}
                    className="form-select"
                  >
                    <option value="">Selecione um deck</option>
                    {decks.map(deck => (
                      <option key={deck.id} value={deck.id}>
                        {deck.nome}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            );
          })}
        </div>

        <button type="submit" className="submit-button">
          Gerar Relatório
        </button>
      </form>
    </div>
  );
}
