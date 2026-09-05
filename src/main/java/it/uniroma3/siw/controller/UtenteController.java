package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.service.UtenteService;

@Controller
public class UtenteController {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping("/utenti")
    public String showUtenti(Model model) {
        model.addAttribute("utenti", utenteService.findAll());
        return "utenti";
    }

    @GetMapping("/utente/{id}")
    public String showProfiloUtente(@PathVariable Long id, Model model) {
        model.addAttribute("utente", utenteService.findById(id).orElse(null));
        return "profiloUtente";
    }
}