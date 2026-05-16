package com.fer.backend.controller;

import com.fer.backend.dto.VrstaRubljaDto;
import com.fer.backend.service.VrstaRubljaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(VrstaRubljaController.class)
class VrstaRubljaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VrstaRubljaService vrstaRubljaService;

    @BeforeEach
    void setUp() {
        reset(vrstaRubljaService);
    }

    @Test
    void should_ReturnIndexView_When_IndexIsAccessed() throws Exception {
        when(vrstaRubljaService.findAll(null)).thenReturn(List.of());

        mockMvc.perform(get("/vrste-rublja"))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/index"))
                .andExpect(model().attributeExists("vrste"));
    }

    @Test
    void should_PassSearchToModel_When_SearchParamProvided() throws Exception {
        when(vrstaRubljaService.findAll("košulja")).thenReturn(List.of());

        mockMvc.perform(get("/vrste-rublja").param("search", "košulja"))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/index"))
                .andExpect(model().attribute("search", "košulja"));
    }

    @Test
    void should_ReturnNewForm_When_NovaFormaIsAccessed() throws Exception {
        mockMvc.perform(get("/vrste-rublja/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/forma"))
                .andExpect(model().attributeExists("vrstaRublja"));
    }

    @Test
    void should_RedirectToList_When_ItemIsSaved() throws Exception {
        doNothing().when(vrstaRubljaService).spremi(any());

        mockMvc.perform(post("/vrste-rublja").param("naziv", "Košulje"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vrste-rublja"));
    }

    @Test
    void should_ReturnFormWithErrors_When_InvalidDataProvided() throws Exception {
        mockMvc.perform(post("/vrste-rublja").param("naziv", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/forma"))
                .andExpect(model().attributeHasFieldErrors("vrstaRublja", "naziv"));
    }

    @Test
    void should_RedirectToList_When_ItemIsDeleted() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(vrstaRubljaService).obrisi(id);

        mockMvc.perform(post("/vrste-rublja/" + id + "/obrisi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vrste-rublja"));

        verify(vrstaRubljaService, times(1)).obrisi(id);
    }

    @Test
    void should_ReturnEditForm_When_EditIsAccessed() throws Exception {
        UUID id = UUID.randomUUID();
        VrstaRubljaDto dto = new VrstaRubljaDto();
        dto.setVrstaRubljaId(id);
        dto.setNaziv("Kosulje");

        when(vrstaRubljaService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/vrste-rublja/" + id + "/uredi"))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/forma"))
                .andExpect(model().attributeExists("vrstaRublja"));
    }

    @Test
    void should_RedirectToList_When_ItemIsUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(vrstaRubljaService).azuriraj(any(), any());

        mockMvc.perform(post("/vrste-rublja/" + id).param("naziv", "Hlace"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vrste-rublja"));

        verify(vrstaRubljaService, times(1)).azuriraj(eq(id), any());
    }

    @Test
    void should_ReturnFormWithErrors_When_UpdateWithInvalidData() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/vrste-rublja/" + id).param("naziv", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/forma"))
                .andExpect(model().attributeHasFieldErrors("vrstaRublja", "naziv"));
    }
}