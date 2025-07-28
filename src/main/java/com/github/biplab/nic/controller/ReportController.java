package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.FeedbackDTO.FeedbackRequestDTO;
import com.github.biplab.nic.dto.FeedbackDTO.FeedbackResponseDTO;
import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.entity.Report;
import com.github.biplab.nic.entity.ReportFeedback;
import com.github.biplab.nic.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponseDTO createReport(@Valid @RequestBody ReportRequestDTO dto) {
        Report report = reportService.submitDepartmentReport(
                dto.getCaseId(),
                dto.getPersonId(),
                dto.getReport(),
                dto.getDepartment()
        );
        return mapToResponse(report);
    }

    @GetMapping("/{id}")
    public ReportResponseDTO getReport(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return mapToResponse(report);
    }


    @GetMapping("/team-member/{personId}")
    public List<ReportResponseDTO> getReportsByPerson(@PathVariable UUID personId) {
        return reportService.getReportsByPersonId(personId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ReportResponseDTO updateReport(
            @PathVariable Long id,
            @RequestParam UUID personId,
            @RequestBody String newContent) {
        Report report = reportService.updateReport(id, personId, newContent);
        return mapToResponse(report);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReport(@PathVariable Long id, @RequestParam UUID supervisorId) {
        reportService.deleteReport(id, supervisorId);
    }



    @PostMapping("/merge")
    public ReportResponseDTO mergeReports(@RequestParam UUID caseId, @RequestParam UUID supervisorId) {
        Report finalReport = reportService.mergeReports(caseId, supervisorId);
        return mapToResponse(finalReport);
    }

    @GetMapping("/case/{caseId}")
    public List<ReportResponseDTO> getAllReportsByCase(@PathVariable UUID caseId) {
        return reportService.getReportsByCaseId(caseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @GetMapping("/case/{caseId}/department")
    public List<ReportResponseDTO> getDepartmentReportsByCase(@PathVariable UUID caseId)  {
        return reportService.getDepartmentReportsByCaseId(caseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/case/{caseId}/final")
    public ResponseEntity<ReportResponseDTO> getFinalReportByCase(@PathVariable UUID caseId)  {
        Optional<Report> finalReport = reportService.getFinalReportByCaseId(caseId);
        if (finalReport.isEmpty()) {
            throw new RuntimeException("No final report found for this case");
        }
        return ResponseEntity.ok(mapToResponse(finalReport.get()));
    }

    // Add to ReportController.java

    @PostMapping("/{reportId}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponseDTO giveFeedback(
            @PathVariable Long reportId,
            @RequestParam UUID supervisorId,
            @RequestBody String feedbackMessage) {

        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setReportId(reportId);
        dto.setSupervisorId(supervisorId);
        dto.setFeedbackMessage(feedbackMessage);

        ReportFeedback feedback = reportService.giveFeedback(dto);
        return reportService.mapToFeedbackResponse(feedback);
    }

    @GetMapping("/feedback/pending")
    public List<FeedbackResponseDTO> getPendingFeedback(@RequestParam UUID personId) {
        return reportService.getPendingFeedbackForPerson(personId);
    }

    @GetMapping("/{reportId}/feedback")
    public List<FeedbackResponseDTO> getFeedbackForReport(@PathVariable Long reportId) {
        // Get all feedback for a specific report
        return reportService.getFeedbackForReport(reportId);
    }


    private ReportResponseDTO mapToResponse(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setCaseId(report.getCaseId());
        dto.setPersonId(report.getPersonId());
        dto.setReport(report.getReport());
        dto.setDepartment(report.getDepartment());
        dto.setSubmittedAt(report.getSubmittedAt());
        dto.setIsFinalReport(report.getIsFinalReport());
        return dto;
    }
}