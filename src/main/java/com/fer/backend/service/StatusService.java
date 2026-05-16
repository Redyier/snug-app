package com.fer.backend.service;

import com.fer.backend.dto.StatusDto;

import java.util.List;
import java.util.UUID;

public interface StatusService {
    List<StatusDto> findByNarudzbaId(UUID narudzbaId);
    StatusDto findTrenutniStatus(UUID narudzbaId);
    void dodajStatus(UUID narudzbaId, String nazivStatusa);
}