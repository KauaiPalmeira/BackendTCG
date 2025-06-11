import React, { useState, useEffect } from 'react';
import { reportService } from '../services/reportService';
import './UltimosReports.css';

export default function UltimosReports() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchReports = async () => {
      try {
        const data = await reportService.getUltimosReports();
        setReports(data);
        setLoading(false);
      } catch (err) {
        setError('Erro ao carregar os relatórios. Tente novamente mais tarde.');
        setLoading(false);
      }
    };

    fetchReports();
  }, []);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="ultimos-reports-container">
        <h2 className="ultimos-reports-title">Carregando...</h2>
      </div>
    );
  }

  if (error) {
    return (
      <div className="ultimos-reports-container">
        <h2 className="ultimos-reports-title">Erro</h2>
        <p style={{ color: 'white' }}>{error}</p>
      </div>
    );
  }

  return (
    <div className="ultimos-reports-container">
      <h2 className="ultimos-reports-title">Últimos Reports</h2>
      
      <div className="reports-grid">
        {reports.map((report) => (
          <div key={report.id} className="report-card">
            <img 
              src={`data:image/png;base64,${report.imagemBase64}`}
              alt={`Relatório do torneio em ${report.local}`}
              className="report-image"
            />
            <div className="report-info">
              <h3 className="report-location">{report.local}</h3>
              <p className="report-date">{formatDate(report.dataTorneio)}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
} 