package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "StatusID", updatable = false, nullable = false)
    private UUID statusId;

    @Column(name = "NazivStatusa", nullable = false, length = 50)
    private String nazivStatusa;

    @Column(name = "VrijemeAzuriranja", nullable = false)
    private LocalDateTime vrijemeAzuriranja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NarudzbaID", nullable = false)
    private Narudzba narudzba;
}
