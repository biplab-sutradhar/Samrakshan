package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
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

    @PostMapping
    public ResponseEntity<ReportResponseDTO> submitReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.ok(reportService.submitReport(reportRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> getReportById(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> updateReport(@PathVariable UUID id, @RequestBody ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.ok(reportService.updateReport(id, reportRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}