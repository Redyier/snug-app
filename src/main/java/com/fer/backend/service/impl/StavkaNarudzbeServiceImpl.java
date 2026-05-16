package com.fer.backend.service.impl;

import com.fer.backend.dto.StavkaNarudzbeFormDto;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.exception.ValidationException;
import com.fer.backend.model.Cjenik;
import com.fer.backend.model.Narudzba;
import com.fer.backend.model.StavkaNarudzbe;
import com.fer.backend.model.VrstaRublja;
import com.fer.backend.repository.CjenikRepository;
import com.fer.backend.repository.NarudzbaRepository;
import com.fer.backend.repository.StavkaNarudzbeRepository;
import com.fer.backend.repository.VrstaRubljaRepository;
import com.fer.backend.service.NarudzbaService;
import com.fer.backend.service.StavkaNarudzbeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StavkaNarudzbeServiceImpl implements StavkaNarudzbeService {

    private final StavkaNarudzbeRepository stavkaNarudzbeRepository;
    private final NarudzbaRepository narudzbaRepository;
    private final VrstaRubljaRepository vrstaRubljaRepository;
    private final CjenikRepository cjenikRepository;
    private final NarudzbaService narudzbaService;

    @Override
    public StavkaNarudzbeFormDto findById(UUID id) {
        StavkaNarudzbe s = stavkaNarudzbeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stavka s ID-om " + id + " nije pronađena."));

        return StavkaNarudzbeFormDto.builder()
                .stavkaId(s.getStavkaId())
                .vrstaRubljaId(s.getVrstaRublja().getVrstaRubljaId())
                .kolicina(s.getKolicina())
                .build();
    }

    @Override
    @Transactional
    public void spremi(UUID narudzbaId, StavkaNarudzbeFormDto dto) {
        Narudzba narudzba = narudzbaRepository.findById(narudzbaId)
                .orElseThrow(() -> new EntityNotFoundException("Narudžba nije pronađena."));

        VrstaRublja vrstaRublja = vrstaRubljaRepository.findById(dto.getVrstaRubljaId())
                .orElseThrow(() -> new EntityNotFoundException("Vrsta rublja nije pronađena."));

        boolean cjenikPostoji = cjenikRepository
                .existsByObrt_IbanAndVrstaRublja_VrstaRubljaId(narudzba.getObrt().getIban(), dto.getVrstaRubljaId());

        if (!cjenikPostoji) {
            throw new ValidationException("Odabrani obrt '" + narudzba.getObrt().getNaziv() + "' nema definiran cjenik za vrstu rublja '" + vrstaRublja.getNaziv() + "'. " + "Nije moguće dodati stavku.");
        }

        StavkaNarudzbe stavka = StavkaNarudzbe.builder()
                .narudzba(narudzba)
                .vrstaRublja(vrstaRublja)
                .kolicina(dto.getKolicina())
                .build();

        stavkaNarudzbeRepository.save(stavka);
        narudzbaService.izracunajIAzurirajIznos(narudzbaId);
    }

    @Override
    @Transactional
    public void azuriraj(UUID stavkaId, StavkaNarudzbeFormDto dto) {
        StavkaNarudzbe stavka = stavkaNarudzbeRepository.findById(stavkaId)
                .orElseThrow(() -> new EntityNotFoundException("Stavka s ID-om " + stavkaId + " nije pronađena."));

        VrstaRublja vrstaRublja = vrstaRubljaRepository.findById(dto.getVrstaRubljaId())
                .orElseThrow(() -> new EntityNotFoundException("Vrsta rublja nije pronađena."));

        boolean cjenikPostoji = cjenikRepository
                .existsByObrt_IbanAndVrstaRublja_VrstaRubljaId(stavka.getNarudzba().getObrt().getIban(), dto.getVrstaRubljaId());

        if (!cjenikPostoji) {
            throw new ValidationException("Odabrani obrt nema definiran cjenik za vrstu rublja '" + vrstaRublja.getNaziv() + "'. " + "Nije moguće ažurirati stavku."
            );
        }

        stavka.setVrstaRublja(vrstaRublja);
        stavka.setKolicina(dto.getKolicina());
        stavkaNarudzbeRepository.save(stavka);
        narudzbaService.izracunajIAzurirajIznos(stavka.getNarudzba().getNarudzbaId());
    }

    @Override
    @Transactional
    public void obrisi(UUID stavkaId) {
        StavkaNarudzbe stavka = stavkaNarudzbeRepository.findById(stavkaId)
                .orElseThrow(() -> new EntityNotFoundException("Stavka nije pronađena."));

        Narudzba narudzba = stavka.getNarudzba();
        stavkaNarudzbeRepository.deleteById(stavkaId);

        narudzbaService.izracunajIAzurirajIznos(narudzba.getNarudzbaId());
    }


}
