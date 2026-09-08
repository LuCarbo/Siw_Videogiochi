# CATALOGO ANALITICO DELLE FUNZIONALITÀ EXTRA E SCHEDA DI RISCHIO
## Esame di Sistemi Informativi su Web (SIW) — Appello di Settembre 2026
### Progetto Personale: "Game Vault" — Piattaforma e Diario Videoludico

---

## 1. INTRODUZIONE E SCOPO DEL DOCUMENTO

Il presente documento costituisce l'inventario completo di tutti i casi d'uso eccedenti la soglia minima regolamentare dei 6 obbligatori e di tutte le soluzioni architetturali accessorie integrate nel progetto "Game Vault".
Per ciascuna funzionalità extra viene fornita:
- La mappatura di classi, file e endpoint coinvolti.
- L'analisi di conformità al pattern architetturale (MVC, Service Layer, Transazionalità).
- Una **Scheda di Rischio per l'Esame Orale** (robustezza, esposizione a domande impreviste, adeguatezza dei permessi RBAC).
- Il **Verdetto Conclusivo dell'Agente Auditor** ([MANTENERE PER VALORIZZARE IL VOTO] / [SEMPLIFICARE / CORREGGERE] / [VALUTARE RIMOZIONE PRE-CONSEGNA]).

---

## 2. ELENCO DEI CASI D'USO OLTRE I 6 MINIMI DI BUSINESS

La tabella seguente cataloga gli 8 ulteriori casi d'uso implementati rispetto ai 6 già certificati nel Report di Conformità:

| ID CU | Caso d'Uso Addizionale | Attore Principale | Endpoint / Handler | Descrizione Funzionale |
| :---: | :--- | :---: | :--- | :--- |
| **CU-07** | **Esplorazione e Filtraggio Multi-Parametrico Catalogo RAWG** | Utente / Anonimo | `GET /rawg/popolari` in [RawgController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgController.java#L40) | Consultazione del catalogo globale di oltre 500.000 giochi con ordinamenti (popolarità, data uscita, voto Metacritic, nome) e filtri (tutti, giochi, DLC). |
| **CU-08** | **Ricerca Asincrona Realtime Autocomplete con React 18** | Utente / Anonimo | `/api/rawg/ricerca` in [RawgRestController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgRestController.java#L28) | Ricerca live interattiva montata nella navbar con debouncing a 400ms, dropdown istantaneo con copertine e anno, click-outside dismissal. |
| **CU-09** | **Importazione "Smart Sync" da API RAWG a PostgreSQL Locale** | Utente Autenticato | `POST /libreria/aggiungiDaRawg` in [VideogiocoLibreriaController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoLibreriaController.java#L56) | Salvataggio rapido di un titolo esterno nel database locale con conversione automatica del DTO in entità persistente e collegamento alla propria libreria. |
| **CU-10** | **Eliminazione Definitiva Videogioco a Catalogo (Admin)** | Ruolo ADMIN | `POST /admin/videogioco/elimina/{id}` in [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L64) | Rimozione fisica del titolo dal DB PostgreSQL con propagazione a cascata (`CascadeType.ALL` + `orphanRemoval`) su commenti e librerie collegate. |
| **CU-11** | **Moderazione e Rimozione Commenti Inappropriati (Admin)** | Ruolo ADMIN | `POST /admin/commento/rimuovi/{id}` in [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L70) | Cancellazione mirata di un commento/voto contestato direttamente dalla dashboard di moderazione o dalla scheda pubblica del videogioco. |
| **CU-12** | **Cancellazione della Sola Recensione Personale (User)** | Utente Autenticato | `POST /libreria/recensione/rimuovi` in [VideogiocoLibreriaController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoLibreriaController.java#L104) | Permette all'utente di azzerare la propria recensione (voto e testo) mantenendo comunque il videogioco nella propria libreria personale. |
| **CU-13** | **Misurazione Sperimentale Benchmark Prestazioni JPA N+1** | Ruolo ADMIN | `GET /admin/benchmark` in [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L80) | Benchmark a runtime che confronta i tempi di caricamento (in millisecondi) tra interrogazione standard LAZY e query ottimizzata con `JOIN FETCH`. |
| **CU-14** | **Esposizione API RESTful CRUD Completa per Videogiochi** | Client REST / Admin | `/api/videogiochi/**` in [VideogiocoRestController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoRestController.java) | API REST standard: `GET` (tutti o singolo), `POST` (creazione con codice 201 e header Location), `DELETE` (rimozione con codice 204), `GET /commenti`. |

---

## 3. SCHEDE DI RISCHIO E ANALISI ARCHITETTURALE DETTAGLIATA

---

### FEATURE EXTRA 1: Integrazione Esterna RAWG Video Games Database API
- **Classi Coinvolte**: [RawgApiService.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/RawgApiService.java), [RawgController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgController.java), [RawgGameDTO.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/dto/RawgGameDTO.java), [RawgResponseDTO.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/model/dto/RawgResponseDTO.java).
- **Valutazione Architetturale**:
  - Utilizzo di DTO dedicati per disaccoppiare la struttura esterna di RAWG dal modello di dominio locale.
  - Implementazione in [VideogiocoLibreriaService.java:L70-L94](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/VideogiocoLibreriaService.java#L70-L94) del pattern non-bloccante: la chiamata HTTP a RAWG viene eseguita **fuori** dalla transazione del DB locale, scongiurando il blocco del connection pool.
  - Meccanismo di Smart Redirect: se un gioco RAWG è già presente in PostgreSQL, la rotta `/rawg/gioco/{id}` reindirizza istantaneamente alla scheda locale `/videogioco/{localId}` senza consumare quote API esterne.
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Elevata. Presenti blocchi `try-catch` attorno a tutte le chiamate `restTemplate`.
  - *Punti Deboli*: In [RawgApiService.java:L30-L48](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/RawgApiService.java#L30-L48), il metodo `getGameById` istanzia un `new RestTemplate()` e ha un bug di concatenazione URL (`baseUrl + rawgId`). Questo metodo è fortunatamente non utilizzato (dead code).
  - *Domande Probabili*: "Cosa succede se i server RAWG sono offline o la quota API termina?" Lo studente deve saper rispondere: "L'applicazione cattura l'eccezione, restituisce una lista vuota o pagina di avviso e il database locale continua a funzionare regolarmente in isolamento".
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Azione consigliata: rimuovere o correggere il metodo legacy non utilizzato `getGameById` per evitare domande ostili sul `new RestTemplate()`.*

---

### FEATURE EXTRA 2: Barra di Ricerca Realtime Asincrona con React 18
- **Classi e Template Coinvolti**: [navbar.html:L48-L208](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/fragments/navbar.html#L48-L208), [RawgRestController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgRestController.java).
- **Valutazione Architetturale**:
  - Piena conformità al requisito didattico di avere *"almeno una parte realizzata in React"*.
  - React 18 e Babel montati via CDN nel frammento globale Thymeleaf `navbar.html`.
  - Utilizzo corretto di Hooks moderni: `useState` per query/risultati/loading, `useEffect` con debouncing a 400ms (`setTimeout` con cleanup `clearTimeout`), `useRef` per gestione click-outside listener.
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Eccellente. La gestione dell'asincronia con debouncing previene il flood di chiamate al server durante la digitazione.
  - *Permessi e Sicurezza*: L'endpoint REST `/api/rawg/ricerca` è in sola lettura (`GET`), aperto a tutti senza CSRF blocking (configurato con `.ignoringRequestMatchers("/api/**")`).
  - *Domande Probabili*: "Perché hai scelto di inserire il componente React direttamente nel template Thymeleaf con Babel anziché fare una build npm separata con Vite/Webpack?" Risposta: "Per mantenere l'applicazione auto-consistente e compatta all'interno del singolo server Spring Boot, integrando una Search Bar asincrona reattiva senza costringere a gestire due server separati o reverse proxy CORS durante l'esame".
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Punto di forza evidente che soddisfa in pieno il requisito React + REST.*

---

### FEATURE EXTRA 3: Benchmark Sperimentale JPA N+1 con Interfaccia Web e Test JUnit 5
- **Classi Coinvolte**: [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L80-L106), [benchmark.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/admin/benchmark.html), [RepositoryVideogioco.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/repo/RepositoryVideogioco.java#L13-L14), [BenchmarkNPlusOneTest.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/test/java/it/uniroma3/siw/benchmark/BenchmarkNPlusOneTest.java).
- **Valutazione Architetturale**:
  - Dimostrazione tangibile della padronanza dei meccanismi interni dell'ORM Hibernate.
  - Misurazione dei tempi di esecuzione tramite `System.nanoTime()` su campioni reali di collezioni.
  - Confronto chiaro tra `findById` (che innesca la query pigra differita all'accesso della collezione) e la query JPQL custom con `LEFT JOIN FETCH` (che recupera tutto in un unico roundtrip SQL).
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Elevata. Se il database locale è privo di record, la pagina Thymeleaf gestisce l'empty-state avvisando l'amministratore di inserire almeno un videogioco prima del test.
  - *Protezione RBAC*: Rigidamente confinato sotto il percorso `/admin/**`, accessibile solo a chi possiede `ROLE_ADMIN`.
  - *Domande Probabili*: "Perché non usare sempre JOIN FETCH per tutte le relazioni?" Risposta: "Perché il JOIN FETCH comporta un overhead di trasferimento memoria consistente (prodotto cartesiano delle righe). Va impiegato miratamente nei casi d'uso in cui si sa a priori che l'associazione dovrà essere integralmente navigata, preferendo LAZY per la navigazione puntuale".
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Feature accademica di massimo livello, perfettamente allineata alle preferenze teoriche del docente di SIW.*

---

### FEATURE EXTRA 4: Dashboard Statistiche e Sistema di Moderazione Centralizzato
- **Classi Coinvolte**: [AdminController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/AdminController.java#L32-L38), [dashboard.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/admin/dashboard.html).
- **Valutazione Architetturale**:
  - Espone i KPI della piattaforma (totale utenti registrati, giochi a catalogo, recensioni aggregate).
  - Include due tabelle gestionali: moderazione recensioni ed eliminazione fisica giochi dal catalogo.
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Molto buona. I form di cancellazione utilizzano il metodo HTTP `POST` con dialoghi di conferma JavaScript (`onsubmit="return confirm(...)";`).
  - *Protezione RBAC*: Protetto da `hasRole('ADMIN')`.
  - *Domande Probabili*: "Cosa accade ai commenti quando l'admin elimina un videogioco?" Risposta: "Grazie a `CascadeType.ALL` e `orphanRemoval = true` mappati su `videogioco.commenti` e `videogioco.videogiocoLibreria`, Hibernate rimuove automaticamente tutti i commenti e i record di possesso orfani in un'unica transazione atomica".
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Dimostra completezza applicativa e attenzione all'integrità del database.*

---

### FEATURE EXTRA 5: Paginazione Avanzata con Finestra Numerica Dinamica
- **Classi Coinvolte**: [RawgController.java:L61-L86](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgController.java#L61-L86), [rawg_popolari.html:L71-L126](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/rawg_popolari.html#L71-L126).
- **Valutazione Architetturale**:
  - Calcola dinamicamente le pagine totali (fissate a un massimo prudenziale di 50 per le quote RAWG).
  - Implementa un algoritmo di finestra scorrevole a 5 pagine (`startPage = Math.max(1, page - 2)`, `endPage = Math.min(totalPages, startPage + 4)`) con visualizzazione di puntini di sospensione (`...`), salto alla prima e all'ultima pagina, e pulsanti Precedente/Successiva disabilitati al confine.
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Alta. Gestisce pagine negative o nulle reimpostando a `page = 1`.
  - *Domande Probabili*: "Come preservi i parametri di ordinamento durante il cambio pagina?" Risposta: "I link di paginazione Thymeleaf ripassano i parametri `page`, `ordering`, `dlc_filter` e `search` nella query string".
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Qualità della User Experience di livello professionale.*

---

### FEATURE EXTRA 6: Community Utenti e Profili Pubblici Navigabili
- **Classi Coinvolte**: [UtenteController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/UtenteController.java), [utenti.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/utenti.html), [profiloUtente.html](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/profiloUtente.html).
- **Valutazione Architetturale**:
  - Permette a qualunque utente (anche anonimo) di consultare la community, visualizzare l'elenco degli iscritti e aprire i singoli profili.
  - La scheda profilo mostra l'avatar generato dinamicamente con l'iniziale del nome utente, la data di registrazione, la collezione dei videogiochi posseduti e lo storico di tutte le recensioni pubblicate.
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Buona. Le associazioni sono navigate in sola lettura (`@Transactional(readOnly = true)`).
  - *Privacy/Sicurezza*: La password hashata dell'utente è marcata con `@JsonIgnore` e non viene mai stampata nei template HTML. L'email non viene esposta pubblicamente nei profili altrui.
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Ottima integrazione social-community per il dominio applicativo.*

---

### FEATURE EXTRA 7: Controller REST con Codici di Stato HTTP Formali
- **Classi Coinvolte**: [VideogiocoRestController.java](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/VideogiocoRestController.java).
- **Valutazione Architetturale**:
  - Controller annotato `@RestController` con prefisso di rotta al plurale `/api/videogiochi`.
  - Rispetto rigoroso dei codici di stato HTTP:
    - `POST /api/videogiochi` restituisce **201 Created** valorizzando l'header `Location` con la URI della risorsa appena creata.
    - `DELETE /api/videogiochi/{id}` restituisce **204 No Content** in caso di successo o **404 Not Found** se l'ID non esiste.
    - `GET /api/videogiochi/{id}` restituisce **200 OK** con payload JSON o **404 Not Found**.
- **Scheda di Rischio per l'Esame Orale**:
  - *Robustezza*: Molto alta. La validazione del payload sfrutta `@Valid @RequestBody VideogiocoForm`.
  - *Sicurezza RBAC*: La creazione e cancellazione via REST sono ristrette ai soli utenti con ruolo `ROLE_ADMIN` in `SecurityConfig.java`.
- **Verdetto Auditor**: **[MANTENERE PER VALORIZZARE IL VOTO]** — *Conferma la piena rispondenza alle best-practice dei servizi web RESTful.*

---

## 4. TABELLA RIASSUNTIVA DEI VERDETTI PER L'ESAME

| Funzionalità Extra | Livello Complessità | Grado di Rischio Orale | Verdetto Auditor |
| :--- | :---: | :---: | :---: |
| **1. Integrazione API RAWG + DTO + Smart Caching** | Alta | Molto Basso | **MANTENERE (Bonifica completata con successo)** |
| **2. React 18 Realtime Search con Debouncing** | Media | Basso | **MANTENERE PER VALORIZZARE IL VOTO** |
| **3. Benchmark Sperimentale JPA N+1 (Web + JUnit 5)** | Alta | Molto Basso | **MANTENERE PER VALORIZZARE IL VOTO** |
| **4. Dashboard Amministrativa con KPI e Moderazione** | Media | Molto Basso | **MANTENERE PER VALORIZZARE IL VOTO** |
| **5. Paginazione Dinamica con Finestra Scorrevole** | Media | Molto Basso | **MANTENERE PER VALORIZZARE IL VOTO** |
| **6. Community Utenti e Schede Profilo Pubbliche** | Bassa | Molto Basso | **MANTENERE PER VALORIZZARE IL VOTO** |
| **7. Suite RESTful con Header Location e Codici Formali** | Media | Molto Basso | **MANTENERE PER VALORIZZARE IL VOTO** |
| **8. Google OAuth2 Auto-Provisioning Sicuro** | Alta | Basso | **MANTENERE PER VALORIZZARE IL VOTO** |

---

## 5. STATO FINALE DEGLI INTERVENTI PRE-CONSEGNA

Tutte le raccomandazioni sono state implementate con successo:
1. **Debito Tecnico in `RawgApiService` Risolto**: Il metodo `getGameById(Long rawgId)` ora delega in modo pulito a `getGameDetails(rawgId)` sfruttando il bean iniettato `this.restTemplate`.
2. **Parent Spring Boot Riallineato**: La versione in `pom.xml` è stata impostata sulla release ufficiale e stabile `3.3.4`.
3. **Containerizzazione e Deploy Cloud Pronti**: Creati `Dockerfile` multi-stage, `docker-compose.yml`, `.dockerignore` e documentazione operativa in `README.md` per il deploy immediato su qualsiasi infrastruttura cloud.
