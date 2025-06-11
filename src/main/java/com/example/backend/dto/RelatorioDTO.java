package com.example.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record RelatorioDTO(
    Long tipoTorneioId,
    Long localId,
    LocalDate dataTorneio,
    Integer numeroParticipantes,
    List<JogadorDTO> jogadores
) {
    public record JogadorDTO(
        String nomeJogador,
        Long deckId,
        Integer posicao
    ) {}
} 