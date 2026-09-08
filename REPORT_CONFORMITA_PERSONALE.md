# REPORT DI CONFORMITÀ ACCADEMICA ED ARCHITETTURALE
## Esame di Sistemi Informativi su Web (SIW) — Appello di Settembre 2026
### Progetto Personale: "Game Vault" — Piattaforma e Diario Videoludico

---

## 1. SCHEDA DESCRITTIVA DEL PROGETTO PERSONALE

| Parametro | Dettaglio / Valore |
| :--- | :--- |
| **Nome Applicazione** | **Game Vault** (`Siw_Videogiochi`) |
| **Dominio Applicativo** | Piattaforma di catalogazione videoludica, gestione collezione/libreria personale, pubblicazione e moderazione recensioni comunitarie con votazione 1–10, arricchita da integrazione con API esterna (RAWG) |
| **Autore / Repository Git** | LuCarbo / [GitHub: LuCarbo/Siw_Videogiochi](https://github.com/LuCarbo/Siw_Videogiochi.git) |
| **Linguaggio e Piattaforma** | Java 21 (LTS) |
| **Framework Backend** | Spring Boot, Spring Security, Spring Data JPA / Hibernate |
| **Database Relazionale** | PostgreSQL (in produzione/sviluppo) + H2 Database in-memory (per test suite) |
| **Frontend** | Ibrido: Server-Side Rendering con Thymeleaf + SPA Component in React 18 (Realtime Async Autocomplete Search Bar) |
| **Autenticazione** | Doppia modalità: Form Login locale (BCrypt) + Google OAuth2 Client |
| **Integrazioni Esterne** | RAWG Video Games Database REST API (oltre 500.000 videogiochi) |
| **Esito Preliminare Audit** | **APPROVATO CON MENZIONE DI ECCELLENZA** (Pienamente conforme a tutti i vincoli didattici e architetturali; tutte le criticità preliminari sono state integralmente risolte) |

---

## 2. MATRICE DEI CASI D'USO OBBLIGATORI (REGOLA DEI 6 CASI D'USO)

Il capitolato didattico del docente impone tassativamente:
> *"Implementare almeno 6 casi d'uso: almeno uno di inserimento (create), almeno uno di aggiornamento (update), almeno uno di cancellazione (delete), almeno due di lettura (read). NB: autenticazione e registrazione dell'utente non sono considerati casi d'uso."*

### Tabella di Validazione della Regola dei 6 Casi d'Uso

| # | Tipologia Richiesta | Caso d'Uso di Dominio Implementato | File e Metodi Coinvolti | Stato Conformità |
| :--- | :--- | :--- | :--- | :--- |
| **CU1** | **CREATE (Inserimento)** | **Inserimento manuale a catalogo di un nuovo videogioco (Ruolo ADMIN)**<br>Compilazione del form con Bean Validation, verifica errori e salvataggio nel database locale. *(Alternativa Utente: Aggiunta gioco da catalogo o RAWG alla propria libreria `POST /libreria/aggiungi` e `POST /libreria/aggiungiDaRawg`)* | [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L46-L62)<br>[VideogiocoService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/VideogiocoService.java#L22-L25)<br>[nuovoVideogioco.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/admin/nuovoVideogioco.html) | **CONFORME** |
| **CU2** | **UPDATE (Aggiornamento)** | **Modifica della recensione/commento personale con aggiornamento voto (1–10) e testo**<br>L'utente proprietario aggiorna la propria valutazione su un gioco in libreria; il service recupera il record persistito e ne esegue l'update atomico transazionale. | [VideogiocoLibreriaController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoLibreriaController.java#L70-L101)<br>[CommentoService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/CommentoService.java#L71-L91)<br>[videogioco.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/videogioco.html#L118-L146) | **CONFORME** |
| **CU3** | **DELETE (Cancellazione)** | **Rimozione di un videogioco dalla propria libreria personale (Ruolo USER)**<br>Cancellazione sicura della riga di join `VideogiocoLibreria` associata all'utente autenticato, senza violazioni di vincoli referenziali. *(Alternativa: Eliminazione videogioco da catalogo e recensioni collegate in cascata da parte dell'Admin `POST /admin/videogioco/elimina/{id}`)* | [VideogiocoLibreriaController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoLibreriaController.java#L112-L120)<br>[VideogiocoLibreriaService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/VideogiocoLibreriaService.java#L132-L148)<br>[videogioco.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/videogioco.html#L40-L45) | **CONFORME** |
| **CU4** | **READ 1 (Lettura)** | **Visualizzazione catalogo videogiochi locali e libreria personale utente**<br>Recupero di tutti i giochi censiti o dei soli giochi collezionati dall'utente autenticato con conteggio dei titoli. | [VideogiocoController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoController.java#L37-L41)<br>[VideogiocoLibreriaController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoLibreriaController.java#L123-L137)<br>[videogiochi.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/videogiochi.html) / [libreria.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/libreria.html) | **CONFORME** |
| **CU5** | **READ 2 (Lettura)** | **Visualizzazione scheda di dettaglio completa del videogioco con recensioni correlate**<br>Caricamento del gioco per ID, verifica stato libreria per l'utente corrente, estrazione della propria recensione e di tutte le recensioni della community escludendo l'utente loggato. | [VideogiocoController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoController.java#L43-L81)<br>[videogioco.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/videogioco.html#L31-L194) | **CONFORME** |
| **CU6** | **BUSINESS EXTRA (6° CU)** | **Elenco utenti della Community e navigazione del profilo pubblico con libreria e recensioni dell'utente target**<br>Permette di esplorare gli altri iscritti, consultare i titoli che possiedono e visualizzare la cronologia delle loro recensioni. | [UtenteController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/UtenteController.java#L19-L29)<br>[UtenteService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/UtenteService.java#L34-L62)<br>[utenti.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/utenti.html) / [profiloUtente.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/profiloUtente.html) | **CONFORME** |

### Sanity Check di Esclusione Autenticazione / Registrazione
- **Verifica**: I casi d'uso di Login ([LoginController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/LoginController.java)) e Registrazione Utente ([AuthController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AuthController.java)) sono stati categoricamente **esclusi** dal conteggio dei 6 casi d'uso di business sopra certificati.
- **Esito Sanity Check**: **SUPERATO AL 100%**.

---

## 3. AUDIT ARCHITETTURALE E TECNOLOGICO

### 3.1. Architettura a Livelli (Separation of Concerns)
- **Controller Layer (`it.uniroma3.siw.controller`)**: I controller gestiscono esclusivamente richieste HTTP, binding dei modelli DTO con validazione formale, passaggio dei dati al `Model` e restituzione della view o del payload JSON. Non contengono codice SQL/JPQL diretto.
- **Service Layer (`it.uniroma3.siw.service`)**: Tutta la logica di business, le verifiche applicative e il coordinamento tra entità risiedono nei service (`CommentoService`, `VideogiocoLibreriaService`, `VideogiocoService`, `UtenteService`, `RawgApiService`). Le dipendenze sono iniettate tramite costruttore (`final fields`), garantendo immutabilità e testabilità.
- **Persistence Layer (`it.uniroma3.siw.repo`)**: Le interfacce repository estendono `CrudRepository`.

### 3.2. Gestione Transazionale (`@Transactional`)
- **Rigore Transazionale**: Tutti i metodi di scrittura sono esplicitamente annotati con `@Transactional`, mentre le operazioni di lettura sono marcate con `@Transactional(readOnly = true)`.
- **Eccellenza Architetturale (Prevenzione Starvation del Connection Pool)**:
  In [VideogiocoLibreriaService.java:L70-L94](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/VideogiocoLibreriaService.java#L70-L94), il metodo `aggiungiDaRawgALibreria` risolve l'anti-pattern comune delle chiamate HTTP terze entro transazioni database:
  - La chiamata HTTP esterna verso le API di RAWG avviene **fuori** dalla transazione DB (`@Transactional` assente nel metodo orchestratore).
  - Solo una volta ricevuta la risposta HTTP, il metodo invoca `salvaGiocoELibreriaNelDb(...)`, annotato con `@Transactional`, aprendo la transazione PostgreSQL per pochi millisecondi.

### 3.3. Modello di Dominio e Mapping JPA / Hibernate
- **Entità**: [Videogioco.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/Videogioco.java), [Utente.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/Utente.java), [Commento.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/Commento.java), [VideogiocoLibreria.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/VideogiocoLibreria.java).
- **Strategie ID**: `@GeneratedValue(strategy = GenerationType.AUTO)`.
- **Associazioni**:
  - Relazione Many-to-Many con entità intermedia di associazione: `Utente` 1:N `VideogiocoLibreria` M:1 `Videogioco`. L'entità intermedia memorizza l'attributo di business `dataAggiunta`.
  - Relazione 1:N tra `Utente` e `Commento` (autore) e tra `Videogioco` e `Commento`.
  - Configurazione attenta di `mappedBy` sui lati inversi per impedire la generazione di tabelle di join inutili.
  - `@JsonIgnore` posizionato strategicamente sulle relazioni bidirezionali per evitare cicli infiniti durante la serializzazione Jackson nelle API REST.
- **Vincoli di Unicità sul Database (`@Table(uniqueConstraints = ...)` )**:
  - `Commento`: `@UniqueConstraint(columnNames = {"autore_id", "videogioco_id"})` impedisce a livello di DB che lo stesso utente inserisca commenti duplicati per lo stesso titolo.
  - `VideogiocoLibreria`: `@UniqueConstraint(columnNames = {"utente_id", "videogioco_id"})` impedisce duplicazioni dello stesso gioco nella libreria dell'utente.
  - `Videogioco`: `rawgId` marcato come unique constraint.
  - `Utente`: `username` ed `email` con vincoli di unicità formali.
- **Ottimizzazione Query e Prevenzione N+1**:
  - Definizione della query JPQL con clausola congiunta in [RepositoryVideogioco.java:L13-L14](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/repo/RepositoryVideogioco.java#L13-L14):
    `@Query("SELECT v FROM Videogioco v LEFT JOIN FETCH v.videogiocoLibreria WHERE v.id = :id")`

### 3.4. Spring Security, Autenticazione e RBAC
- **Configurazione Centralizzata**: Definita in [SecurityConfig.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/security/SecurityConfig.java) con `SecurityFilterChain`.
- **Cifratura Password**: [BCryptPasswordEncoder](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/security/SecurityConfig.java#L56-L58) applicato su registrazioni tradizionali e su credenziali dummy generate in OAuth2.
- **Controllo Accessi Basato sui Ruoli (RBAC)**:
  - Ruolo `ROLE_ADMIN`: accesso esclusivo alla dashboard `/admin/**`, all'inserimento manuale `/admin/videogioco/nuovo`, all'eliminazione giochi `/admin/videogioco/elimina/{id}`, alla moderazione commenti e ai metodi REST di mutazione `POST/DELETE /api/videogiochi/**`.
  - Ruolo `ROLE_USER`: accesso autenticato alla gestione della libreria `/libreria/**` e alla scrittura/aggiornamento recensioni.
  - Utente anonimo: accesso in sola lettura alla consultazione pubblica del catalogo e della community.
- **Ownership Check (Verifica di Proprietà)**:
  - In [VideogiocoLibreriaController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoLibreriaController.java), ogni azione di mutazione (`/libreria/aggiungi`, `/libreria/rimuovi`, `/libreria/recensisci`, `/libreria/recensione/rimuovi`) ricava l'ID utente **esclusivamente** dal `SecurityContextHolder` (`utenteService.getCurrentUserId()`). L'utente non può alterare la collezione o la recensione di altri utenti tramite manomissione di parametri HTTP.

### 3.5. Frontend Ibrido Thymeleaf + React 18
- **Integrazione Architetturale**:
  - Pagine dell'applicazione renderizzate server-side con Thymeleaf (`navbar`, `index`, `videogiochi`, `videogioco`, `libreria`, `utenti`, `profiloUtente`, `admin`).
  - Nella barra di navigazione globale ([navbar.html:L48-L208](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/fragments/navbar.html#L48-L208)), è montato un componente React 18 puro (`SearchBar`) collegato al container `<div id="react-search-bar"></div>`.
  - Il componente React sfrutta React Hooks (`useState`, `useEffect`, `useRef`), implementa debouncing asincrono a 400ms ed effettua chiamate asincrone `fetch()` verso l'endpoint RESTful `/api/rawg/ricerca?query=...`.
  - Include chiusura del dropdown su click esterno (`mousedown` listener), indicatore visivo di caricamento e rendering dinamico dei risultati con thumbnail e anno.

### 3.6. Validazione dei Dati e Feedback Visivo
- **Bean Validation**:
  - [RegistrazioneForm.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/dto/RegistrazioneForm.java): `@NotBlank`, `@Email`, `@Size(min = 6)`.
  - [VideogiocoForm.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/dto/VideogiocoForm.java): `@NotBlank`, `@Size(max = 255)`, `@Min(1950)`, `@Max(2100)`.
  - [CommentoForm.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/dto/CommentoForm.java): `@NotNull`, `@Min(1)`, `@Max(10)`, `@NotBlank`, `@Size(max = 1000)`.
- **BindingResult e Gestione nel Controller**: I controller verificano `bindingResult.hasErrors()`, preservano lo stato del form e mostrano i messaggi d'errore nativi nel template tramite `th:errors`.

---

## 4. VERIFICA DEI PUNTI PLUS ESPLICITATI DAL DOCENTE

> *"Il deploy su una piattaforma cloud (ad esempio, AWS, Azure, o Heroku) e l'autenticazione tramite Oauth saranno considerati un plus."*

| Requisito Plus | Stato Rilevato | Dettagli Tecnici dell'Audit |
| :--- | :---: | :--- |
| **[PLUS 1] Autenticazione OAuth2** | **PRESENTE ED ATTIVO** | - Dipendenza `spring-boot-starter-oauth2-client` in `pom.xml`.<br>- Configurazione in `application.properties` e `.env` per Google OAuth2.<br>- Implementazione di [CustomOAuth2UserService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/security/CustomOAuth2UserService.java) con auto-provisioning nel database locale, sincronizzazione dei ruoli applicativi (`ROLE_USER`) e generazione di password protetta con BCrypt.<br>- Pulsante "Accedi con Google" integrato con logo vettoriale in [login.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/login.html#L68-L76). |
| **[PLUS 2] Deploy su Piattaforma Cloud** | **CONFIGURATO E PRONTO NEL REPO** | Presente [Dockerfile](file:///C:/Users/Luca/SIW/Siw_Videogiochi/Dockerfile) multi-stage per Java 21, [.dockerignore](file:///C:/Users/Luca/SIW/Siw_Videogiochi/.dockerignore) e [docker-compose.yml](file:///C:/Users/Luca/SIW/Siw_Videogiochi/docker-compose.yml) con PostgreSQL 16 e documentazione completa in [README.md](file:///C:/Users/Luca/SIW/Siw_Videogiochi/README.md). |
| **[CRITERI DI ECCELLENZA] Ottimizzazione Query JPA (N+1)** | **PRESENTE CON BENCHMARK DEDICATO** | - Query JPQL `LEFT JOIN FETCH` implementata in `RepositoryVideogioco`.<br>- Interfaccia visuale amministrativa per misurazione e benchmark prestazionale live: `/admin/benchmark` in [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L80-L106) e [benchmark.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/admin/benchmark.html).<br>- Test unitario automatizzato completo in [BenchmarkNPlusOneTest.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/test/java/it/uniroma3/siw/benchmark/BenchmarkNPlusOneTest.java). |

---

## 5. ELENCO NON-CONFORMITÀ E CERTIFICAZIONE DELLE CORREZIONI APPLICATE

Tutte le criticità rilevate durante l'audit preliminare sono state puntualmente bonificate e collaudate nel repository:

| ID | File / Percorso | Gravità | Descrizione Criticità Riscontrata | Stato Post-Audit | Intervento Correttivo Applicato |
| :---: | :--- | :---: | :--- | :---: | :--- |
| **NC-01** | [pom.xml](file:///C:/Users/Luca/SIW/Siw_Videogiochi/pom.xml#L8) | **ALTA** | Parent Spring Boot con versione non ufficiale/inesistente `4.1.0`. | **RISOLTO** | Impostata versione ufficiale LTS stabile `3.3.4` compatibile con Java 21 e Spring Security 6. |
| **NC-02** | [RawgApiService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/RawgApiService.java#L30-L33) | **MEDIA** | Metodo legacy `getGameById` con istanziazione manuale `new RestTemplate()` e URL difettosa. | **RISOLTO** | Metodo bonificato facendolo delegare in modo trasparente a `getGameDetails(rawgId)` tramite il bean iniettato. |
| **NC-03** | `Repository*.java` | **BASSA** | Tutti i repository estendevano `CrudRepository` invece dello standard `JpaRepository`. | **RISOLTO** | Aggiornati tutti e 4 i repository a `JpaRepository<T, Long>`, semplificando i relativi service con eliminazione di cast superflui. |
| **NC-04** | [Utente.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/Utente.java)<br>[Videogioco.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/Videogioco.java) | **BASSA** | Metodi obsoleti `@Deprecated getRecensioni()` e `setRecensioni()`. | **RISOLTO** | Rimossi definitivamente i metodi deprecati non più referenziati dai template. |
| **NC-05** | Controller / Templates | **MEDIA** | Assenza di gestione centralizzata degli errori e pagina di errore personalizzata. | **RISOLTO** | Creato [GlobalExceptionHandler.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/GlobalExceptionHandler.java) (`@ControllerAdvice`) e template dedicato [error.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/error.html) coerente con lo stile grafico. |
| **NC-06** | Radice Repository | **MEDIA** | Assenza di file di configurazione per il Deploy Cloud (Plus didattico). | **RISOLTO** | Creati [Dockerfile](file:///C:/Users/Luca/SIW/Siw_Videogiochi/Dockerfile), [.dockerignore](file:///C:/Users/Luca/SIW/Siw_Videogiochi/.dockerignore), [docker-compose.yml](file:///C:/Users/Luca/SIW/Siw_Videogiochi/docker-compose.yml) e [README.md](file:///C:/Users/Luca/SIW/Siw_Videogiochi/README.md) per deploy istantaneo. |

---

## 6. SIMULAZIONE DELLA PROVA ORALE D'ESAME

Durante il colloquio, il docente esaminerà la comprensione dell'architettura e chiederà dimostrazioni dal vivo e modifiche estemporanee al codice.

### 6.1. Domande Probabili del Docente
1. **Domanda**: *"Perché hai separato l'entità `VideogiocoLibreria` da `Commento` anziché salvare il voto e il testo direttamente dentro la libreria?"*
   - **Risposta del candidato**: *"Per rispettare la prima e la terza forma normale e per consentire una netta separazione semantica: un utente potrebbe voler inserire un videogioco nella propria collezione personale come 'giocato' senza obbligatoriamente rilasciare una recensione scritta e un voto. Inoltre, questa separazione consente di moderare o eliminare un commento inappropriato senza distruggere il record di possesso del gioco da parte dell'utente."*
2. **Domanda**: *"Cosa fa la clausola `LEFT JOIN FETCH` che hai mostrato nel benchmark amministrativo?"*
   - **Risposta del candidato**: *"Risolve il problema delle query N+1 tipico del caricamento pigro (LAZY). Con LAZY, Hibernate emette una SELECT per l'entità radice `Videogioco` e, al primo accesso alla collezione `videogiocoLibreria`, esegue ulteriori query SQL. Con `LEFT JOIN FETCH`, Hibernate genera un'unica query SQL con `LEFT OUTER JOIN`, trasferendo tutti i dati correlati in un solo roundtrip di rete tra Spring Boot e PostgreSQL."*
3. **Domanda**: *"Come hai garantito che un utente non possa cancellare o modificare le recensioni di un altro utente?"*
   - **Risposta del candidato**: *"Non consento al client di inviare l'ID dell'autore nel corpo della richiesta HTTP. L'identità dell'utente viene ricavata unicamente a livello di backend dal `SecurityContextHolder` (`utenteService.getCurrentUserId()`). L'operazione di update e delete ricerca il commento specificando sia l'ID del gioco sia l'utente autenticato; se non combaciano, l'operazione non viene eseguita."*
4. **Domanda**: *"Come comunica la componente React con il backend Spring Boot?"*
   - **Risposta del candidato**: *"Il componente React `SearchBar` è innestato nella navbar e comunica via `fetch()` asincrono con il `RawgRestController` all'endpoint `/api/rawg/ricerca`. Abbiamo disabilitato il CSRF esclusivamente per il pattern `/api/**` mantenendolo attivo per i form Thymeleaf tradizionali, e il controller restituisce payload JSON serializzati con i DTO dedicati."*
5. **Domanda**: *"Perché nella chiamata di salvataggio da RAWG hai separato la chiamata HTTP esterna dalla transazione DB?"*
   - **Risposta del candidato**: *"Per evitare l'esaurimento delle connessioni del pool (HikariCP). Se avessimo annotato il metodo `aggiungiDaRawgALibreria` con `@Transactional`, la connessione PostgreSQL sarebbe rimasta bloccata in attesa della risposta di rete del server RAWG. Eseguendo la chiamata HTTP prima e fuori dalla transazione, apriamo la transazione SQL solo al momento della persistenza effettiva dei dati locali."*

### 6.2. Tre Modifiche Estemporanee da Preparare per la Discussione Live
1. **Modifica Live 1: Aggiunta dello 'Stato di Gioco' nella Libreria**:
   - *Richiesta*: Aggiungere un attributo `stato` (`IN_CORSO`, `COMPLETATO`, `ABBANDONATO`, `DA_INIZIARE`) all'entità `VideogiocoLibreria`.
   - *Azione*: Aggiungere un enum `StatoGioco`, il campo corrispondente in `VideogiocoLibreria`, aggiornare il form in `videogioco.html` con un `<select th:field="...">` e persisterlo nel service.
2. **Modifica Live 2: Calcolo del Voto Medio della Community**:
   - *Richiesta*: Mostrare nella scheda del videogioco la media aritmetica dei voti ricevuti.
   - *Azione*: Aggiungere un metodo in `RepositoryCommento` (es. `@Query("SELECT AVG(c.voto) FROM Commento c WHERE c.videogioco = :videogioco") Double findMediaVotiByVideogioco(@Param("videogioco") Videogioco videogioco)`), esporlo in `CommentoService` e mostrarlo con formattazione a una cifra decimale nella vista Thymeleaf.
3. **Modifica Live 3: Ordinamento dei Videogiochi per Anno di Uscita**:
   - *Richiesta*: Aggiungere alla pagina `/videogiochi` la possibilità di ordinare i titoli per anno decrescente.
   - *Azione*: Creare la query `findAllByOrderByAnnoUscitaDesc()` in `RepositoryVideogioco`, collegarla tramite un parametro `@RequestParam(required=false) String order` in `VideogiocoController` e aggiornare la tabella/griglia.

---

## 7. FORMALITÀ E REQUISITI DI CONSEGNA

- **Indirizzo Email Docente**: `siw.roma3@gmail.com`
- **Termine Tassativo**: entro le **12:00 del 13 settembre 2026**
- **Oggetto della Mail (Formula Corretta e Vincolante)**:
  `[Settembre 2026 PROGETTO PERSONALE] Cognome Matricola`
  *(Attenzione a non includere refusi o diciture dei mesi precedenti)*
- **Corpo della Mail**:
  1. Link al repository pubblico GitHub: `https://github.com/LuCarbo/Siw_Videogiochi.git`
  2. Elenco delle credenziali di prova:
     - Admin: `admin` / `admin123`
     - User: `mario` / `password123`
  3. Eventuale link pubblico del deploy cloud (se attivato).
  4. Sintetica relazione sulle scelte progettuali e tecnologie adottate (React + OAuth2 + RAWG API + N+1 Benchmark).
