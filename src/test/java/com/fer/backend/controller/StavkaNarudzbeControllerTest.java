package com.fer.backend.controller;

import com.fer.backend.dto.StavkaNarudzbeFormDto;
import com.fer.backend.dto.VrstaRubljaDto;
import com.fer.backend.exception.ValidationException;
import com.fer.backend.service.StavkaNarudzbeService;
import com.fer.backend.service.VrstaRubljaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StavkaNarudzbeController.class)
public class StavkaNarudzbeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StavkaNarudzbeService stavkaNarudzbeService;

    @MockitoBean
    private VrstaRubljaService vrstaRubljaService;

    @Test
    void should_ReturnNewItemForm_When_NovaFormaIsAccessed() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(get("/narudzbe/" + narudzbaId + "/stavke/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"))
                .andExpect(model().attributeExists("stavka"))
                .andExpect(model().attributeExists("narudzbaId"))
                .andExpect(model().attributeExists("vrsteRublja"));
    }

    @Test
    void should_RedirectToOrder_When_ItemIsSaved() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        doNothing().when(stavkaNarudzbeService).spremi(any(), any());
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/stavke")
                        .param("vrstaRubljaId", UUID.randomUUID().toString())
                        .param("kolicina", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzbaId));
    }

    @Test
    void should_ReturnFormWithError_When_ValidationFails() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());
        doThrow(new ValidationException("Nema cjenik."))
                .when(stavkaNarudzbeService).spremi(any(), any());

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/stavke")
                        .param("vrstaRubljaId", UUID.randomUUID().toString())
                        .param("kolicina", "2.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"))
                .andExpect(model().attributeExists("greska"));
    }

    @Test
    void should_ReturnFormWithErrors_When_InvalidDataProvided() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/stavke")
                        .param("vrstaRubljaId", "")
                        .param("kolicina", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"));
    }

    @Test
    void should_ReturnEditForm_When_EditIsAccessed() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        UUID stavkaId = UUID.randomUUID();

        StavkaNarudzbeFormDto dto = new StavkaNarudzbeFormDto();
        dto.setStavkaId(stavkaId);
        dto.setVrstaRubljaId(UUID.randomUUID());
        dto.setKolicina(new BigDecimal("2.0"));

        when(stavkaNarudzbeService.findById(stavkaId)).thenReturn(dto);
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(get("/narudzbe/" + narudzbaId + "/stavke/" + stavkaId + "/uredi"))
                .andExpect(status().isOk())
                .andExpect(view().name("stavke/forma"))
                .andExpect(model().attributeExists("stavka"));
    }

    @Test
    void should_RedirectToOrder_When_ItemIsUpdated() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        UUID stavkaId = UUID.randomUUID();
        doNothing().when(stavkaNarudzbeService).azuriraj(any(), any());

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/stavke/" + stavkaId)
                        .param("vrstaRubljaId", UUID.randomUUID().toString())
                        .param("kolicina", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzbaId));
    }

    @Test
    void should_RedirectToOrder_When_ItemIsDeleted() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        UUID stavkaId = UUID.randomUUID();
        doNothing().when(stavkaNarudzbeService).obrisi(stavkaId);

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/stavke/" + stavkaId + "/obrisi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzbaId));

        verify(stavkaNarudzbeService, times(1)).obrisi(stavkaId);
    }

}
