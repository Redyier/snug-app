package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "KorisnikID", updatable = false, nullable = false)
    private UUID korisnikId;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "Lozinka", nullable = false, length = 50)
    private String lozinka;

    @Column(name = "Telefon", nullable = false, length = 20)
    private String telefon;

    @Column(name = "DatumRegistracije", nullable = false)
    private LocalDate datumRegistracije;

    @OneToMany(mappedBy = "korisnik", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Adresa> adrese;
}
