package it.uniroma3.siw.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.RepositoryUtente;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final RepositoryUtente utenteRepository;

    public CustomOAuth2UserService(RepositoryUtente utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String username = email != null ? email : oAuth2User.getName();

        Utente utente = utenteRepository.findByUsername(username).orElse(null);
        if (utente == null && email != null) {
            utente = utenteRepository.findByEmail(email).orElse(null);
        }

        if (utente == null) {
            utente = new Utente();
            utente.setUsername(username);
            utente.setEmail(email != null ? email : "oauth_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
            // Genera una password sicura cifrata con BCrypt per impedire accessi abusivi via formLogin
            utente.setPassword(BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()));
            utente.setRuolo("USER");
            utente.setDataRegistrazione(LocalDate.now());
            utente = utenteRepository.save(utente);
        }

        // Sincronizza i ruoli applicativi dell'utente (USER / ADMIN) in Spring Security
        String ruolo = utente.getRuolo() != null ? utente.getRuolo().toUpperCase() : "USER";
        String roleAuth = ruolo.startsWith("ROLE_") ? ruolo : "ROLE_" + ruolo;
        String rawAuth = ruolo.startsWith("ROLE_") ? ruolo.substring(5) : ruolo;

        Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(roleAuth));
        authorities.add(new SimpleGrantedAuthority(rawAuth));

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (userNameAttributeName == null || userNameAttributeName.isEmpty()) {
            userNameAttributeName = "sub";
        }

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }
}
