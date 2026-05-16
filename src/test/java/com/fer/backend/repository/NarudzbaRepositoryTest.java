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
class NarudzbaRepositoryTest {

    @Autowired
    private NarudzbaRepository narudzbaRepository;

    @Autowired
    private PrivatnaOsobaRepository privatnaOsobaRepository;

    @Autowired
    private ObrtRepository obrtRepository;

    @Autowired
    private AdresaRepository adresaRepository;

    @Autowired
    private KorisnikRepository korisnikRepository;

    private Narudzba narudzba;
    private PrivatnaOsoba privatnaOsoba;
    private Obrt obrt;
    private Adresa adresa;

    @BeforeEach
    void setUp() {
        narudzbaRepository.deleteAll();

        privatnaOsoba = new PrivatnaOsoba();
        privatnaOsoba.setEmail("ivana@test.com");
        privatnaOsoba.setLozinka("lozinka");
        privatnaOsoba.setTelefon("0911234567");
        privatnaOsoba.setDatumRegistracije(LocalDate.now());
        privatnaOsoba.setKorisnickoIme("ivana.kovac");
        privatnaOsoba.setIme("Ivana");
        privatnaOsoba.setPrezime("Kovac");
        privatnaOsobaRepository.save(privatnaOsoba);

        obrt = new Obrt();
        obrt.setEmail("obrt@test.com");
        obrt.setLozinka("lozinka");
        obrt.setTelefon("0919999999");
        obrt.setDatumRegistracije(LocalDate.now());
        obrt.setIban("HR1210010051863000160  ");
        obrt.setNaziv("Test obrt");
        obrtRepository.save(obrt);

        adresa = new Adresa();
        adresa.setUlica("Ilica");
        adresa.setKucniBroj("1");
        adresa.setGrad("Zagreb");
        adresa.setPostanskiBroj("10000");
        adresa.setKorisnik(privatnaOsoba);
        adresaRepository.save(adresa);

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
    void should_FindAllOrders_When_Requested() {
        List<Narudzba> result = narudzbaRepository.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void should_FindOrderById_When_OrderExists() {
        Optional<Narudzba> result = narudzbaRepository.findById(narudzba.getNarudzbaId());
        assertThat(result).isPresent();
        assertThat(result.get().getUkupniIznos()).isEqualByComparingTo("45.00");
    }

    @Test
    void should_ReturnEmpty_When_OrderDoesNotExist() {
        Optional<Narudzba> result = narudzbaRepository.findById(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    void should_FindByKorisnickoIme_When_SearchMatches() {
        List<Narudzba> result = narudzbaRepository.findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase("ivana");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrivatnaOsoba().getKorisnickoIme()).isEqualTo("ivana.kovac");
    }

    @Test
    void should_ReturnEmpty_When_SearchDoesNotMatch() {
        List<Narudzba> result = narudzbaRepository.findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase("nepostojeci");
        assertThat(result).isEmpty();
    }

    @Test
    void should_FindByKorisnickoImeCaseInsensitive_When_SearchIsUppercase() {
        List<Narudzba> result = narudzbaRepository.findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase("IVANA");
        assertThat(result).hasSize(1);
    }

    @Test
    void should_SaveOrder_When_ValidOrderProvided() {
        Narudzba nova = Narudzba.builder()
                .datumNarucivanja(LocalDate.now())
                .terminPrikupa(LocalDate.now().plusDays(5))
                .ukupniIznos(new BigDecimal("30.00"))
                .privatnaOsoba(privatnaOsoba)
                .obrt(obrt)
                .adresa(adresa)
                .build();

        Narudzba saved = narudzbaRepository.save(nova);

        assertThat(saved.getNarudzbaId()).isNotNull();
        assertThat(narudzbaRepository.findAll()).hasSize(2);
    }

    @Test
    void should_DeleteOrder_When_OrderExists() {
        narudzbaRepository.deleteById(narudzba.getNarudzbaId());
        assertThat(narudzbaRepository.findById(narudzba.getNarudzbaId())).isEmpty();
    }
}