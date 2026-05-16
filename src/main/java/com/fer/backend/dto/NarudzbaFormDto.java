package com.fer.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NarudzbaFormDto {
    private UUID narudzbaId;

    private LocalDate datumNarucivanja;

    private LocalDate terminPrikupa;

    private BigDecimal ukupniIznos;

    @NotNull(message = "Korisnik je obavezan.")
    private String korisnickoIme;

    private String obrtIban;

    @NotNull(message = "Adresa je obavezna.")
    private UUID adresaId;

    @NotEmpty(message = "Morate dodati barem jednu stavku.")
    private List<StavkaPretragaDto> stavke;
}