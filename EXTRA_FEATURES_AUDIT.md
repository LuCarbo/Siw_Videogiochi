# AUDIT DELLE FUNZIONALITÀ EXTRA E BONUS (PROGETTO PERSONALE SIW)
**Corso:** Sistemi Informativi su Web (SIW) — Appello Settembre 2026  
**Progetto:** Progetto Personale d'Esame (`Siw_Videogiochi` / Game Vault)  
**Ruolo del Revisore:** Senior Software Engineer & Academic Reviewer  
**Data Audit:** 05 Settembre 2026  

---

## 1. TABELLA RIASSUNTIVA DELLE FUNZIONALITÀ EXTRA RILEVATE

| Feature Extra | File e Componenti Coinvolti | Tipologia | Livello di Rischio all'Orale | Raccomandazione |
| :--- | :--- | :---: | :---: | :---: |
| **Integrazione API Esterna RAWG** | `RawgApiService.java`<br>`RawgController.java`<br>`RawgRestController.java`<br>`RawgGameDTO.java`, `RawgResponseDTO.java`<br>`rawg_popolari.html`, `rawg_dettagli.html`<br>`AppConfig.java`, `application.properties` | Extra Spontaneo (Non richiesto dal docente) | 🔴 **ALTO** | **ISOLA / CREA CATALOGO LOCALE DI SICUREZZA** |
| **Infinite Scroll Client-Side (JS IntersectionObserver)** | `rawg_popolari.html` (righe 83-174) | Extra Spontaneo | 🟡 **MEDIO** | **MANTIENI O ISOLA** (Non farsi cogliere impreparati su Spring Data `Pageable`) |
| **Filtri e Ordinamenti RAWG (DLC, Metacritic, Date)** | `RawgApiService.java` (`getGamesWithFilters`)<br>`rawg_popolari.html` (form filtri) | Extra Spontaneo | 🟡 **MEDIO-ALTO** | **ISOLA / MANTIENI COME PRESENTAZIONE** |
| **Autenticazione Google OAuth2** | `pom.xml` (`spring-boot-starter-oauth2-client`)<br>`application.properties` (`spring.security.oauth2...`)<br>`SecurityConfig.java` (`.oauth2Login`)<br>`UtenteService.java` (`getCurrentUserId`)<br>`login.html` (bottone Google) | **Bonus Ufficiale del Docente** (Sez. 3 Specifiche) | 🟢 / 🟡 **BASSO-MEDIO** | **MANTIENI** (con fallback offline sicuro) |
| **Libreria `spring-dotenv`** | `pom.xml` (`me.paulschwarz:spring-dotenv`)<br>`application.properties` (`spring.config.import`) | Extra Tecnico | 🟢 **BASSO** | **MANTIENI** |
| **React SearchBar via CDN & Babel Standalone** | `fragments/navbar.html` (righe 36-117)<br>`RawgRestController.java` (`/api/rawg/ricerca`) | Requisito Obbligatorio (realizzato in-browser) | 🟡 **MEDIO** | **MANTIENI** (sapendo motivare l'assenza di Webpack/Vite) |
| **Query `findByIdWithVideogiocoLibreria` Incompleta** | `RepositoryVideogioco.java` (riga 13)<br>`VideogiocoService.java` (riga 40) | Bozza Incompleta | 🟡 **MEDIO** | **VALORIZZA NEL TEST N+1 FORMALE** |

---

## 2. SCHEDA DETTAGLIATA PER SINGOLA FEATURE EXTRA

---

### FEATURE 1: Integrazione API Esterna RAWG (Video Games Database)

#### Cos'è e dove risiede nel codice:
L'applicazione è quasi interamente imperniata sul consumo dell'API REST pubblica [RAWG](https://rawg.io/apidocs).
- **Service:** [`RawgApiService.java`](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/RawgApiService.java) effettua chiamate HTTP sincrone tramite `RestTemplate` a `https://api.rawg.io/api`. Include metodi per giochi popolari, dettagli, link agli store di terze parti e screenshot.
- **Controller MVC:** [`RawgController.java`](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgController.java) espone `/rawg/popolari` e `/rawg/gioco/{id}`.
- **Controller REST:** [`RawgRestController.java`](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/controller/RawgRestController.java) fa da reverse proxy verso RAWG per la search bar.
- **DTOs:** `RawgGameDTO.java`, `RawgResponseDTO.java`.
- **Configurazioni:** `application.properties` contiene `rawg.api.key=0e5c5b5a081948b2b7bf4a7c809d3bb3`.

#### Pro (Valore Aggiunto):
- Fornisce un catalogo enorme di giochi reali con copertine in alta risoluzione, descrizioni, screenshot e store ufficiali.
- Rende l'applicazione visivamente accattivante e simile a una moderna web app commerciale (es. Steam o Backloggd).

#### Contro e Rischi all'Esame Orale:
1. **Dipendenza Critica dalla Rete:** Se durante l'esame la connessione dell'aula vacilla, o la API key scade/supera il rate limit (RAWG ha limiti severi sul piano free), l'home page si rompe completamente e l'esame rischia di bloccarsi.
2. **Disallineamento Metodologico con il Corso SIW:** Il corso SIW valuta le competenze su **Spring Data JPA, Hibernate, JPQL e PostgreSQL**. In questo progetto, la navigazione del catalogo, i dettagli e la ricerca NON interrogano il database PostgreSQL, ma interrogano RAWG! Il professore potrebbe obiettare: *"Dov'è la tabella dei giochi sul DB locale? Perché state interrogando un'API esterna invece delle entità JPA che vi ho chiesto di modellare?"*.
3. **Chiamata Esterna Bloccante in Transazione DB:** In `VideogiocoLibreriaService.aggiungiDaRawgALibreria`, la chiamata `rawgApiService.getGameDetails(rawgId)` è incapsulata in un metodo `@Transactional`. Se RAWG ci mette 2 secondi a rispondere, la connessione al DB PostgreSQL rimane sequestrata per 2 secondi.
4. **Richieste Estemporanee del Docente:** Se il docente chiede: *"Modifica la query del catalogo per mostrare solo i giochi con più di 3 recensioni"*, non puoi farlo facilmente perché il catalogo principale non è nel database!

#### Piano di Rimozione/Isolamento Pulito (Safe Decoupling):
Se si decidesse di rimuovere o disattivare RAWG per basarsi solo su DB locale:
1. **Creare un catalogo locale nel DB:** Inserire 10-15 videogiochi direttamente nel database tramite `InitData.java` con titoli, copertine e descrizioni.
2. **Reindirizzare la rotta principale:** In `VideogiocoController.java`, fare in modo che `/` e `/videogiochi` mostrino i giochi da `videogiocoService.findAll()` invece di reindirizzare a `/rawg/popolari`.
3. **Sostituire la SearchBar:** Modificare `/api/rawg/ricerca` (o creare `/api/videogiochi/ricerca`) affinché interroghi `videogiocoRepository.findByTitoloContainingIgnoreCase(query)`.
4. **Eliminare i file RAWG:** Eliminare `RawgApiService.java`, `RawgController.java`, `RawgRestController.java`, `RawgGameDTO.java`, `RawgResponseDTO.java`, `rawg_popolari.html`, `rawg_dettagli.html` e la proprietà `rawg.api.key`.

---

### FEATURE 2: Infinite Scroll Client-Side con IntersectionObserver

#### Cos'è e dove risiede nel codice:
- Nel template [`rawg_popolari.html:L83-L174`](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/rawg_popolari.html#L83-L174), è presente un elemento sentinel `#infinite-scroll-sentry` monitorato da un `IntersectionObserver` JavaScript. Quando l'utente scorre fino in fondo alla pagina, uno script asincrono chiama `/api/rawg/ricerca?page=N` e fa l'append manuale di elementi HTML (`insertAdjacentHTML`) nel DOM.

#### Pro (Valore Aggiunto):
- Esperienza utente fluida, simile a social network o store moderni, senza ricaricare la pagina.

#### Contro e Rischi all'Esame Orale:
1. **Conflitto con i Requisiti di Paginazione Standard:** All'esame di SIW, la domanda classica del docente sulla paginazione riguarda **Spring Data `Pageable` e `Page<T>`** lato server e i bottoni "Precedente / Successivo" in Thymeleaf.
2. **Fragilità del DOM Scripting:** La duplicazione del markup HTML delle card sia in Thymeleaf (server-side) sia dentro una stringa JavaScript letterale (client-side) viola il principio DRY (Don't Repeat Yourself). Se modifichi la grafica della card nel template Thymeleaf, devi ricordarti di modificarla identica nella stringa JS.

#### Procedura di Rimozione Pulita:
1. Aprire `src/main/resources/templates/rawg_popolari.html`.
2. Rimuovere i blocchi HTML `#loading-spinner` e `#infinite-scroll-sentry` (righe 84-96).
3. Rimuovere l'intero tag `<script>...</script>` (righe 98-174).
4. Se serve la paginazione, aggiungere due bottoni standard "Pagina Precedente" e "Pagina Successiva" che passano il parametro `?page=...` al controller.

---

### FEATURE 3: Filtri Avanzati e Ordinamento via Query Params RAWG

#### Cos'è e dove risiede nel codice:
- In [`RawgApiService.java:L72-L105`](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/java/it/uniroma3/siw/service/RawgApiService.java#L72-L105), il metodo `getGamesWithFilters` manipola dinamicamente i parametri di query dell'URL inviato a RAWG (`ordering`, `exclude_additions`, `tags`, `dates`).
- In `rawg_popolari.html:L23-L60`, un form HTML permette di selezionare l'ordinamento (Popolarità, Data, Metacritic, Nome) e il tipo di contenuto (Giochi o DLC).

#### Pro (Valore Aggiunto):
- Mostra una gestione articolata di parametri GET e manipolazione dell'URL tramite `UriComponentsBuilder`.

#### Contro e Rischi all'Esame Orale:
- Nessuno di questi filtri viene applicato tramite query SQL o JPQL su PostgreSQL.
- Se il docente chiede: *"Aggiungi un filtro per filtrare i giochi per anno sul database"*, non si possono riutilizzare queste classi perché operano su un'API terza.

#### Procedura di Rimozione Pulita:
1. In `rawg_popolari.html`, eliminare il form `<form action="/rawg/popolari" ...>`.
2. In `RawgController.java`, semplificare `showPopularGames` rimuovendo i parametri `@RequestParam`.
3. In `RawgApiService.java`, rimuovere `getGamesWithFilters` e utilizzare solo `getPopularGames`.

---

### FEATURE 4: Autenticazione Google OAuth2

#### Cos'è e dove risiede nel codice:
- Dipendenza: `spring-boot-starter-oauth2-client` in `pom.xml`.
- Configurazione: `spring.security.oauth2.client.registration.google...` in `application.properties`.
- Security: `.oauth2Login(oauth2 -> oauth2.loginPage("/login").defaultSuccessUrl("/rawg/popolari"))` in `SecurityConfig.java`.
- Gestione Utente: In `UtenteService.java:L58-L76`, estrazione del principal `OAuth2User`, prelievo dell'email e salvataggio automatico dell'utente con password placeholder `oauth2_placeholder` se non già presente nel DB.
- Template: Bottone "Accedi con Google" in `login.html:L45-L47`.

#### Pro (Valore Aggiunto):
- **È UN BONUS UFFICIALE PREVISTO DAL DOCENTE** (Sezione 3 delle specifiche d'esame: *"PLUS / BONUS UFFICIALI DEL DOCENTE: Autenticazione tramite OAuth (es. Google / GitHub)"*).
- Dimostra padronanza del protocollo OAuth2 e dell'integrazione di identity provider moderni in Spring Security.

#### Contro e Rischi all'Esame Orale:
1. **Configurazione delle Variabili d'Ambiente:** Richiede che `GOOGLE_CLIENT_ID` e `GOOGLE_CLIENT_SECRET` siano valorizzate. Se mancano, l'avvio dell'applicazione potrebbe fallire a meno di non gestire l'import opzionale.
2. **Dipendenza da Rete / Redirect URI:** Google OAuth richiede che l'URL di redirect (`http://localhost:8080/login/oauth2/code/google`) sia autorizzato nella Google Cloud Console. Se all'esame si avvia l'applicazione su una porta diversa (es. 8081) o senza connessione internet, il login con Google fallirà con `redirect_uri_mismatch`.
3. **Password Placeholder Inconsistente:** Nel metodo `UtenteService.getCurrentUserId()`, se l'utente si logga via OAuth2, viene salvato con `u.setPassword("oauth2_placeholder")` in chiaro (non cifrata con BCrypt).

#### Procedura di Mantenimento in Sicurezza (o Rimozione se Offline):
- **Strategia Consigliata: Mantienila**, ma tieni pronto l'utente pre-cablato `mario` / `password123` e il form di registrazione locale, in modo da poter fare l'intero esame offline senza cliccare su Google se la rete dell'università non collabora.
- **Se decidi di rimuoverla del tutto:**
  1. Rimuovere la dipendenza `spring-boot-starter-oauth2-client` da `pom.xml`.
  2. Rimuovere le 3 righe `spring.security.oauth2...` da `application.properties`.
  3. In `SecurityConfig.java`, eliminare il blocco `.oauth2Login(...)`.
  4. In `UtenteService.java`, rimuovere il ramo `else if (authentication.getPrincipal() instanceof OAuth2User)`.
  5. In `login.html`, rimuovere il link ad `oauth2/authorization/google`.

---

### FEATURE 5: Libreria `spring-dotenv` (Gestione File `.env`)

#### Cos'è e dove risiede nel codice:
- In `pom.xml`, dipendenza `me.paulschwarz:spring-dotenv:4.0.0`.
- In `application.properties`: `spring.config.import=optional:file:.env[.properties]`.

#### Pro (Valore Aggiunto):
- Best practice di sicurezza ingegneristica (Twelve-Factor App): evita di committare chiavi private e password del database nel repository git.

#### Contro e Rischi all'Esame Orale:
- Rischio minimo: solo accertarsi che il file `.env` sia presente nella cartella del progetto sul PC usato all'orale, altrimenti le variabili `${DB_PASSWORD}` o `${GOOGLE_CLIENT_ID}` ricadranno sui valori di default.

#### Raccomandazione:
- **MANTIENI.** È una pratica eccellente e non disturba minimamente l'architettura.

---

### FEATURE 6: Componente React SearchBar integrato via CDN e Babel Standalone

#### Cos'è e dove risiede nel codice:
- In [`fragments/navbar.html:L36-L117`](file:///C:/Users/Luca/SIW/Siw_Videogiochi/src/main/resources/templates/fragments/navbar.html#L36-L117).
- Utilizza `<script type="text/babel">` con transpilazione a runtime, invocando l'endpoint `/api/rawg/ricerca`.

#### Pro (Valore Aggiunto):
- **Soddisfa il requisito obbligatorio del Frontend Ibrido (Thymeleaf + React)** senza la necessità di:
  - Installare Node.js ed npm sul computer dell'esame.
  - Eseguire `npm run build` o configurare plugin Maven complessi (`frontend-maven-plugin`).
  - Rischiare errori di dipendenze Node o porte separate (es. Vite su 5173 e Spring su 8080 con problemi CORS).

#### Contro e Rischi all'Esame Orale:
1. **La Domanda del Professore:** Il professore potrebbe chiedere: *"Come viene compilato questo codice JSX?"*. Bisogna rispondere chiaramente che in questo prototipo si usa Babel Standalone in-browser per semplificare il runtime, ma che in produzione verrebbe pre-compilato con Vite o Webpack.
2. **Chiamata a RAWG:** La search bar cerca su RAWG e non sul DB PostgreSQL locale. Se all'orale non c'è internet, la search bar non restituirà risultati.

#### Raccomandazione:
- **MANTIENI**, ma fai puntare la search bar a un endpoint locale (es. `/api/videogiochi/ricerca`) che cerca nei videogiochi del database, così funziona anche offline al 100%!

---

### FEATURE 7: Query Custom `findByIdWithVideogiocoLibreria` (Incompleto N+1)

#### Cos'è e dove risiede nel codice:
- In `RepositoryVideogioco.java`:
  ```java
  @Query("SELECT v FROM Videogioco v LEFT JOIN FETCH v.videogiocoLibreria WHERE v.id = :id")
  public Optional<Videogioco> findByIdWithVideogiocoLibreria(@Param("id") Long id);
  ```
- In `VideogiocoService.java:L40`: `findByIdConRecensioni(id)`.

#### Valutazione:
- È un pezzo di codice rimasto "orfano": l'intenzione era corretta (preparare una query con `JOIN FETCH` per risolvere il problema N+1), ma non è collegata ad alcuna vista né a un test dimostrativo.

#### Raccomandazione:
- **NON ELIMINARLA**: Usala come base per creare la suite di test o lo script di confronto per il **Requisito 6 (Analisi Sperimentale N+1)** richiesto dal docente!

---

## 3. CONSIGLIO STRATEGICO FINALE PER L'ESAME ORALE

### L'Assetto Ideale per una Prova Orale Impeccabile

Durante l'esame orale di SIW, il docente adotta tipicamente questo schema di interrogazione:
1. Chiede di autenticarsi con due utenti distinti (un utente normale e un admin).
2. Chiede di mostrare un'operazione CRUD completa (es. inserimento con validazione e gestione errori, modifica e cancellazione).
3. Chiede di aprire il codice di un Controller, di un Service e di un'Entità per verificare annotazioni (`@Transactional`, `@Valid`, `@ManyToOne`, `equals/hashCode`).
4. **Richiede una modifica live del codice al momento** (es. *"Aggiungi un campo 'piattaforma' al videogioco"* oppure *"Fai una query che restituisca i giochi con voto medio superiore a 8"* oppure *"Impedisci la cancellazione se la recensione ha più di un giorno"*).

### Perché l'attuale architettura RAWG-dipendente è ad alto rischio:
Se il docente ti chiede di aggiungere un filtro o modificare un'entità, e la maggior parte dell'applicazione mostra dati volatili da un'API esterna, la modifica al volo rischia di diventare complicatissima o fallire a causa del disallineamento tra DTO di RAWG ed entità locali.

### Configurazione Strategica Consigliata:
1. **Pre-popolare il Database Locale:** Nel file `InitData.java`, inserire 5-10 videogiochi completi di copertina (anche usando URL reali di immagini) e diverse recensioni scritte da utenti differenti.
2. **Garantire la Navigazione Offline:** Fare in modo che se la connessione internet non c'è, il sito funzioni perfettamente su `http://localhost:8080/` visualizzando i giochi dal database PostgreSQL locale.
3. **Mantenere RAWG come "Importatore Intelligente":** Presentare RAWG al professore non come il cuore dell'app, ma come una **funzionalità avanzata**: *"L'applicazione gestisce un catalogo locale persistito su PostgreSQL; per arricchire il catalogo, abbiamo implementato un'integrazione REST avanzata con RAWG che consente di cercare titoli esterni e importarli automaticamente nel nostro DB locale"*. In questo modo la feature diventa un prestigioso punto di merito anziché una debolezza architetturale!
4. **Completare i Bloccanti:** Prima di toccare qualsiasi grafica, sistemare la validazione `@Valid`, il ruolo `ADMIN`, il form di registrazione e lo script del benchmark N+1.
