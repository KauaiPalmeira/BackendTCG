package com.example.backend.controller;

import com.example.backend.entity.TipoTorneio;
import com.example.backend.repository.TipoTorneioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-torneio")
public class TipoTorneioController {

    private final TipoTorneioRepository tipoTorneioRepository;

    public TipoTorneioController(TipoTorneioRepository tipoTorneioRepository) {
        this.tipoTorneioRepository = tipoTorneioRepository;
    }

    @GetMapping
    public List<TipoTorneio> listar() {
        return tipoTorneioRepository.findAll();
    }
} 