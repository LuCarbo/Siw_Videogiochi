# Game Vault

Progetto per l'esame di Sistemi Informativi su Web (SIW) - Università degli Studi Roma Tre.  
Applicazione web per la consultazione e gestione di un catalogo videogiochi, libreria personale e recensioni della community.

## Requisiti

- Java 21
- PostgreSQL (database locale denominato `videogiochi`, porta 5432)

## Avvio dell'applicazione

### Con Maven
Assicurarsi che PostgreSQL sia in esecuzione (credenziali predefinite: `postgres`/`postgres` o configurabili nel file `.env`), quindi avviare con:

```bash
./mvnw spring-boot:run
```

L'applicazione sarà raggiungibile su `http://localhost:8080`.

### Con Docker (alternativa)
Se si preferisce usare Docker (avvia sia PostgreSQL che l'applicazione):

```bash
docker compose up
```

## Account di prova

All'avvio vengono creati automaticamente i seguenti account dimostrativi:

- **Admin**: `admin` / `admin123`
- **User**: `mario` / `password123`

## Test

Per eseguire la suite di test:

```bash
./mvnw test
```
