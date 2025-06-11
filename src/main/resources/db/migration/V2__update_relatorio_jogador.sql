-- Add nome_jogador column
ALTER TABLE relatorio_jogador ADD COLUMN IF NOT EXISTS nome_jogador VARCHAR(255);

-- Make jogador_id nullable
ALTER TABLE relatorio_jogador ALTER COLUMN jogador_id DROP NOT NULL;

-- Add numero_participantes column to relatorio
ALTER TABLE relatorio ADD COLUMN IF NOT EXISTS numero_participantes INTEGER; 