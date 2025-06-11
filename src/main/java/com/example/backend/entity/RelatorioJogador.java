package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "relatorio_jogador")
@Data
public class RelatorioJogador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private Relatorio relatorio;

    @ManyToOne
    @JoinColumn(name = "jogador_id", nullable = true)
    private Jogador jogador;

    @Column(name = "nome_jogador", nullable = false)
    private String nomeJogador;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(nullable = false)
    private Integer posicao;

    @Column(nullable = false)
    private Integer pontos;
}
