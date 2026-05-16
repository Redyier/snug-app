package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Narudzba {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "NarudzbaID", updatable = false, nullable = false)
    private UUID narudzbaId;

    @Column(name = "DatumNarucivanja", nullable = false)
    private LocalDate datumNarucivanja;

    @Column(name = "UkupniIznos", nullable = false, precision = 10, scale = 2)
    private BigDecimal ukupniIznos;

    @Column(name = "TerminPrikupa", nullable = false)
    private LocalDate terminPrikupa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KorisnickoIme", referencedColumnName = "KorisnickoIme", nullable = false)
    private PrivatnaOsoba privatnaOsoba;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IBAN", referencedColumnName = "IBAN", nullable = false)
    private Obrt obrt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AdresaID", nullable = false)
    private Adresa adresa;

    @OneToMany(mappedBy = "narudzba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StavkaNarudzbe> stavke;

    @OneToMany(mappedBy = "narudzba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Status> statusi;

    @OneToOne(mappedBy = "narudzba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recenzija recenzija;
}
