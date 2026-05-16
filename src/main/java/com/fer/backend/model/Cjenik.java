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
public class Cjenik {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CjenikID", updatable = false, nullable = false)
    private UUID cjenikId;

    @Column(name = "CijenaPoKg", nullable = false, precision = 8, scale = 2)
    private BigDecimal cijenaPoKg;

    @Column(name = "MultiplikatorNeradnogDana", nullable = false, precision = 4, scale = 2)
    private BigDecimal multiplikatorNeradnogDana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IBAN", nullable = false, referencedColumnName = "IBAN")
    private Obrt obrt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VrstaRubljaID", nullable = false)
    private VrstaRublja vrstaRublja;
}
