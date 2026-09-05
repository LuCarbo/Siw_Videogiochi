package it.uniroma3.siw.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.dto.RegistrazioneForm;
import it.uniroma3.siw.service.UtenteService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UtenteService utenteService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UtenteService utenteService, PasswordEncoder passwordEncoder) {
        this.utenteService = utenteService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registrazioneForm", new RegistrazioneForm());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registrazioneForm") RegistrazioneForm form,
                                      BindingResult bindingResult,
                                      Model model) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Le password non coincidono");
        }

        if (utenteService.existsByUsername(form.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Nome utente già occupato da un altro account");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        utenteService.registraNuovoUtente(form, passwordEncoder);
        return "redirect:/login?registered=true";
    }
}
