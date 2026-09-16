package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "videogioco", uniqueConstraints = {
    @UniqueConstraint(columnNames = "rawgId")
})
public class Videogioco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private Long rawgId;

    @NotBlank
    @Column(nullable = false)
    private String titolo;

    @Column(length = 2000)
    private String descrizione;
    
    private Integer annoUscita;
    
    @Column(columnDefinition = "TEXT")
    private String urlCopertina;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "videogioco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideogiocoLibreria> videogiocoLibreria = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "videogioco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commento> commenti = new ArrayList<>();
    
    public Videogioco() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRawgId() {
		return rawgId;
	}

	public void setRawgId(Long rawgId) {
		this.rawgId = rawgId;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getAnnoUscita() {
		return annoUscita;
	}

	public void setAnnoUscita(Integer annoUscita) {
		this.annoUscita = annoUscita;
	}

	public String getUrlCopertina() {
		return urlCopertina;
	}

	public void setUrlCopertina(String urlCopertina) {
		this.urlCopertina = urlCopertina;
	}

	public List<VideogiocoLibreria> getVideogiocoLibreria() {
		return videogiocoLibreria;
	}

	public void setVideogiocoLibreria(List<VideogiocoLibreria> videogiocoLibreria) {
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
        Videogioco that = (Videogioco) o;
        if (rawgId != null && that.rawgId != null) {
            return Objects.equals(rawgId, that.rawgId);
        }
        return Objects.equals(titolo, that.titolo) && Objects.equals(annoUscita, that.annoUscita);
    }

    @Override
    public int hashCode() {
        if (rawgId != null) {
            return Objects.hash(rawgId);
        }
        return Objects.hash(titolo, annoUscita);
    }

    @Override
    public String toString() {
        return "Videogioco{" +
                "id=" + id +
                ", rawgId=" + rawgId +
                ", titolo='" + titolo + '\'' +
                ", annoUscita=" + annoUscita +
                '}';
    }
}