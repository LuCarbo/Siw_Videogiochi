package it.uniroma3.siw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "utente", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username")
})
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @JsonIgnore
    @NotBlank
    @Column(nullable = false)
    private String password;

    private LocalDate dataRegistrazione;

    @Column(nullable = false)
    private String ruolo;

    @JsonIgnore
    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideogiocoLibreria> videogiocoLibreria = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "autore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commento> commenti = new ArrayList<>();

    public Utente() {
    }

    @PrePersist
    public void prePersist() {
        if (this.dataRegistrazione == null) {
            this.dataRegistrazione = LocalDate.now();
        }
        if (this.ruolo == null) {
            this.ruolo = "USER";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public List<VideogiocoLibreria> getVideogiocoLibreria() {
        return videogiocoLibreria;
    }

    public void setVideogiocoLibreria(List<VideogiocoLibreria> videogiocoLibreria) {
        this.videogiocoLibreria = videogiocoLibreria;
    }

    @Deprecated
    public List<VideogiocoLibreria> getRecensioni() {
        return videogiocoLibreria;
    }

    @Deprecated
    public void setRecensioni(List<VideogiocoLibreria> videogiocoLibreria) {
        this.videogiocoLibreria = videogiocoLibreria;
    }

    public List<Commento> getCommenti() {
        return commenti;
    }

    public void setCommenti(List<Commento> commenti) {
        this.commenti = commenti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return Objects.equals(username, utente.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", ruolo='" + ruolo + '\'' +
                ", dataRegistrazione=" + dataRegistrazione +
                '}';
    }
}