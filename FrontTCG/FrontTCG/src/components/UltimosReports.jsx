import React, { useState, useEffect } from 'react';
import { reportService } from '../services/reportService';
import './UltimosReports.css';

const ReportCard = ({ report }) => {
  const [imageUrl, setImageUrl] = useState(null);
  const [loadError, setLoadError] = useState(false);

  useEffect(() => {
    if (!report || !report.imagemBase64) return;
    setImageUrl(`data:image/jpeg;base64,${report.imagemBase64}`);
  }, [report]);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: '2-digit'
    });
  };

  if (!imageUrl && !loadError) {
    return (
      <div className="report-card" id={`report-${report.id}`}>
        <div className="loading-overlay">
          <div className="loading-spinner" />
        </div>
      </div>
    );
  }

  if (loadError) {
    return (
      <div className="report-card error" id={`report-${report.id}`}>
        <div className="error-content">
          <p>Erro ao carregar imagem</p>
        </div>
      </div>
    );
  }

  return (
    <div className="report-card" id={`report-${report.id}`}>
      <div className="image-container">
        <img 
          src={imageUrl}
          alt={`Relatório do torneio em ${report.local}`}
          className="report-image"
        />
      </div>
      <div className="report-info">
        <h3 className="report-location">{report.local}</h3>
        <p className="report-date">{formatDate(report.dataTorneio)}</p>
      </div>
    </div>
  );
};

const UltimosReports = () => {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchReports = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await reportService.getAllReports();
        setReports(data);
      } catch (err) {
        setError('Erro ao carregar relatórios');
      } finally {
        setLoading(false);
      }
    };
    fetchReports();
  }, []);

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
        <p className="error-message">{error}</p>
      </div>
    );
  }

  return (
    <div className="ultimos-reports-container">
      <h2 className="ultimos-reports-title">Últimos Reports</h2>
      <div className="reports-grid">
        {reports.length === 0 ? (
          <p className="no-reports-message">Nenhum relatório encontrado</p>
        ) : (
          reports.map((report) => (
            <ReportCard 
              key={`report-${report.id}`} 
              report={report}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default UltimosReports; 