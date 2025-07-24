package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.CaseDto.CaseRequestDTO;
import com.github.biplab.nic.dto.CaseDto.CaseResponseDTO;
import com.github.biplab.nic.dto.CaseDto.CaseDetailsDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.CaseDetails;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.CaseDetailsRepository;
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
    private CaseDetailsRepository caseDetailsRepository;

    @Autowired
    private TeamFormationService teamFormationService;

    public CaseResponseDTO submitCase(CaseRequestDTO caseRequestDTO) {
        ChildMarriageCase caseEntity = new ChildMarriageCase();

        caseEntity.setComplainantPhone(caseRequestDTO.getComplainantPhone());

        caseEntity.setReportedAt(caseRequestDTO.getReportedAt() != null ? caseRequestDTO.getReportedAt() : LocalDateTime.now());

        caseEntity.setStatus("PENDING"); // Pending until team is formed

        ChildMarriageCase savedCase = caseRepository.save(caseEntity);

        String subdivision = null;  // Extract from caseDetails if provided

        if (caseRequestDTO.getCaseDetails() != null) {
            CaseDetails caseDetail = new CaseDetails();
            caseDetail.setCaseId(savedCase);

            caseDetail.setMarriageDate(caseRequestDTO.getCaseDetails().getMarriageDate());
            caseDetail.setBoyName(caseRequestDTO.getCaseDetails().getBoyName());
            caseDetail.setBoyFatherName(caseRequestDTO.getCaseDetails().getBoyFatherName());
            caseDetail.setGirlName(caseRequestDTO.getCaseDetails().getGirlName());
            caseDetail.setGirlFatherName(caseRequestDTO.getCaseDetails().getGirlFatherName());
            caseDetail.setGirlAddress(caseRequestDTO.getCaseDetails().getGirlAddress());
            caseDetail.setGirlSubdivision(caseRequestDTO.getCaseDetails().getGirlSubdivision());
            caseDetail.setMarriageAddress(caseRequestDTO.getCaseDetails().getMarriageAddress());
            caseDetail.setPoliceStationNearMarriageLocation(caseRequestDTO.getCaseDetails().getPoliceStationNearMarriageLocation());
            caseDetailsRepository.save(caseDetail);
            savedCase.getCaseDetails().add(caseDetail);

            subdivision = caseRequestDTO.getCaseDetails().getGirlSubdivision();  // Use for team search
        }

        if (subdivision == null) {
            subdivision = "Default Subdivision";  // Fallback
        }

        // Initiate team formation using subdivision for person search
        teamFormationService.initiateTeamFormation(savedCase.getId(), subdivision);

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

    public CaseResponseDTO updateCase(UUID id, CaseRequestDTO caseRequestDTO) {
        ChildMarriageCase caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + id));
          caseEntity.setComplainantPhone(caseRequestDTO.getComplainantPhone());
         caseEntity.setReportedAt(caseRequestDTO.getReportedAt() != null ? caseRequestDTO.getReportedAt() : caseEntity.getReportedAt());

        caseEntity.setStatus(caseRequestDTO.getStatus());

        if (caseRequestDTO.getCaseDetails() != null) {
            caseEntity.getCaseDetails().clear();
            CaseDetails caseDetail = new CaseDetails();
            caseDetail.setCaseId(caseEntity);

            caseDetail.setMarriageDate(caseRequestDTO.getCaseDetails().getMarriageDate());
            caseDetail.setBoyName(caseRequestDTO.getCaseDetails().getBoyName());
            caseDetail.setBoyFatherName(caseRequestDTO.getCaseDetails().getBoyFatherName());
            caseDetail.setGirlName(caseRequestDTO.getCaseDetails().getGirlName());
            caseDetail.setGirlFatherName(caseRequestDTO.getCaseDetails().getGirlFatherName());
            caseDetail.setGirlAddress(caseRequestDTO.getCaseDetails().getGirlAddress());
            caseDetail.setGirlSubdivision(caseRequestDTO.getCaseDetails().getGirlSubdivision());
            caseDetail.setMarriageAddress(caseRequestDTO.getCaseDetails().getMarriageAddress());
            caseDetail.setPoliceStationNearMarriageLocation(caseRequestDTO.getCaseDetails().getPoliceStationNearMarriageLocation());
            caseEntity.getCaseDetails().add(caseDetail);
            caseDetailsRepository.save(caseDetail);
        }

        ChildMarriageCase updatedCase = caseRepository.save(caseEntity);
        return mapToResponseDTO(updatedCase);
    }

    public void deleteCase(UUID id) {
        ChildMarriageCase caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + id));
        caseRepository.delete(caseEntity);
    }

    private CaseResponseDTO mapToResponseDTO(ChildMarriageCase caseEntity) {
        CaseResponseDTO dto = new CaseResponseDTO();
        dto.setId(caseEntity.getId());
        dto.setComplainantPhone(caseEntity.getComplainantPhone());
        dto.setReportedAt(caseEntity.getReportedAt());
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
                caseDetails.getCreatedAt(),
                caseDetails.getUpdatedAt(),
                caseDetails.getDepartmentMembers(),
                caseDetails.getSupervisorId(),
                caseDetails.getMarriageDate(),
                caseDetails.getBoyName(),
                caseDetails.getBoyFatherName(),
                caseDetails.getBoyAddress(),
                caseDetails.getBoySubdivision(),
                caseDetails.getGirlName(),
                caseDetails.getGirlFatherName(),
                caseDetails.getGirlAddress(),
                caseDetails.getGirlSubdivision(),
                caseDetails.getTeamId(),
                caseDetails.getMarriageAddress(),
                caseDetails.getMarriagelocationlandmark(),
                caseDetails.getPoliceStationNearMarriageLocation()
        );
    }
}
