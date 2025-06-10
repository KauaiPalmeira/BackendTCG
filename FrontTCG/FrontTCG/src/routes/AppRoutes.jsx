import { Routes, Route } from 'react-router-dom';
import ReportForm from '../pages/ReportForm';
import HomePage from '../pages/HomePage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/reportar-torneio" element={<ReportForm />} />
    </Routes>
  );
}