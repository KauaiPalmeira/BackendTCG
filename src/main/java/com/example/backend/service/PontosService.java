package com.example.backend.service;


import com.example.backend.entity.TipoTorneio;
import org.springframework.stereotype.Service;

@Service
public class PontosService {

    private static final int[][] PONTOS_BASE = {
            {8, 15, 5},
            {16, 25, 10},
            {26, 31, 15},
            {32, 49, 25},
            {50, 69, 60},
            {70, 99, 70},
            {100, Integer.MAX_VALUE, 90}
    };

    private static final double[][] MULTIPLICADORES_REGULAR = {
            {1, 1.4},
            {2, 1.0},
            {3, 0.8},
            {4, 0.8},
            {5, 0.2},
            {6, 0.2},
            {7, 0.2},
            {8, 0.2}
    };

    private static final double[][] MULTIPLICADORES_CHALLENGE = {
            {1, 2.4},
            {2, 1.8},
            {3, 1.4},
            {4, 1.4},
            {5, 1.0},
            {6, 1.0},
            {7, 1.0},
            {8, 1.0}
    };

    private static final double[][] MULTIPLICADORES_CUP = {
            {1, 3.6},
            {2, 2.6},
            {3, 2.0},
            {4, 2.0},
            {5, 1.3},
            {6, 1.3},
            {7, 1.3},
            {8, 1.3}
    };

    public int calcularPontos(TipoTorneio tipoTorneio, int numeroParticipantes, int posicao) {
        int pontosBase = getPontosBase(numeroParticipantes);
        double multiplicador = getMultiplicador(tipoTorneio, posicao);
        return (int) (pontosBase * multiplicador);
    }

    private int getPontosBase(int numeroParticipantes) {
        for (int[] faixa : PONTOS_BASE) {
            if (numeroParticipantes >= faixa[0] && numeroParticipantes <= faixa[1]) {
                return faixa[2];
            }
        }
        return 0; // Caso inválido
    }

    private double getMultiplicador(TipoTorneio tipoTorneio, int posicao) {
        double[][] multiplicadores;
        switch (tipoTorneio.getNome().toLowerCase()) {
            case "league cup":
                multiplicadores = MULTIPLICADORES_CUP;
                break;
            case "league challenge":
                multiplicadores = MULTIPLICADORES_CHALLENGE;
                break;
            default:
                multiplicadores = MULTIPLICADORES_REGULAR;
        }
        for (double[] m : multiplicadores) {
            if ((int) m[0] == posicao) {
                return m[1];
            }
        }
        return 0.0; // Caso inválido
    }
}
