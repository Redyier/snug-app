package com.fer.backend.service.impl;

import com.fer.backend.dto.StatusDto;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.model.Narudzba;
import com.fer.backend.model.Status;
import com.fer.backend.repository.NarudzbaRepository;
import com.fer.backend.repository.StatusRepository;
import com.fer.backend.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;
    private final NarudzbaRepository narudzbaRepository;

    @Override
    public List<StatusDto> findByNarudzbaId(UUID narudzbaId) {
        return statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)
                .stream()
                .map(s -> StatusDto.builder()
                        .statusId(s.getStatusId())
                        .nazivStatusa(s.getNazivStatusa())
                        .vrijemeAzuriranja(s.getVrijemeAzuriranja())
                        .narudzbaId(s.getNarudzba().getNarudzbaId())
                        .build())
                .toList();
    }

    @Override
    public StatusDto findTrenutniStatus(UUID narudzbaId) {
        return statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)
                .stream()
                .findFirst()
                .map(s -> StatusDto.builder()
                        .statusId(s.getStatusId())
                        .nazivStatusa(s.getNazivStatusa())
                        .vrijemeAzuriranja(s.getVrijemeAzuriranja())
                        .narudzbaId(s.getNarudzba().getNarudzbaId())
                        .build())
                .orElse(null);
    }

    @Override
    @Transactional
    public void dodajStatus(UUID narudzbaId, String nazivStatusa) {
        Narudzba narudzba = narudzbaRepository.findById(narudzbaId)
                .orElseThrow(() -> new EntityNotFoundException("Narudžba s ID-om " + narudzbaId + " nije pronađena."));

        Status status = Status.builder()
                .nazivStatusa(nazivStatusa)
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(narudzba)
                .build();

        statusRepository.save(status);
    }
}
