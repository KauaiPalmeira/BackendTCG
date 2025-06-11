import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ReportForm from '../pages/ReportForm';
import UltimosReports from '../pages/UltimosReports';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<UltimosReports />} />
      <Route path="/reportar" element={<ReportForm />} />
      
      {/* Redirecionamentos das rotas antigas */}
      <Route path="/reportar-torneio" element={<Navigate to="/reportar" replace />} />
      <Route path="/report-torneio" element={<Navigate to="/reportar" replace />} />
      <Route path="/ultimos-relatorios" element={<Navigate to="/" replace />} />
    </Routes>
  );
}