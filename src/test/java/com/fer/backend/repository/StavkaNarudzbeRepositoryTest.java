package com.fer.backend.repository;

import com.fer.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class StavkaNarudzbeRepositoryTest {

    @Autowired
    private StavkaNarudzbeRepository stavkaNarudzbeRepository;

    @Autowired
    private NarudzbaRepository narudzbaRepository;

    @Autowired
    private PrivatnaOsobaRepository privatnaOsobaRepository;

    @Autowired
    private ObrtRepository obrtRepository;

    @Autowired
    private AdresaRepository adresaRepository;

    @Autowired
    private VrstaRubljaRepository vrstaRubljaRepository;

    private Narudzba narudzba;
    private VrstaRublja vrstaRublja;

    @BeforeEach
    void setUp() {
        stavkaNarudzbeRepository.deleteAll();
        narudzbaRepository.deleteAll();
        adresaRepository.deleteAll();
        vrstaRubljaRepository.deleteAll();
        privatnaOsobaRepository.deleteAll();
        obrtRepository.deleteAll();

        PrivatnaOsoba privatnaOsoba = new PrivatnaOsoba();
        privatnaOsoba.setEmail("ivana@test.com");
        privatnaOsoba.setLozinka("lozinka");
        privatnaOsoba.setTelefon("0911234567");
        privatnaOsoba.setDatumRegistracije(LocalDate.now());
        privatnaOsoba.setKorisnickoIme("ivana.kovac");
        privatnaOsoba.setIme("Ivana");
        privatnaOsoba.setPrezime("Kovac");
        privatnaOsobaRepository.save(privatnaOsoba);

        Obrt obrt = new Obrt();
        obrt.setEmail("obrt@test.com");
        obrt.setLozinka("lozinka");
        obrt.setTelefon("0919999999");
        obrt.setDatumRegistracije(LocalDate.now());
        obrt.setIban("HR1210010051863000160            ");
        obrt.setNaziv("Test obrt");
        obrtRepository.save(obrt);

        Adresa adresa = new Adresa();
        adresa.setUlica("Ilica");
        adresa.setKucniBroj("1");
        adresa.setGrad("Zagreb");
        adresa.setPostanskiBroj("10000");
        adresa.setKorisnik(privatnaOsoba);
        adresaRepository.save(adresa);

        vrstaRublja = new VrstaRublja();
        vrstaRublja.setNaziv("Kosulje");
        vrstaRubljaRepository.save(vrstaRublja);

        narudzba = new Narudzba();
        narudzba.setDatumNarucivanja(LocalDate.now());
        narudzba.setTerminPrikupa(LocalDate.now().plusDays(3));
        narudzba.setUkupniIznos(new BigDecimal("45.00"));
        narudzba.setPrivatnaOsoba(privatnaOsoba);
        narudzba.setObrt(obrt);
        narudzba.setAdresa(adresa);
        narudzbaRepository.save(narudzba);
    }

    @Test
    void should_ReturnItems_When_OrderHasItems() {
        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setNarudzba(narudzba);
        stavka.setVrstaRublja(vrstaRublja);
        stavkaNarudzbeRepository.save(stavka);

        List<StavkaNarudzbe> result = stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(narudzba.getNarudzbaId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKolicina()).isEqualByComparingTo("2.0");
    }

    @Test
    void should_ReturnEmptyList_When_OrderHasNoItems() {
        List<StavkaNarudzbe> result = stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(narudzba.getNarudzbaId());

        assertThat(result).isEmpty();
    }

    @Test
    void should_ReturnEmptyList_When_OrderDoesNotExist() {
        List<StavkaNarudzbe> result = stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void should_SaveItem_When_ValidItemProvided() {
        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("3.5"));
        stavka.setNarudzba(narudzba);
        stavka.setVrstaRublja(vrstaRublja);

        StavkaNarudzbe saved = stavkaNarudzbeRepository.save(stavka);

        assertThat(saved.getStavkaId()).isNotNull();
        assertThat(saved.getKolicina()).isEqualByComparingTo("3.5");
    }

    @Test
    void should_DeleteItem_When_ItemExists() {
        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setNarudzba(narudzba);
        stavka.setVrstaRublja(vrstaRublja);
        stavkaNarudzbeRepository.save(stavka);

        stavkaNarudzbeRepository.deleteById(stavka.getStavkaId());

        Optional<StavkaNarudzbe> result = stavkaNarudzbeRepository.findById(stavka.getStavkaId());
        assertThat(result).isEmpty();
    }
}
