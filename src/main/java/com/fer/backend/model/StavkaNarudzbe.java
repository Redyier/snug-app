package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StavkaNarudzbe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "StavkaID", updatable = false, nullable = false)
    private UUID stavkaId;

    @Column(name = "Kolicina", nullable = false, precision = 6, scale = 2)
    private BigDecimal kolicina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NarudzbaID", nullable = false)
    private Narudzba narudzba;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VrstaRubljaID", nullable = false)
    private VrstaRublja vrstaRublja;
}
