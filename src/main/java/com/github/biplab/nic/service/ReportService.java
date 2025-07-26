package com.github.biplab.nic.service;

import com.github.biplab.nic.entity.Report;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.ReportRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TeamFormationRepository teamFormationRepository;

    // Submit department report (one per department per case)
    public Report submitDepartmentReport(UUID caseId, UUID personId, String content, String department) {
        // Validate: Person must be a team member in the assigned department for this case
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        TeamFormation team = teamFormationRepository.findByCaseId_Id(caseId)
                .orElseThrow(() -> new RuntimeException("No team formed for case"));

        List<UUID> deptMembers = team.getDepartmentMembers().getOrDefault(department, List.of());
        if (!deptMembers.contains(personId)) {
            throw new RuntimeException("Only assigned team members from this department can submit reports");
        }

        // Enforce: Only one report per department + case
        if (reportRepository.existsByCaseIdAndDepartmentAndIsFinalReportFalse(caseId, department)) {
            throw new RuntimeException("Report already submitted for this department and case");
        }

        Report report = Report.builder()
                .caseId(caseId)
                .personId(personId)
                .report(content)
                .department(department)
                .isFinalReport(false)
                .build();

        return reportRepository.save(report);
    }

    // Create a new department report (alias for submission)
    public Report createReport(UUID caseId, UUID personId, String content, String department) {
        return submitDepartmentReport(caseId, personId, content, department);
    }

    // Update an existing report (only by the original submitter or supervisor)
    public Report updateReport(Long reportId, UUID personId, String newContent) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getIsFinalReport()) {
            throw new RuntimeException("Final reports cannot be updated");
        }

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // Validate: Only original submitter or supervisor can update
        if (!report.getPersonId().equals(personId) &&
                (person.getRole() != Role.SUPERVISOR || person.getRank() > 1)) {
            throw new RuntimeException("Only the original submitter or a rank <=1 supervisor can update this report");
        }

        report.setReport(newContent);
        report.setSubmittedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // Delete a report (only by supervisor with rank <=1)
    public void deleteReport(Long reportId, UUID supervisorId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Person supervisor = personRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if (supervisor.getRole() != Role.SUPERVISOR || supervisor.getRank() > 1) {
            throw new RuntimeException("Only supervisors with rank <=1 can delete reports");
        }

        if (report.getIsFinalReport()) {
            throw new RuntimeException("Final reports cannot be deleted");
        }

        reportRepository.delete(report);
    }

    // Get a single report by ID
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    // Get all reports for a case (including final if exists)
    public List<Report> getReportsByCaseId(UUID caseId) {
        return reportRepository.findByCaseId(caseId);
    }

    // Get all reports submitted by a person
    public List<Report> getReportsByPersonId(UUID personId) {
        return reportRepository.findByPersonId(personId);
    }

    // Get only department reports for a case (excludes final)
    public List<Report> getDepartmentReportsByCaseId(UUID caseId) {
        return reportRepository.findByCaseIdAndIsFinalReportFalse(caseId);
    }

    // Get final report for a case (if exists)
    public Optional<Report> getFinalReportByCaseId(UUID caseId) {
        return reportRepository.findByCaseIdAndIsFinalReportTrue(caseId);
    }

    // Merge reports into a single final report
    @Transactional
    public Report mergeReports(UUID caseId, UUID supervisorId) {
        // Validate: Only SUPERVISOR with rank <=1 can merge
        Person supervisor = personRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if (supervisor.getRole() != Role.SUPERVISOR || supervisor.getRank() > 1) {
            throw new RuntimeException("Only supervisors with rank 1 or better can merge reports");
        }

        // Check if final report already exists
        Optional<Report> existingFinal = reportRepository.findByCaseIdAndIsFinalReportTrue(caseId);
        if (existingFinal.isPresent()) {
            throw new RuntimeException("Reports already merged for this case");
        }

        // Gather all department reports
        List<Report> deptReports = reportRepository.findByCaseIdAndIsFinalReportFalse(caseId);
        if (deptReports.isEmpty()) {
            throw new RuntimeException("No department reports available to merge");
        }

        // Create merged content
        String mergedContent = deptReports.stream()
                .map(report -> String.format("**%s Department Report:**\n%s",
                        report.getDepartment(), report.getReport()))
                .collect(Collectors.joining("\n\n--- DEPARTMENT SEPARATOR ---\n\n"));

        // Create final report
        Report finalReport = Report.builder()
                .caseId(caseId)
                .personId(supervisorId)
                .report(mergedContent)
                .department(null) // No specific department for final report
                .isFinalReport(true)
                .build();

        return reportRepository.save(finalReport);
    }

    // Check if all required departments have submitted reports
    public boolean allDepartmentsSubmitted(UUID caseId) {
        TeamFormation team = teamFormationRepository.findByCaseId_Id(caseId)
                .orElseThrow(() -> new RuntimeException("No team formed for case"));

        List<String> requiredDepts = team.getDepartmentMembers().keySet().stream().toList();
        List<String> submittedDepts = reportRepository.findByCaseIdAndIsFinalReportFalse(caseId)
                .stream()
                .map(Report::getDepartment)
                .distinct()
                .toList();

        return submittedDepts.containsAll(requiredDepts);
    }
}
