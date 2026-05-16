package com.fer.backend.repository;

import com.fer.backend.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, UUID> {
}
