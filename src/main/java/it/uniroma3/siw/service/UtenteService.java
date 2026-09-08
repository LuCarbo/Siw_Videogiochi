package it.uniroma3.siw.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.dto.RegistrazioneForm;
import it.uniroma3.siw.repo.RepositoryUtente;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Service
public class UtenteService {

    private final RepositoryUtente utenteRepository;

    public UtenteService(RepositoryUtente utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public Utente save(Utente utente) {
        return utenteRepository.save(utente);
    }

    @Transactional(readOnly = true)
    public Optional<Utente> findById(Long id) {
        return utenteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Utente> findByUsername(String username) {
        return utenteRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return utenteRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<Utente> findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return utenteRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public List<Utente> findAll() {
        return (List<Utente>) utenteRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        utenteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Utente> getCurrentUtente() {
        Long id = getCurrentUserId();
        if (id == null) {
            return Optional.empty();
        }
        return utenteRepository.findById(id);
    }

    @Transactional
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username;
        String email = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            email = oauth2User.getAttribute("email");
            username = email != null ? email : authentication.getName();
        } else {
            username = authentication.getName();
        }

        Utente u = findByUsername(username).orElse(null);
        if (u == null && email != null) {
            u = utenteRepository.findByEmail(email).orElse(null);
        }

        if (u == null) {
            u = new Utente();
            u.setUsername(username);
            u.setEmail(email != null ? email : username + "@oauth.local");
            u.setPassword(org.springframework.security.crypto.bcrypt.BCrypt.hashpw(java.util.UUID.randomUUID().toString(), org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
            u.setRuolo("USER");
            u.setDataRegistrazione(LocalDate.now());
            u = utenteRepository.save(u);
        }
        return u.getId();
    }

    @Transactional
    public Utente registraNuovoUtente(RegistrazioneForm form, PasswordEncoder passwordEncoder) {
        Utente nuovo = new Utente();
        nuovo.setUsername(form.getUsername().trim());
        nuovo.setEmail(form.getEmail().trim());
        nuovo.setPassword(passwordEncoder.encode(form.getPassword()));
        nuovo.setRuolo("USER");
        nuovo.setDataRegistrazione(LocalDate.now());
        return utenteRepository.save(nuovo);
    }
}
