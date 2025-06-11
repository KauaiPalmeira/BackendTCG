package com.example.backend.dto;

import java.time.LocalDate;

public class RelatorioResponseDTO {
    private Long id;
    private String local;
    private LocalDate dataTorneio;
    private String imagemBase64;

    public RelatorioResponseDTO(Long id, String local, LocalDate dataTorneio, String imagemBase64) {
        this.id = id;
        this.local = local;
        this.dataTorneio = dataTorneio;
        this.imagemBase64 = imagemBase64;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public LocalDate getDataTorneio() {
        return dataTorneio;
    }

    public void setDataTorneio(LocalDate dataTorneio) {
        this.dataTorneio = dataTorneio;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
}
