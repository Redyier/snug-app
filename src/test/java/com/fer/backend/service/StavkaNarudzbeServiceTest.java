package com.fer.backend.service;

import com.fer.backend.dto.StavkaNarudzbeFormDto;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.exception.ValidationException;
import com.fer.backend.model.*;
import com.fer.backend.repository.*;
import com.fer.backend.service.impl.StavkaNarudzbeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StavkaNarudzbeServiceTest {

    @Mock
    private StavkaNarudzbeRepository stavkaNarudzbeRepository;

    @Mock
    private NarudzbaRepository narudzbaRepository;

    @Mock
    private VrstaRubljaRepository vrstaRubljaRepository;

    @Mock
    private CjenikRepository cjenikRepository;

    @Mock
    private NarudzbaService narudzbaService;

    @InjectMocks
    private StavkaNarudzbeServiceImpl stavkaNarudzbeService;

    private UUID stavkaId;
    private UUID narudzbaId;
    private UUID vrstaRubljaId;
    private Narudzba narudzba;
    private Obrt obrt;
    private VrstaRublja vrstaRublja;
    private StavkaNarudzbe stavka;

    @BeforeEach
    void setUp() {
        stavkaId = UUID.randomUUID();
        narudzbaId = UUID.randomUUID();
        vrstaRubljaId = UUID.randomUUID();

        obrt = new Obrt();
        obrt.setIban("HR123");
        obrt.setNaziv("Test obrt");

        narudzba = new Narudzba();
        narudzba.setNarudzbaId(narudzbaId);
        narudzba.setObrt(obrt);

        vrstaRublja = new VrstaRublja();
        vrstaRublja.setVrstaRubljaId(vrstaRubljaId);
        vrstaRublja.setNaziv("Kosulje");

        stavka = new StavkaNarudzbe();
        stavka.setStavkaId(stavkaId);
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setVrstaRublja(vrstaRublja);
        stavka.setNarudzba(narudzba);
    }

    @Test
    void should_ReturnItemDto_When_ItemExists() {
        when(stavkaNarudzbeRepository.findById(stavkaId)).thenReturn(Optional.of(stavka));

        StavkaNarudzbeFormDto result = stavkaNarudzbeService.findById(stavkaId);

        assertThat(result.getStavkaId()).isEqualTo(stavkaId);
        assertThat(result.getKolicina()).isEqualByComparingTo("2.0");
        assertThat(result.getVrstaRubljaId()).isEqualTo(vrstaRubljaId);
    }

    @Test
    void should_ThrowException_When_ItemNotFound() {
        when(stavkaNarudzbeRepository.findById(stavkaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stavkaNarudzbeService.findById(stavkaId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_SaveItem_When_PriceListExists() {
        StavkaNarudzbeFormDto dto = new StavkaNarudzbeFormDto();
        dto.setVrstaRubljaId(vrstaRubljaId);
        dto.setKolicina(new BigDecimal("2.0"));

        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.of(narudzba));
        when(vrstaRubljaRepository.findById(vrstaRubljaId)).thenReturn(Optional.of(vrstaRublja));
        when(cjenikRepository.existsByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(true);
        when(stavkaNarudzbeRepository.save(any())).thenReturn(stavka);
        doNothing().when(narudzbaService).izracunajIAzurirajIznos(narudzbaId);

        stavkaNarudzbeService.spremi(narudzbaId, dto);

        verify(stavkaNarudzbeRepository).save(any());
        verify(narudzbaService).izracunajIAzurirajIznos(narudzbaId);
    }

    @Test
    void should_ThrowValidationException_When_NoPriceListExists() {
        StavkaNarudzbeFormDto dto = new StavkaNarudzbeFormDto();
        dto.setVrstaRubljaId(vrstaRubljaId);
        dto.setKolicina(new BigDecimal("2.0"));

        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.of(narudzba));
        when(vrstaRubljaRepository.findById(vrstaRubljaId)).thenReturn(Optional.of(vrstaRublja));
        when(cjenikRepository.existsByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(false);

        assertThatThrownBy(() -> stavkaNarudzbeService.spremi(narudzbaId, dto)).isInstanceOf(ValidationException.class);

        verify(stavkaNarudzbeRepository, never()).save(any());
    }

    @Test
    void should_ThrowException_When_SavingWithNonExistentOrder() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stavkaNarudzbeService.spremi(narudzbaId, new StavkaNarudzbeFormDto())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_UpdateItem_When_PriceListExists() {
        StavkaNarudzbeFormDto dto = new StavkaNarudzbeFormDto();
        dto.setVrstaRubljaId(vrstaRubljaId);
        dto.setKolicina(new BigDecimal("3.0"));

        when(stavkaNarudzbeRepository.findById(stavkaId)).thenReturn(Optional.of(stavka));
        when(vrstaRubljaRepository.findById(vrstaRubljaId)).thenReturn(Optional.of(vrstaRublja));
        when(cjenikRepository.existsByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(true);
        when(stavkaNarudzbeRepository.save(any())).thenReturn(stavka);
        doNothing().when(narudzbaService).izracunajIAzurirajIznos(narudzbaId);

        stavkaNarudzbeService.azuriraj(stavkaId, dto);

        verify(stavkaNarudzbeRepository).save(argThat(s -> s.getKolicina().compareTo(new BigDecimal("3.0")) == 0));
        verify(narudzbaService).izracunajIAzurirajIznos(narudzbaId);
    }

    @Test
    void should_ThrowValidationException_When_UpdatingWithNoPriceList() {
        StavkaNarudzbeFormDto dto = new StavkaNarudzbeFormDto();
        dto.setVrstaRubljaId(vrstaRubljaId);
        dto.setKolicina(new BigDecimal("2.0"));

        when(stavkaNarudzbeRepository.findById(stavkaId)).thenReturn(Optional.of(stavka));
        when(vrstaRubljaRepository.findById(vrstaRubljaId)).thenReturn(Optional.of(vrstaRublja));
        when(cjenikRepository.existsByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(false);

        assertThatThrownBy(() -> stavkaNarudzbeService.azuriraj(stavkaId, dto)).isInstanceOf(ValidationException.class);

        verify(stavkaNarudzbeRepository, never()).save(any());
    }

    @Test
    void should_DeleteItem_When_ItemExists() {
        when(stavkaNarudzbeRepository.findById(stavkaId)).thenReturn(Optional.of(stavka));
        doNothing().when(stavkaNarudzbeRepository).deleteById(stavkaId);
        doNothing().when(narudzbaService).izracunajIAzurirajIznos(narudzbaId);

        stavkaNarudzbeService.obrisi(stavkaId);

        verify(stavkaNarudzbeRepository).deleteById(stavkaId);
        verify(narudzbaService).izracunajIAzurirajIznos(narudzbaId);
    }

    @Test
    void should_ThrowException_When_DeletingNonExistentItem() {
        when(stavkaNarudzbeRepository.findById(stavkaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stavkaNarudzbeService.obrisi(stavkaId)).isInstanceOf(EntityNotFoundException.class);

        verify(stavkaNarudzbeRepository, never()).deleteById(any());
    }
}
