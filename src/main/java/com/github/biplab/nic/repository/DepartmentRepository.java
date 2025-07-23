package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Departments, UUID> {
    Optional<Departments> findByName(String name);
}
