package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import it.uniroma3.siw.model.dto.RawgGameDTO;
import it.uniroma3.siw.model.dto.RawgResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RawgApiService {

    @Value("${rawg.api.key}")
    private String apiKey;

    @Value("${rawg.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public RawgApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RawgGameDTO getGameById(Long rawgId) {

        // Creiamo lo strumento di Spring Boot per fare richieste su Internet
        RestTemplate restTemplate = new RestTemplate();

        // indirizzo finale da chiamare
        String url = baseUrl + rawgId + "?key=" + apiKey;

        try {
            // richiesta HTTP GET.
            RawgGameDTO giocoTrovato = restTemplate.getForObject(url, RawgGameDTO.class);
            return giocoTrovato;

        } catch (Exception e) {
            // Se RAWG dà un errore lo cattura
            System.out.println("Attenzione! Errore durante il recupero del gioco da RAWG: " + e.getMessage());
            return null;
        }
    }

    // Recupera una lista dei giochi più popolari (o aggiunti di recente).
    public List<RawgGameDTO> getPopularGames() {
        return fetchGamesList(UriComponentsBuilder.fromUriString(baseUrl)
                .path("/games")
                .queryParam("key", apiKey)
                .queryParam("ordering", "-added")
                .queryParam("page_size", 20)
                .toUriString());
    }

   
    //Cerca giochi tramite query testuale.
    public List<RawgGameDTO> searchGames(String query) {
        return fetchGamesList(UriComponentsBuilder.fromUriString(baseUrl)
                .path("/games")
                .queryParam("key", apiKey)
                .queryParam("search", query)
                .queryParam("page_size", 20)
                .toUriString());
    }

    //Recupera i giochi con filtri avanzati.
    public List<RawgGameDTO> getGamesWithFilters(String query, String dlcFilter, String ordering, Integer page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/games")
                .queryParam("key", apiKey)
                .queryParam("page_size", 20);

        if (page != null && page > 0) {
            builder.queryParam("page", page);
        }

        if (query != null && !query.trim().isEmpty()) {
            builder.queryParam("search", query);
        }

        if ("games".equalsIgnoreCase(dlcFilter)) {
            builder.queryParam("exclude_additions", true);
        } else if ("dlc".equalsIgnoreCase(dlcFilter)) {
            builder.queryParam("tags", "dlc");
        }

        if (ordering != null && !ordering.trim().isEmpty()) {
            builder.queryParam("ordering", ordering);
            if (ordering.contains("released")) {
                // Il gioco deve essere rilasciato
                String today = java.time.LocalDate.now().toString();
                builder.queryParam("dates", "1950-01-01," + today);
            }
        } else if (query == null || query.trim().isEmpty()) {
            // Ordine predefinito per i popolari
            builder.queryParam("ordering", "-added");
        }

        return fetchGamesList(builder.toUriString());
    }

    private List<RawgGameDTO> fetchGamesList(String url) {
        try {
            RawgResponseDTO response = restTemplate.getForObject(url, RawgResponseDTO.class);
            if (response != null && response.getResults() != null) {
                return response.getResults();
            }
        } catch (Exception e) {
            System.err.println("Errore durante la comunicazione con RAWG: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    
    // Recupera i dettagli completi di un singolo gioco tramite il suo ID di RAWG.
    public RawgGameDTO getGameDetails(Long rawgId) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/games/" + rawgId)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            RawgGameDTO game = restTemplate.getForObject(url, RawgGameDTO.class);
            if (game != null) {

                // Recupero URL reali degli store
                if (game.getStores() != null) {
                    String storesLinksUrl = UriComponentsBuilder.fromUriString(baseUrl)
                            .path("/games/" + rawgId + "/stores")
                            .queryParam("key", apiKey)
                            .toUriString();
                    try {
                        Map<String, Object> storesResponse = restTemplate.getForObject(storesLinksUrl, Map.class);
                        if (storesResponse != null && storesResponse.containsKey("results")) {
                            List<Map<String, Object>> results = (List<Map<String, Object>>) storesResponse
                                    .get("results");
                            Map<Integer, String> storeUrlMap = new java.util.HashMap<>();
                            for (Map<String, Object> res : results) {
                                if (res.containsKey("store_id") && res.containsKey("url")) {
                                    storeUrlMap.put(((Number) res.get("store_id")).intValue(), (String) res.get("url"));
                                }
                            }
                            for (Map<String, Object> storeWrapper : game.getStores()) {
                                if (storeWrapper.containsKey("store") && storeWrapper.get("store") != null) {
                                    Map<String, Object> storeDetails = (Map<String, Object>) storeWrapper.get("store");
                                    if (storeDetails.containsKey("id")) {
                                        int sId = ((Number) storeDetails.get("id")).intValue();
                                        if (storeUrlMap.containsKey(sId)) {
                                            storeWrapper.put("url", storeUrlMap.get(sId));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Errore recupero link stores: " + e.getMessage());
                    }
                }
                String screenshotsUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/games/" + rawgId + "/screenshots")
                        .queryParam("key", apiKey)
                        .toUriString();

                try {
                    Map<String, Object> screenshotsResponse = restTemplate.getForObject(screenshotsUrl, Map.class);
                    if (screenshotsResponse != null && screenshotsResponse.containsKey("results")) {
                        List<Map<String, Object>> results = (List<Map<String, Object>>) screenshotsResponse
                                .get("results");
                        List<String> images = new java.util.ArrayList<>();
                        for (Map<String, Object> res : results) {
                            if (res.containsKey("image")) {
                                images.add((String) res.get("image"));
                            }
                        }
                        game.setScreenshots(images);
                    }
                } catch (Exception e) {
                    System.err.println("Errore recupero screenshots: " + e.getMessage());
                }
            }
            return game;
        } catch (Exception e) {
            System.err.println("Errore durante il recupero dei dettagli del gioco: " + e.getMessage());
            return null;
        }
    }
}
