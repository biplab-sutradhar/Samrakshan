package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.ReportDto.ReportRequestDTO;
import com.github.biplab.nic.dto.ReportDto.ReportResponseDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.Report;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.PersonRepository;
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
    private ReportRepository reportRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private PersonRepository personRepository;

    public ReportResponseDTO submitReport(ReportRequestDTO reportRequestDTO) {
        ChildMarriageCase caseRef = caseRepository.findById(reportRequestDTO.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + reportRequestDTO.getCaseId()));

        Person submittedBy = personRepository.findById(reportRequestDTO.getSubmittedBy())
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + reportRequestDTO.getSubmittedBy()));

        Report report = new Report();
        report.setCaseRefId(caseRef);
        report.setSubmittedBy(submittedBy);
        report.setDepartment(reportRequestDTO.getDepartment());
        report.setContent(reportRequestDTO.getContent());
        report.setSubmittedAt(LocalDateTime.now());
        Report savedReport = reportRepository.save(report);

        return mapToResponseDTO(savedReport);
    }

    public ReportResponseDTO getReportById(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        return mapToResponseDTO(report);
    }

    public List<ReportResponseDTO> getAllReportsByCaseId(UUID caseId) {
        List<Report> reports = reportRepository.findByCaseRefIdId(caseId);
        return reports.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ReportResponseDTO updateReport(UUID id, ReportRequestDTO reportRequestDTO) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));

        // Update only mutable fields
        report.setDepartment(reportRequestDTO.getDepartment());
        report.setContent(reportRequestDTO.getContent());
        report.setSubmittedAt(LocalDateTime.now()); // Update timestamp on modification
        Report updatedReport = reportRepository.save(report);

        return mapToResponseDTO(updatedReport);
    }

    public void deleteReport(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        reportRepository.delete(report);
    }

    private ReportResponseDTO mapToResponseDTO(Report report) {
        return new ReportResponseDTO(
                report.getId(),
                report.getCaseRefId().getId(),
                report.getSubmittedBy().getId(),
                report.getDepartment(),
                report.getContent(),
                report.getSubmittedAt()
        );
    }
}