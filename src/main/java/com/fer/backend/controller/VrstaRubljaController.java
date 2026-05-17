package com.fer.backend.controller;

import com.fer.backend.dto.VrstaRubljaDto;
import com.fer.backend.exception.ValidationException;
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
@RequestMapping("/vrste-rublja")
@RequiredArgsConstructor
public class VrstaRubljaController {

    private final VrstaRubljaService vrstaRubljaService;

    @GetMapping
    public String index(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("vrste", vrstaRubljaService.findAll(search));
        model.addAttribute("search", search);
        return "vrste-rublja/index";
    }

    @GetMapping("/nova")
    public String novaForma(Model model) {
        model.addAttribute("vrstaRublja", new VrstaRubljaDto());
        return "vrste-rublja/forma";
    }

    @PostMapping
    public String spremi(@Valid @ModelAttribute("vrstaRublja") VrstaRubljaDto dto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "vrste-rublja/forma";
        }

        try {
            vrstaRubljaService.spremi(dto);
            redirectAttributes.addFlashAttribute("uspjeh", "Vrsta rublja uspješno dodana.");
            return "redirect:/vrste-rublja";
        }

        catch (ValidationException e) {
            model.addAttribute("greska", e.getMessage());
            return "vrste-rublja/forma";
        }
    }

    @GetMapping("/{id}/uredi")
    public String urediForma(@PathVariable UUID id, Model model) {
        model.addAttribute("vrstaRublja", vrstaRubljaService.findById(id));
        return "vrste-rublja/forma";
    }

    @PostMapping("/{id}")
    public String azuriraj(@PathVariable UUID id, @Valid @ModelAttribute("vrstaRublja") VrstaRubljaDto dto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "vrste-rublja/forma";
        }

        try {
            vrstaRubljaService.azuriraj(id, dto);
            redirectAttributes.addFlashAttribute("uspjeh", "Vrsta rublja uspješno azurirana.");
            return "redirect:/vrste-rublja";
        }

        catch (ValidationException e) {
            model.addAttribute("greska", e.getMessage());
            return "vrste-rublja/forma";
        }
    }

    @PostMapping("/{id}/obrisi")
    public String obrisi(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        vrstaRubljaService.obrisi(id);
        redirectAttributes.addFlashAttribute("uspjeh", "Vrsta rublja uspješno obrisana.");
        return "redirect:/vrste-rublja";
    }
}