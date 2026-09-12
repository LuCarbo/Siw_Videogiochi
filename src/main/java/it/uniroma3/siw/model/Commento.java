package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "commento", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"autore_id", "videogioco_id"})
}, indexes = {
    @Index(name = "idx_commento_videogioco", columnList = "videogioco_id")
})
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Il voto è obbligatorio")
    @Min(value = 1, message = "Il voto minimo è 1")
    @Max(value = 10, message = "Il voto massimo è 10")
    @Column(nullable = false)
    private Integer voto;

    @NotBlank(message = "Il commento non può essere vuoto")
    @Size(max = 1000, message = "Il commento non può superare i 1000 caratteri")
    @Column(length = 1000, nullable = false)
    private String testo;

    private LocalDate dataScrittura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autore_id")
    private Utente autore;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videogioco_id")
    private Videogioco videogioco;

    public Commento() {
    }

    @PrePersist
    public void prePersist() {
        if (this.dataScrittura == null) {
            this.dataScrittura = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public LocalDate getDataScrittura() {
        return dataScrittura;
    }

    public void setDataScrittura(LocalDate dataScrittura) {
        this.dataScrittura = dataScrittura;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
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
        Commento commento = (Commento) o;
        return Objects.equals(autore, commento.autore) && Objects.equals(videogioco, commento.videogioco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(autore, videogioco);
    }

    @Override
    public String toString() {
        return "Commento{" +
                "id=" + id +
                ", voto=" + voto +
                ", dataScrittura=" + dataScrittura +
                '}';
    }
}
