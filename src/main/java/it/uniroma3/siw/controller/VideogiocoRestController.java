package it.uniroma3.siw.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.dto.VideogiocoForm;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.VideogiocoService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videogiochi")
public class VideogiocoRestController {

    private final VideogiocoService videogiocoService;
    private final CommentoService commentoService;

    public VideogiocoRestController(VideogiocoService videogiocoService,
                                    CommentoService commentoService) {
        this.videogiocoService = videogiocoService;
        this.commentoService = commentoService;
    }

    /**
     * GET /api/videogiochi -> 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Videogioco>> getTuttiVideogiochi() {
        List<Videogioco> giochi = videogiocoService.findAll();
        return ResponseEntity.ok(giochi);
    }

    /**
     * GET /api/videogiochi/{id} -> 200 OK o 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<Videogioco> getVideogiocoById(@PathVariable Long id) {
        Optional<Videogioco> gioco = videogiocoService.findById(id);
        return gioco.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * POST /api/videogiochi -> 201 CREATED con Location header
     */
    @PostMapping
    public ResponseEntity<Videogioco> creaVideogioco(@Valid @RequestBody VideogiocoForm form) {
        Videogioco nuovo = new Videogioco();
        nuovo.setTitolo(form.getTitolo().trim());
        nuovo.setAnnoUscita(form.getAnnoUscita());
        nuovo.setDescrizione(form.getDescrizione());
        nuovo.setUrlCopertina(form.getUrlCopertina());

        Videogioco salvato = videogiocoService.save(nuovo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvato.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvato);
    }

    /**
     * DELETE /api/videogiochi/{id} -> 204 NO CONTENT o 404 NOT FOUND
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaVideogioco(@PathVariable Long id) {
        Optional<Videogioco> gioco = videogiocoService.findById(id);
        if (gioco.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        videogiocoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/videogiochi/{id}/commenti -> 200 OK o 404 NOT FOUND
     */
    @GetMapping("/{id}/commenti")
    public ResponseEntity<List<Commento>> getCommentiVideogioco(@PathVariable Long id) {
        Optional<Videogioco> gioco = videogiocoService.findById(id);
        if (gioco.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Commento> commenti = commentoService.findRecensioniByVideogioco(gioco.get());
        return ResponseEntity.ok(commenti);
    }
}
