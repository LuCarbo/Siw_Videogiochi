package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.service.RawgApiService;
import it.uniroma3.siw.service.UtenteService;
import it.uniroma3.siw.service.VideogiocoLibreriaService;
import it.uniroma3.siw.service.VideogiocoService;

import java.util.Optional;

@Controller
public class RawgController {

    private final RawgApiService rawgApiService;
    private final VideogiocoLibreriaService videogiocoLibreriaService;
    private final VideogiocoService videogiocoService;
    private final UtenteService utenteService;

    public RawgController(RawgApiService rawgApiService,
                          VideogiocoLibreriaService videogiocoLibreriaService,
                          VideogiocoService videogiocoService,
                          UtenteService utenteService) {
        this.rawgApiService = rawgApiService;
        this.videogiocoLibreriaService = videogiocoLibreriaService;
        this.videogiocoService = videogiocoService;
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

    // Questa rotta mostra i dettagli di un singolo gioco: se è già nel DB PostgreSQL locale,
    // reindirizza istantaneamente alla scheda locale senza fare richieste HTTP esterne!
    @GetMapping("/rawg/gioco/{id}")
    public String showGameDetails(@PathVariable("id") Long id, Model model) {
        Optional<Videogioco> giocoLocale = videogiocoService.findByRawgId(id);
        if (giocoLocale.isPresent()) {
            return "redirect:/videogioco/" + giocoLocale.get().getId();
        }

        // Passiamo i dettagli del gioco all'HTML per l'anteprima
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
