package com.fer.backend.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdresaDto {

    private UUID adresaId;
    private String ulica;
    private String kucniBroj;
    private String grad;
    private String postanskiBroj;

    public String getPunaAdresa() {
        return ulica + " " + kucniBroj + ", " + grad;
    }
}