package com.fer.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StavkaNarudzbeFormDto {
    private UUID stavkaId;

    @NotNull(message = "Vrsta rublja je obavezna.")
    private UUID vrstaRubljaId;

    @NotNull(message = "Količina je obavezna.")
    @DecimalMin(value = "0.1", message = "Količina mora biti veća od 0.")
    private BigDecimal kolicina;
}