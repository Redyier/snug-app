package com.fer.backend.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivatnaOsobaDto {

    private UUID korisnikId;
    private String korisnickoIme;
    private String ime;
    private String prezime;

}
