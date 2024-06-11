package cz.czechitas.java2webapps.lekce8.controller;

import cz.czechitas.java2webapps.lekce8.entity.Osoba;
import cz.czechitas.java2webapps.lekce8.repository.OsobaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class OsobaController {

    private final OsobaRepository repository;

    private final List<Osoba> seznamOsob = List.of(
            new Osoba(1L, "Božena", "Němcová", LocalDate.of(1820, 2, 4), "Vídeň", null, null)
    );

    @Autowired
    public OsobaController(OsobaRepository repository) {
        this.repository = repository;
    }


    @InitBinder
    public void nullStringBinding(WebDataBinder binder) {
        //prázdné textové řetězce nahradit null hodnotou
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/")
    public ModelAndView seznam() {
        return new ModelAndView("seznam")
//                .addObject("osoby", seznamOsob);
                .addObject("osoby", repository.findAll());
    }

    @GetMapping("/novy")
    public ModelAndView novy() {
        return new ModelAndView("detail")
                .addObject("osoba", new Osoba());
    }

    @PostMapping("/novy")
    public String pridat(@ModelAttribute("osoba") @Valid Osoba osoba, BindingResult bindingResult) {
        osoba.setId(null);
        if (bindingResult.hasErrors()) {
            return "detail";
        }
        repository.save(osoba);
        return "redirect:/";
    }

    @GetMapping("/{id:[0-9]+}")
    public Object detail(@PathVariable long id) {
        Optional<Osoba> osoba = repository.findById(id);
        if (osoba.isPresent()) {
            return new ModelAndView("detail")
                    .addObject("osoba", osoba.get());
//                        seznamOsob.get(0));}
        }
        System.out.println("error");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id:[0-9]+}")
    public String ulozit(@ModelAttribute("osoba") @Valid Osoba osoba, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("err");
            return "detail";
        }
        repository.save(osoba);
        return "redirect:/";
    }

    @PostMapping(value = "/{id:[0-9]+}", params = "akce=smazat")
    public String smazat(@PathVariable long id) {
        repository.deleteById(id);
        return "redirect:/";
    }
}
