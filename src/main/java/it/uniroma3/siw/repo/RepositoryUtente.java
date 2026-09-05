package it.uniroma3.siw.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Utente;

public interface RepositoryUtente extends CrudRepository<Utente, Long>{
	
	Optional<Utente> findByUsername(String username);
		
}