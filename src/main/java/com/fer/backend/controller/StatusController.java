package com.fer.backend.controller;

import com.fer.backend.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/narudzbe/{narudzbaId}/statusi")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @PostMapping
    public String dodajStatus(@PathVariable UUID narudzbaId, @RequestParam String nazivStatusa, RedirectAttributes redirectAttributes) {

        if (nazivStatusa == null || nazivStatusa.isBlank()) {
            redirectAttributes.addFlashAttribute("greska", "Naziv statusa ne smije biti prazan.");
            return "redirect:/narudzbe/" + narudzbaId;
        }

        statusService.dodajStatus(narudzbaId, nazivStatusa);
        redirectAttributes.addFlashAttribute("uspjeh", "Status uspješno dodan.");
        return "redirect:/narudzbe/" + narudzbaId;
    }
}
