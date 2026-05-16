package com.fer.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.fer.backend.service.StatusService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
public class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatusService statusService;

    @Test
    void should_RedirectWithError_When_StatusNameIsBlank() throws Exception {
        UUID narudzbaId = UUID.randomUUID();

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/statusi")
                        .param("nazivStatusa", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzbaId));

        verify(statusService, never()).dodajStatus(any(), any());
    }

    @Test
    void should_AddStatusAndRedirect_When_ValidStatusProvided() throws Exception {
        UUID narudzbaId = UUID.randomUUID();
        doNothing().when(statusService).dodajStatus(narudzbaId, "Zaprimljeno");

        mockMvc.perform(post("/narudzbe/" + narudzbaId + "/statusi").param("nazivStatusa", "Zaprimljeno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/narudzbe/" + narudzbaId));

        verify(statusService, times(1)).dodajStatus(narudzbaId, "Zaprimljeno");
    }
}
