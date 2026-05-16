package com.fer.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StavkaNarudzbeDto {
    private UUID stavkaId;
    private String vrstaRubljaNaziv;
    private BigDecimal kolicina;
}