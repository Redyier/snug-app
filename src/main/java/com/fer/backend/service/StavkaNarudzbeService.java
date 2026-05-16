package com.fer.backend.service;

import com.fer.backend.dto.StavkaNarudzbeFormDto;

import java.util.UUID;

public interface StavkaNarudzbeService {

    StavkaNarudzbeFormDto findById(UUID id);
    void spremi(UUID narudzbaId, StavkaNarudzbeFormDto dto);
    void azuriraj(UUID stavkaId, StavkaNarudzbeFormDto dto);
    void obrisi(UUID stavkaId);
}
