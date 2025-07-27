package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.PersonDto.PersonRequestDTO;
import com.github.biplab.nic.dto.PersonDto.PersonResponseDTO;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonResponseDTO> createPerson(@RequestBody PersonRequestDTO personRequestDTO) {
        return ResponseEntity.ok(personService.createPerson(personRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable UUID id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @GetMapping
    public ResponseEntity<List<PersonResponseDTO>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> updatePerson(@PathVariable UUID id, @RequestBody PersonRequestDTO personRequestDTO) {
        return ResponseEntity.ok(personService.updatePerson(id, personRequestDTO));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PersonResponseDTO>> createPersons(@RequestBody List<PersonRequestDTO> personRequestDTOList) {
        return ResponseEntity.ok(personService.createPersons(personRequestDTOList));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        boolean isAuthenticated = personService.login(email, password);
        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @GetMapping("/search")
    public List<Person> searchPersons(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer rank,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String officeName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String subdivision,
            @RequestParam(required = false) String postName
    ) {
        return personService.search(role, department, rank, district, designation, officeName, status, subdivision, postName);
    }

    @GetMapping("/email")
    public ResponseEntity<Person> getPersonByEmail(@RequestParam String email) {
        Person person = personService.getPersonByEmail(email);
        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name")
    public ResponseEntity<PersonResponseDTO> getPersonByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        try {
            PersonResponseDTO person = personService.getPersonByName(firstName, lastName);
            return ResponseEntity.ok(person);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}