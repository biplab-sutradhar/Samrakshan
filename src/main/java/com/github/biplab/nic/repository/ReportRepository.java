package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<List<Report>> findByCaseId(UUID caseId);
    Optional<List<Report>> findByPersonId(UUID personId);
    Optional<Report> findByCaseIdAndDepartmentAndIsMergedFalse(UUID caseId, String department);

    // Get all department reports for a case (for merging)
    List<Report> findByCaseIdAndIsMergedFalse(UUID caseId);

    // Get the merged report for a case
    Optional<Report> findByCaseIdAndIsMergedTrue(UUID caseId);

}