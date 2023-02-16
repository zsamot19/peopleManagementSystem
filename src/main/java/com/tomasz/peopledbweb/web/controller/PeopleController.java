package com.tomasz.peopledbweb.web.controller;

import com.tomasz.peopledbweb.biz.data.FileStorageRepository;
import com.tomasz.peopledbweb.biz.data.PersonRepository;
import com.tomasz.peopledbweb.biz.model.Person;
import com.tomasz.peopledbweb.biz.service.PersonService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@RequestMapping("/")
@Controller
@Log4j2
public class PeopleController {
    private final PersonRepository personRepository;
    private final FileStorageRepository fileStorageRepository;
    private final PersonService personService;
    public PeopleController(PersonRepository personRepository, FileStorageRepository fileStorageRepository, PersonService personService) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.personService = personService;
    }
    @GetMapping(path = "/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename){
        String dispo = """
                attachment; filename="%s"
                """;
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, format(dispo, filename))
                .body(fileStorageRepository.findByName(filename));
    }
    @ModelAttribute("person")
    public Person getPerson(){
        return new Person();
    }
    @ModelAttribute("people")
    public Iterable<Person> getPeople(){
        return personRepository.findAll();
    }
    @PostMapping
    public String savePerson(@Valid Person person, Errors errors, @RequestParam("photoFileName") MultipartFile photoFile) throws IOException {
        if(!errors.hasErrors()){
            personService.save(person, photoFile.getInputStream());
            return "redirect:";
        }
        return "";
    }
    @PostMapping(params = "delete=true")
    public String deletePerson(@RequestParam(name = "selection") Optional<List<Long>> selections){
        selections.ifPresent(personService::deleteAllById);
        return "redirect:";
    }
    @PostMapping(params = "edit=true")
    public String editPerson(@RequestParam(name = "selection") Optional<List<Long>> selections, Model model){
        if(selections.isPresent()){
            personRepository.findById(selections.get().get(0)).ifPresent(person -> {
                model.addAttribute("person", person);
            });
        }
        return "people";
    }
    @PostMapping(params = "export=true")
    public ResponseEntity<String> exportCSV(@RequestParam(name = "selection") Optional<List<Long>> selections){
        String peopleCSV = personService.exportToCsv(selections);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/csv"));
        headers.setContentDispositionFormData("filename", "people.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(peopleCSV);
    }
    @GetMapping
    public String showPeoplePage(){
        return "people";
    }
}
