package it.uniroma3.siw.security;


import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.VideogiocoLibreria;
import it.uniroma3.siw.repository.RepositoryCommento;
import it.uniroma3.siw.repository.RepositoryUtente;
import it.uniroma3.siw.repository.RepositoryVideogioco;
import it.uniroma3.siw.repository.RepositoryVideogiocoLibreria;
import it.uniroma3.siw.service.RawgApiService;

import java.time.LocalDate;

@Component
public class InitData implements CommandLineRunner {

    private final RepositoryUtente utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final RepositoryVideogioco videogiocoRepository;
    private final RepositoryVideogiocoLibreria videogiocoLibreriaRepository;
    private final RepositoryCommento commentoRepository;
    private final RawgApiService rawgApiService;

    public InitData(RepositoryUtente utenteRepository,
                    PasswordEncoder passwordEncoder,
                    RepositoryVideogioco videogiocoRepository,
                    RepositoryVideogiocoLibreria videogiocoLibreriaRepository,
                    RepositoryCommento commentoRepository,
                    RawgApiService rawgApiService) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.videogiocoRepository = videogiocoRepository;
        this.videogiocoLibreriaRepository = videogiocoLibreriaRepository;
        this.commentoRepository = commentoRepository;
        this.rawgApiService = rawgApiService;
    }

    @Override
    public void run(String... args) throws Exception {
        
        Utente mario = utenteRepository.findByUsername("mario").orElse(null);
        if (mario == null) {
            mario = new Utente();
            mario.setUsername("mario");
            mario.setPassword(passwordEncoder.encode("password123"));
            mario.setEmail("mario@example.com"); 
            mario.setRuolo("USER");
            mario.setDataRegistrazione(LocalDate.now());
            mario = utenteRepository.save(mario);
        }

        Utente admin = utenteRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = new Utente();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRuolo("ADMIN");
            admin.setDataRegistrazione(LocalDate.now());
            admin = utenteRepository.save(admin);
        }

        // Inizializzazione titoli dimostrativi nel DB locale se catalogo vuoto
        if (videogiocoRepository.count() == 0) {
            Videogioco witcher = new Videogioco();
            witcher.setRawgId(3328L);
            witcher.setTitolo("The Witcher 3: Wild Hunt");
            witcher.setAnnoUscita(2015);
            witcher.setUrlCopertina("https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg");
            witcher.setDescrizione("The Witcher 3: Wild Hunt è un gioco di ruolo d'azione con una ricca trama open world, ambientato in un universo fantasy visivamente sbalorditivo pieno di scelte significative e conseguenze d'impatto.");
            witcher = videogiocoRepository.save(witcher);

            Videogioco cyberpunk = new Videogioco();
            cyberpunk.setRawgId(41494L);
            cyberpunk.setTitolo("Cyberpunk 2077");
            cyberpunk.setAnnoUscita(2020);
            cyberpunk.setUrlCopertina("https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c8c59c92.jpg");
            cyberpunk.setDescrizione("Cyberpunk 2077 è un'avventura a mondo aperto ambientata a Night City, una megalopoli ossessionata dal potere, dal glamour e dalla modificazione corporea.");
            cyberpunk = videogiocoRepository.save(cyberpunk);

            Videogioco eldenRing = new Videogioco();
            eldenRing.setRawgId(3272L);
            eldenRing.setTitolo("Elden Ring");
            eldenRing.setAnnoUscita(2022);
            eldenRing.setUrlCopertina("https://media.rawg.io/media/games/b29/b296255301801269389280d96d92e59e.jpg");
            eldenRing.setDescrizione("L'Interregno ti attende. Vivi una nuova avventura fantasy epica ideata da Hidetaka Miyazaki e George R.R. Martin.");
            eldenRing = videogiocoRepository.save(eldenRing);

            // Aggiungi The Witcher 3 alla libreria di Mario
            VideogiocoLibreria vlMario = new VideogiocoLibreria();
            vlMario.setUtente(mario);
            vlMario.setVideogioco(witcher);
            vlMario.setDataAggiunta(LocalDate.now());
            videogiocoLibreriaRepository.save(vlMario);

            // Aggiungi recensione dimostrativa di Mario su The Witcher 3
            Commento commMario = new Commento();
            commMario.setAutore(mario);
            commMario.setVideogioco(witcher);
            commMario.setVoto(9);
            commMario.setTesto("Capolavoro assoluto della narrativa RPG open world. Ambientazione spettacolare e missioni secondarie memorabili.");
            commMario.setDataScrittura(LocalDate.now());
            commentoRepository.save(commMario);

            // Aggiungi Cyberpunk 2077 alla libreria dell'Admin
            VideogiocoLibreria vlAdmin = new VideogiocoLibreria();
            vlAdmin.setUtente(admin);
            vlAdmin.setVideogioco(cyberpunk);
            vlAdmin.setDataAggiunta(LocalDate.now());
            videogiocoLibreriaRepository.save(vlAdmin);
        }

        // Pre-riscaldamento asincrono della cache del catalogo RAWG per garantire caricamenti istantanei
        new Thread(() -> {
            try {
                rawgApiService.getGamesResponseWithFilters(null, "all", "-added", 1);
            } catch (Exception ignored) {
            }
        }, "rawg-cache-warmer").start();
    }
}