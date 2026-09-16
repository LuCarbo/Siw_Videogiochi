package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.exception.DuplicateEntityException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Videogioco;
import it.uniroma3.siw.model.dto.VideogiocoForm;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.UtenteService;
import it.uniroma3.siw.service.VideogiocoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VideogiocoService videogiocoService;
    private final CommentoService commentoService;
    private final UtenteService utenteService;

    public AdminController(VideogiocoService videogiocoService,
                           CommentoService commentoService,
                           UtenteService utenteService) {
        this.videogiocoService = videogiocoService;
        this.commentoService = commentoService;
        this.utenteService = utenteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("utenti", utenteService.findAll());
        model.addAttribute("videogiochi", videogiocoService.findAll());
        model.addAttribute("commenti", commentoService.findAll());
        return "admin/dashboard";
    }

    @GetMapping("/videogioco/nuovo")
    public String showNuovoVideogiocoForm(Model model) {
        model.addAttribute("videogiocoForm", new VideogiocoForm());
        return "admin/nuovoVideogioco";
    }

    @PostMapping("/videogioco/nuovo")
    public String salvaNuovoVideogioco(@Valid @ModelAttribute("videogiocoForm") VideogiocoForm form,
                                       BindingResult bindingResult,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/nuovoVideogioco";
        }

        Videogioco videogioco = new Videogioco();
        videogioco.setTitolo(form.getTitolo().trim());
        videogioco.setAnnoUscita(form.getAnnoUscita());
        videogioco.setDescrizione(form.getDescrizione());

        // Gestione immagine: conversione del file caricato in Base64 Data URI
        MultipartFile file = form.getImmagineFile();
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                bindingResult.rejectValue("immagineFile", "error.immagineFile", "Il file caricato deve essere un'immagine valida (PNG, JPG, WEBP)");
                return "admin/nuovoVideogioco";
            }
            try {
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                videogioco.setUrlCopertina("data:" + contentType + ";base64," + base64Data);
            } catch (IOException e) {
                bindingResult.rejectValue("immagineFile", "error.immagineFile", "Errore durante la lettura del file immagine");
                return "admin/nuovoVideogioco";
            }
        } else if (form.getUrlCopertina() != null && !form.getUrlCopertina().isBlank()) {
            videogioco.setUrlCopertina(form.getUrlCopertina().trim());
        }

        try {
            videogioco = videogiocoService.save(videogioco);
            return "redirect:/videogioco/" + videogioco.getId();
        } catch (DuplicateEntityException e) {
            if (e.getFieldName() != null) {
                bindingResult.rejectValue(e.getFieldName(), "error." + e.getFieldName(), e.getMessage());
            } else {
                bindingResult.reject("error.videogioco", e.getMessage());
            }
            return "admin/nuovoVideogioco";
        }
    }

    @PostMapping("/videogioco/elimina/{id}")
    public String eliminaVideogioco(@PathVariable Long id) {
        videogiocoService.deleteById(id);
        return "redirect:/videogiochi";
    }

    @PostMapping("/commento/rimuovi/{id}")
    public String moderaCommento(@PathVariable Long id,
                                 @RequestParam(value = "videogiocoId", required = false) Long videogiocoId) {
        commentoService.eliminaCommentoById(id);
        if (videogiocoId != null) {
            return "redirect:/videogioco/" + videogiocoId;
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/benchmark")
    public String eseguiBenchmark(Model model) {
        java.util.List<Videogioco> tutti = videogiocoService.findAll();
        if (!tutti.isEmpty()) {
            Long id = tutti.get(0).getId();

            long startLazy = System.nanoTime();
            Videogioco vLazy = videogiocoService.findById(id).orElse(null);
            int lazyCount = (vLazy != null && vLazy.getVideogiocoLibreria() != null) ? vLazy.getVideogiocoLibreria().size() : 0;
            double timeLazyMs = (System.nanoTime() - startLazy) / 1_000_000.0;

            long startFetch = System.nanoTime();
            Videogioco vFetch = videogiocoService.findByIdConRecensioni(id).orElse(null);
            int fetchCount = (vFetch != null && vFetch.getVideogiocoLibreria() != null) ? vFetch.getVideogiocoLibreria().size() : 0;
            double timeFetchMs = (System.nanoTime() - startFetch) / 1_000_000.0;

            model.addAttribute("giocoTitolo", vLazy != null ? vLazy.getTitolo() : "N/A");
            model.addAttribute("lazyCount", lazyCount);
            model.addAttribute("timeLazyMs", String.format(java.util.Locale.US, "%.3f", timeLazyMs));
            model.addAttribute("fetchCount", fetchCount);
            model.addAttribute("timeFetchMs", String.format(java.util.Locale.US, "%.3f", timeFetchMs));
            model.addAttribute("eseguito", true);
        } else {
            model.addAttribute("eseguito", false);
        }
        return "admin/benchmark";
    }
}
