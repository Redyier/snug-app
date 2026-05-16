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
public class StavkaNarudzbeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Autowired
    private CjenikRepository cjenikRepository;

    private Narudzba narudzba;
    private VrstaRublja vrstaRublja;
    private Cjenik cjenik;

    @BeforeEach
    void setUp() {
        stavkaNarudzbeRepository.deleteAll();
        narudzbaRepository.deleteAll();
        cjenikRepository.deleteAll();
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

        cjenik = new Cjenik();
        cjenik.setCijenaPoKg(new BigDecimal("10.00"));
        cjenik.setMultiplikatorNeradnogDana(new BigDecimal("1.00"));
        cjenik.setObrt(obrt);
        cjenik.setVrstaRublja(vrstaRublja);
        cjenikRepository.save(cjenik);

        narudzba = new Narudzba();
        narudzba.setDatumNarucivanja(LocalDate.now());
        narudzba.setTerminPrikupa(LocalDate.now().plusDays(3));
        narudzba.setUkupniIznos(new BigDecimal("0.00"));
        narudzba.setPrivatnaOsoba(privatnaOsoba);
        narudzba.setObrt(obrt);
        narudzba.setAdresa(adresa);
        narudzbaRepository.save(narudzba);
    }

    @Test
    void should_ReturnNewItemForm_When_NovaFormaIsAccessed() throws Exception {
        mockMvc.perform(get("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"))
                .andExpect(model().attributeExists("stavka"))
                .andExpect(model().attributeExists("vrsteRublja"));
    }

    @Test
    void should_SaveItemAndRedirect_When_ValidDataAndPriceListExists() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke")
                        .param("vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("kolicina", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzba.getNarudzbaId()));

        assertThat(stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(narudzba.getNarudzbaId())).hasSize(1);
    }

    @Test
    void should_UpdateTotalAmount_When_ItemIsSaved() throws Exception {
        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke")
                        .param("vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("kolicina", "2.0"))
                .andExpect(status().is3xxRedirection());

        Narudzba updated = narudzbaRepository.findById(narudzba.getNarudzbaId()).get();
        assertThat(updated.getUkupniIznos()).isEqualByComparingTo("20.00");
    }

    @Test
    void should_ReturnFormWithError_When_NoPriceListExists() throws Exception {
        cjenikRepository.deleteAll();

        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke")
                        .param("vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("kolicina", "2.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"))
                .andExpect(model().attributeExists("greska"));

        assertThat(stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(narudzba.getNarudzbaId())).isEmpty();
    }

    @Test
    void should_ReturnEditForm_When_EditIsAccessed() throws Exception {
        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setNarudzba(narudzba);
        stavka.setVrstaRublja(vrstaRublja);
        stavkaNarudzbeRepository.save(stavka);

        mockMvc.perform(get("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke/" + stavka.getStavkaId() + "/uredi"))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"))
                .andExpect(model().attributeExists("stavka"));
    }

    @Test
    void should_UpdateItem_When_ValidDataProvided() throws Exception {
        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setNarudzba(narudzba);
        stavka.setVrstaRublja(vrstaRublja);
        stavkaNarudzbeRepository.save(stavka);

        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke/" + stavka.getStavkaId())
                        .param("vrstaRubljaId", vrstaRublja.getVrstaRubljaId().toString())
                        .param("kolicina", "5.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzba.getNarudzbaId()));

        StavkaNarudzbe updated = stavkaNarudzbeRepository.findById(stavka.getStavkaId()).get();
        assertThat(updated.getKolicina()).isEqualByComparingTo("5.0");
    }

    @Test
    void should_DeleteItem_When_ItemExists() throws Exception {
        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setNarudzba(narudzba);
        stavka.setVrstaRublja(vrstaRublja);
        stavkaNarudzbeRepository.save(stavka);

        mockMvc.perform(post("/narudzbe/" + narudzba.getNarudzbaId() + "/stavke/" + stavka.getStavkaId() + "/obrisi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzba.getNarudzbaId()));

        assertThat(stavkaNarudzbeRepository.findById(stavka.getStavkaId())).isEmpty();
    }
}
