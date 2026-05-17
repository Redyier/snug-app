package com.fer.backend.service.impl;

import com.fer.backend.dto.VrstaRubljaDto;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.exception.ValidationException;
import com.fer.backend.model.VrstaRublja;
import com.fer.backend.repository.VrstaRubljaRepository;
import com.fer.backend.service.VrstaRubljaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VrstaRubljaServiceImpl implements VrstaRubljaService {

    private final VrstaRubljaRepository vrstaRubljaRepository;

    @Override
    public List<VrstaRubljaDto> findAll(String search) {
        List<VrstaRublja> vrste;

        if (search != null && !search.isBlank()) {
            vrste = vrstaRubljaRepository.findByNazivContainingIgnoreCase(search);
        }

        else {
            vrste = vrstaRubljaRepository.findAll();
        }

        return vrste.stream()
                .map(v -> VrstaRubljaDto.builder()
                        .vrstaRubljaId(v.getVrstaRubljaId())
                        .naziv(v.getNaziv())
                        .build())
                .toList();
    }

    @Override
    public VrstaRubljaDto findById(UUID id) {
        VrstaRublja v = vrstaRubljaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vrsta rublja s ID-om " + id + " nije pronađena."));

        return VrstaRubljaDto.builder()
                .vrstaRubljaId(v.getVrstaRubljaId())
                .naziv(v.getNaziv())
                .build();
    }

    @Override
    public void spremi(VrstaRubljaDto dto) {

        if (vrstaRubljaRepository.existsByNaziv(dto.getNaziv())) {
            throw new ValidationException("Vrsta rublja s nazivom '" + dto.getNaziv() + "' već postoji.");
        }

        VrstaRublja vrstaRublja = VrstaRublja.builder()
                .naziv(dto.getNaziv())
                .build();
        vrstaRubljaRepository.save(vrstaRublja);
    }

    @Override
    public void azuriraj(UUID id, VrstaRubljaDto dto) {

        if (vrstaRubljaRepository.existsByNaziv(dto.getNaziv())) {
            throw new ValidationException("Vrsta rublja s nazivom '" + dto.getNaziv() + "' već postoji.");
        }

        VrstaRublja vrstaRublja = vrstaRubljaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vrsta rublja s ID-om " + id + " nije pronađena."));
        vrstaRublja.setNaziv(dto.getNaziv());
        vrstaRubljaRepository.save(vrstaRublja);
    }

    @Override
    public void obrisi(UUID id) {
        if (!vrstaRubljaRepository.existsById(id)) {
            throw new EntityNotFoundException("Vrsta rublja s ID-om " + id + " nije pronađena.");
        }
        vrstaRubljaRepository.deleteById(id);
    }
}
