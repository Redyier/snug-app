package com.fer.backend.repository;

import com.fer.backend.model.VrstaRublja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class VrstaRubljaRepositoryTest {

    @Autowired
    private VrstaRubljaRepository vrstaRubljaRepository;

    @BeforeEach
    void setUp() {
        vrstaRubljaRepository.deleteAll();

        vrstaRubljaRepository.save(VrstaRublja.builder().naziv("Košulje").build());
        vrstaRubljaRepository.save(VrstaRublja.builder().naziv("Hlače").build());
        vrstaRubljaRepository.save(VrstaRublja.builder().naziv("Posteljina").build());
    }

    @Test
    void findAll_trebaNaciSveVrste() {
        List<VrstaRublja> vrste = vrstaRubljaRepository.findAll();
        assertThat(vrste).hasSize(3);
    }

    @Test
    void findByNazivContainingIgnoreCase_trebaNaciPoDijelu() {
        List<VrstaRublja> rezultat = vrstaRubljaRepository.findByNazivContainingIgnoreCase("košu");

        assertThat(rezultat).hasSize(1);
        assertThat(rezultat.get(0).getNaziv()).isEqualTo("Košulje");
    }

    @Test
    void findByNazivContainingIgnoreCase_bezObziraNaVelicinaSlovă() {
        List<VrstaRublja> rezultat = vrstaRubljaRepository.findByNazivContainingIgnoreCase("HLAČE");

        assertThat(rezultat).hasSize(1);
        assertThat(rezultat.get(0).getNaziv()).isEqualTo("Hlače");
    }

    @Test
    void existsByNaziv_sPostojecimNazivom_trebaVratitiTrue() {
        boolean postoji = vrstaRubljaRepository.existsByNaziv("Košulje");
        assertThat(postoji).isTrue();
    }

    @Test
    void existsByNaziv_sNepostojecimNazivom_trebaVratitiTrue() {
        boolean postoji = vrstaRubljaRepository.existsByNaziv("Nepostojece");
        assertThat(postoji).isFalse();
    }

    @Test
    void save_trebaSpremitiNovuVrstu() {
        VrstaRublja nova = VrstaRublja.builder().naziv("Džemperi").build();
        VrstaRublja spremljena = vrstaRubljaRepository.save(nova);

        assertThat(spremljena.getVrstaRubljaId()).isNotNull();
        assertThat(vrstaRubljaRepository.findAll()).hasSize(4);
    }

    @Test
    void delete_trebaBrisatiVrstu() {
        VrstaRublja vrsta = vrstaRubljaRepository.findByNazivContainingIgnoreCase("Košulje").get(0);

        vrstaRubljaRepository.deleteById(vrsta.getVrstaRubljaId());

        Optional<VrstaRublja> obrisana = vrstaRubljaRepository.findById(vrsta.getVrstaRubljaId());
        assertThat(obrisana).isEmpty();
    }
}
