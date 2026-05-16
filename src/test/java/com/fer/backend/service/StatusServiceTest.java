package com.fer.backend.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fer.backend.dto.StatusDto;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.model.Narudzba;
import com.fer.backend.model.Status;
import com.fer.backend.repository.NarudzbaRepository;
import com.fer.backend.repository.StatusRepository;
import com.fer.backend.service.impl.StatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private NarudzbaRepository narudzbaRepository;

    @InjectMocks
    private StatusServiceImpl statusService;

    private UUID narudzbaId;
    private Narudzba narudzba;
    private Status status;

    @BeforeEach
    void setUp() {
        narudzbaId = UUID.randomUUID();

        narudzba = new Narudzba();
        narudzba.setNarudzbaId(narudzbaId);

        status = Status.builder()
                .statusId(UUID.randomUUID())
                .nazivStatusa("Zaprimljeno")
                .vrijemeAzuriranja(LocalDateTime.now())
                .narudzba(narudzba)
                .build();
    }

    @Test
    void should_ReturnStatusList_When_OrderHasStatuses() {
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of(status));

        List<StatusDto> result = statusService.findByNarudzbaId(narudzbaId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNazivStatusa()).isEqualTo("Zaprimljeno");
        assertThat(result.get(0).getNarudzbaId()).isEqualTo(narudzbaId);
    }

    @Test
    void should_ReturnEmptyList_When_OrderHasNoStatuses() {
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of());

        List<StatusDto> result = statusService.findByNarudzbaId(narudzbaId);

        assertThat(result).isEmpty();
    }

    @Test
    void should_ReturnCurrentStatus_When_StatusesExist() {
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of(status));

        StatusDto result = statusService.findTrenutniStatus(narudzbaId);

        assertThat(result).isNotNull();
        assertThat(result.getNazivStatusa()).isEqualTo("Zaprimljeno");
    }

    @Test
    void should_ReturnNull_When_NoStatusesExist() {
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of());

        StatusDto result = statusService.findTrenutniStatus(narudzbaId);

        assertThat(result).isNull();
    }

    @Test
    void should_SaveStatus_When_ValidDataProvided() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.of(narudzba));
        when(statusRepository.save(any())).thenReturn(status);

        statusService.dodajStatus(narudzbaId, "Zaprimljeno");

        verify(statusRepository).save(argThat(s -> s.getNazivStatusa().equals("Zaprimljeno") && s.getNarudzba().getNarudzbaId().equals(narudzbaId)));
    }

    @Test
    void should_ThrowException_When_OrderNotFound() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statusService.dodajStatus(narudzbaId, "Zaprimljeno")).isInstanceOf(EntityNotFoundException.class);

        verify(statusRepository, never()).save(any());
    }
}
