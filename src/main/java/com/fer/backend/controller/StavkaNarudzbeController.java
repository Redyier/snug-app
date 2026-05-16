package com.fer.backend.controller;

import com.fer.backend.dto.StavkaNarudzbeFormDto;
import com.fer.backend.exception.ValidationException;
import com.fer.backend.service.StavkaNarudzbeService;
import com.fer.backend.service.VrstaRubljaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/narudzbe/{narudzbaId}/stavke")
@RequiredArgsConstructor
public class StavkaNarudzbeController {

    private final StavkaNarudzbeService stavkaNarudzbeService;
    private final VrstaRubljaService vrstaRubljaService;

    @GetMapping("/nova")
    public String novaForma(@PathVariable UUID narudzbaId, Model model) {
        model.addAttribute("stavka", new StavkaNarudzbeFormDto());
        model.addAttribute("narudzbaId", narudzbaId);
        model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
        return "stavke/forma";
    }

    @PostMapping
    public String spremi(@PathVariable UUID narudzbaId, @Valid @ModelAttribute("stavka") StavkaNarudzbeFormDto dto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("narudzbaId", narudzbaId);
            model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
            return "stavke/forma";
        }

        try {
            stavkaNarudzbeService.spremi(narudzbaId, dto);
            redirectAttributes.addFlashAttribute("uspjeh", "Stavka uspješno dodana.");
            return "redirect:/narudzbe/" + narudzbaId;
        }

        catch (ValidationException e) {
            model.addAttribute("narudzbaId", narudzbaId);
            model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
            model.addAttribute("greska", e.getMessage());
            return "stavke/forma";
        }
    }

    @GetMapping("/{stavkaId}/uredi")
    public String urediForma(@PathVariable UUID narudzbaId,
                             @PathVariable UUID stavkaId,
                             Model model) {
        model.addAttribute("stavka", stavkaNarudzbeService.findById(stavkaId));
        model.addAttribute("narudzbaId", narudzbaId);
        model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
        return "stavke/forma";
    }

    @PostMapping("/{stavkaId}")
    public String azuriraj(@PathVariable UUID narudzbaId, @PathVariable UUID stavkaId, @Valid @ModelAttribute("stavka") StavkaNarudzbeFormDto dto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("narudzbaId", narudzbaId);
            model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
            return "stavke/forma";
        }

        stavkaNarudzbeService.azuriraj(stavkaId, dto);
        redirectAttributes.addFlashAttribute("uspjeh", "Stavka uspješno ažurirana.");
        return "redirect:/narudzbe/" + narudzbaId;
    }

    @PostMapping("/{stavkaId}/obrisi")
    public String obrisi(@PathVariable UUID narudzbaId,
                         @PathVariable UUID stavkaId,
                         RedirectAttributes redirectAttributes) {
        stavkaNarudzbeService.obrisi(stavkaId);
        redirectAttributes.addFlashAttribute("uspjeh", "Stavka uspješno obrisana.");
        return "redirect:/narudzbe/" + narudzbaId;
    }
}
