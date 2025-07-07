package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.CaseDto.CaseRequestDTO;
import com.github.biplab.nic.dto.CaseDto.CaseResponseDTO;
import com.github.biplab.nic.dto.CaseDto.CaseDetailsDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.CaseDetails;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.CaseDetailsRepository;
import com.github.biplab.nic.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CaseDetailsRepository caseDetailsRepository;

    public CaseResponseDTO submitCase(CaseRequestDTO caseRequestDTO) {
        Person createdBy = personRepository.findById(caseRequestDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + caseRequestDTO.getCreatedBy()));

        ChildMarriageCase caseEntity = new ChildMarriageCase();
        caseEntity.setComplainantName(caseRequestDTO.getComplainantName());
        caseEntity.setComplainantPhone(caseRequestDTO.getComplainantPhone());
        caseEntity.setCaseAddress(caseRequestDTO.getCaseAddress());
        caseEntity.setDescription(caseRequestDTO.getDescription());
        caseEntity.setReportedAt(caseRequestDTO.getReportedAt());
        caseEntity.setCreatedBy(createdBy);
        caseEntity.setStatus(caseRequestDTO.getStatus());
        ChildMarriageCase savedCase = caseRepository.save(caseEntity);

        // Handle case details if provided
        if (caseRequestDTO.getCaseDetails() != null) {
            CaseDetails caseDetail = new CaseDetails();
            caseDetail.setCaseId(savedCase);
            caseDetail.setNotes(caseRequestDTO.getCaseDetails().getNotes());
            caseDetail.setEvidencePath(caseRequestDTO.getCaseDetails().getEvidencePath());
            caseDetailsRepository.save(caseDetail);
        }

        return mapToResponseDTO(savedCase);
    }

    public CaseResponseDTO getCaseById(UUID id) {
        ChildMarriageCase caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + id));
        return mapToResponseDTO(caseEntity);
    }

    public List<CaseResponseDTO> getAllCases() {
        return caseRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private CaseResponseDTO mapToResponseDTO(ChildMarriageCase caseEntity) {
        CaseResponseDTO dto = new CaseResponseDTO();
        dto.setId(caseEntity.getId());
        dto.setComplainantName(caseEntity.getComplainantName());
        dto.setComplainantPhone(caseEntity.getComplainantPhone());
        dto.setCaseAddress(caseEntity.getCaseAddress());
        dto.setDescription(caseEntity.getDescription());
        dto.setReportedAt(caseEntity.getReportedAt());
        dto.setCreatedBy(caseEntity.getCreatedBy() != null ? caseEntity.getCreatedBy().getId() : null);
        dto.setStatus(caseEntity.getStatus());
        dto.setCreatedAt(caseEntity.getCreatedAt());
        dto.setUpdatedAt(caseEntity.getUpdatedAt());
        dto.setCaseDetails(caseEntity.getCaseDetails().stream()
                .map(this::mapToCaseDetailsDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private CaseDetailsDTO mapToCaseDetailsDTO(CaseDetails caseDetails) {
        return new CaseDetailsDTO(
                caseDetails.getId(),
                caseDetails.getCaseId().getId(),
                caseDetails.getNotes(),
                caseDetails.getEvidencePath(),
                caseDetails.getCreatedAt(),
                caseDetails.getUpdatedAt()
        );
    }
}