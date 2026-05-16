package com.fer.backend.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObrtDto {

    private UUID korisnikId;
    private String iban;
    private String naziv;

}