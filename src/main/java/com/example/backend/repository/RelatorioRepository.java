package com.example.backend.repository;

import com.example.backend.entity.Relatorio;
import com.example.backend.dto.RelatorioSimplesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {
    @Query("SELECT DISTINCT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "LEFT JOIN FETCH r.tipoTorneio " +
           "LEFT JOIN FETCH r.jogadores j " +
           "LEFT JOIN FETCH j.deck " +
           "LEFT JOIN FETCH j.jogador " +
           "ORDER BY r.dataTorneio DESC")
    List<Relatorio> findAllWithRelationships();

    @Query(value = "SELECT DISTINCT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "LEFT JOIN FETCH r.tipoTorneio " +
           "LEFT JOIN FETCH r.jogadores j " +
           "LEFT JOIN FETCH j.deck " +
           "LEFT JOIN FETCH j.jogador " +
           "ORDER BY r.dataTorneio DESC",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Relatorio r")
    Page<Relatorio> findAllWithRelationships(Pageable pageable);

    @Query("SELECT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "WHERE r.id = :id")
    Optional<Relatorio> findByIdWithLocal(Long id);

    @Query("SELECT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "WHERE r.dataTorneio < (SELECT r2.dataTorneio FROM Relatorio r2 WHERE r2.id = :lastLoadedId) " +
           "ORDER BY r.dataTorneio DESC")
    List<Relatorio> findNextRelatorio(Long lastLoadedId, Pageable pageable);

    @Query("SELECT r FROM Relatorio r " +
           "LEFT JOIN FETCH r.local " +
           "ORDER BY r.dataTorneio DESC")
    List<Relatorio> findMostRecentRelatorio(Pageable pageable);

    @Query("SELECT new com.example.backend.dto.RelatorioSimplesDTO(r.id, l.nome, r.dataTorneio, r.imagem) " +
           "FROM Relatorio r LEFT JOIN r.local l ORDER BY r.dataTorneio DESC")
    List<RelatorioSimplesDTO> findTop20Simples(Pageable pageable);
}
