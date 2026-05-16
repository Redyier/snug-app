package com.fer.backend.repository;

import com.fer.backend.model.Obrt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObrtRepository extends JpaRepository<Obrt, UUID> {
    Optional<Obrt> findByIban(String iban);
}
