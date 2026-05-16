package com.fer.backend.service;

import com.fer.backend.dto.VrstaRubljaDto;

import java.util.List;
import java.util.UUID;

public interface VrstaRubljaService {
    List<VrstaRubljaDto> findAll(String search);
    VrstaRubljaDto findById(UUID id);
    void spremi(VrstaRubljaDto dto);
    void azuriraj(UUID id, VrstaRubljaDto dto);
    void obrisi(UUID id);
}
