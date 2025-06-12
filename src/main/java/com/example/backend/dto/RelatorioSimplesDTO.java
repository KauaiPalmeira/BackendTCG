package com.example.backend.dto;

import java.time.LocalDate;

public record RelatorioSimplesDTO(Long id, String local, LocalDate dataTorneio, byte[] imagem) {} 