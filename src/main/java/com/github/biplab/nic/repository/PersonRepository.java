package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    List<Person> findByRoleAndDistrict(Role role, String district);
    List<Person> findByDepartmentAndDistrict(Department department, String district);
    List<Person> findByDepartmentAndDistrictAndRole(Department department, String district, Role role);
    List<Person> findByRole(Role role);
    Optional<Person> findByEmail(String email);
}