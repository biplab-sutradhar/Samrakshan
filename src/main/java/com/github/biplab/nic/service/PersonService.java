package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.PersonDto.PersonRequestDTO;
import com.github.biplab.nic.dto.PersonDto.PersonResponseDTO;
import com.github.biplab.nic.entity.Departments;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.Post;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.DepartmentRepository;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PostRepository postRepository;

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
        person.setRole(Role.valueOf(personRequestDTO.getRole()));

        // Validate department existence (optional, but recommended)
        departmentRepository.findByName(personRequestDTO.getDepartment())
                .orElseThrow(() -> new RuntimeException("Department not found: " + personRequestDTO.getDepartment()));
        person.setDepartment(personRequestDTO.getDepartment());  // Set string directly

        // Lookup post for rank (assuming Post has rank)
        Post post = postRepository.findByPostNameAndDepartment(personRequestDTO.getDesignation(), personRequestDTO.getDepartment())
                .orElseThrow(() -> new RuntimeException("Post not found for designation: " + personRequestDTO.getDesignation() + " in department: " + personRequestDTO.getDepartment()));
        person.setDesignation(personRequestDTO.getDesignation());
        person.setRank(post.getRank());
        person.setPostName(post.getPostName());

        person.setDistrict(personRequestDTO.getDistrict());
        person.setOfficeName(personRequestDTO.getOfficeName());
        person.setStatus(personRequestDTO.getStatus());
        person.setSubdivision(personRequestDTO.getSubdivision());

        person.setPassword(passwordEncoder.encode(personRequestDTO.getPassword()));
        Person savedPerson = personRepository.save(person);
        return mapToResponseDTO(savedPerson);
    }

    public List<PersonResponseDTO> createPersons(List<PersonRequestDTO> personRequestDTOList) {
        List<Person> persons = personRequestDTOList.stream().map(dto -> {
            Person person = new Person();
            person.setFirstName(dto.getFirstName());
            person.setLastName(dto.getLastName());
            person.setEmail(dto.getEmail());
            person.setPhoneNumber(dto.getPhoneNumber());
            person.setGender(dto.getGender());
            person.setAddress(dto.getAddress());
            if (dto.getRole() != null) {
                person.setRole(Role.valueOf(dto.getRole()));
            } else {
                person.setRole(null);
            }

            // Validate department
            departmentRepository.findByName(dto.getDepartment())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + dto.getDepartment()));
            person.setDepartment(dto.getDepartment());

            // Lookup post
            person.setDesignation(dto.getDesignation());
            person.setPostName(dto.getPostName());
            person.setRank(dto.getRank());

            person.setDistrict(dto.getDistrict());
            person.setOfficeName(dto.getOfficeName());
            person.setStatus(dto.getStatus());
            person.setSubdivision(dto.getSubdivision());

            person.setPassword(passwordEncoder.encode(dto.getPassword()));
            return person;
        }).toList();

        List<Person> savedPersons = personRepository.saveAll(persons);
        return savedPersons.stream().map(this::mapToResponseDTO).toList();
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
        person.setRole(Role.valueOf(personRequestDTO.getRole()));

        if (personRequestDTO.getDepartment() != null) {
            departmentRepository.findByName(personRequestDTO.getDepartment())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + personRequestDTO.getDepartment()));
            person.setDepartment(personRequestDTO.getDepartment());
        }

        if (personRequestDTO.getDesignation() != null) {
            String deptName = personRequestDTO.getDepartment() != null ? personRequestDTO.getDepartment() : person.getDepartment();
            if (deptName == null) {
                throw new RuntimeException("Department required for designation update");
            }
            Post post = postRepository.findByPostNameAndDepartment(personRequestDTO.getDesignation(), deptName)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            person.setDesignation(personRequestDTO.getDesignation());
            person.setRank(post.getRank());
            person.setPostName(post.getPostName());
        }

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

    public List<Person> search(String role, String department, Integer rank, String district,
                               String designation, String officeName, String status,
                               String subdivision, String postName) {
        return personRepository.findByFilters(role, department, rank, district, designation, officeName, status, subdivision, postName);
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
        dto.setRole(person.getRole() != null ? person.getRole().toString() : null);
        dto.setDepartment(person.getDepartment());
        dto.setCreatedAt(person.getCreatedAt());
        dto.setUpdatedAt(person.getUpdatedAt());
        dto.setDistrict(person.getDistrict());
        dto.setDesignation(person.getDesignation());
        dto.setOfficeName(person.getOfficeName());
        dto.setStatus(person.getStatus());
        dto.setSubdivision(person.getSubdivision());
        dto.setRank(person.getRank());
        dto.setPostName(person.getPostName());
        return dto;
    }
}
