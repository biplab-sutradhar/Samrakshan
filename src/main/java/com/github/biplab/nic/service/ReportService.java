package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.entity.Report;
import com.github.biplab.nic.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository; // Ensure this is injected

    public ReportResponseDTO createReport(ReportRequestDTO reportRequestDTO) {
        Report report = new Report();
        report.setCaseId(reportRequestDTO.getCaseId());
        report.setPersonId(reportRequestDTO.getPersonId());
        report.setContent(reportRequestDTO.getContent());
        report.setSubmittedAt(LocalDateTime.now());
        Report savedReport = reportRepository.save(report);
        return mapToResponseDTO(savedReport);
    }

    public ReportResponseDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        return mapToResponseDTO(report);
    }

    public ReportResponseDTO updateReport(Long id, ReportRequestDTO reportRequestDTO) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        report.setCaseId(reportRequestDTO.getCaseId());
        report.setPersonId(reportRequestDTO.getPersonId());
        report.setContent(reportRequestDTO.getContent());
        report.setSubmittedAt(LocalDateTime.now());
        Report updatedReport = reportRepository.save(report);
        return mapToResponseDTO(updatedReport);
    }

    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        reportRepository.delete(report);
    }

    public List<ReportResponseDTO> getReportsByCaseId(UUID caseId) {
        List<Report> reports = reportRepository.findByCaseId(caseId)
                .orElseThrow(() -> new RuntimeException("No reports found for case ID: " + caseId));
        return reports.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReportResponseDTO> getReportsByPersonId(UUID personId) {
        List<Report> reports = reportRepository.findByPersonId(personId)
                .orElseThrow(() -> new RuntimeException("No reports found for person ID: " + personId));
        return reports.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReportResponseDTO mapToResponseDTO(Report report) {
        return new ReportResponseDTO(
                report.getId(),
                report.getCaseId(),
                report.getPersonId(),
                report.getContent(),
                report.getSubmittedAt()
        );
    }
}