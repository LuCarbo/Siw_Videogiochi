package it.uniroma3.siw.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.repo.RepositoryVideogioco;

import java.util.List;
import java.util.Optional;

@Service
public class VideogiocoService {

    private final RepositoryVideogioco videogiocoRepository;

    public VideogiocoService(RepositoryVideogioco videogiocoRepository) {
        this.videogiocoRepository = videogiocoRepository;
    }

    @Transactional
    public Videogioco save(Videogioco videogioco) {
        return videogiocoRepository.save(videogioco);
    }

    @Transactional(readOnly = true)
    public Optional<Videogioco> findById(Long id) {
        return videogiocoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Videogioco> findByRawgId(Long rawgId) {
        return videogiocoRepository.findByRawgId(rawgId);
    }

    @Transactional(readOnly = true)
    public List<Videogioco> findAll() {
        return videogiocoRepository.findAllWithCommenti();
    }

    @Transactional
    public void deleteById(Long id) {
        videogiocoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Videogioco> findByIdConRecensioni(Long id) {
        return videogiocoRepository.findByIdWithVideogiocoLibreria(id); 
    }
}
