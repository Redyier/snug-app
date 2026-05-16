package com.fer.backend.repository;

import com.fer.backend.model.Narudzba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NarudzbaRepository extends JpaRepository<Narudzba, UUID> {
    List<Narudzba> findByPrivatnaOsoba_KorisnickoImeContainingIgnoreCase(String search);
}
