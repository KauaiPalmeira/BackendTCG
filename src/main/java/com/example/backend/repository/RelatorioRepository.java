package com.example.backend.repository;

import com.example.backend.entity.Relatorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {
    @Query("SELECT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "LEFT JOIN FETCH r.tipoTorneio " +
           "LEFT JOIN FETCH r.jogadores j " +
           "LEFT JOIN FETCH j.deck " +
           "ORDER BY r.dataTorneio DESC")
    List<Relatorio> findAllWithRelationships();

    @Query(value = "SELECT DISTINCT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "LEFT JOIN FETCH r.tipoTorneio " +
           "LEFT JOIN FETCH r.jogadores j " +
           "LEFT JOIN FETCH j.deck " +
           "ORDER BY r.dataTorneio DESC",
           countQuery = "SELECT COUNT(r) FROM Relatorio r")
    Page<Relatorio> findAllWithRelationships(Pageable pageable);
}
