package com.fer.backend.service.impl;

import com.fer.backend.dto.ObrtDto;
import com.fer.backend.repository.ObrtRepository;
import com.fer.backend.service.ObrtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObrtServiceImpl implements ObrtService {

    private final ObrtRepository obrtRepository;

    @Override
    public List<ObrtDto> findAll() {
        return obrtRepository.findAll()
                .stream()
                .map(o -> ObrtDto.builder()
                        .korisnikId(o.getKorisnikId())
                        .iban(o.getIban())
                        .naziv(o.getNaziv())
                        .build())
                .toList();
    }
}
