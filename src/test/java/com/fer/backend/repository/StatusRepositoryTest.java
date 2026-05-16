package com.fer.backend.repository;

import com.fer.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class StatusRepositoryTest {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private NarudzbaRepository narudzbaRepository;

    @Autowired
    private PrivatnaOsobaRepository privatnaOsobaRepository;

    @Autowired
    private ObrtRepository obrtRepository;

    @Autowired
    private AdresaRepository adresaRepository;

    private Narudzba narudzba;

    @BeforeEach
    void setUp() {
        statusRepository.deleteAll();
        narudzbaRepository.deleteAll();
        adresaRepository.deleteAll();
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
    void should_ReturnStatusesOrderedByTime_When_MultipleStatusesExist(){
        Status prvi = Status.builder()
                .nazivStatusa("Zaprimljeno")
                .vrijemeAzuriranja(LocalDateTime.now().minusHours(2))
                .narudzba(narudzba)
                .build();
        statusRepository.save(prvi);

        Status drugi = Status.builder()
                .nazivStatusa("Prihvaceno")
                .vrijemeAzuriranja(LocalDateTime.now().minusHours(1))
                .narudzba(narudzba)
                .build();
        statusRepository.save(drugi);

        Status treci = Status.builder()
                .nazivStatusa("U obradi")
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(narudzba)
                .build();
        statusRepository.save(treci);

        List<Status> result = statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzba.getNarudzbaId());

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getNazivStatusa()).isEqualTo("U obradi");
        assertThat(result.get(1).getNazivStatusa()).isEqualTo("Prihvaceno");
        assertThat(result.get(2).getNazivStatusa()).isEqualTo("Zaprimljeno");
    }

    @Test
    void should_ReturnEmptyList_When_OrderHasNoStatuses() {
        List<Status> result = statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzba.getNarudzbaId());

        assertThat(result).isEmpty();
    }

    @Test
    void should_SaveStatus_When_ValidStatusProvided() {
        Status status = Status.builder()
                .nazivStatusa("Zaprimljeno")
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(narudzba)
                .build();

        Status saved = statusRepository.save(status);

        assertThat(saved.getStatusId()).isNotNull();
        assertThat(saved.getNazivStatusa()).isEqualTo("Zaprimljeno");
    }

    @Test
    void should_DeleteStatus_When_StatusExists() {
        Status status = Status.builder()
                .nazivStatusa("Zaprimljeno")
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(narudzba)
                .build();
        statusRepository.save(status);

        statusRepository.deleteById(status.getStatusId());

        assertThat(statusRepository.findById(status.getStatusId())).isEmpty();
    }
}
