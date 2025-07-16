package com.github.biplab.nic.service;

import com.github.biplab.nic.entity.Departments;
import com.github.biplab.nic.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Departments> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Departments> getDepartmentById(UUID id) {
        return departmentRepository.findById(id);
    }

    public Departments createDepartment(Departments department) {
        return departmentRepository.save(department);
    }

    public Departments updateDepartment(UUID id, Departments departmentDetails) {
        return departmentRepository.findById(id).map(department -> {
            department.setName(departmentDetails.getName());
            return departmentRepository.save(department);
        }).orElse(null);
    }

    public boolean deleteDepartment(UUID id) {
        return departmentRepository.findById(id).map(department -> {
            departmentRepository.delete(department);
            return true;
        }).orElse(false);
    }
}

