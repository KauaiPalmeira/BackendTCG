import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import LOGOTCG from '../assets/LOGOTCG.png';
import './Header.css';

export default function Header() {
  const location = useLocation();

  return (
    <header className="header">
      <div className="header-content">
        <Link to="/">
          <img
            src={LOGOTCG}
            alt="Liga Pokémon TCG - Ceará"
            className="header-logo"
          />
        </Link>
        <h1 className="header-title">
          Liga Pokémon TCG - Ceará
        </h1>
        <nav className="header-nav">
          <Link 
            to="/" 
            className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}
          >
            ÚLTIMOS TORNEIOS
          </Link>
          <Link 
            to="/reportar" 
            className={`nav-link ${location.pathname === '/reportar' ? 'active' : ''}`}
          >
            REPORT
          </Link>
        </nav>
      </div>
    </header>
  );
}
