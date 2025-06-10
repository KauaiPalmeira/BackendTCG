import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import { format } from 'date-fns';

export default function ReportForm() {
  const [selectedDate, setSelectedDate] = useState(null);
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

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Implement form submission
    console.log(formData);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1 style={{
        fontFamily: '"Open Sans Hebrew Condensed", sans-serif',
        fontSize: '24px',
        fontWeight: 'bold',
        marginBottom: '20px'
      }}>
        REPORTAR TORNEIO
      </h1>

      <div style={{
        backgroundColor: '#FFFFFF',
        padding: '30px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <div style={{ display: 'flex', gap: '40px' }}>
          {/* Left side */}
          <div style={{ flex: 1 }}>
            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontFamily: '"Open Sans Hebrew Condensed", sans-serif' }}>TORNEIO</label>
              <select
                name="tournament"
                value={formData.tournament}
                onChange={handleInputChange}
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #E8E8E8',
                  borderRadius: '4px',
                  backgroundColor: '#E8E8E8',
                  color: '#353535',
                  appearance: 'none',
                  backgroundImage: 'url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'currentColor\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3e%3cpolyline points=\'6 9 12 15 18 9\'%3e%3c/polyline%3e%3c/svg%3e")',
                  backgroundRepeat: 'no-repeat',
                  backgroundPosition: 'right 10px center',
                  backgroundSize: '20px'
                }}
              >
                <option value="">Selecione um torneio</option>
                {/* TODO: Add options from backend */}
              </select>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontFamily: '"Open Sans Hebrew Condensed", sans-serif' }}>LOCAL</label>
              <select
                name="location"
                value={formData.location}
                onChange={handleInputChange}
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #E8E8E8',
                  borderRadius: '4px',
                  backgroundColor: '#E8E8E8',
                  color: '#353535',
                  appearance: 'none',
                  backgroundImage: 'url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'currentColor\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3e%3cpolyline points=\'6 9 12 15 18 9\'%3e%3c/polyline%3e%3c/svg%3e")',
                  backgroundRepeat: 'no-repeat',
                  backgroundPosition: 'right 10px center',
                  backgroundSize: '20px'
                }}
              >
                <option value="">Selecione um local</option>
                {/* TODO: Add options from backend */}
              </select>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontFamily: '"Open Sans Hebrew Condensed", sans-serif' }}>DIA DO TORNEIO</label>
              <DatePicker
                selected={selectedDate}
                onChange={(date) => setSelectedDate(date)}
                dateFormat="dd/MM/yyyy"
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #E8E8E8',
                  borderRadius: '4px',
                  backgroundColor: '#E8E8E8',
                  color: '#353535'
                }}
              />
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontFamily: '"Open Sans Hebrew Condensed", sans-serif' }}>Q° DE PLAYERS</label>
              <input
                type="number"
                name="players"
                value={formData.players}
                onChange={handleInputChange}
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #E8E8E8',
                  borderRadius: '4px',
                  backgroundColor: '#E8E8E8',
                  color: '#353535'
                }}
              />
            </div>
          </div>

          {/* Right side */}
          <div style={{ flex: 1 }}>
            {[1, 2, 3, 4, 5, 6, 7, 8].map((num) => (
              <div key={num} style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '8px', fontFamily: '"Open Sans Hebrew Condensed", sans-serif' }}>PLAYER {num}°</label>
                  <input
                    type="text"
                    name={`player${num}`}
                    value={formData[`player${num}`]}
                    onChange={handleInputChange}
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #E8E8E8',
                      borderRadius: '4px',
                      backgroundColor: '#E8E8E8',
                      color: '#353535'
                    }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '8px', fontFamily: '"Open Sans Hebrew Condensed", sans-serif' }}>DECK</label>
                  <select
                    name={`deck${num}`}
                    value={formData[`deck${num}`]}
                    onChange={handleInputChange}
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #E8E8E8',
                      borderRadius: '4px',
                      backgroundColor: '#E8E8E8',
                      color: '#353535',
                      appearance: 'none',
                      backgroundImage: 'url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'currentColor\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3e%3cpolyline points=\'6 9 12 15 18 9\'%3e%3c/polyline%3e%3c/svg%3e")',
                      backgroundRepeat: 'no-repeat',
                      backgroundPosition: 'right 10px center',
                      backgroundSize: '20px'
                    }}
                  >
                    <option value="">Selecione um deck</option>
                    {/* TODO: Add options from backend */}
                  </select>
                </div>
              </div>
            ))}
          </div>
        </div>

        <button
          onClick={handleSubmit}
          style={{
            backgroundColor: '#031D4D',
            color: '#FFFFFF',
            padding: '12px 24px',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontFamily: '"Open Sans Hebrew Condensed", sans-serif',
            fontSize: '16px',
            marginTop: '20px'
          }}
        >
          GERAR REPORT
        </button>
      </div>
    </div>
  );
}
