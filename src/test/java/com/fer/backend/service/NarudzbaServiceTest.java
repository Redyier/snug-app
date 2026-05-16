package com.fer.backend.service;

import com.fer.backend.dto.*;
import com.fer.backend.exception.EntityNotFoundException;
import com.fer.backend.model.*;
import com.fer.backend.model.enums.StatusNarudzbe;
import com.fer.backend.repository.*;
import com.fer.backend.service.impl.NarudzbaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NarudzbaServiceTest {

    @Mock
    private NarudzbaRepository narudzbaRepository;

    @Mock
    private PrivatnaOsobaRepository privatnaOsobaRepository;

    @Mock
    private ObrtRepository obrtRepository;

    @Mock
    private AdresaRepository adresaRepository;

    @Mock
    private StavkaNarudzbeRepository stavkaNarudzbeRepository;

    @Mock
    private CjenikRepository cjenikRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private VrstaRubljaRepository vrstaRubljaRepository;

    @InjectMocks
    private NarudzbaServiceImpl narudzbaService;

    private UUID narudzbaId;
    private Narudzba narudzba;
    private PrivatnaOsoba privatnaOsoba;
    private Obrt obrt;
    private Adresa adresa;

    @BeforeEach
    void setUp() {
        narudzbaId = UUID.randomUUID();

        privatnaOsoba = new PrivatnaOsoba();
        privatnaOsoba.setKorisnickoIme("ivana.kovac");
        privatnaOsoba.setIme("Ivana");
        privatnaOsoba.setPrezime("Kovac");

        obrt = new Obrt();
        obrt.setIban("HR123");
        obrt.setNaziv("Test obrt");

        adresa = new Adresa();
        adresa.setAdresaId(UUID.randomUUID());
        adresa.setUlica("Ilica");
        adresa.setKucniBroj("1");
        adresa.setGrad("Zagreb");
        adresa.setPostanskiBroj("10000");

        narudzba = new Narudzba();
        narudzba.setNarudzbaId(narudzbaId);
        narudzba.setDatumNarucivanja(LocalDate.now());
        narudzba.setTerminPrikupa(LocalDate.now().plusDays(3));
        narudzba.setUkupniIznos(new BigDecimal("45.00"));
        narudzba.setPrivatnaOsoba(privatnaOsoba);
        narudzba.setObrt(obrt);
        narudzba.setAdresa(adresa);
    }

    @Test
    void should_ReturnAllOrders_When_SearchIsNull() {
        when(narudzbaRepository.findAll()).thenReturn(List.of(narudzba));
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of());

        List<NarudzbaListDto> result = narudzbaService.findAll(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKorisnickoIme()).isEqualTo("ivana.kovac");
        verify(narudzbaRepository).findAll();
    }

    @Test
    void should_ReturnFilteredOrders_When_SearchIsProvided() {
        when(narudzbaRepository.findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase("ivana")).thenReturn(List.of(narudzba));
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of());

        List<NarudzbaListDto> result = narudzbaService.findAll("ivana");

        assertThat(result).hasSize(1);
        verify(narudzbaRepository).findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase("ivana");
        verify(narudzbaRepository, never()).findAll();
    }

    @Test
    void should_ReturnOrderDto_When_OrderExists() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.of(narudzba));

        NarudzbaFormDto result = narudzbaService.findById(narudzbaId);

        assertThat(result.getKorisnickoIme()).isEqualTo("ivana.kovac");
        assertThat(result.getObrtIban()).isEqualTo("HR123");
    }

    @Test
    void should_ThrowException_When_OrderNotFound() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> narudzbaService.findById(narudzbaId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_ReturnOrderItems_When_OrderExists() {
        VrstaRublja vrstaRublja = new VrstaRublja();
        vrstaRublja.setNaziv("Kosulje");

        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setStavkaId(UUID.randomUUID());
        stavka.setKolicina(new BigDecimal("2.0"));
        stavka.setVrstaRublja(vrstaRublja);

        when(stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(narudzbaId)).thenReturn(List.of(stavka));

        List<StavkaNarudzbeDto> result = narudzbaService.findStavke(narudzbaId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVrstaRubljaNaziv()).isEqualTo("Kosulje");
    }

    @Test
    void should_SaveOrderWithStatusAndItems_When_ValidDtoProvided() {
        UUID vrstaRubljaId = UUID.randomUUID();

        StavkaPretragaDto stavkaDto = new StavkaPretragaDto();
        stavkaDto.setVrstaRubljaId(vrstaRubljaId);
        stavkaDto.setKolicina(new BigDecimal("2.0"));

        NarudzbaFormDto dto = new NarudzbaFormDto();
        dto.setKorisnickoIme("ivana.kovac");
        dto.setObrtIban("HR123");
        dto.setAdresaId(adresa.getAdresaId());
        dto.setTerminPrikupa(LocalDate.now().plusDays(5));
        dto.setStavke(List.of(stavkaDto));

        VrstaRublja vrstaRublja = new VrstaRublja();
        vrstaRublja.setVrstaRubljaId(vrstaRubljaId);
        vrstaRublja.setNaziv("Kosulje");

        Cjenik cjenik = new Cjenik();
        cjenik.setCijenaPoKg(new BigDecimal("10.00"));
        cjenik.setMultiplikatorNeradnogDana(new BigDecimal("1.5"));

        when(privatnaOsobaRepository.findByKorisnickoIme("ivana.kovac")).thenReturn(Optional.of(privatnaOsoba));
        when(obrtRepository.findByIban("HR123")).thenReturn(Optional.of(obrt));
        when(adresaRepository.findById(adresa.getAdresaId())).thenReturn(Optional.of(adresa));
        when(cjenikRepository.findByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(Optional.of(cjenik));
        when(vrstaRubljaRepository.findById(vrstaRubljaId)).thenReturn(Optional.of(vrstaRublja));
        when(narudzbaRepository.save(any())).thenReturn(narudzba);
        when(stavkaNarudzbeRepository.save(any())).thenReturn(new StavkaNarudzbe());
        when(statusRepository.save(any())).thenReturn(new Status());

        UUID result = narudzbaService.save(dto);

        assertThat(result).isEqualTo(narudzbaId);
        verify(narudzbaRepository).save(any());
        verify(stavkaNarudzbeRepository).save(any());
        verify(statusRepository).save(argThat(s -> s.getNazivStatusa().equals(StatusNarudzbe.ZAPRIMLJENO.getNaziv())
        ));
    }

    @Test
    void should_UpdateOrder_When_ValidDtoProvided() {
        NarudzbaFormDto dto = new NarudzbaFormDto();
        dto.setKorisnickoIme("ivana.kovac");
        dto.setObrtIban("HR123");
        dto.setAdresaId(adresa.getAdresaId());
        dto.setTerminPrikupa(LocalDate.now().plusDays(7));

        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.of(narudzba));
        when(privatnaOsobaRepository.findByKorisnickoIme("ivana.kovac")).thenReturn(Optional.of(privatnaOsoba));
        when(obrtRepository.findByIban("HR123")).thenReturn(Optional.of(obrt));
        when(adresaRepository.findById(adresa.getAdresaId())).thenReturn(Optional.of(adresa));
        when(narudzbaRepository.save(any())).thenReturn(narudzba);

        narudzbaService.update(narudzbaId, dto);

        verify(narudzbaRepository).save(argThat(n -> n.getTerminPrikupa().equals(LocalDate.now().plusDays(7))
        ));
    }

    @Test
    void should_ThrowException_When_UpdatingNonExistentOrder() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> narudzbaService.update(narudzbaId, new NarudzbaFormDto())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_DeleteOrder_When_OrderExists() {
        when(narudzbaRepository.existsById(narudzbaId)).thenReturn(true);
        doNothing().when(narudzbaRepository).deleteById(narudzbaId);

        narudzbaService.delete(narudzbaId);

        verify(narudzbaRepository).deleteById(narudzbaId);
    }

    @Test
    void should_ThrowException_When_DeletingNonExistentOrder() {
        when(narudzbaRepository.existsById(narudzbaId)).thenReturn(false);

        assertThatThrownBy(() -> narudzbaService.delete(narudzbaId)).isInstanceOf(EntityNotFoundException.class);

        verify(narudzbaRepository, never()).deleteById(any());
    }

    @Test
    void should_ReturnAvailableObrti_When_PriceListExists() {
        UUID vrstaRubljaId = UUID.randomUUID();

        StavkaPretragaDto stavka = new StavkaPretragaDto();
        stavka.setVrstaRubljaId(vrstaRubljaId);
        stavka.setKolicina(new BigDecimal("2.0"));

        Cjenik cjenik = new Cjenik();
        cjenik.setCijenaPoKg(new BigDecimal("10.00"));
        cjenik.setMultiplikatorNeradnogDana(new BigDecimal("1.0"));

        when(obrtRepository.findAll()).thenReturn(List.of(obrt));
        when(cjenikRepository.findByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(Optional.of(cjenik));

        List<ObrtSCjenomDto> result = narudzbaService.pretraga(List.of(stavka), LocalDate.now().plusDays(3));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isDostupan()).isTrue();
        assertThat(result.get(0).getUkupniIznos()).isEqualByComparingTo("20.00");
    }

    @Test
    void should_ReturnUnavailableObrt_When_NoPriceListExists() {
        UUID vrstaRubljaId = UUID.randomUUID();

        StavkaPretragaDto stavka = new StavkaPretragaDto();
        stavka.setVrstaRubljaId(vrstaRubljaId);
        stavka.setKolicina(new BigDecimal("2.0"));

        when(obrtRepository.findAll()).thenReturn(List.of(obrt));
        when(cjenikRepository.findByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRubljaId)).thenReturn(Optional.empty());

        List<ObrtSCjenomDto> result = narudzbaService.pretraga(List.of(stavka), LocalDate.now().plusDays(3));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isDostupan()).isFalse();
    }

    @Test
    void should_ReturnStatusInList_When_OrderHasCurrentStatus() {
        Status status = new Status();
        status.setNazivStatusa("Zaprimljeno");
        status.setNarudzba(narudzba);

        when(narudzbaRepository.findAll()).thenReturn(List.of(narudzba));
        when(statusRepository.findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(narudzbaId)).thenReturn(List.of(status));

        List<NarudzbaListDto> result = narudzbaService.findAll(null);

        assertThat(result.get(0).getTrenutniStatus()).isEqualTo("Zaprimljeno");
    }

    @Test
    void should_ThrowException_When_SavingWithNonExistentUser() {
        NarudzbaFormDto dto = new NarudzbaFormDto();
        dto.setKorisnickoIme("nepostojeci");
        dto.setObrtIban("HR123");
        dto.setAdresaId(UUID.randomUUID());
        dto.setTerminPrikupa(LocalDate.now().plusDays(5));
        dto.setStavke(List.of());

        when(privatnaOsobaRepository.findByKorisnickoIme("nepostojeci")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> narudzbaService.save(dto)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_ThrowException_When_SavingWithNonExistentObrt() {
        NarudzbaFormDto dto = new NarudzbaFormDto();
        dto.setKorisnickoIme("ivana.kovac");
        dto.setObrtIban("NEPOSTOJECI");
        dto.setAdresaId(UUID.randomUUID());
        dto.setTerminPrikupa(LocalDate.now().plusDays(5));
        dto.setStavke(List.of());

        when(privatnaOsobaRepository.findByKorisnickoIme("ivana.kovac")).thenReturn(Optional.of(privatnaOsoba));
        when(obrtRepository.findByIban("NEPOSTOJECI")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> narudzbaService.save(dto)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_UpdateTotalAmount_When_IzracunajIsCalled() {
        VrstaRublja vrstaRublja = new VrstaRublja();
        vrstaRublja.setVrstaRubljaId(UUID.randomUUID());

        Cjenik cjenik = new Cjenik();
        cjenik.setCijenaPoKg(new BigDecimal("10.00"));
        cjenik.setMultiplikatorNeradnogDana(new BigDecimal("1.0"));

        StavkaNarudzbe stavka = new StavkaNarudzbe();
        stavka.setKolicina(new BigDecimal("3.0"));
        stavka.setVrstaRublja(vrstaRublja);
        stavka.setNarudzba(narudzba);

        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.of(narudzba));
        when(stavkaNarudzbeRepository.findByNarudzba_NarudzbaId(narudzbaId)).thenReturn(List.of(stavka));
        when(cjenikRepository.findByObrt_IbanAndVrstaRublja_VrstaRubljaId("HR123", vrstaRublja.getVrstaRubljaId())).thenReturn(Optional.of(cjenik));
        when(narudzbaRepository.save(any())).thenReturn(narudzba);

        narudzbaService.izracunajIAzurirajIznos(narudzbaId);

        verify(narudzbaRepository).save(argThat(n -> n.getUkupniIznos().compareTo(new BigDecimal("30.00")) == 0));
    }

    @Test
    void should_ThrowException_When_IzracunajCalledWithNonExistentOrder() {
        when(narudzbaRepository.findById(narudzbaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> narudzbaService.izracunajIAzurirajIznos(narudzbaId)).isInstanceOf(EntityNotFoundException.class);
    }
}
