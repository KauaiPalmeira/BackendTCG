import React from 'react';
import LOGOTCG from '../assets/LOGOTCG.png';

export default function Header() {
  return (
    <header
      style={{
        width: '100%',
        height: '80px', // Personalize o tamanho aqui
        display: 'flex',
        alignItems: 'center',
        padding: '0 20px',
        background: 'linear-gradient(to bottom, rgba(3, 29, 77, 1) 77%, rgba(7, 65, 111, 0.66) 120%)',
      }}
    >
      <img
        src={LOGOTCG}
        alt="Logo TCG"
        style={{
          height: '70px', // Ajustável
          marginRight: '16px',
        }}
      />
      <h1
        style={{
          fontFamily: '"Open Sans Hebrew Condensed", sans-serif',
          color: '#FFFFFF',
          fontSize: '24px',
          fontWeight: 'bold',
        }}
      >
        LIGA POKÉMON TCG - CEARÁ
      </h1>
    </header>
  );
}
