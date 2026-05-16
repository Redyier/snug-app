package com.fer.backend.repository;

import com.fer.backend.model.VrstaRublja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VrstaRubljaRepository extends JpaRepository<VrstaRublja, UUID> {
    List<VrstaRublja> findByNazivContainingIgnoreCase(String naziv);

    boolean existsByNaziv(String košulje);
}
