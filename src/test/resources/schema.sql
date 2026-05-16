CREATE TABLE IF NOT EXISTS KORISNIK
(
    DatumRegistracije DATE         NOT NULL,
    KorisnikID        UUID         NOT NULL,
    Email             VARCHAR(100) NOT NULL,
    Lozinka           VARCHAR(50)  NOT NULL,
    Telefon           VARCHAR(20)  NOT NULL,
    PRIMARY KEY (KorisnikID),
    UNIQUE (Email)
);

CREATE TABLE IF NOT EXISTS PRIVATNAOSOBA
(
    Ime           VARCHAR(50) NOT NULL,
    Prezime       VARCHAR(50) NOT NULL,
    KorisnickoIme VARCHAR(50) NOT NULL,
    KorisnikID    UUID        NOT NULL,
    PRIMARY KEY (KorisnikID),
    UNIQUE (KorisnickoIme),
    FOREIGN KEY (KorisnikID) REFERENCES KORISNIK(KorisnikID)
);

CREATE TABLE IF NOT EXISTS OBRT
(
    IBAN       CHAR(34)     NOT NULL,
    Naziv      VARCHAR(100) NOT NULL,
    KorisnikID UUID         NOT NULL,
    PRIMARY KEY (KorisnikID),
    UNIQUE (IBAN),
    FOREIGN KEY (KorisnikID) REFERENCES KORISNIK(KorisnikID)
);

CREATE TABLE IF NOT EXISTS ADRESA
(
    AdresaID      UUID        NOT NULL,
    Ulica         VARCHAR(50) NOT NULL,
    KucniBroj     VARCHAR(10) NOT NULL,
    Grad          VARCHAR(30) NOT NULL,
    PostanskiBroj CHAR(5)     NOT NULL,
    KorisnikID    UUID        NOT NULL,
    PRIMARY KEY (AdresaID),
    FOREIGN KEY (KorisnikID) REFERENCES KORISNIK(KorisnikID)
);

CREATE TABLE IF NOT EXISTS VRSTARUBLJA
(
    VrstaRubljaID UUID        NOT NULL,
    Naziv         VARCHAR(50) NOT NULL,
    PRIMARY KEY (VrstaRubljaID)
);

CREATE TABLE IF NOT EXISTS CJENIK
(
    CjenikID                  UUID         NOT NULL,
    CijenaPoKg                NUMERIC(8,2) NOT NULL,
    MultiplikatorNeradnogDana NUMERIC(4,2) NOT NULL,
    IBAN                      CHAR(34)     NOT NULL,
    VrstaRubljaID             UUID         NOT NULL,
    PRIMARY KEY (CjenikID),
    FOREIGN KEY (IBAN)          REFERENCES OBRT(IBAN),
    FOREIGN KEY (VrstaRubljaID) REFERENCES VRSTARUBLJA(VrstaRubljaID)
);

CREATE TABLE IF NOT EXISTS NARUDZBA
(
    NarudzbaID       UUID          NOT NULL,
    DatumNarucivanja DATE          NOT NULL,
    UkupniIznos      NUMERIC(10,2) NOT NULL,
    TerminPrikupa    DATE          NOT NULL,
    KorisnickoIme    VARCHAR(50)   NOT NULL,
    IBAN             CHAR(34)      NOT NULL,
    AdresaID         UUID          NOT NULL,
    PRIMARY KEY (NarudzbaID),
    FOREIGN KEY (KorisnickoIme) REFERENCES PRIVATNAOSOBA(KorisnickoIme),
    FOREIGN KEY (IBAN)          REFERENCES OBRT(IBAN),
    FOREIGN KEY (AdresaID)      REFERENCES ADRESA(AdresaID)
);

CREATE TABLE IF NOT EXISTS STAVKANARUDZBE
(
    StavkaID      UUID         NOT NULL,
    Kolicina      NUMERIC(6,2) NOT NULL,
    NarudzbaID    UUID         NOT NULL,
    VrstaRubljaID UUID         NOT NULL,
    PRIMARY KEY (StavkaID),
    FOREIGN KEY (NarudzbaID)    REFERENCES NARUDZBA(NarudzbaID),
    FOREIGN KEY (VrstaRubljaID) REFERENCES VRSTARUBLJA(VrstaRubljaID)
);

CREATE TABLE IF NOT EXISTS STATUS
(
    StatusID          UUID        NOT NULL,
    NazivStatusa      VARCHAR(50) NOT NULL,
    VrijemeAzuriranja TIMESTAMP   NOT NULL,
    NarudzbaID        UUID        NOT NULL,
    PRIMARY KEY (StatusID),
    FOREIGN KEY (NarudzbaID) REFERENCES NARUDZBA(NarudzbaID)
);

CREATE TABLE IF NOT EXISTS RECENZIJA
(
    RecenzijaID    UUID     NOT NULL,
    Ocjena         SMALLINT NOT NULL CHECK (Ocjena BETWEEN 1 AND 5),
    Komentar       TEXT     NOT NULL,
    DatumRecenzije DATE     NOT NULL,
    NarudzbaID     UUID     NOT NULL,
    PRIMARY KEY (RecenzijaID),
    UNIQUE (NarudzbaID),
    FOREIGN KEY (NarudzbaID) REFERENCES NARUDZBA(NarudzbaID)
);