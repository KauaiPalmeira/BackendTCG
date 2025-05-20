package com.example.backend.repository;

import com.example.backend.entity.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    Optional<Jogador> findByNome(String nome);
}