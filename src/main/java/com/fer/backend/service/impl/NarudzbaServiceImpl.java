package com.fer.backend.service.impl;

import com.fer.backend.dto.*;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.model.*;
import com.fer.backend.model.enums.StatusNarudzbe;
import com.fer.backend.repository.*;
import com.fer.backend.service.NarudzbaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NarudzbaServiceImpl implements NarudzbaService {

    private final NarudzbaRepository narudzbaRepository;
    private final PrivatnaOsobaRepository privatnaOsobaRepository;
    private final ObrtRepository obrtRepository;
    private final AdresaRepository adresaRepository;
    private final StavkaNarudzbeRepository stavkaNarudzbeRepository;
    private final CjenikRepository cjenikRepository;
    private final StatusRepository statusRepository;
    private final VrstaRubljaRepository vrstaRubljaRepository;

    @Override
    public List<NarudzbaListDto> findAll(String search) {
        List<Narudzba> narudzbe;

        if (search != null && !search.isBlank()) {
            narudzbe = narudzbaRepository.findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase(search);
        }

        else {
            narudzbe = narudzbaRepository.findAll();
        }

        return narudzbe.stream()
                .map(n -> {
                    String trenutniStatus = statusRepository
                            .findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(
                                    n.getNarudzbaId())
                            .stream()
                            .findFirst()
                            .map(Status::getNazivStatusa)
                            .orElse("Nije postavljeno");

                    return NarudzbaListDto.builder()
                            .narudzbaId(n.getNarudzbaId())
                            .datumNarucivanja(n.getDatumNarucivanja())
                            .terminPrikupa(n.getTerminPrikupa())
                            .ukupniIznos(n.getUkupniIznos())
                            .korisnickoIme(n.getPrivatnaOsoba().getKorisnickoIme())
                            .obrtNaziv(n.getObrt().getNaziv())
                            .trenutniStatus(trenutniStatus)
                            .build();
                })
                .toList();
    }

    @Override
    public NarudzbaFormDto findById(UUID id) {
        Narudzba n = narudzbaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Narudžba s ID-om " + id + " nije pronađena."));

        return NarudzbaFormDto.builder()
                .narudzbaId(n.getNarudzbaId())
                .datumNarucivanja(n.getDatumNarucivanja())
                .terminPrikupa(n.getTerminPrikupa())
                .ukupniIznos(n.getUkupniIznos())
                .korisnickoIme(n.getPrivatnaOsoba().getKorisnickoIme())
                .obrtIban(n.getObrt().getIban())
                .adresaId(n.getAdresa().getAdresaId())
                .build();
    }

    @Override
    @Transactional
    public List<StavkaNarudzbeDto> findStavke(UUID narudzbaId) {
        return stavkaNarudzbeRepository
                .findByNarudzba_NarudzbaId(narudzbaId)
                .stream()
                .map(s -> StavkaNarudzbeDto.builder()
                        .stavkaId(s.getStavkaId())
                        .vrstaRubljaNaziv(s.getVrstaRublja().getNaziv())
                        .kolicina(s.getKolicina())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public UUID save(NarudzbaFormDto dto) {
        PrivatnaOsoba privatnaOsoba = privatnaOsobaRepository
                .findByKorisnickoIme(dto.getKorisnickoIme())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Korisnik " + dto.getKorisnickoIme() + " nije pronađen."));

        Obrt obrt = obrtRepository.findByIban(dto.getObrtIban())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Obrt s IBAN-om " + dto.getObrtIban() + " nije pronađen."));

        Adresa adresa = adresaRepository.findById(dto.getAdresaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Adresa nije pronađena."));

        BigDecimal ukupno = dto.getStavke().stream()
                .map(s -> {
                    Cjenik cjenik = cjenikRepository
                            .findByObrt_IbanAndVrstaRublja_VrstaRubljaId(
                                    obrt.getIban(),
                                    s.getVrstaRubljaId()
                            )
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Cjenik nije pronađen."));
                    return s.getKolicina()
                            .multiply(cjenik.getCijenaPoKg())
                            .multiply(cjenik.getMultiplikatorNeradnogDana());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Narudzba narudzba = Narudzba.builder()
                .datumNarucivanja(LocalDate.now())
                .terminPrikupa(dto.getTerminPrikupa())
                .ukupniIznos(ukupno)
                .privatnaOsoba(privatnaOsoba)
                .obrt(obrt)
                .adresa(adresa)
                .build();

        Narudzba spremljena = narudzbaRepository.save(narudzba);

        for (StavkaPretragaDto stavkaDto : dto.getStavke()) {
            VrstaRublja vrstaRublja = vrstaRubljaRepository.findById(stavkaDto.getVrstaRubljaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Vrsta rublja nije pronađena."));

            stavkaNarudzbeRepository.save(StavkaNarudzbe.builder()
                    .narudzba(spremljena)
                    .vrstaRublja(vrstaRublja)
                    .kolicina(stavkaDto.getKolicina())
                    .build());
        }

        statusRepository.save(Status.builder()
                .nazivStatusa(StatusNarudzbe.ZAPRIMLJENO.getNaziv())
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(spremljena)
                .build());

        return spremljena.getNarudzbaId();
    }

    @Override
    @Transactional
    public void update(UUID id, NarudzbaFormDto dto) {
        Narudzba narudzba = narudzbaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Narudžba s ID-om " + id + " nije pronađena."));

        PrivatnaOsoba privatnaOsoba = privatnaOsobaRepository
                .findByKorisnickoIme(dto.getKorisnickoIme())
                .orElseThrow(() -> new EntityNotFoundException("Korisnik " + dto.getKorisnickoIme() + " nije pronađen."));

        Obrt obrt = obrtRepository.findByIban(dto.getObrtIban())
                .orElseThrow(() -> new EntityNotFoundException("Obrt s IBAN-om " + dto.getObrtIban() + " nije pronađen."));

        Adresa adresa = adresaRepository.findById(dto.getAdresaId())
                .orElseThrow(() -> new EntityNotFoundException("Adresa nije pronađena."));

        narudzba.setTerminPrikupa(dto.getTerminPrikupa());
        narudzba.setPrivatnaOsoba(privatnaOsoba);
        narudzba.setObrt(obrt);
        narudzba.setAdresa(adresa);

        narudzbaRepository.save(narudzba);
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        if (!narudzbaRepository.existsById(id)) {
            throw new EntityNotFoundException("Narudžba s ID-om " + id + " nije pronađena.");
        }

        narudzbaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void izracunajIAzurirajIznos(UUID narudzbaId) {
        Narudzba narudzba = narudzbaRepository.findById(narudzbaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Narudžba nije pronađena."));

        List<StavkaNarudzbe> stavke = stavkaNarudzbeRepository
                .findByNarudzba_NarudzbaId(narudzbaId);

        BigDecimal ukupno = stavke.stream()
                .map(s -> {
                    Cjenik cjenik = cjenikRepository
                            .findByObrt_IbanAndVrstaRublja_VrstaRubljaId(
                                    narudzba.getObrt().getIban(),
                                    s.getVrstaRublja().getVrstaRubljaId()
                            )
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Cjenik nije pronađen."));

                    return s.getKolicina()
                            .multiply(cjenik.getCijenaPoKg())
                            .multiply(cjenik.getMultiplikatorNeradnogDana());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        narudzba.setUkupniIznos(ukupno);
        narudzbaRepository.save(narudzba);
    }

    @Override
    public List<ObrtSCjenomDto> pretraga(List<StavkaPretragaDto> stavke, LocalDate terminPrikupa) {
        List<Obrt> sviObrти = obrtRepository.findAll();

        return sviObrти.stream()
                .map(obrt -> {
                    try {
                        BigDecimal ukupno = BigDecimal.ZERO;

                        for (StavkaPretragaDto stavka : stavke) {
                            Cjenik cjenik = cjenikRepository.findByObrt_IbanAndVrstaRublja_VrstaRubljaId(obrt.getIban(), stavka.getVrstaRubljaId())
                                    .orElseThrow(() -> new EntityNotFoundException("Nema cjenik."));

                            ukupno = ukupno.add(stavka.getKolicina()
                                            .multiply(cjenik.getCijenaPoKg())
                                            .multiply(cjenik.getMultiplikatorNeradnogDana())
                            );
                        }

                        return ObrtSCjenomDto.builder()
                                .iban(obrt.getIban())
                                .naziv(obrt.getNaziv())
                                .ukupniIznos(ukupno)
                                .dostupan(true)
                                .build();

                    }

                    catch (EntityNotFoundException e) {
                        return ObrtSCjenomDto.builder()
                                .iban(obrt.getIban())
                                .naziv(obrt.getNaziv())
                                .ukupniIznos(BigDecimal.ZERO)
                                .dostupan(false)
                                .razlogNedostupnosti("Nema definiran cjenik za sve odabrane vrste rublja.")
                                .build();
                    }
                })
                .toList();
    }
}
