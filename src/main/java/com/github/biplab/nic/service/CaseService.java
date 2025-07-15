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
import com.github.biplab.nic.repository.TeamFormationRepository;
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

    @Autowired
    private TeamFormationRepository teamFormationRepository;

    @Autowired
    private TeamFormationService teamFormationService;

    public CaseResponseDTO submitCase(CaseRequestDTO caseRequestDTO) {
        ChildMarriageCase caseEntity = new ChildMarriageCase();
        caseEntity.setComplainantName(caseRequestDTO.getComplainantName());
        caseEntity.setComplainantPhone(caseRequestDTO.getComplainantPhone());
        caseEntity.setCaseAddress(caseRequestDTO.getCaseAddress());
        caseEntity.setDistrict(caseRequestDTO.getDistrict());
        caseEntity.setState(caseRequestDTO.getState());
        caseEntity.setDescription(caseRequestDTO.getDescription());
        caseEntity.setReportedAt(caseRequestDTO.getReportedAt() != null ? caseRequestDTO.getReportedAt() : LocalDateTime.now());
        caseEntity.setCreatedBy(caseRequestDTO.getCreatedBy());
        caseEntity.setStatus("PENDING");
        ChildMarriageCase savedCase = caseRepository.save(caseEntity);

        if (caseRequestDTO.getCaseDetails() != null) {
            CaseDetails caseDetail = new CaseDetails();
            caseDetail.setCaseId(savedCase);
            caseDetail.setNotes(caseRequestDTO.getCaseDetails().getNotes());
            caseDetail.setEvidencePath(caseRequestDTO.getCaseDetails().getEvidencePath());
            caseDetail.setMarriageDate(caseRequestDTO.getCaseDetails().getMarriageDate());
            caseDetail.setBoyName(caseRequestDTO.getCaseDetails().getBoyName());
            caseDetail.setBoyFatherName(caseRequestDTO.getCaseDetails().getBoyFatherName());
            caseDetail.setBoyAddress(caseRequestDTO.getCaseDetails().getBoyAddress());
            caseDetail.setBoyAge(caseRequestDTO.getCaseDetails().getBoyAge());
            caseDetail.setGirlName(caseRequestDTO.getCaseDetails().getGirlName());
            caseDetail.setGirlFatherName(caseRequestDTO.getCaseDetails().getGirlFatherName());
            caseDetail.setGirlAge(caseRequestDTO.getCaseDetails().getGirlAge());
            caseDetail.setGirlAddress(caseRequestDTO.getCaseDetails().getGirlAddress());
            caseDetail.setGirlVillage(caseRequestDTO.getCaseDetails().getGirlVillage());
            caseDetail.setGirlPoliceStation(caseRequestDTO.getCaseDetails().getGirlPoliceStation());
            caseDetail.setGirlPostOffice(caseRequestDTO.getCaseDetails().getGirlPostOffice());
            caseDetail.setGirlSubdivision(caseRequestDTO.getCaseDetails().getGirlSubdivision());
            caseDetail.setGirlDistrict(caseRequestDTO.getCaseDetails().getGirlDistrict());
            // teamId should be set by TeamFormationService, so omit here
            caseDetail.setMarriageAddress(caseRequestDTO.getCaseDetails().getMarriageAddress());
            caseDetailsRepository.save(caseDetail);
            savedCase.getCaseDetails().add(caseDetail);
        }

        teamFormationService.initiateTeamFormation(savedCase.getId(), caseEntity.getDistrict());
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
        caseEntity.setComplainantName(caseRequestDTO.getComplainantName());
        caseEntity.setComplainantPhone(caseRequestDTO.getComplainantPhone());
        caseEntity.setCaseAddress(caseRequestDTO.getCaseAddress());
        caseEntity.setDistrict(caseRequestDTO.getDistrict());
        caseEntity.setState(caseRequestDTO.getState());
        caseEntity.setDescription(caseRequestDTO.getDescription());
        caseEntity.setReportedAt(caseRequestDTO.getReportedAt() != null ? caseRequestDTO.getReportedAt() : caseEntity.getReportedAt());
        caseEntity.setCreatedBy(caseRequestDTO.getCreatedBy());
        caseEntity.setStatus(caseRequestDTO.getStatus());

        if (caseRequestDTO.getCaseDetails() != null) {
            caseEntity.getCaseDetails().clear();
            CaseDetails caseDetail = new CaseDetails();
            caseDetail.setCaseId(caseEntity);
            caseDetail.setNotes(caseRequestDTO.getCaseDetails().getNotes());
            caseDetail.setEvidencePath(caseRequestDTO.getCaseDetails().getEvidencePath());
            caseDetail.setMarriageDate(caseRequestDTO.getCaseDetails().getMarriageDate());
            caseDetail.setBoyName(caseRequestDTO.getCaseDetails().getBoyName());
            caseDetail.setBoyFatherName(caseRequestDTO.getCaseDetails().getBoyFatherName());
            caseDetail.setBoyAddress(caseRequestDTO.getCaseDetails().getBoyAddress());
            caseDetail.setBoyAge(caseRequestDTO.getCaseDetails().getBoyAge());
            caseDetail.setGirlName(caseRequestDTO.getCaseDetails().getGirlName());
            caseDetail.setGirlFatherName(caseRequestDTO.getCaseDetails().getGirlFatherName());
            caseDetail.setGirlAge(caseRequestDTO.getCaseDetails().getGirlAge());
            caseDetail.setGirlAddress(caseRequestDTO.getCaseDetails().getGirlAddress());
            caseDetail.setGirlVillage(caseRequestDTO.getCaseDetails().getGirlVillage());
            caseDetail.setGirlPoliceStation(caseRequestDTO.getCaseDetails().getGirlPoliceStation());
            caseDetail.setGirlPostOffice(caseRequestDTO.getCaseDetails().getGirlPostOffice());
            caseDetail.setGirlSubdivision(caseRequestDTO.getCaseDetails().getGirlSubdivision());
            caseDetail.setGirlDistrict(caseRequestDTO.getCaseDetails().getGirlDistrict());
            // teamId should be set by TeamFormationService
            caseDetail.setMarriageAddress(caseRequestDTO.getCaseDetails().getMarriageAddress());
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
        dto.setComplainantName(caseEntity.getComplainantName());
        dto.setComplainantPhone(caseEntity.getComplainantPhone());
        dto.setCaseAddress(caseEntity.getCaseAddress());
        dto.setDistrict(caseEntity.getDistrict());
        dto.setState(caseEntity.getState());
        dto.setDescription(caseEntity.getDescription());
        dto.setReportedAt(caseEntity.getReportedAt());
        dto.setCreatedBy(caseEntity.getCreatedBy());
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
                caseDetails.getUpdatedAt(),
                caseDetails.getPoliceMembers(),
                caseDetails.getDiceMembers(),
                caseDetails.getAdminMembers(),
                caseDetails.getSupervisorId(),
                caseDetails.getMarriageDate(),
                caseDetails.getBoyName(),
                caseDetails.getBoyFatherName(),
                caseDetails.getBoyAddress(),
                caseDetails.getBoyAge(),
                caseDetails.getGirlName(),
                caseDetails.getGirlFatherName(),
                caseDetails.getGirlAge(),
                caseDetails.getGirlAddress(),
                caseDetails.getGirlVillage(),
                caseDetails.getGirlPoliceStation(),
                caseDetails.getGirlPostOffice(),
                caseDetails.getGirlSubdivision(),
                caseDetails.getGirlDistrict(),
                caseDetails.getTeamId(),
                caseDetails.getMarriageAddress()
        );
    }
}