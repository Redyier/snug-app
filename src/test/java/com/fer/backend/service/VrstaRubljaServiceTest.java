package com.fer.backend.service;

import com.fer.backend.dto.VrstaRubljaDto;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.model.VrstaRublja;
import com.fer.backend.repository.VrstaRubljaRepository;
import com.fer.backend.service.impl.VrstaRubljaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VrstaRubljaServiceTest {

    @Mock
    private VrstaRubljaRepository vrstaRubljaRepository;

    @InjectMocks
    private VrstaRubljaServiceImpl vrstaRubljaService;

    private VrstaRublja vrstaRublja;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        vrstaRublja = VrstaRublja.builder()
                .vrstaRubljaId(id)
                .naziv("Kosulje")
                .build();
    }

    @Test
    void should_ReturnAllItems_When_SearchIsNull() {
        when(vrstaRubljaRepository.findAll()).thenReturn(List.of(vrstaRublja));

        List<VrstaRubljaDto> result = vrstaRubljaService.findAll(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNaziv()).isEqualTo("Kosulje");
        verify(vrstaRubljaRepository).findAll();
    }

    @Test
    void should_ReturnFilteredItems_When_SearchIsProvided() {
        when(vrstaRubljaRepository.findByNazivContainingIgnoreCase("kosulja")).thenReturn(List.of(vrstaRublja));

        List<VrstaRubljaDto> result = vrstaRubljaService.findAll("kosulja");

        assertThat(result).hasSize(1);
        verify(vrstaRubljaRepository).findByNazivContainingIgnoreCase("kosulja");
        verify(vrstaRubljaRepository, never()).findAll();
    }

    @Test
    void should_ReturnDto_When_ItemExists() {
        when(vrstaRubljaRepository.findById(id)).thenReturn(Optional.of(vrstaRublja));

        VrstaRubljaDto result = vrstaRubljaService.findById(id);

        assertThat(result.getNaziv()).isEqualTo("Kosulje");
        assertThat(result.getVrstaRubljaId()).isEqualTo(id);
    }

    @Test
    void should_ThrowException_When_ItemNotFound() {
        when(vrstaRubljaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vrstaRubljaService.findById(id)).isInstanceOf(EntityNotFoundException.class).hasMessageContaining(id.toString());
    }

    @Test
    void should_SaveItem_When_ValidDtoProvided() {
        VrstaRubljaDto dto = new VrstaRubljaDto();
        dto.setNaziv("Hlace");

        vrstaRubljaService.spremi(dto);

        verify(vrstaRubljaRepository).save(argThat(v -> v.getNaziv().equals("Hlace")));
    }

    @Test
    void should_UpdateItem_When_ItemExists() {
        VrstaRubljaDto dto = new VrstaRubljaDto();
        dto.setNaziv("Hlace");

        when(vrstaRubljaRepository.findById(id)).thenReturn(Optional.of(vrstaRublja));

        vrstaRubljaService.azuriraj(id, dto);

        verify(vrstaRubljaRepository).save(argThat(v -> v.getNaziv().equals("Hlace")));
    }

    @Test
    void should_ThrowException_When_UpdatingNonExistentItem() {
        when(vrstaRubljaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vrstaRubljaService.azuriraj(id, new VrstaRubljaDto())).isInstanceOf(EntityNotFoundException.class);

        verify(vrstaRubljaRepository, never()).save(any());
    }

    @Test
    void should_DeleteItem_When_ItemExists() {
        when(vrstaRubljaRepository.existsById(id)).thenReturn(true);
        doNothing().when(vrstaRubljaRepository).deleteById(id);

        vrstaRubljaService.obrisi(id);

        verify(vrstaRubljaRepository).deleteById(id);
    }

    @Test
    void should_ThrowException_When_DeletingNonExistentItem() {
        when(vrstaRubljaRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> vrstaRubljaService.obrisi(id)).isInstanceOf(EntityNotFoundException.class);

        verify(vrstaRubljaRepository, never()).deleteById(any());
    }
}
