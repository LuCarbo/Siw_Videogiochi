package it.uniroma3.siw.benchmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.VideogiocoLibreria;
import it.uniroma3.siw.repository.RepositoryUtente;
import it.uniroma3.siw.repository.RepositoryVideogioco;
import it.uniroma3.siw.repository.RepositoryVideogiocoLibreria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BenchmarkNPlusOneTest {

    @Autowired
    private RepositoryVideogioco videogiocoRepository;

    @Autowired
    private RepositoryUtente utenteRepository;

    @Autowired
    private RepositoryVideogiocoLibreria videogiocoLibreriaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Long testVideogiocoId;

    @BeforeEach
    public void setupData() {
        // Pulisce e prepara dati di test consistenti
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            videogiocoLibreriaRepository.deleteAll();

            Videogioco v = new Videogioco();
            v.setTitolo("The Witcher 3: Wild Hunt - Benchmark Edition");
            v.setAnnoUscita(2015);
            v.setDescrizione("Benchmark test game");
            v = videogiocoRepository.save(v);
            this.testVideogiocoId = v.getId();

            for (int i = 1; i <= 15; i++) {
                String uname = "benchmark_user_" + i;
                Utente u = utenteRepository.findByUsername(uname).orElse(null);
                if (u == null) {
                    u = new Utente();
                    u.setUsername(uname);
                    u.setEmail(uname + "@test.com");
                    u.setPassword("pass");
                    u.setRuolo("USER");
                    u.setDataRegistrazione(LocalDate.now());
                    u = utenteRepository.save(u);
                }

                VideogiocoLibreria vl = new VideogiocoLibreria();
                vl.setUtente(u);
                vl.setVideogioco(v);
                vl.setDataAggiunta(LocalDate.now());
                videogiocoLibreriaRepository.save(vl);
            }
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @Test
    @DisplayName("Confronto Sperimentale: Standard LAZY vs JOIN FETCH (Risoluzione N+1)")
    public void testBenchmarkLazyVsJoinFetch() {
        System.out.println("========================================================================");
        System.out.println("   BENCHMARK SPERIMENTALE N+1 (SIW - Appello Settembre 2026)");
        System.out.println("========================================================================");

        // --- STRATEGIA 1: Standard findById (LAZY Collection) ---
        long startLazy = System.nanoTime();
        TransactionStatus tx1 = transactionManager.getTransaction(new DefaultTransactionDefinition());
        int countLazy = 0;
        try {
            Optional<Videogioco> lazyGameOpt = videogiocoRepository.findById(testVideogiocoId);
            assertTrue(lazyGameOpt.isPresent());
            Videogioco lazyGame = lazyGameOpt.get();

            // Navigazione della collezione LAZY: scatena query aggiuntiva per la collezione
            List<VideogiocoLibreria> libreria = lazyGame.getVideogiocoLibreria();
            countLazy = libreria.size();
            for (VideogiocoLibreria vl : libreria) {
                assertNotNull(vl.getId());
            }
            transactionManager.commit(tx1);
        } catch (Exception e) {
            transactionManager.rollback(tx1);
            throw e;
        }
        long durationLazyNs = System.nanoTime() - startLazy;
        double durationLazyMs = durationLazyNs / 1_000_000.0;

        // --- STRATEGIA 2: LEFT JOIN FETCH (Risoluzione N+1) ---
        long startFetch = System.nanoTime();
        TransactionStatus tx2 = transactionManager.getTransaction(new DefaultTransactionDefinition());
        int countFetch = 0;
        try {
            Optional<Videogioco> fetchGameOpt = videogiocoRepository.findByIdWithVideogiocoLibreria(testVideogiocoId);
            assertTrue(fetchGameOpt.isPresent());
            Videogioco fetchGame = fetchGameOpt.get();

            // Con JOIN FETCH la collezione è già precaricata con una singola SELECT congiunta
            List<VideogiocoLibreria> libreria = fetchGame.getVideogiocoLibreria();
            countFetch = libreria.size();
            for (VideogiocoLibreria vl : libreria) {
                assertNotNull(vl.getId());
            }
            transactionManager.commit(tx2);
        } catch (Exception e) {
            transactionManager.rollback(tx2);
            throw e;
        }
        long durationFetchNs = System.nanoTime() - startFetch;
        double durationFetchMs = durationFetchNs / 1_000_000.0;

        // Stampa report comparativo
        System.out.printf("| %-25s | %-15s | %-18s |\n", "Strategia", "Elementi Caricati", "Tempo Esecuzione");
        System.out.println("|---------------------------|-------------------|--------------------|");
        System.out.printf("| 1. Standard (LAZY)        | %-17d | %14.3f ms |\n", countLazy, durationLazyMs);
        System.out.printf("| 2. Ottimizzato (JOIN FETCH)| %-17d | %14.3f ms |\n", countFetch, durationFetchMs);
        System.out.println("========================================================================");
        System.out.println("RIEPILOGO CONFRONTO:");
        System.out.println("- La strategia 1 (LAZY) esegue inizialmente 1 query per l'entità radice.");
        System.out.println("  Al primo accesso alla collezione 'videogiocoLibreria', Hibernate emette una query SQL aggiuntiva.");
        System.out.println("- La strategia 2 (JOIN FETCH) utilizza la clausola JPQL 'LEFT JOIN FETCH v.videogiocoLibreria',");
        System.out.println("  eseguendo una singola query SQL con JOIN, eliminando il problema delle query N+1.");
        System.out.println("========================================================================");

        assertEquals(countLazy, countFetch);
        assertTrue(countFetch > 0);
    }
}
