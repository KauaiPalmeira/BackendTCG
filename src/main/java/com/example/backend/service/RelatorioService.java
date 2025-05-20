package com.example.backend.service;

import com.example.backend.dto.RelatorioRequestDTO;
import com.example.backend.dto.RelatorioResponseDTO;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.ArrayList;
import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final RelatorioJogadorRepository relatorioJogadorRepository;
    private final TipoTorneioRepository tipoTorneioRepository;
    private final LocalRepository localRepository;
    private final JogadorRepository jogadorRepository;
    private final DeckRepository deckRepository;
    private final ImagemService imagemService;
    private final PontosService pontosService;

    public RelatorioService(
            RelatorioRepository relatorioRepository,
            RelatorioJogadorRepository relatorioJogadorRepository,
            TipoTorneioRepository tipoTorneioRepository,
            LocalRepository localRepository,
            JogadorRepository jogadorRepository,
            DeckRepository deckRepository,
            ImagemService imagemService,
            PontosService pontosService
    ) {
        this.relatorioRepository = relatorioRepository;
        this.relatorioJogadorRepository = relatorioJogadorRepository;
        this.tipoTorneioRepository = tipoTorneioRepository;
        this.localRepository = localRepository;
        this.jogadorRepository = jogadorRepository;
        this.deckRepository = deckRepository;
        this.imagemService = imagemService;
        this.pontosService = pontosService;
    }
    @Transactional
    public RelatorioResponseDTO criarRelatorio(RelatorioRequestDTO request) throws Exception {
        // Validar posições únicas
        List<Integer> posicoes = request.jogadores()
                .stream()
                .map(RelatorioRequestDTO.JogadorRelatorioDTO::posicao)
                .toList();
        if (posicoes.stream().distinct().count() != posicoes.size() || !posicoes.containsAll(List.of(1, 2, 3, 4, 5, 6, 7, 8))) {
            throw new IllegalArgumentException("As posições devem ser únicas e de 1 a 8.");
        }

        // Buscar entidades
        TipoTorneio tipoTorneio = tipoTorneioRepository.findById(request.tipoTorneioId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de torneio não encontrado"));
        Local local = localRepository.findById(request.localId())
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado"));

        // Criar relatório
        Relatorio relatorio = new Relatorio();
        relatorio.setTipoTorneio(tipoTorneio);
        relatorio.setLocal(local);
        relatorio.setDataTorneio(request.dataTorneio());
        relatorio.setImagem(new byte[0]); // Placeholder
        relatorio = relatorioRepository.save(relatorio);

        // Processar jogadores
        List<RelatorioJogador> relatorioJogadores = new ArrayList<>();
        for (RelatorioRequestDTO.JogadorRelatorioDTO j : request.jogadores()) {
            // Buscar ou criar jogador
            Jogador jogador = jogadorRepository.findByNome(j.nomeJogador())
                    .orElseGet(() -> {
                        Jogador novoJogador = new Jogador();
                        novoJogador.setNome(j.nomeJogador());
                        return jogadorRepository.save(novoJogador);
                    });

            Deck deck = deckRepository.findById(j.deckId())
                    .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado: " + j.deckId()));

            RelatorioJogador rj = new RelatorioJogador();
            rj.setRelatorio(relatorio);
            rj.setJogador(jogador);
            rj.setDeck(deck);
            rj.setPosicao(j.posicao());
            rj.setPontos(pontosService.calcularPontos(tipoTorneio, request.numeroParticipantes(), j.posicao()));
            relatorioJogadores.add(rj);
        }
        relatorioJogadorRepository.saveAll(relatorioJogadores);

        // Gerar imagem
        byte[] imagem = imagemService.gerarImagemRelatorio(relatorio, relatorioJogadores);
        relatorio.setImagem(imagem);
        relatorioRepository.save(relatorio);

        return new RelatorioResponseDTO(relatorio.getId(), imagem);
    }
}
