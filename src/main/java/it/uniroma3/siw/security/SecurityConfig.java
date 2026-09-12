package it.uniroma3.siw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/videogioco/**", "/videogiochi", "/css/**", "/images/**",
                                "/js/**", "/error", "/register",
                                "/rawg/popolari", "/rawg/gioco/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rawg/**", "/api/videogiochi/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/videogiochi/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/videogiochi/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/libreria/**").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)))

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Metodo di supporto per la navbar (Thymeleaf): restituisce il nome visualizzato dell'utente loggato.
     * Gestisce sia gli utenti classici (username) sia gli utenti Google OAuth2 (nome reale anziché sub numerico).
     */
    public String getUserDisplayName() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return "Utente";
            }

            Object principal = authentication.getPrincipal();

            // 1. Utente con form login classico (UserDetails) -> ritorna 'mario', 'admin', ecc.
            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            }

            // 2. Utente con Google OAuth2 -> estrae il nome reale di Google anziché il sub numerico
            if (principal instanceof OAuth2User oauth2User) {
                Object name = oauth2User.getAttribute("name");
                if (name != null && !name.toString().isBlank() && !name.toString().matches("\\d+")) {
                    return name.toString().trim();
                }
                Object givenName = oauth2User.getAttribute("given_name");
                if (givenName != null && !givenName.toString().isBlank() && !givenName.toString().matches("\\d+")) {
                    return givenName.toString().trim();
                }
                Object email = oauth2User.getAttribute("email");
                if (email != null && !email.toString().isBlank()) {
                    return email.toString().trim();
                }
            }

            // 3. Fallback sul nome dell'autenticazione se non è solo numerico
            String authName = authentication.getName();
            if (authName != null && !authName.matches("\\d+")) {
                return authName;
            }
        } catch (Exception ignored) {
        }
        return "Utente";
    }
}