package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, UUID> {
}

