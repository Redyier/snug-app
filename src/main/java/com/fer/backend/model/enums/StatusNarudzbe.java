package com.fer.backend.model.enums;

import java.util.List;

public enum StatusNarudzbe {
    ZAPRIMLJENO("Zaprimljeno"),
    U_OBRADI("U obradi"),
    GLACANJE_U_TIJEKU("Glačanje u tijeku"),
    ZAVRSENO("Završeno"),
    DOSTAVA_U_TIJEKU("Dostava u tijeku"),
    ISPORUCENO("Isporučeno"),
    OTKAZANO("Otkazano"),
    PRIHVACENO("Prihvaćeno"),
    ODBIJENO("Odbijeno");

    private final String naziv;

    StatusNarudzbe(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public List<StatusNarudzbe> getMogucaSljedeciStatusi() {
        return switch (this) {
            case ZAPRIMLJENO -> List.of(PRIHVACENO, ODBIJENO);
            case PRIHVACENO -> List.of(U_OBRADI, OTKAZANO);
            case U_OBRADI -> List.of(GLACANJE_U_TIJEKU, OTKAZANO);
            case GLACANJE_U_TIJEKU -> List.of(ZAVRSENO);
            case ZAVRSENO -> List.of(DOSTAVA_U_TIJEKU);
            case DOSTAVA_U_TIJEKU -> List.of(ISPORUCENO);
            case ODBIJENO, ISPORUCENO, OTKAZANO -> List.of();
        };
    }

    public static StatusNarudzbe fromNaziv(String naziv) {
        for (StatusNarudzbe s : values()) {
            if (s.naziv.equals(naziv)) {
                return s;
            }
        }
        return null;
    }
}