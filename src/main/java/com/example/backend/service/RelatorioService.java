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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final LocalRepository localRepository;
    private final TipoTorneioRepository tipoTorneioRepository;
    private final DeckRepository deckRepository;
    private final ImagemService imagemService;
    private final PontosService pontosService;

    // Cache maps
    private volatile Map<Long, Local> localCache;
    private volatile Map<Long, TipoTorneio> tipoTorneioCache;
    private volatile Map<Long, Deck> deckCache;

    public RelatorioService(
            RelatorioRepository relatorioRepository,
            LocalRepository localRepository,
            TipoTorneioRepository tipoTorneioRepository,
            DeckRepository deckRepository,
            ImagemService imagemService,
            PontosService pontosService) {
        this.relatorioRepository = relatorioRepository;
        this.localRepository = localRepository;
        this.tipoTorneioRepository = tipoTorneioRepository;
        this.deckRepository = deckRepository;
        this.imagemService = imagemService;
        this.pontosService = pontosService;
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

    // Method to refresh caches if needed
    @Transactional(readOnly = true)
    public void refreshCaches() {
        initializeCaches();
    }
}
