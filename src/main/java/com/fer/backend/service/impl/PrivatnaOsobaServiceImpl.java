package com.fer.backend.service.impl;

import com.fer.backend.dto.PrivatnaOsobaDto;
import com.fer.backend.repository.PrivatnaOsobaRepository;
import com.fer.backend.service.PrivatnaOsobaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivatnaOsobaServiceImpl implements PrivatnaOsobaService {

    private final PrivatnaOsobaRepository privatnaOsobaRepository;

    @Override
    public List<PrivatnaOsobaDto> findAll() {
        return privatnaOsobaRepository.findAll()
                .stream()
                .map(p -> PrivatnaOsobaDto.builder()
                        .korisnikId(p.getKorisnikId())
                        .korisnickoIme(p.getKorisnickoIme())
                        .ime(p.getIme())
                        .prezime(p.getPrezime())
                        .build())
                .toList();
    }
}
