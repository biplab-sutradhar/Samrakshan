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
    List<Person> findByDepartmentAndDistrictAndRole(Department department, String district, Role role);
    List<Person> findByRoleAndDistrictAndRank(Role role, String district, Integer rank);
    List<Person> findByDepartmentAndDistrictAndRoleAndRank(Department department, String district, Role role, Integer rank);
    Optional<Person> findByEmail(String email);
    List<Person> findByDistrict(String district); // Added method
}