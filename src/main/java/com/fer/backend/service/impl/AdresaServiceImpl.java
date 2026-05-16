package com.fer.backend.service.impl;

import com.fer.backend.dto.AdresaDto;
import com.fer.backend.repository.AdresaRepository;
import com.fer.backend.service.AdresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdresaServiceImpl implements AdresaService {

    private final AdresaRepository adresaRepository;

    @Override
    public List<AdresaDto> findAll() {
        return adresaRepository.findAll()
                .stream()
                .map(a -> AdresaDto.builder()
                        .adresaId(a.getAdresaId())
                        .ulica(a.getUlica())
                        .kucniBroj(a.getKucniBroj())
                        .grad(a.getGrad())
                        .postanskiBroj(a.getPostanskiBroj())
                        .build())
                .toList();
    }
}
