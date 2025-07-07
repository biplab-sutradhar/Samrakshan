package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.PersonDto.PersonRequestDTO;
import com.github.biplab.nic.dto.PersonDto.PersonResponseDTO;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public PersonResponseDTO createPerson(PersonRequestDTO personRequestDTO) {
        Person person = new Person();
        person.setFirstName(personRequestDTO.getFirstName());
        person.setLastName(personRequestDTO.getLastName());
        person.setEmail(personRequestDTO.getEmail());
        person.setPhoneNumber(personRequestDTO.getPhoneNumber());
        person.setGender(personRequestDTO.getGender());
        person.setAddress(personRequestDTO.getAddress());
        person.setRole(personRequestDTO.getRole());
        person.setDepartment(personRequestDTO.getDepartment());
        Person savedPerson = personRepository.save(person);
        return mapToResponseDTO(savedPerson);
    }

    public PersonResponseDTO getPersonById(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id));
        return mapToResponseDTO(person);
    }

    public List<PersonResponseDTO> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private PersonResponseDTO mapToResponseDTO(Person person) {
        return new PersonResponseDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getPhoneNumber(),
                person.getGender(),
                person.getAddress(),
                person.getRole(),
                person.getDepartment(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }
}