package it.uniroma3.siw.repo;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.Videogioco;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RepositoryCommento extends JpaRepository<Commento, Long> {
    
    Optional<Commento> findByAutoreAndVideogioco(Utente autore, Videogioco videogioco);
    
    List<Commento> findByVideogioco(Videogioco videogioco);

    List<Commento> findByVideogiocoAndAutoreNot(Videogioco videogioco, Utente autore);

    List<Commento> findByAutore(Utente autore);
}
