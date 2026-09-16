package it.uniroma3.siw.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

public class VideogiocoForm {

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(max = 255, message = "Il titolo non può superare i 255 caratteri")
    private String titolo;

    @Min(value = 1950, message = "L'anno di uscita non può essere anteriore al 1950")
    @Max(value = 2100, message = "L'anno di uscita non può superare il 2100")
    private Integer annoUscita;

    @Size(max = 2000, message = "La descrizione non può superare i 2000 caratteri")
    private String descrizione;

    private String urlCopertina;

    @JsonIgnore
    private MultipartFile immagineFile;

    public VideogiocoForm() {
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Integer getAnnoUscita() {
        return annoUscita;
    }

    public void setAnnoUscita(Integer annoUscita) {
        this.annoUscita = annoUscita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getUrlCopertina() {
        return urlCopertina;
    }

    public void setUrlCopertina(String urlCopertina) {
        this.urlCopertina = urlCopertina;
    }

    public MultipartFile getImmagineFile() {
        return immagineFile;
    }

    public void setImmagineFile(MultipartFile immagineFile) {
        this.immagineFile = immagineFile;
    }
}
