package com.fer.backend.repository;

import com.fer.backend.model.Recenzija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecenzijaRepository extends JpaRepository<Recenzija, UUID> {
}
