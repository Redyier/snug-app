package com.fer.backend.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObrtSCjenomDto {
    private String iban;
    private String naziv;
    private BigDecimal ukupniIznos;
    private boolean dostupan;
    private String razlogNedostupnosti;
}