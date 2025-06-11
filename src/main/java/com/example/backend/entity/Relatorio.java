package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relatorio")
@Data
public class Relatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipo_torneio_id", nullable = false)
    private TipoTorneio tipoTorneio;

    @ManyToOne
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    @Column(name = "data_torneio", nullable = false)
    private LocalDate dataTorneio;

    @Column(name = "numero_participantes")
    private Integer numeroParticipantes;

    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelatorioJogador> jogadores = new ArrayList<>();

    @Column(nullable = false)
    private byte[] imagem;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
