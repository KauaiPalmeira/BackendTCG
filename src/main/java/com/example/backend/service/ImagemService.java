package com.example.backend.service;

import com.example.backend.entity.Relatorio;
import com.example.backend.entity.RelatorioJogador;
import org.springframework.stereotype.Service;

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
public class ImagemService {

    private static final int IMAGE_WIDTH = 274;
    private static final int IMAGE_HEIGHT = 175;
    private static final int IMAGE_X = 761;
    private static final Color TEXT_COLOR = new Color(0x031D4D);
    private static final Font FONT_NOME = new Font("OpenSansHebrewCondensed-ExtraBold", Font.BOLD, 45);
    private static final Font FONT_DECK = new Font("Open Sans Hebrew Condensed", Font.BOLD, 34);
    private static final Font FONT_DATA = new Font("OpenSansHebrewCondensed-ExtraBoldItalic", Font.BOLD | Font.ITALIC, 78);
    private static final Color COR_DATA = new Color(0xFFFFFF);
    private static final int TEXT_Y_OFFSET = 24;

    private static final int[][] TEXT_COORDS = {
            {258, 506, 571},
            {258, 677, 743},
            {258, 853, 916},
            {258, 1025, 1089},
            {258, 1198, 1260},
            {258, 1369, 1432},
            {258, 1545, 1604},
            {258, 1713, 1776}
    };

    private static final int[] IMAGE_Y_COORDS = {
            498, 673, 848, 1019, 1198, 1363, 1535, 1725
    };

    public byte[] gerarImagemRelatorio(Relatorio relatorio, List<RelatorioJogador> jogadores) throws IOException {
        // Carregar o template do relatório
        String templatePath = "/templates/Template Report - League Cup.png";
        BufferedImage template = loadImage(templatePath);

        BufferedImage output = new BufferedImage(template.getWidth(), template.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(template, 0, 0, null);
        g.setColor(TEXT_COLOR);

        // Desenhar data
        g.setFont(FONT_DATA);
        FontMetrics metricsData = g.getFontMetrics(FONT_DATA);
        int dataX = 665;
        int dataY = 320 + metricsData.getAscent() - 24;
        g.setColor(COR_DATA);
        g.drawString(relatorio.getDataTorneio().format(DateTimeFormatter.ofPattern("dd/MM/yy")), dataX, dataY);
        g.setColor(TEXT_COLOR);

        // Desenhar jogadores e decks
        for (int i = 0; i < Math.min(jogadores.size(), 8); i++) {
            RelatorioJogador rj = jogadores.get(i);
            // Gerar caminho da imagem com base no nome do deck
            String imagemCaminho = "/templates/" + rj.getDeck().getNome().toLowerCase().replace(" ", "_") + ".png";
            BufferedImage pokemonImage = loadImage(imagemCaminho);

            int textX = TEXT_COORDS[i][0];
            int nomeY = TEXT_COORDS[i][1];
            int deckY = TEXT_COORDS[i][2];
            int imageY = IMAGE_Y_COORDS[i];

            g.drawImage(pokemonImage, IMAGE_X, imageY, IMAGE_WIDTH, IMAGE_HEIGHT, null);

            g.setFont(FONT_NOME);
            FontMetrics metricsNome = g.getFontMetrics(FONT_NOME);
            int adjustedNomeY = nomeY + metricsNome.getAscent() + TEXT_Y_OFFSET;
            g.drawString(rj.getJogador().getNome(), textX, adjustedNomeY);

            g.setFont(FONT_DECK);
            FontMetrics metricsDeck = g.getFontMetrics(FONT_DECK);
            int adjustedDeckY = deckY + metricsDeck.getAscent() + TEXT_Y_OFFSET;
            g.drawString(rj.getDeck().getNome(), textX, adjustedDeckY);
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(output, "png", baos);
        return baos.toByteArray();
    }

    private BufferedImage loadImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
        if (image == null) {
            throw new IOException("Imagem não encontrada no classpath: " + path);
        }
        return image;
    }
}
