package com.example.backend.service;

import com.example.backend.dto.RelatorioDTO;
import com.example.backend.dto.RelatorioResponseDTO;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final LocalRepository localRepository;
    private final TipoTorneioRepository tipoTorneioRepository;
    private final DeckRepository deckRepository;
    private final ImagemService imagemService;
    private final PontosService pontosService;
    private final CachedImageService cachedImageService;
    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);

    // Cache maps
    private volatile Map<Long, Local> localCache;
    private volatile Map<Long, TipoTorneio> tipoTorneioCache;
    private volatile Map<Long, Deck> deckCache;
    private final List<Relatorio> relatoriosCache = new java.util.ArrayList<>();

    public RelatorioService(
            RelatorioRepository relatorioRepository,
            LocalRepository localRepository,
            TipoTorneioRepository tipoTorneioRepository,
            DeckRepository deckRepository,
            ImagemService imagemService,
            PontosService pontosService,
            CachedImageService cachedImageService) {
        this.relatorioRepository = relatorioRepository;
        this.localRepository = localRepository;
        this.tipoTorneioRepository = tipoTorneioRepository;
        this.deckRepository = deckRepository;
        this.imagemService = imagemService;
        this.pontosService = pontosService;
        this.cachedImageService = cachedImageService;
        initializeCaches();
    }

    private void initializeCaches() {
        // Initialize caches with all entities
        this.localCache = localRepository.findAll().stream()
                .collect(Collectors.toConcurrentMap(Local::getId, Function.identity()));
        this.tipoTorneioCache = tipoTorneioRepository.findAll().stream()
                .collect(Collectors.toConcurrentMap(TipoTorneio::getId, Function.identity()));
        this.deckCache = deckRepository.findAll().stream()
                .collect(Collectors.toConcurrentMap(Deck::getId, Function.identity()));
    }

    @Transactional
    public RelatorioResponseDTO criarRelatorio(RelatorioDTO relatorioDTO) throws IOException {
        // Get entities from cache
        Local local = localCache.get(relatorioDTO.localId());
        if (local == null) {
            throw new IllegalArgumentException("Local não encontrado");
        }

        TipoTorneio tipoTorneio = tipoTorneioCache.get(relatorioDTO.tipoTorneioId());
        if (tipoTorneio == null) {
            throw new IllegalArgumentException("Tipo de torneio não encontrado");
        }

        // Criar o relatório
        Relatorio relatorio = new Relatorio();
        relatorio.setLocal(local);
        relatorio.setTipoTorneio(tipoTorneio);
        relatorio.setDataTorneio(relatorioDTO.dataTorneio());
        relatorio.setNumeroParticipantes(relatorioDTO.numeroParticipantes());

        // Processar jogadores
        for (RelatorioDTO.JogadorDTO jogadorDTO : relatorioDTO.jogadores()) {
            Deck deck = deckCache.get(jogadorDTO.deckId());
            if (deck == null) {
                throw new IllegalArgumentException("Deck não encontrado");
            }

            RelatorioJogador jogador = new RelatorioJogador();
            jogador.setNomeJogador(jogadorDTO.nomeJogador());
            jogador.setDeck(deck);
            jogador.setPosicao(jogadorDTO.posicao());
            jogador.setRelatorio(relatorio);
            jogador.setJogador(null);
            
            // Calcular e definir os pontos
            int pontos = pontosService.calcularPontos(
                tipoTorneio,
                relatorioDTO.numeroParticipantes(),
                jogadorDTO.posicao()
            );
            jogador.setPontos(pontos);
            
            relatorio.getJogadores().add(jogador);
        }

        // Gerar imagem antes de salvar
        byte[] imagem = imagemService.gerarImagemRelatorio(relatorio, relatorio.getJogadores());
        relatorio.setImagem(imagem);

        // Salvar o relatório com a imagem já definida
        relatorio = relatorioRepository.save(relatorio);
        
        String base64 = Base64.getEncoder().encodeToString(imagem);

        return new RelatorioResponseDTO(
            relatorio.getId(),
            relatorio.getLocal().getNome(),
            relatorio.getDataTorneio(),
            base64
        );
    }

    public List<Relatorio> buscarUltimosRelatorios() {
        // Use the optimized query with fetch joins
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataTorneio"));
        return relatorioRepository.findAllWithRelationships(pageRequest).getContent();
    }

    public Relatorio findById(Long id) {
        return relatorioRepository.findByIdWithLocal(id)
            .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));
    }

    // Method to refresh caches if needed
    @Transactional(readOnly = true)
    public void refreshCaches() {
        initializeCaches();
    }

    public Relatorio buscarProximoRelatorio(Long lastLoadedId) {
        if (lastLoadedId == null) {
            // Se não houver ID anterior, retorna o mais recente
            PageRequest pageRequest = PageRequest.of(0, 1);
            List<Relatorio> relatorios = relatorioRepository.findMostRecentRelatorio(pageRequest);
            return relatorios.isEmpty() ? null : relatorios.get(0);
        } else {
            // Se houver ID anterior, retorna o próximo mais antigo
            PageRequest pageRequest = PageRequest.of(0, 1);
            List<Relatorio> relatorios = relatorioRepository.findNextRelatorio(lastLoadedId, pageRequest);
            return relatorios.isEmpty() ? null : relatorios.get(0);
        }
    }

    public List<Relatorio> buscarTodosRelatorios() {
        return relatorioRepository.findAllWithRelationships();
    }

    @PostConstruct
    public void preloadReports() {
        log.info("Pré-carregando até 20 relatórios e imagens em memória...");
        var pageRequest = org.springframework.data.domain.PageRequest.of(0, 20);
        List<com.example.backend.dto.RelatorioSimplesDTO> relatorios = relatorioRepository.findTop20Simples(pageRequest);
        relatoriosCache.clear();
        int count = 0;
        for (var dto : relatorios) {
            try {
                var relatorio = new com.example.backend.entity.Relatorio();
                relatorio.setId(dto.id());
                var local = new com.example.backend.entity.Local();
                local.setNome(dto.local());
                relatorio.setLocal(local);
                relatorio.setDataTorneio(dto.dataTorneio());
                relatorio.setImagem(dto.imagem());
                relatoriosCache.add(relatorio);
                cachedImageService.cacheReport(relatorio);
                count++;
            } catch (Exception e) {
                log.error("Erro ao cachear relatório {}: {}", dto.id(), e.getMessage());
            }
        }
        log.info("Pré-carregados {} relatórios e imagens em memória.", count);
    }

    public List<Relatorio> getRelatoriosCache() {
        return relatoriosCache;
    }
}
