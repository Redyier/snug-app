package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AdresaID", updatable = false, nullable = false)
    private UUID adresaId;

    @Column(name = "Ulica", nullable = false, length = 50)
    private String ulica;

    @Column(name = "KucniBroj", nullable = false, length = 10)
    private String kucniBroj;

    @Column(name = "Grad", nullable = false, length = 30)
    private String grad;

    @Column(name = "PostanskiBroj", nullable = false, columnDefinition = "bpchar(5)")
    private String postanskiBroj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KorisnikID", nullable = false)
    private Korisnik korisnik;
}
