package it.uniroma3.siw.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.VideogiocoLibreria;
import it.uniroma3.siw.model.dto.RawgGameDTO;
import it.uniroma3.siw.repository.RepositoryUtente;
import it.uniroma3.siw.repository.RepositoryVideogioco;
import it.uniroma3.siw.repository.RepositoryVideogiocoLibreria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideogiocoLibreriaService {

    private final RepositoryVideogiocoLibreria videogiocoLibreriaRepository;
    private final RawgApiService rawgApiService;
    private final RepositoryVideogioco videogiocoRepository;
    private final RepositoryUtente utenteRepository;

    public VideogiocoLibreriaService(RepositoryVideogiocoLibreria videogiocoLibreriaRepository,
            RawgApiService rawgApiService,
            RepositoryVideogioco videogiocoRepository,
            RepositoryUtente utenteRepository) {
        this.videogiocoLibreriaRepository = videogiocoLibreriaRepository;
        this.rawgApiService = rawgApiService;
        this.videogiocoRepository = videogiocoRepository;
        this.utenteRepository = utenteRepository;
    }

    @Transactional(readOnly = true)
    public boolean esisteGiocoDaRawgInLibreria(Long idUtente, Long rawgId) {
        if (idUtente == null || rawgId == null) {
            return false;
        }
        Utente utente = utenteRepository.findById(idUtente).orElse(null);
        Videogioco videogioco = videogiocoRepository.findByRawgId(rawgId).orElse(null);

        return isGiocoInLibreria(utente, videogioco);
    }

    @Transactional(readOnly = true)
    public boolean isGiocoInLibreria(Utente utente, Videogioco videogioco) {
        if (utente != null && videogioco != null) {
            return videogiocoLibreriaRepository.existsByUtenteAndVideogioco(utente, videogioco);
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Videogioco> ottieniVideogiochiLibreriaUtente(Long idUtente) {
        if (idUtente == null) {
            return List.of();
        }
        // Recupera tutti i collegamenti Utente-Gioco dal database
        List<VideogiocoLibreria> elementiLibreria = videogiocoLibreriaRepository.findByUtenteId(idUtente);

        // Estrae solo gli oggetti "Videogioco" dalla libreria
        return elementiLibreria.stream()
                .map(VideogiocoLibreria::getVideogioco)
                .collect(Collectors.toList());
    }

    // Recupera i dettagli del gioco da RAWG prima di aprire la transazione sul database,
    // in modo da non occupare la connessione al database durante la richiesta HTTP.
    public Videogioco aggiungiDaRawgALibreria(Long utenteId, Long rawgId) {
        if (utenteId == null || rawgId == null) {
            return null;
        }

        // Verifica preliminare (read-only) se il gioco esiste già nel DB locale
        Videogioco videogioco = videogiocoRepository.findByRawgId(rawgId).orElse(null);
        RawgGameDTO dto = null;

        // Se non esiste, chiama l'API esterna FUORI dalla transazione DB
        if (videogioco == null) {
            dto = rawgApiService.getGameDetails(rawgId);
            if (dto == null) {
                return null;
            }
        }

        // Persiste in modo atomico nel DB e restituisce il videogioco salvato
        return salvaGiocoELibreriaNelDb(utenteId, rawgId, dto);
    }

    @Transactional
    public Videogioco salvaGiocoELibreriaNelDb(Long utenteId, Long rawgId, RawgGameDTO dto) {
        Utente utente = utenteRepository.findById(utenteId).orElse(null);
        if (utente == null) {
            return null;
        }

        Videogioco videogioco = videogiocoRepository.findByRawgId(rawgId).orElse(null);
        if (videogioco == null && dto != null) {
            videogioco = new Videogioco();
            videogioco.setRawgId(dto.getId());
            videogioco.setTitolo(dto.getName());
            videogioco.setUrlCopertina(dto.getBackground_image());
            if (dto.getReleased() != null && dto.getReleased().length() >= 4) {
                try {
                    videogioco.setAnnoUscita(Integer.parseInt(dto.getReleased().substring(0, 4)));
                } catch (NumberFormatException ignored) {}
            }
            if (dto.getDescription_raw() != null && !dto.getDescription_raw().trim().isEmpty()) {
                String desc = dto.getDescription_raw().trim();
                videogioco.setDescrizione(desc.length() > 1900 ? desc.substring(0, 1900) + "..." : desc);
            }
            videogioco = videogiocoRepository.save(videogioco);
        }

        if (videogioco != null && !videogiocoLibreriaRepository.existsByUtenteAndVideogioco(utente, videogioco)) {
            VideogiocoLibreria nuovaAggiunta = new VideogiocoLibreria();
            nuovaAggiunta.setUtente(utente);
            nuovaAggiunta.setVideogioco(videogioco);
            nuovaAggiunta.setDataAggiunta(LocalDate.now());
            videogiocoLibreriaRepository.save(nuovaAggiunta);
        }

        return videogioco;
    }

    @Transactional
    public void rimuoviDaLibreria(Long idUtente, Long videogiocoId) {
        if (idUtente == null || videogiocoId == null) {
            return;
        }
        Utente utente = utenteRepository.findById(idUtente).orElse(null);
        Videogioco videogioco = videogiocoRepository.findById(videogiocoId).orElse(null);

        if (utente != null && videogioco != null) {
            VideogiocoLibreria collegamento = videogiocoLibreriaRepository.findByUtenteAndVideogioco(utente, videogioco)
                    .orElse(null);

            if (collegamento != null) {
                videogiocoLibreriaRepository.delete(collegamento);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<VideogiocoLibreria> findAll() {
        return videogiocoLibreriaRepository.findAll();
    }

    @Transactional
    public void aggiungiVideogiocoALibreria(Long utenteId, Long videogiocoId) {
        if (utenteId == null || videogiocoId == null) {
            return;
        }
        Utente utente = utenteRepository.findById(utenteId).orElse(null);
        Videogioco videogioco = videogiocoRepository.findById(videogiocoId).orElse(null);

        if (utente != null && videogioco != null && !videogiocoLibreriaRepository.existsByUtenteAndVideogioco(utente, videogioco)) {
            VideogiocoLibreria nuovaAggiunta = new VideogiocoLibreria();
            nuovaAggiunta.setUtente(utente);
            nuovaAggiunta.setVideogioco(videogioco);
            nuovaAggiunta.setDataAggiunta(LocalDate.now());
            videogiocoLibreriaRepository.save(nuovaAggiunta);
        }
    }

    @Transactional
    public void save(VideogiocoLibreria videogiocoLibreria) {
        if (videogiocoLibreria.getDataAggiunta() == null) {
            videogiocoLibreria.setDataAggiunta(LocalDate.now());
        }
        videogiocoLibreriaRepository.save(videogiocoLibreria);
    }

    @Transactional(readOnly = true)
    public Optional<VideogiocoLibreria> findById(Long id) {
        return videogiocoLibreriaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<VideogiocoLibreria> findByVideogiocoId(Long videogiocoId) {
        return videogiocoLibreriaRepository.findByVideogiocoId(videogiocoId);
    }

    @Transactional(readOnly = true)
    public List<VideogiocoLibreria> findByUtenteId(Long utenteId) {
        return videogiocoLibreriaRepository.findByUtenteId(utenteId);
    }

    @Transactional
    public void deleteById(Long id) {
        videogiocoLibreriaRepository.deleteById(id);
    }
}