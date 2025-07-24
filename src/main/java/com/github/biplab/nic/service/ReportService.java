package com.github.biplab.nic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.biplab.nic.entity.Report;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.ReportRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TeamFormationRepository teamFormationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Create a new department report (alias for submission with validation)
    public Report createReport(UUID caseId, UUID personId, String content, String department) {
        return submitDepartmentReport(caseId, personId, content, department);
    }

    // Core submission logic (one per department per case, only by assigned members)
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
        if (reportRepository.findByCaseIdAndDepartmentAndIsMergedFalse(caseId, department).isPresent()) {
            throw new RuntimeException("Report already submitted for this department and case");
        }

        Report report = new Report();
        report.setCaseId(caseId);
        report.setPersonId(personId);
        report.setContent(content);
        report.setDepartment(department);
        report.setSubmittedAt(LocalDateTime.now());
        report.setIsMerged(false);
        return reportRepository.save(report);
    }

    // Update an existing report (only by the original submitter or supervisor)
    public Report updateReport(Long reportId, UUID personId, String newContent) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if (report.getIsMerged()) {
            throw new RuntimeException("Merged reports cannot be updated");
        }

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // Validate: Only original submitter or supervisor can update
        if (!report.getPersonId().equals(personId) && (person.getRole() != Role.SUPERVISOR || person.getRank() > 1)) {
            throw new RuntimeException("Only the original submitter or a rank <=1 supervisor can update this report");
        }

        report.setContent(newContent);
        report.setSubmittedAt(LocalDateTime.now()); // Update timestamp
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

        if (report.getIsMerged()) {
            throw new RuntimeException("Merged reports cannot be deleted");
        }

        reportRepository.delete(report);
    }

    // Get a single report by ID
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    // Get all reports for a case (including merged if exists)
    public List<Report> getReportsByCaseId(UUID caseId) {
        return reportRepository.findByCaseId(caseId).orElseThrow(() -> new RuntimeException("No reports found for case"));
    }

    // Get all reports submitted by a person
    public List<Report> getReportsByPersonId(UUID personId) {
        return reportRepository.findByPersonId(personId).orElseThrow(() -> new RuntimeException("No reports found for person"));
    }

    // Merge reports into a single JSON row (now acts as the final submission)
    public Report mergeReports(UUID caseId, UUID supervisorId, String finalSummary) {  // Added optional finalSummary param
        // Validate: Only SUPERVISOR with rank <=1 can merge
        Person supervisor = personRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        if (supervisor.getRole() != Role.SUPERVISOR || supervisor.getRank() > 1) {
            throw new RuntimeException("Only supervisors with rank 1 or better can merge reports");
        }

        // Check if merged report already exists
        Optional<Report> existingMerged = reportRepository.findByCaseIdAndIsMergedTrue(caseId);
        if (existingMerged.isPresent()) {
            throw new RuntimeException("Reports already merged for this case");
        }

        // Gather all department reports
        List<Report> deptReports = reportRepository.findByCaseIdAndIsMergedFalse(caseId);
        if (deptReports.isEmpty()) {
            throw new RuntimeException("No department reports available to merge");
        }

        // Build merged JSON for departments
        Map<String, String> mergedContent = new HashMap<>();
        for (Report r : deptReports) {
            mergedContent.put(r.getDepartment(), r.getContent());
        }

        String jsonContent;
        try {
            jsonContent = objectMapper.writeValueAsString(mergedContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating merged JSON", e);
        }

        // Create merged row (this is now the final submission)
        Report mergedReport = new Report();
        mergedReport.setCaseId(caseId);
        mergedReport.setPersonId(supervisorId);
        mergedReport.setContent(jsonContent); // JSON aggregate for departments
        mergedReport.setFinalContent(finalSummary); // Optional single-string summary
        mergedReport.setDepartment("AGGREGATE");
        mergedReport.setSubmittedAt(LocalDateTime.now());
        mergedReport.setIsMerged(true);
        return reportRepository.save(mergedReport);
    }
}
