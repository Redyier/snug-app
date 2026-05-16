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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NarudzbaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Autowired
    private CjenikRepository cjenikRepository;

    private PrivatnaOsoba privatnaOsoba;
    private Obrt obrt;
    private Adresa adresa;
    private VrstaRublja vrstaRublja;
    private Cjenik cjenik;
    private Narudzba narudzba;

    @BeforeEach
    void setUp() {
        narudzbaRepository.deleteAll();
        cjenikRepository.deleteAll();
        adresaRepository.deleteAll();
        vrstaRubljaRepository.deleteAll();
        privatnaOsobaRepository.deleteAll();
        obrtRepository.deleteAll();

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
        obrt.setIban("HR1210010051863000160            ");
        obrt.setNaziv("Test obrt");
        obrtRepository.save(obrt);

        adresa = new Adresa();
        adresa.setUlica("Ilica");
        adresa.setKucniBroj("1");
        adresa.setGrad("Zagreb");
        adresa.setPostanskiBroj("10000");
        adresa.setKorisnik(privatnaOsoba);
        adresaRepository.save(adresa);

        vrstaRublja = new VrstaRublja();
        vrstaRublja.setNaziv("Kosulje");
        vrstaRubljaRepository.save(vrstaRublja);

        cjenik = new Cjenik();
        cjenik.setCijenaPoKg(new BigDecimal("10.00"));
        cjenik.setMultiplikatorNeradnogDana(new BigDecimal("1.00"));
        cjenik.setObrt(obrt);
        cjenik.setVrstaRublja(vrstaRublja);
        cjenikRepository.save(cjenik);

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
    void should_ReturnOrderList_When_IndexIsAccessed() throws Exception {
        mockMvc.perform(get("/narudzbe"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/index"))
                .andExpect(model().attributeExists("narudzbe"));
    }

    @Test
    void should_ReturnOrderDetail_When_OrderExists() throws Exception {
        mockMvc.perform(get("/narudzbe/" + narudzba.getNarudzbaId()))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/detail"))
                .andExpect(model().attributeExists("narudzba"))
                .andExpect(model().attributeExists("stavke"))
                .andExpect(model().attributeExists("statusi"));
    }

    @Test
    void should_CreateOrderAndRedirect_When_ValidDataProvided() throws Exception {
        mockMvc.perform(post("/narudzbe/potvrdi")
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", adresa.getAdresaId().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(5).toString())
                        .param("obrtIban", obrt.getIban())
                        .param("stavke[0].vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().is3xxRedirection());

        assertThat(narudzbaRepository.findAll()).hasSize(2);
    }

    @Test
    void should_DeleteOrderAndRedirect_When_OrderExists() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/obrisi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe"));

        assertThat(narudzbaRepository.findById(narudzba.getNarudzbaId())).isEmpty();
    }

    @Test
    void should_ReturnAvailableObrti_When_SearchIsPerformed() throws Exception {
        mockMvc.perform(post("/narudzbe/pretraga")
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", adresa.getAdresaId().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(5).toString())
                        .param("stavke[0].vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/izracun"))
                .andExpect(model().attributeExists("obrtiSCijenama"));
    }

    @Test
    void should_UpdateOrder_When_ValidDataProvided() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId())
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", adresa.getAdresaId().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(7).toString())
                        .param("obrtIban", obrt.getIban())
                        .param("stavke[0].vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzba.getNarudzbaId()));

        Narudzba updated = narudzbaRepository.findById(narudzba.getNarudzbaId()).get();
        assertThat(updated.getTerminPrikupa()).isEqualTo(LocalDate.now().plusDays(7));
    }
}