package com.example.backend.dto;

public record JogadorRelatorioDTO(
        Long jogadorId,
        Long deckId,
        Integer posicao
) {}
