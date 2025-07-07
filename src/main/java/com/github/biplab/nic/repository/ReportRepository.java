package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByCaseRefIdId(UUID caseRefId);
}