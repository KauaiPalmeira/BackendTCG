package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "jogador")
@Data
public class Jogador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID identificador;

    @Column(nullable = false, length = 100)
    private String nome;

    @PrePersist
    public void prePersist() {
        if (identificador == null) {
            identificador = UUID.randomUUID();
        }
    }
}