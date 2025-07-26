package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCaseId(UUID caseId);
    List<Report> findByPersonId(UUID personId);
    List<Report> findByCaseIdAndIsFinalReportFalse(UUID caseId);



    Optional<Report> findByCaseIdAndIsFinalReportTrue(UUID caseId);

    boolean existsByCaseIdAndDepartmentAndIsFinalReportFalse(UUID caseId, String department);

    List<Report> findByPersonIdAndIsFinalReportFalse(UUID personId);

}