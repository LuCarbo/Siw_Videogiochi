package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "videogioco_libreria", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"utente_id", "videogioco_id"})
})
public class VideogiocoLibreria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate dataAggiunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videogioco_id")
    private Videogioco videogioco;

    public VideogiocoLibreria() {
    }

    @PrePersist
    public void prePersist() {
        if (this.dataAggiunta == null) {
            this.dataAggiunta = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAggiunta() {
        return dataAggiunta;
    }

    public void setDataAggiunta(LocalDate dataAggiunta) {
        this.dataAggiunta = dataAggiunta;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Videogioco getVideogioco() {
        return videogioco;
    }

    public void setVideogioco(Videogioco videogioco) {
        this.videogioco = videogioco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideogiocoLibreria that = (VideogiocoLibreria) o;
        return Objects.equals(utente, that.utente) && Objects.equals(videogioco, that.videogioco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(utente, videogioco);
    }

    @Override
    public String toString() {
        return "VideogiocoLibreria{" +
                "id=" + id +
                ", dataAggiunta=" + dataAggiunta +
                '}';
    }
}