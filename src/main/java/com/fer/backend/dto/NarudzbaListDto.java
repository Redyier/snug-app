package com.fer.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NarudzbaListDto {

    private UUID narudzbaId;
    private LocalDate datumNarucivanja;
    private LocalDate terminPrikupa;
    private BigDecimal ukupniIznos;
    private String korisnickoIme;
    private String obrtNaziv;
    private String trenutniStatus;

}