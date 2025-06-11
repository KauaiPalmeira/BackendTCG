package com.example.backend.controller;

import com.example.backend.dto.RelatorioDTO;
import com.example.backend.dto.RelatorioResponseDTO;
import com.example.backend.entity.Relatorio;
import com.example.backend.service.RelatorioService;
import com.example.backend.service.ImagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ImagemService imagemService;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioController.class);

    public RelatorioController(RelatorioService relatorioService, ImagemService imagemService) {
        this.relatorioService = relatorioService;
        this.imagemService = imagemService;
    }

    @PostMapping
    public ResponseEntity<?> criarRelatorio(@RequestBody RelatorioDTO relatorioDTO) {
        try {
            logger.info("Iniciando criação de relatório para torneio tipo {} em {}", 
                       relatorioDTO.tipoTorneioId(), relatorioDTO.dataTorneio());
            
            RelatorioResponseDTO response = relatorioService.criarRelatorio(relatorioDTO);
            
            logger.info("Relatório criado com sucesso. ID: {}", response.getId());
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=relatorio.png")
                    .body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Erro de validação ao criar relatório: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erro de validação", e.getMessage()));
        } catch (IOException e) {
            logger.error("Erro ao gerar imagem do relatório: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno", "Erro ao gerar imagem do relatório"));
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar relatório: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno", "Ocorreu um erro inesperado"));
        }
    }

    @GetMapping("/ultimos")
    public ResponseEntity<?> getUltimosRelatorios() {
        try {
            logger.info("Buscando últimos relatórios");
            List<Relatorio> relatorios = relatorioService.buscarUltimosRelatorios();
            
            List<RelatorioResponseDTO> response = relatorios.stream()
                .map(relatorio -> {
                    String base64 = Base64.getEncoder().encodeToString(relatorio.getImagem());
                    return new RelatorioResponseDTO(
                        relatorio.getId(),
                        relatorio.getLocal().getNome(),
                        relatorio.getDataTorneio(),
                        base64
                    );
                })
                .collect(Collectors.toList());

            logger.info("Retornando {} relatórios", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao buscar últimos relatórios: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno", "Erro ao buscar relatórios"));
        }
    }

    private static class ErrorResponse {
        private final String error;
        private final String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
