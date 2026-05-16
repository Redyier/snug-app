package com.fer.backend.repository;

import com.fer.backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {
    List<Status> findByNarudzba_NarudzbaIdOrderByVrijemeAzuriranjaDesc(UUID narudzbaId);
}
