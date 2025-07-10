package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.PersonDto.PersonRequestDTO;
import com.github.biplab.nic.dto.PersonDto.PersonResponseDTO;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role; // Added import
import com.github.biplab.nic.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public PersonResponseDTO createPerson(PersonRequestDTO personRequestDTO) {
        Person person = new Person();
        person.setFirstName(personRequestDTO.getFirstName());
        person.setLastName(personRequestDTO.getLastName());
        person.setEmail(personRequestDTO.getEmail());
        person.setPhoneNumber(personRequestDTO.getPhoneNumber());
        person.setGender(personRequestDTO.getGender());
        person.setAddress(personRequestDTO.getAddress());
        person.setRole(Role.valueOf(personRequestDTO.getRole().toString())); // Convert String to Role enum
        person.setDepartment(personRequestDTO.getDepartment() != null ? Department.valueOf(personRequestDTO.getDepartment().toString()) : null); // Convert String to Department enum
        person.setPassword(passwordEncoder.encode(personRequestDTO.getPassword()));
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

    public PersonResponseDTO updatePerson(UUID id, PersonRequestDTO personRequestDTO) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id));
        person.setFirstName(personRequestDTO.getFirstName());
        person.setLastName(personRequestDTO.getLastName());
        person.setEmail(personRequestDTO.getEmail());
        person.setPhoneNumber(personRequestDTO.getPhoneNumber());
        person.setGender(personRequestDTO.getGender());
        person.setAddress(personRequestDTO.getAddress());
        person.setRole(Role.valueOf(personRequestDTO.getRole().toString()));
        person.setDepartment(personRequestDTO.getDepartment() != null ? Department.valueOf(personRequestDTO.getDepartment().toString()) : null);
        if (personRequestDTO.getPassword() != null && !personRequestDTO.getPassword().isEmpty()) {
            person.setPassword(passwordEncoder.encode(personRequestDTO.getPassword()));
        }
        Person updatedPerson = personRepository.save(person);
        return mapToResponseDTO(updatedPerson);
    }

    public void deletePerson(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id));
        personRepository.delete(person);
    }

    public boolean login(String email, String password) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Person not found with email: " + email));
        return passwordEncoder.matches(password, person.getPassword());
    }

    private PersonResponseDTO mapToResponseDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setEmail(person.getEmail());
        dto.setPhoneNumber(person.getPhoneNumber());
        dto.setGender(person.getGender());
        dto.setAddress(person.getAddress());
        dto.setRole(person.getRole()); // Directly set Role enum
        dto.setDepartment(person.getDepartment()); // Directly set Department enum
        dto.setCreatedAt(person.getCreatedAt());
        dto.setUpdatedAt(person.getUpdatedAt());
        return dto;
    }
}