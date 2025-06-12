package com.example.backend.controller;

import com.example.backend.dto.RelatorioDTO;
import com.example.backend.dto.RelatorioResponseDTO;
import com.example.backend.entity.Relatorio;
import com.example.backend.service.RelatorioService;
import com.example.backend.service.ImagemService;
import com.example.backend.service.CachedImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175"
})
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ImagemService imagemService;
    private final CachedImageService cachedImageService;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioController.class);

    public RelatorioController(RelatorioService relatorioService, 
                             ImagemService imagemService,
                             CachedImageService cachedImageService) {
        this.relatorioService = relatorioService;
        this.imagemService = imagemService;
        this.cachedImageService = cachedImageService;
    }

    @PostMapping
    public ResponseEntity<?> criarRelatorio(@RequestBody RelatorioDTO relatorioDTO) {
        try {
            logger.info("Iniciando criação de relatório para torneio tipo {} em {}", 
                       relatorioDTO.tipoTorneioId(), relatorioDTO.dataTorneio());
            
            RelatorioResponseDTO response = relatorioService.criarRelatorio(relatorioDTO);
            
            // Cache the new report immediately
            Relatorio relatorio = relatorioService.findById(response.getId());
            cachedImageService.cacheReport(relatorio);
            
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

    @GetMapping("/sequencial")
    public ResponseEntity<?> getRelatorioSequencial(@RequestParam(required = false) Long lastLoadedId) {
        try {
            logger.info("Recebida requisição para /sequencial. LastLoadedId: {}", lastLoadedId);
            
            Relatorio relatorio = relatorioService.buscarProximoRelatorio(lastLoadedId);
            if (relatorio == null) {
                logger.info("Nenhum relatório encontrado para lastLoadedId: {}", lastLoadedId);
                return ResponseEntity.ok().build();
            }
            
            logger.info("Relatório encontrado - ID: {}, Local: {}, Data: {}", 
                       relatorio.getId(), 
                       relatorio.getLocal().getNome(), 
                       relatorio.getDataTorneio());

            String base64 = cachedImageService.getLowQualityImage(relatorio);
            RelatorioResponseDTO response = new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getLocal().getNome(),
                relatorio.getDataTorneio(),
                base64
            );

            logger.info("Retornando relatório com ID: {}", relatorio.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar relatório sequencial: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno", "Erro ao buscar relatório"));
        }
    }

    @GetMapping("/{id}/high-quality")
    public ResponseEntity<?> getHighQualityImage(@PathVariable Long id) {
        try {
            Relatorio relatorio = relatorioService.findById(id);
            if (relatorio == null) {
                return ResponseEntity.notFound().build();
            }

            String base64 = cachedImageService.getHighQualityImage(relatorio);
            return ResponseEntity.ok(new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getLocal().getNome(),
                relatorio.getDataTorneio(),
                base64
            ));
        } catch (Exception e) {
            logger.error("Error getting high quality image for report {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno", "Erro ao buscar imagem de alta qualidade"));
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<?> getTodosRelatorios() {
        try {
            logger.info("Recebida requisição para /todos (todos os relatórios em memória)");
            var relatorios = relatorioService.getRelatoriosCache();
            var dtos = relatorios.stream()
                .map(relatorio -> {
                    String base64 = null;
                    try {
                        base64 = cachedImageService.getLowQualityImage(relatorio);
                    } catch (Exception e) {
                        logger.error("Erro ao gerar imagem base64 para relatório {}", relatorio.getId(), e);
                    }
                    return new RelatorioResponseDTO(
                        relatorio.getId(),
                        relatorio.getLocal().getNome(),
                        relatorio.getDataTorneio(),
                        base64
                    );
                })
                .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os relatórios: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro interno", "Erro ao buscar todos os relatórios"));
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
