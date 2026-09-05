package it.uniroma3.siw.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentoForm {

    private Long videogiocoId;

    @NotNull(message = "Il voto è obbligatorio (da 1 a 10)")
    @Min(value = 1, message = "Il voto deve essere almeno 1")
    @Max(value = 10, message = "Il voto non può superare 10")
    private Integer voto;

    @NotBlank(message = "Il testo del commento non può essere vuoto")
    @Size(max = 1000, message = "Il commento non può superare i 1000 caratteri")
    private String testo;

    public CommentoForm() {
    }

    public CommentoForm(Long videogiocoId, Integer voto, String testo) {
        this.videogiocoId = videogiocoId;
        this.voto = voto;
        this.testo = testo;
    }

    public Long getVideogiocoId() {
        return videogiocoId;
    }

    public void setVideogiocoId(Long videogiocoId) {
        this.videogiocoId = videogiocoId;
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
}
