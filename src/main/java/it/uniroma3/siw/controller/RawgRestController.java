package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.model.dto.RawgGameDTO;
import it.uniroma3.siw.service.RawgApiService;

@RestController
@RequestMapping("/api/rawg")
public class RawgRestController {

    private final RawgApiService rawgApiService;

    public RawgRestController(RawgApiService rawgApiService) {
        this.rawgApiService = rawgApiService;
    }

    @GetMapping("/popolari")
    public List<RawgGameDTO> getPopularGames() {
        return rawgApiService.getPopularGames();
    }

    @GetMapping("/ricerca")
    public List<RawgGameDTO> searchGames(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "dlc_filter", required = false) String dlcFilter,
            @RequestParam(value = "ordering", required = false) String ordering,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        return rawgApiService.getGamesWithFilters(query, dlcFilter, ordering, page);
    }
}
