package com.fer.backend.repository;

import com.fer.backend.model.StavkaNarudzbe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StavkaNarudzbeRepository extends JpaRepository<StavkaNarudzbe, UUID> {
    List<StavkaNarudzbe> findByNarudzba_NarudzbaId(UUID narudzbaId);
}
