package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.VideogiocoLibreria;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.dto.CommentoForm;
import it.uniroma3.siw.service.UtenteService;
import it.uniroma3.siw.service.VideogiocoLibreriaService;
import it.uniroma3.siw.service.VideogiocoService;
import it.uniroma3.siw.service.CommentoService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class VideogiocoLibreriaController {

    private final VideogiocoLibreriaService videogiocoLibreriaService;
    private final UtenteService utenteService;
    private final CommentoService commentoService;
    private final VideogiocoService videogiocoService;

    public VideogiocoLibreriaController(VideogiocoLibreriaService videogiocoLibreriaService,
            UtenteService utenteService,
            CommentoService commentoService,
            VideogiocoService videogiocoService) {
        this.videogiocoLibreriaService = videogiocoLibreriaService;
        this.utenteService = utenteService;
        this.commentoService = commentoService;
        this.videogiocoService = videogiocoService;
    }

    // Questa rotta gestisce il salvataggio di un nuovo gioco nella libreria
    @PostMapping("/libreria/aggiungi")
    public String addVideogiocoLibreria(@ModelAttribute VideogiocoLibreria videogiocoLibreria) {
        videogiocoLibreriaService.save(videogiocoLibreria);
        return "redirect:/videogioco/" + videogiocoLibreria.getVideogioco().getId();
    }

    // Questa rotta gestisce l'aggiunta di un gioco proveniente dalle API di RAWG
    @PostMapping("/libreria/aggiungiDaRawg")
    public String aggiungiGiocoDaRawg(@RequestParam("rawgId") Long rawgId) {
        Long idUtenteAttuale = utenteService.getCurrentUserId();
        videogiocoLibreriaService.aggiungiDaRawgALibreria(idUtenteAttuale, rawgId);
        return "redirect:/rawg/gioco/" + rawgId;
    }

    // Rotta per inserire o modificare la recensione con Bean Validation server-side
    @PostMapping("/libreria/recensisci")
    public String recensisciGioco(@Valid @ModelAttribute("commentoForm") CommentoForm commentoForm,
                                  BindingResult bindingResult,
                                  Model model) {
        Long idUtenteAttuale = utenteService.getCurrentUserId();
        if (idUtenteAttuale == null) {
            return "redirect:/login";
        }

        Long videogiocoId = commentoForm.getVideogiocoId();
        if (videogiocoId == null) {
            return "redirect:/videogiochi";
        }

        Videogioco videogioco = videogiocoService.findById(videogiocoId).orElse(null);
        if (videogioco == null) {
            return "redirect:/videogiochi";
        }

        if (bindingResult.hasErrors()) {
            Utente utente = utenteService.findById(idUtenteAttuale).orElse(null);
            model.addAttribute("videogioco", videogioco);
            model.addAttribute("inLibreria", videogiocoLibreriaService.isGiocoInLibreria(utente, videogioco));
            model.addAttribute("recensione", commentoService.findRecensione(utente, videogioco));
            model.addAttribute("altreRecensioni", commentoService.findRecensioniCommunity(videogioco, idUtenteAttuale));
            model.addAttribute("haErroriValidazione", true);
            return "videogioco";
        }

        commentoService.aggiornaVotoECommento(idUtenteAttuale, videogiocoId, commentoForm.getVoto(), commentoForm.getTesto());
        return "redirect:/videogioco/" + videogiocoId;
    }

    // Rotta per eliminare la recensione
    @PostMapping("/libreria/recensione/rimuovi")
    public String rimuoviRecensione(@RequestParam("videogiocoId") Long videogiocoId) {
        Long idUtenteAttuale = utenteService.getCurrentUserId();
        commentoService.rimuoviCommento(idUtenteAttuale, videogiocoId);
        return "redirect:/videogioco/" + videogiocoId;
    }

    // Questa rotta gestisce la rimozione di un gioco dalla libreria personale
    @PostMapping("/libreria/rimuovi")
    public String rimuoviGiocoDaLibreria(@RequestParam("videogiocoId") Long videogiocoId) {

        Long idUtenteAttuale = utenteService.getCurrentUserId();
        videogiocoLibreriaService.rimuoviDaLibreria(idUtenteAttuale, videogiocoId);

        // Reindirizza alla libreria personale
        return "redirect:/libreria";
    }

    // Questa rotta mostra la pagina della libreria personale
    @GetMapping("/libreria")
    public String mostraLibreriaPersonale(Model model) {

        Long idUtenteAttuale = utenteService.getCurrentUserId();

        // Chiama il service
        List<Videogioco> videogiochiDellUtente = videogiocoLibreriaService
                .ottieniVideogiochiLibreriaUtente(idUtenteAttuale);

        // Passa la lista all'HTML
        model.addAttribute("videogiochi", videogiochiDellUtente);

        // Restituisce il file "libreria.html"
        return "libreria";
    }
}