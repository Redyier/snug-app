package com.fer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "KorisnikID")
public class PrivatnaOsoba extends Korisnik{

    @Column(name = "KorisnickoIme", nullable = false, unique = true, length = 50)
    private String korisnickoIme;

    @Column(name = "Ime", nullable = false, length = 50)
    private String ime;

    @Column(name = "Prezime", nullable = false, length = 50)
    private String prezime;

    @OneToMany(mappedBy = "privatnaOsoba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Narudzba> narudzbe;
}
