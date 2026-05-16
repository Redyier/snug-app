package com.fer.backend.repository;

import com.fer.backend.model.PrivatnaOsoba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrivatnaOsobaRepository extends JpaRepository<PrivatnaOsoba, UUID> {
    Optional<PrivatnaOsoba> findByKorisnickoIme(String korisnickoIme);
}
