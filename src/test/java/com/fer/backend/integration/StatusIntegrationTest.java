package com.fer.backend.integration;

import com.fer.backend.model.*;
import com.fer.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StatusIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
        narudzba.setUkupniIznos(new BigDecimal("20.00"));
        narudzba.setPrivatnaOsoba(privatnaOsoba);
        narudzba.setObrt(obrt);
        narudzba.setAdresa(adresa);
        narudzbaRepository.save(narudzba);
    }

    @Test
    void should_AddStatusAndRedirect_When_ValidStatusProvided() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/statusi")
                        .param("nazivStatusa", "Zaprimljeno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzba.getNarudzbaId()));

        assertThat(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzba.getNarudzbaId())).hasSize(1);
    }

    @Test
    void should_SaveCorrectStatusName_When_StatusIsAdded() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/statusi")
                        .param("nazivStatusa", "Prihvaceno"))
                .andExpect(status().is3xxRedirection());

        Status saved = statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzba.getNarudzbaId()).get(0);

        assertThat(saved.getNazivStatusa()).isEqualTo("Prihvaceno");
        assertThat(saved.getNarudzba().getNarudzbaId()).isEqualTo(narudzba.getNarudzbaId());
    }

    @Test
    void should_RedirectWithoutSaving_When_StatusNameIsBlank() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/statusi")
                        .param("nazivStatusa", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzba.getNarudzbaId()));

        assertThat(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzba.getNarudzbaId())).isEmpty();
    }

    @Test
    void should_ReturnStatusesOrderedByTime_When_MultipleStatusesAdded() throws Exception {
        statusRepository.save(Status.builder()
                .nazivStatusa("Zaprimljeno")
                .vrijemeAzuriranja(LocalDateTime.now().minusHours(2))
                .narudzba(narudzba)
                .build());

        statusRepository.save(Status.builder()
                .nazivStatusa("Prihvaceno")
                .vrijemeAzuriranja(LocalDateTime.now().minusHours(1))
                .narudzba(narudzba)
                .build());

        statusRepository.save(Status.builder()
                .nazivStatusa("U obradi")
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(narudzba)
                .build());

        var statusi = statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzba.getNarudzbaId());

        assertThat(statusi).hasSize(3);
        assertThat(statusi.get(0).getNazivStatusa()).isEqualTo("U obradi");
        assertThat(statusi.get(2).getNazivStatusa()).isEqualTo("Zaprimljeno");
    }
}
