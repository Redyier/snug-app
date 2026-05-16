package com.fer.backend.integration;


import com.fer.backend.model.VrstaRublja;
import com.fer.backend.repository.VrstaRubljaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VrstaRubljaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VrstaRubljaRepository vrstaRubljaRepository;

    @BeforeEach
    void setUp() {
        vrstaRubljaRepository.deleteAll();
        vrstaRubljaRepository.save(VrstaRublja.builder().naziv("Kosulje").build());
        vrstaRubljaRepository.save(VrstaRublja.builder().naziv("Hlace").build());
    }

    @Test
    void should_ReturnList_When_IndexIsAccessed() throws Exception {
        mockMvc.perform(get("/vrste-rublja"))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/index"))
                .andExpect(model().attributeExists("vrste"));
    }

    @Test
    void should_ReturnFilteredList_When_SearchIsProvided() throws Exception {
        mockMvc.perform(get("/vrste-rublja").param("search", "kosu"))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/index"))
                .andExpect(model().attributeExists("vrste"));
    }

    @Test
    void should_CreateItem_When_ValidDataProvided() throws Exception {
        mockMvc.perform(post("/vrste-rublja")
                        .param("naziv", "Posteljina"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vrste-rublja"));

        assertThat(vrstaRubljaRepository.findAll()).hasSize(3);
        assertThat(vrstaRubljaRepository.findByNazivContainingIgnoreCase("Posteljina")).hasSize(1);
    }

    @Test
    void should_NotCreateItem_When_InvalidDataProvided() throws Exception {
        mockMvc.perform(post("/vrste-rublja")
                        .param("naziv", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("vrste-rublja/forma"));

        assertThat(vrstaRubljaRepository.findAll()).hasSize(2);
    }

    @Test
    void should_UpdateItem_When_ValidDataProvided() throws Exception {
        VrstaRublja vrsta = vrstaRubljaRepository.findByNazivContainingIgnoreCase("Kosulje").get(0);

        mockMvc.perform(post("/vrste-rublja/" + vrsta.getVrstaRubljaId())
                        .param("naziv", "Kosulje i bluze"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vrste-rublja"));

        VrstaRublja updated = vrstaRubljaRepository.findById(vrsta.getVrstaRubljaId()).get();
        assertThat(updated.getNaziv()).isEqualTo("Kosulje i bluze");
    }

    @Test
    void should_DeleteItem_When_ItemExists() throws Exception {
        VrstaRublja vrsta = vrstaRubljaRepository.findByNazivContainingIgnoreCase("Kosulje").get(0);

        mockMvc.perform(post("/vrste-rublja/" + vrsta.getVrstaRubljaId() + "/obrisi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vrste-rublja"));

        assertThat(vrstaRubljaRepository.findById(vrsta.getVrstaRubljaId())).isEmpty();
        assertThat(vrstaRubljaRepository.findAll()).hasSize(1);
    }
}
