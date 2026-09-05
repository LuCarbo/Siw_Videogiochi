Sei un Senior Software Engineer e Revisore Accademico esperto di Spring Boot, JPA/Hibernate, Spring Security, React e Thymeleaf.
Il tuo compito è analizzare approfonditamente l'INTERO codebase di questo repository ed effettuare un audit rigoroso rispetto alle specifiche ufficiali del PROGETTO PERSONALE d'esame per Sistemi Informativi su Web (SIW - Appello Settembre 2026).

NON modificare alcun file di codice sorgente in questa fase. Devi ispezionare tutti i file sorgente (Java, template Thymeleaf, componenti React, configurazioni, script SQL, pom.xml o build.gradle) e generare DUE FILE DI REPORT distinti nella root del progetto:
1. `SIW_PERSONALE_COMPLIANCE_REPORT.md` (Verifica puntuale dei requisiti del professore)
2. `EXTRA_FEATURES_AUDIT.md` (Censimento e analisi delle funzionalità extra da valutare se tenere o rimuovere)

Segui scrupolosamente le specifiche d'esame del professore riportate di seguito:

================================================================================
SPECIFICHE UFFICIALI DEL PROFESSORE (PROGETTO PERSONALE - SETTEMBRE 2026)
================================================================================
1. CASI D'USO OBBLIGATORI:
   - Devono essere implementati ALMENO 6 casi d'uso sul dominio applicativo.
   - REGOLA FONDAMENTALE DEL DOCENTE: "Autenticazione e registrazione dell'utente NON sono considerati casi d'uso".
   - Composizione minima richiesta dei 6 casi d'uso:
     * Almeno 1 caso d'uso di INSERIMENTO (Create) dei dati di un'entità.
     * Almeno 1 caso d'uso di AGGIORNAMENTO (Update) dei dati di un'entità.
     * Almeno 1 caso d'uso di CANCELLAZIONE (Delete) di una o più entità.
     * Almeno 2 casi d'uso di LETTURA (Read) dei dati di una o più entità (es. catalogo/elenco e pagina di dettaglio).
     * I restanti per arrivare ad almeno 6 devono essere ulteriori operazioni sul dominio.

2. STACK TECNOLOGICO E ARCHITETTURA:
   - Backend: Spring Boot + JPA / Hibernate + PostgreSQL (o RDBMS compatibile).
   - Frontend Ibrido: Thymeleaf per la parte server-rendered e React per almeno una parte/funzionalità del frontend.
   - Integrazione: La parte React DEVE comunicare con il backend tramite API REST realizzate con Spring Boot (@RestController) con scambio dati JSON.
   - Architettura a 3 livelli: Controller -> Service -> Repository. NESSUNA logica applicativa o query nei Controller.
   - Transazioni (@Transactional): annotazioni esplicite nei Service distinguendo le letture (@Transactional(readOnly = true)) e le scritture (@Transactional).
   - Validazione: validazione dei dati di input nei controller (@Valid, BindingResult) e gestione errori.
   - Sicurezza: Spring Security con autenticazione (username/password) e autorizzazione basata su ruoli.

3. PLUS / BONUS UFFICIALI DEL DOCENTE:
   - Autenticazione tramite OAuth (es. Google / GitHub).
   - Deployment su piattaforma cloud (AWS, Azure, Heroku, Render, ecc.).

================================================================================
FASE 1: VERIFICA RIGOROSA DEL CODEBASE
================================================================================
Ispeziona l'intero progetto e verifica:
1. Modello di Dominio JPA: entità, relazioni (@OneToMany, @ManyToOne, @ManyToMany, mappedBy), fetch LAZY di default sulle collezioni, correttezza di equals(), hashCode() e assenza di loop ricorsivi in toString() o Jackson JSON.
2. Matrice dei Casi d'Uso: elenca e mappa ogni caso d'uso distinguendo Create, Update, Delete, Read. Verifica tassativamente che ce ne siano almeno 6 escludendo login e signup!
3. Architettura e Transazioni: Controller puliti, Service completi, uso corretto di @Transactional (readOnly = true / standard), gestione atomicità.
4. Integrazione Frontend (Thymeleaf + React): verifica quale parte è in Thymeleaf, quale in React e se i controller REST (@RestController) sono conformi per semantica HTTP e codici di stato.
5. Sicurezza: configurazione SecurityFilterChain, password hashing (BCrypt), autorizzazione sugli endpoint.

================================================================================
FASE 2: CENSIMENTO FUNZIONALITÀ EXTRA E BONUS
================================================================================
Individua qualsiasi entità, logica, libreria o funzione che NON sia strettamente richiesta dai requisiti base sopra indicati, inclusi i bonus ufficiali (OAuth, Cloud deploy) o feature spontanee (filtri complessi, upload immagini, paginazione, Swagger, notifiche, ecc.).
Valuta ogni feature in vista dell'ESAME ORALE (durante il quale il docente chiederà di modificare il codice in tempo reale: un codice sovraccarico o fragile rischia di bloccarti).

================================================================================
FORMATO DEI DUE REPORT DA GENERARE NELLA ROOT DEL PROGETTO
================================================================================
Crea i seguenti due file Markdown formattati in modo chiaro e dettagliato:

FILE 1: `SIW_PERSONALE_COMPLIANCE_REPORT.md`
- Riepilogo: Dominio applicativo, entità trovate, stato complessivo di conformità (% conformità, requisiti OK, MANCANTI, WARNING).
- Tabella Dettagliata dei Casi d'Uso:
  | ID | Nome Caso d'Uso | Categoria (Create/Update/Delete/Read) | Frontend (Thymeleaf/React) | Controller | Service | Esito (Valido / Non Conforme) |
  (Con conteggio finale netto che esclude login e registrazione).
- Audit Architetturale: Layering, JPA/Hibernate, @Transactional, API REST + React, Sicurezza.
- Lista Prioritaria delle Azioni Correttive Obbligatorie da fare prima dell'esame.

FILE 2: `EXTRA_FEATURES_AUDIT.md`
- Tabella delle Feature Extra Rilevate:
  | Feature Extra | File Coinvolti | Tipo (Bonus Ufficiale / Extra Spontaneo) | Rischio all'Esame Orale (Basso/Medio/Alto) | Raccomandazione (Mantieni / Rimuovi / Isola) |
- Scheda di dettaglio per ogni feature extra:
  * Cosa fa e dove risiede nel codice.
  * Pro (valore aggiunto per il voto o bonus previsto).
  * Contro (rischi durante le modifiche estemporanee del professore all'orale).
  * Procedura passo-passo di rimozione pulita (come toglierla senza rompere il resto se si decide di eliminarla).
- Consiglio strategico finale sull'assetto ideale per l'orale.

Inizia l'ispezione del progetto e genera i due file.