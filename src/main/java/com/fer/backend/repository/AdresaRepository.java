package com.fer.backend.repository;

import com.fer.backend.model.Adresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdresaRepository extends JpaRepository<Adresa, UUID> {
}
