package it.uniroma3.siw.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repo.RepositoryUtente; // Assicurati che il nome coincida

@Service
public class CustomUserDetailsService implements UserDetailsService {

   private final RepositoryUtente utenteRepository;

   public CustomUserDetailsService(RepositoryUtente utenteRepository) {
       this.utenteRepository = utenteRepository;
   }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

        String ruolo = utente.getRuolo() != null ? utente.getRuolo().toUpperCase() : "USER";
        String roleAuth = ruolo.startsWith("ROLE_") ? ruolo : "ROLE_" + ruolo;
        String rawAuth = ruolo.startsWith("ROLE_") ? ruolo.substring(5) : ruolo;

        return User.builder()
                .username(utente.getUsername())
                .password(utente.getPassword())
                .authorities(roleAuth, rawAuth) 
                .build();
    }
}