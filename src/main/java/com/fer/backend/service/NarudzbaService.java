package com.fer.backend.service;

import com.fer.backend.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface NarudzbaService {
    List<NarudzbaListDto> findAll(String search);
    NarudzbaFormDto findById(UUID id);
    List<StavkaNarudzbeDto> findStavke(UUID narudzbaId);
    UUID save(NarudzbaFormDto dto);
    void update(UUID id, NarudzbaFormDto dto);
    void delete(UUID id);
    void izracunajIAzurirajIznos(UUID narudzbaId);
    List<ObrtSCjenomDto> pretraga(List<StavkaPretragaDto> stavke, LocalDate terminPrikupa);
}