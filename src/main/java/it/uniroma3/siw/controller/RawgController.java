package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import it.uniroma3.siw.service.RawgApiService;
import it.uniroma3.siw.service.UtenteService;
import it.uniroma3.siw.service.VideogiocoLibreriaService;

@Controller
public class RawgController {

    private final RawgApiService rawgApiService;
    private final VideogiocoLibreriaService videogiocoLibreriaService;
    private final UtenteService utenteService;

    public RawgController(RawgApiService rawgApiService, VideogiocoLibreriaService videogiocoLibreriaService,
            UtenteService utenteService) {
        this.rawgApiService = rawgApiService;
        this.videogiocoLibreriaService = videogiocoLibreriaService;
        this.utenteService = utenteService;
    }

    // Questa rotta restituisce la vista dei giochi popolari con filtri
    @GetMapping("/rawg/popolari")
    public String showPopularGames(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "dlc_filter", required = false) String dlcFilter,
            @RequestParam(value = "ordering", required = false) String ordering,
            Model model) {

        model.addAttribute("search", search);
        model.addAttribute("dlc_filter", dlcFilter != null ? dlcFilter : "all");
        model.addAttribute("ordering", ordering != null ? ordering : "-added");

        model.addAttribute("giochi", rawgApiService.getGamesWithFilters(search, dlcFilter, ordering, 1));

        return "rawg_popolari";
    }

    // Questa rotta mostra i dettagli di un singolo gioco preso da RAWG tramite il suo ID
    @GetMapping("/rawg/gioco/{id}")
    public String showGameDetails(@PathVariable("id") Long id, Model model) {
        // Passiamo i dettagli del gioco all'HTML
        model.addAttribute("gioco", rawgApiService.getGameDetails(id));

        Long idUtenteAttuale = utenteService.getCurrentUserId();
        boolean inLibreria = false;
        if (idUtenteAttuale != null) {
            inLibreria = videogiocoLibreriaService.esisteGiocoDaRawgInLibreria(idUtenteAttuale, id);
        }
        // Passiamo la variabile "isInLibreria" all'HTML
        model.addAttribute("isInLibreria", inLibreria);
        return "rawg_dettagli";
    }
}
