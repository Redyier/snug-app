package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VrstaRublja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "VrstaRubljaID", updatable = false, nullable = false)
    private UUID vrstaRubljaId;

    @Column(name = "Naziv", nullable = false, length = 50)
    private String naziv;

    @OneToMany(mappedBy = "vrstaRublja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cjenik> cjenici;

    @OneToMany(mappedBy = "vrstaRublja", fetch = FetchType.LAZY)
    private List<StavkaNarudzbe> stavke;
}
