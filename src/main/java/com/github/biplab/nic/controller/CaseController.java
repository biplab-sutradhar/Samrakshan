package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.CaseDto.CaseRequestDTO;
import com.github.biplab.nic.dto.CaseDto.CaseResponseDTO;
import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.service.CaseService;
import com.github.biplab.nic.service.ReportService;
import com.github.biplab.nic.service.TeamFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;
    private final TeamFormationService teamFormationService;
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<CaseResponseDTO> submitCase(@RequestBody CaseRequestDTO caseRequestDTO) {
        return ResponseEntity.ok(caseService.submitCase(caseRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponseDTO> getCaseById(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @GetMapping
    public ResponseEntity<List<CaseResponseDTO>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    @PostMapping("/{caseId}/team")
    public ResponseEntity<TeamFormationDTO> formTeam(@PathVariable UUID caseId, @RequestBody TeamFormationDTO teamFormationDTO) {
        teamFormationDTO.setCaseId(caseId);
        return ResponseEntity.ok(teamFormationService.createTeamFormation(teamFormationDTO));
    }

    @PostMapping("/{caseId}/report")
    public ResponseEntity<ReportResponseDTO> submitReport(@PathVariable UUID caseId, @RequestBody ReportRequestDTO reportRequestDTO) {
        reportRequestDTO.setCaseId(caseId);
        return ResponseEntity.ok(reportService.submitReport(reportRequestDTO));
    }

    @GetMapping("/{caseId}/reports")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByCaseId(@PathVariable UUID caseId) {
        return ResponseEntity.ok(reportService.getAllReportsByCaseId(caseId));
    }

    @PutMapping("/{caseId}/report/{reportId}")
    public ResponseEntity<ReportResponseDTO> updateReport(@PathVariable UUID caseId, @PathVariable UUID reportId, @RequestBody ReportRequestDTO reportRequestDTO) {
        // Validate caseId matches report's caseRef
        ReportResponseDTO existingReport = reportService.getReportById(reportId);
        if (!existingReport.getCaseId().equals(caseId)) {
            throw new RuntimeException("Report does not belong to the specified case");
        }
        return ResponseEntity.ok(reportService.updateReport(reportId, reportRequestDTO));
    }

    @DeleteMapping("/{caseId}/report/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID caseId, @PathVariable UUID reportId) {
        // Validate caseId matches report's caseRef
        ReportResponseDTO existingReport = reportService.getReportById(reportId);
        if (!existingReport.getCaseId().equals(caseId)) {
            throw new RuntimeException("Report does not belong to the specified case");
        }
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}