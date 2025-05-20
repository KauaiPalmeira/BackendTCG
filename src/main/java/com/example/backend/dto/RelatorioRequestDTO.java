package com.example.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record RelatorioRequestDTO(
        Long tipoTorneioId,
        Long localId,
        LocalDate dataTorneio,
        Integer numeroParticipantes,
        List<JogadorRelatorioDTO> jogadores
) {
    public record JogadorRelatorioDTO(
            String nomeJogador,
            Long deckId,
            Integer posicao
    ) {}
}