package it.uniroma3.siw.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Utente;

public interface RepositoryUtente extends JpaRepository<Utente, Long>{
	
	Optional<Utente> findByUsername(String username);
	
	Optional<Utente> findByEmail(String email);

	boolean existsByEmail(String email);
}