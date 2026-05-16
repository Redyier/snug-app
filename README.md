# SNUG — Sustav naručivanja usluga glačanja

## Pokretanje aplikacije

### Preduvjeti

- [Docker](https://www.docker.com/products/docker-desktop) instaliran i pokrenut Docker Desktop

### Pokretanje

```bash
docker-compose up --build
```

Aplikacija će biti dostupna na [http://localhost:8080](http://localhost:8080).

PostgreSQL baza podataka automatski se kreira i popunjava testnim podacima prilikom prvog pokretanja.

### Zaustavljanje

```bash
docker-compose down
```

Za brisanje baze podataka zajedno s podacima:

```bash
docker-compose down -v
```

---

## Pokretanje bez Dockera

### Preduvjeti

- Java 26
- PostgreSQL 17
- Gradle

### Konfiguracija baze podataka

Kreirajte bazu podataka i korisnika u PostgreSQL-u:

```sql
CREATE DATABASE snug_db;
CREATE USER snug_user WITH PASSWORD 'snug_password';
GRANT ALL PRIVILEGES ON DATABASE snug_db TO snug_user;
```

Pokrenite SQL skriptu za kreiranje tablica i unos testnih podataka:

```bash
psql -U snug_user -d snug_db -f src/main/resources/db/init.sql
```

### Pokretanje aplikacije

```bash
./gradlew bootRun
```

Aplikacija će biti dostupna na [http://localhost:8080](http://localhost:8080).

---

## Pokretanje testova

```bash
./gradlew test
```

Za pokretanje testova s izvještajem o pokrivenosti koda otvorite projekt u IntelliJ IDEA i pokrenite testove s opcijom **Run with Coverage**.

---

## Tehnologije

- Java 26
- Spring Boot 4
- Spring MVC + Thymeleaf
- Spring Data JPA + Hibernate
- PostgreSQL
- Docker
- JUnit 5 + Mockito
- H2 (testna baza)