package com.fer.backend.controller;

import com.fer.backend.dto.*;
import com.fer.backend.model.enums.StatusNarudzbe;
import com.fer.backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NarudzbaController.class)
public class NarudzbaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NarudzbaService narudzbaService;

    @MockitoBean
    private PrivatnaOsobaService privatnaOsobaService;

    @MockitoBean
    private ObrtService obrtService;

    @MockitoBean
    private AdresaService adresaService;

    @MockitoBean
    private StatusService statusService;

    @MockitoBean
    private VrstaRubljaService vrstaRubljaService;

    @Test
    void should_ReturnIndexView_When_IndexIsAccessed() throws Exception {
        when(narudzbaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(get("/narudzbe"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/index"))
                .andExpect(model().attributeExists("narudzbe"));
    }

    @Test
    void should_PassSearchToModel_When_SearchParamProvided() throws Exception {
        when(narudzbaService.findAll("ivana")).thenReturn(List.of());

        mockMvc.perform(get("/narudzbe").param("search", "ivana"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/index"))
                .andExpect(model().attribute("search", "ivana"));
    }

    @Test
    void should_ReturnNewOrderForm_When_NovaFormIsAccessed() throws Exception {
        when(privatnaOsobaService.findAll()).thenReturn(List.of());
        when(obrtService.findAll()).thenReturn(List.of());
        when(adresaService.findAll()).thenReturn(List.of());
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(get("/narudzbe/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/nova"))
                .andExpect(model().attributeExists("narudzba"));
    }

    @Test
    void should_ReturnDetailView_When_OrderIsAccessed() throws Exception {
        UUID id = UUID.randomUUID();

        NarudzbaFormDto dto = new NarudzbaFormDto();
        dto.setNarudzbaId(id);
        dto.setKorisnickoIme("ivana.kovac");
        dto.setObrtIban("HR123");
        dto.setAdresaId(UUID.randomUUID());
        dto.setTerminPrikupa(LocalDate.now().plusDays(5));

        StatusDto status = new StatusDto();
        status.setNazivStatusa(StatusNarudzbe.ZAPRIMLJENO.getNaziv());

        when(narudzbaService.findById(id)).thenReturn(dto);
        when(narudzbaService.findStavke(id)).thenReturn(List.of());
        when(statusService.findByNarudzbaId(id)).thenReturn(List.of(status));
        when(statusService.findTrenutniStatus(id)).thenReturn(status);
        when(privatnaOsobaService.findAll()).thenReturn(List.of());
        when(obrtService.findAll()).thenReturn(List.of());
        when(adresaService.findAll()).thenReturn(List.of());
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(get("/narudzbe/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/detail"))
                .andExpect(model().attributeExists("narudzba"))
                .andExpect(model().attributeExists("stavke"))
                .andExpect(model().attributeExists("statusi"));
    }

    @Test
    void should_RedirectToList_When_OrderIsDeleted() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(narudzbaService).delete(id);

        mockMvc.perform(post("/narudzbe/" + id + "/obrisi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe"));

        verify(narudzbaService, times(1)).delete(id);
    }

    @Test
    void should_ReturnNovaWithError_When_NoAvailableObrti() throws Exception {
        when(narudzbaService.pretraga(any(), any())).thenReturn(List.of(
                ObrtSCjenomDto.builder()
                        .iban("HR123")
                        .naziv("Test obrt")
                        .ukupniIznos(BigDecimal.ZERO)
                        .dostupan(false)
                        .razlogNedostupnosti("No price list.")
                        .build()
        ));
        when(privatnaOsobaService.findAll()).thenReturn(List.of());
        when(obrtService.findAll()).thenReturn(List.of());
        when(adresaService.findAll()).thenReturn(List.of());
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(post("/narudzbe/pretraga")
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", UUID.randomUUID().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(5).toString())
                        .param("stavke[0].vrstaRubljaId", UUID.randomUUID().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/nova"))
                .andExpect(model().attributeExists("greska"));
    }

    @Test
    void should_ReturnIzracunView_When_AvailableObrtiFound() throws Exception {
        when(narudzbaService.pretraga(any(), any())).thenReturn(List.of(
                ObrtSCjenomDto.builder()
                        .iban("HR123")
                        .naziv("Test obrt")
                        .ukupniIznos(new BigDecimal("45.00"))
                        .dostupan(true)
                        .build()
        ));
        when(privatnaOsobaService.findAll()).thenReturn(List.of());
        when(obrtService.findAll()).thenReturn(List.of());
        when(adresaService.findAll()).thenReturn(List.of());
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(post("/narudzbe/pretraga")
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", UUID.randomUUID().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(5).toString())
                        .param("stavke[0].vrstaRubljaId", UUID.randomUUID().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("narudzbe/izracun"))
                .andExpect(model().attributeExists("obrtiSCijenama"));
    }

    @Test
    void should_RedirectToDetail_When_OrderIsConfirmed() throws Exception {
        UUID id = UUID.randomUUID();
        when(narudzbaService.save(any())).thenReturn(id);

        mockMvc.perform(post("/narudzbe/potvrdi")
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", UUID.randomUUID().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(5).toString())
                        .param("obrtIban", "HR123")
                        .param("stavke[0].vrstaRubljaId", UUID.randomUUID().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + id));
    }

    @Test
    void should_RedirectToDetail_When_OrderIsUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(narudzbaService).update(any(), any());
        when(statusService.findTrenutniStatus(id)).thenReturn(null);
        when(narudzbaService.findStavke(id)).thenReturn(List.of());
        when(statusService.findByNarudzbaId(id)).thenReturn(List.of());
        when(privatnaOsobaService.findAll()).thenReturn(List.of());
        when(obrtService.findAll()).thenReturn(List.of());
        when(adresaService.findAll()).thenReturn(List.of());
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(post("/narudzbe/" + id)
                        .param("korisnickoIme", "ivana.kovac")
                        .param("adresaId", UUID.randomUUID().toString())
                        .param("terminPrikupa", LocalDate.now().plusDays(5).toString())
                        .param("obrtIban", "HR123")
                        .param("stavke[0].vrstaRubljaId", UUID.randomUUID().toString())
                        .param("stavke[0].kolicina", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + id));
    }
}
