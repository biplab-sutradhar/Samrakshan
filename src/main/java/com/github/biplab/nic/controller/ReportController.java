package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.entity.Report;
import com.github.biplab.nic.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id, @RequestParam UUID supervisorId) {
        reportService.deleteReport(id, supervisorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(toResponseDTO(report)); // Map to DTO
    }

    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(@RequestBody ReportRequestDTO requestDTO) {
        Report report = reportService.createReport(requestDTO.getCaseId(), requestDTO.getPersonId(),
                requestDTO.getContent(), requestDTO.getDepartment());
        return ResponseEntity.ok(toResponseDTO(report));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> updateReport(@PathVariable Long id,
                                                          @RequestBody ReportRequestDTO requestDTO) {
        Report updated = reportService.updateReport(id, requestDTO.getPersonId(), requestDTO.getContent());
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByCaseId(@PathVariable UUID caseId) {
        List<Report> reports = reportService.getReportsByCaseId(caseId);
        List<ReportResponseDTO> dtos = reports.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/team-member/{personId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByPersonId(@PathVariable UUID personId) {
        List<Report> reports = reportService.getReportsByPersonId(personId);
        List<ReportResponseDTO> dtos = reports.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @PostMapping("/merge")
    public ResponseEntity<ReportResponseDTO> mergeReports(@RequestParam UUID caseId,
                                                          @RequestParam UUID supervisorId,
                                                          @RequestParam(required = false) String finalSummary) {
        Report merged = reportService.mergeReports(caseId, supervisorId, finalSummary);
        return ResponseEntity.ok(toResponseDTO(merged));
    }


    // Helper: Map Report entity to ReportResponseDTO (customize as needed)
    private ReportResponseDTO toResponseDTO(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setCaseId(report.getCaseId());
        dto.setContent(report.getContent());
        dto.setPersonId(report.getPersonId());
        dto.setSubmittedAt(report.getSubmittedAt());
        dto.setDepartment(report.getDepartment());
        dto.setFinalContent(report.getFinalContent());
        dto.setIsMerged(report.getIsMerged());
        return dto;
    }


}
