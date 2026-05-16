package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "KorisnikID")
public class Obrt extends Korisnik{

    @Column(name = "IBAN", nullable = false, unique = true, columnDefinition = "bpchar(34)")
    private String iban;

    @Column(name = "Naziv", nullable = false, length = 100)
    private String naziv;

    @OneToMany(mappedBy = "obrt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cjenik> cjenici;

    @OneToMany(mappedBy = "obrt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Narudzba> narudzbe;
}
