package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recenzija {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "RecenzijaID", updatable = false, nullable = false)
    private UUID recenzijaId;

    @Column(name = "Ocjena", nullable = false)
    private Short ocjena;

    @Column(name = "Komentar", nullable = false, columnDefinition = "TEXT")
    private String komentar;

    @Column(name = "DatumRecenzije", nullable = false)
    private LocalDate datumRecenzije;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NarudzbaID", nullable = false, unique = true)
    private Narudzba narudzba;
}
