package com.example.backend.service;

import com.example.backend.entity.Relatorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class CachedImageService {
    private static final Logger logger = LoggerFactory.getLogger(CachedImageService.class);
    private static final int LOW_QUALITY_WIDTH = 500;  // Reduced width for low quality
    private static final int CACHE_LIMIT = 20;  // Keep last 20 reports in cache

    private final ConcurrentHashMap<Long, String> lowQualityCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> highQualityCache = new ConcurrentHashMap<>();
    private final List<Long> cacheOrder = new ArrayList<>();  // To track cache order

    public String getLowQualityImage(Relatorio relatorio) throws IOException {
        return lowQualityCache.computeIfAbsent(relatorio.getId(), id -> {
            try {
                return generateLowQualityImage(relatorio.getImagem());
            } catch (IOException e) {
                logger.error("Error generating low quality image for report {}", id, e);
                return null;
            }
        });
    }

    public String getHighQualityImage(Relatorio relatorio) {
        return highQualityCache.computeIfAbsent(relatorio.getId(), id -> {
            String base64 = Base64.getEncoder().encodeToString(relatorio.getImagem());
            updateCacheOrder(id);
            return base64;
        });
    }

    @Async
    public void cacheReport(Relatorio relatorio) {
        try {
            // Generate and cache low quality version
            String lowQuality = generateLowQualityImage(relatorio.getImagem());
            lowQualityCache.put(relatorio.getId(), lowQuality);

            // Cache high quality version
            String highQuality = Base64.getEncoder().encodeToString(relatorio.getImagem());
            highQualityCache.put(relatorio.getId(), highQuality);

            updateCacheOrder(relatorio.getId());
        } catch (IOException e) {
            logger.error("Error caching report {}", relatorio.getId(), e);
        }
    }

    private String generateLowQualityImage(byte[] originalImage) throws IOException {
        // Read the original image
        BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(originalImage));
        
        // Calculate new height maintaining aspect ratio
        int newHeight = (int) ((double) img.getHeight() * LOW_QUALITY_WIDTH / img.getWidth());
        
        // Create resized image
        BufferedImage resized = new BufferedImage(LOW_QUALITY_WIDTH, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, LOW_QUALITY_WIDTH, newHeight, null);
        g.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        
        return Base64.getEncoder().encodeToString(bytes);
    }

    private synchronized void updateCacheOrder(Long id) {
        cacheOrder.remove(id);
        cacheOrder.add(0, id);  // Add to front
        
        // Remove oldest if cache is too large
        while (cacheOrder.size() > CACHE_LIMIT) {
            Long oldestId = cacheOrder.remove(cacheOrder.size() - 1);
            lowQualityCache.remove(oldestId);
            highQualityCache.remove(oldestId);
        }
    }

    public void clearCache() {
        lowQualityCache.clear();
        highQualityCache.clear();
        cacheOrder.clear();
    }
} 