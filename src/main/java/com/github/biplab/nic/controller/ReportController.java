package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.ok(reportService.createReport(reportRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> updateReport(@PathVariable Long id, @RequestBody ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.ok(reportService.updateReport(id, reportRequestDTO));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByCaseId(@PathVariable UUID caseId) {
        return ResponseEntity.ok(reportService.getReportsByCaseId(caseId));
    }

    @GetMapping("/team-member/{personId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByPersonId(@PathVariable UUID personId) {
        return ResponseEntity.ok(reportService.getReportsByPersonId(personId));
    }
}