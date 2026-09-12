package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Videogioco;

public interface RepositoryVideogioco extends JpaRepository<Videogioco, Long> {

    @Query("SELECT v FROM Videogioco v LEFT JOIN FETCH v.videogiocoLibreria WHERE v.id = :id")
    public Optional<Videogioco> findByIdWithVideogiocoLibreria(@Param("id") Long id);

    @Query("SELECT DISTINCT v FROM Videogioco v LEFT JOIN FETCH v.commenti")
    public List<Videogioco> findAllWithCommenti();

    Optional<Videogioco> findByRawgId(Long rawgId);

    boolean existsByTitoloIgnoreCase(String titolo);

    boolean existsByTitoloIgnoreCaseAndIdNot(String titolo, Long id);

}
