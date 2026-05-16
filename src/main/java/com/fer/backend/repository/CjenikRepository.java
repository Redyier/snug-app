package com.fer.backend.repository;

import com.fer.backend.model.Cjenik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CjenikRepository extends JpaRepository<Cjenik, UUID> {
    boolean existsByObrt_IbanAndVrstaRublja_VrstaRubljaId(String iban, UUID vrstaRubljaId);

    Optional<Cjenik> findByObrt_IbanAndVrstaRublja_VrstaRubljaId(String iban, UUID vrstaRubljaId);
}
