package com.fer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VrstaRubljaDto {

    private UUID vrstaRubljaId;

    @NotBlank(message = "Naziv je obavezan.")
    @Size(max = 50, message = "Naziv ne smije biti duži od 50 znakova.")
    private String naziv;
}