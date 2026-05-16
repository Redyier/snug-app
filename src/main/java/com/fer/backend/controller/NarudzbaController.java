package com.fer.backend.controller;

import com.fer.backend.dto.NarudzbaFormDto;
import com.fer.backend.dto.ObrtSCjenomDto;
import com.fer.backend.dto.StatusDto;
import com.fer.backend.dto.StavkaPretragaDto;
import com.fer.backend.model.enums.StatusNarudzbe;
import com.fer.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/narudzbe")
@RequiredArgsConstructor
public class NarudzbaController {

    private final NarudzbaService narudzbaService;
    private final PrivatnaOsobaService privatnaOsobaService;
    private final ObrtService obrtService;
    private final AdresaService adresaService;
    private final StatusService statusService;
    private final VrstaRubljaService vrstaRubljaService;

    @GetMapping
    public String index(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("narudzbe", narudzbaService.findAll(search));
        model.addAttribute("search", search);
        return "narudzbe/index";
    }

    @GetMapping("/nova")
    public String novaForma(Model model) {
        NarudzbaFormDto dto = new NarudzbaFormDto();
        dto.setStavke(new ArrayList<>(List.of(new StavkaPretragaDto())));
        model.addAttribute("narudzba", dto);
        popuniDropdowne(model);
        return "narudzbe/nova";
    }

    @PostMapping("/potvrdi")
    public String potvrdi(@ModelAttribute("narudzba") NarudzbaFormDto dto, RedirectAttributes redirectAttributes) {

        UUID narudzbaId = narudzbaService.save(dto);
        redirectAttributes.addFlashAttribute("uspjeh", "Narudžba uspješno kreirana i zaprimljena!");
        return "redirect:/narudzbe/" + narudzbaId;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("narudzba", narudzbaService.findById(id));
        model.addAttribute("stavke", narudzbaService.findStavke(id));
        model.addAttribute("statusi", statusService.findByNarudzbaId(id));

        StatusDto trenutni = statusService.findTrenutniStatus(id);
        model.addAttribute("trenutniStatus", trenutni);

        if (trenutni != null) {
            StatusNarudzbe trenutniEnum =
                    StatusNarudzbe.fromNaziv(trenutni.getNazivStatusa());
            if (trenutniEnum != null) {
                model.addAttribute("dostupniStatusi",
                        trenutniEnum.getMogucaSljedeciStatusi());
            } else {
                model.addAttribute("dostupniStatusi", List.of());
            }
        } else {
            model.addAttribute("dostupniStatusi", List.of());
        }

        popuniDropdowne(model);
        return "narudzbe/detail";
    }

    @PostMapping("/{id}")
    public String azuriraj(@PathVariable UUID id, @Valid @ModelAttribute("narudzba") NarudzbaFormDto dto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("stavke", narudzbaService.findStavke(id));
            model.addAttribute("statusi", statusService.findByNarudzbaId(id));
            model.addAttribute("trenutniStatus", statusService.findTrenutniStatus(id));
            popuniDropdowne(model);
            return "narudzbe/detail";
        }

        narudzbaService.update(id, dto);
        redirectAttributes.addFlashAttribute("uspjeh", "Narudžba uspješno ažurirana.");
        return "redirect:/narudzbe/" + id;
    }

    @PostMapping("/{id}/obrisi")
    public String obrisi(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        narudzbaService.delete(id);
        redirectAttributes.addFlashAttribute("uspjeh", "Narudžba uspješno obrisana.");
        return "redirect:/narudzbe";
    }

    @PostMapping("/pretraga")
    public String pretraga(@Valid @ModelAttribute("narudzba") NarudzbaFormDto dto,
                           BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            result.getAllErrors().forEach(e -> System.out.println("ERROR: " + e));
            popuniDropdowne(model);
            return "narudzbe/nova";
        }

        List<ObrtSCjenomDto> obrtiSCijenama =
                narudzbaService.pretraga(dto.getStavke(), dto.getTerminPrikupa());

        boolean imaDostupnih = obrtiSCijenama.stream()
                .anyMatch(ObrtSCjenomDto::isDostupan);

        if (!imaDostupnih) {
            model.addAttribute("greska", "Nema dostupnih obrta za odabrane vrste rublja.");
            popuniDropdowne(model);
            return "narudzbe/nova";
        }

        model.addAttribute("narudzba", dto);
        model.addAttribute("obrtiSCijenama", obrtiSCijenama);
        model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
        popuniDropdowne(model);
        return "narudzbe/izracun";
    }

    private void popuniDropdowne(Model model) {
        model.addAttribute("privatneOsobe", privatnaOsobaService.findAll());
        model.addAttribute("obrti", obrtService.findAll());
        model.addAttribute("adrese", adresaService.findAll());
        model.addAttribute("vrsteRublja", vrstaRubljaService.findAll(null));
    }
}
