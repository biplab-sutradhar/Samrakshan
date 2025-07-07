package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    List<Person> findByDepartmentAndRole(Department department, Role role);
    List<Person> findByRole(Role role);
}