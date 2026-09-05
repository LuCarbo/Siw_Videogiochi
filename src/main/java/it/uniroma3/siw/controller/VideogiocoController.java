package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.dto.CommentoForm;
import it.uniroma3.siw.service.UtenteService;
import it.uniroma3.siw.service.VideogiocoService;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.VideogiocoLibreriaService;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class VideogiocoController {

    private final VideogiocoService videogiocoService;
    private final UtenteService utenteService;
    private final CommentoService commentoService;
    private final VideogiocoLibreriaService videogiocoLibreriaService;

    public VideogiocoController(VideogiocoService videogiocoService,
            UtenteService utenteService,
            CommentoService commentoService,
            VideogiocoLibreriaService videogiocoLibreriaService) {
        this.videogiocoService = videogiocoService;
        this.utenteService = utenteService;
        this.commentoService = commentoService;
        this.videogiocoLibreriaService = videogiocoLibreriaService;
    }

    @GetMapping("/videogiochi")
    public String showVideogiochi(Model model) {
        model.addAttribute("videogiochi", videogiocoService.findAll());
        return "videogiochi";
    }

    @GetMapping("/videogioco/{id}")
    public String showVideogioco(@PathVariable Long id, Model model) {
        Videogioco videogioco = videogiocoService.findById(id).orElse(null);
        if (videogioco == null) {
            return "redirect:/videogiochi";
        }
        model.addAttribute("videogioco", videogioco);

        Long idUtenteAttuale = utenteService.getCurrentUserId();
        Commento recensione = null;
        boolean inLibreria = false;

        if (idUtenteAttuale != null) {
            Utente utente = utenteService.findById(idUtenteAttuale).orElse(null);
            if (utente != null) {
                inLibreria = videogiocoLibreriaService.isGiocoInLibreria(utente, videogioco);
                recensione = commentoService.findRecensione(utente, videogioco);
            }
        }

        model.addAttribute("inLibreria", inLibreria);
        model.addAttribute("recensione", recensione);

        if (!model.containsAttribute("commentoForm")) {
            CommentoForm form = new CommentoForm();
            form.setVideogiocoId(videogioco.getId());
            if (recensione != null) {
                form.setVoto(recensione.getVoto());
                form.setTesto(recensione.getTesto());
            }
            model.addAttribute("commentoForm", form);
        }

        // Recupero delegato al Service (senza filtri in-memory nel Controller)
        List<Commento> altreRecensioni = commentoService.findRecensioniCommunity(videogioco, idUtenteAttuale);
        model.addAttribute("altreRecensioni", altreRecensioni);

        return "videogioco";
    }

    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/rawg/popolari";
    }
}