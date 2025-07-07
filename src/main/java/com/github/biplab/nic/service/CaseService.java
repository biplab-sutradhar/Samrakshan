package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.CaseDto.CaseRequestDTO;
import com.github.biplab.nic.dto.CaseDto.CaseResponseDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    public CaseResponseDTO submitCase(CaseRequestDTO caseRequestDTO) {
        ChildMarriageCase newCase = new ChildMarriageCase();
        newCase.setComplainant(caseRequestDTO.getComplainant());
        newCase.setAddress(caseRequestDTO.getAddress());
        newCase.setCaseDetails(caseRequestDTO.getCaseDetails());
        newCase.setMarriageDate(caseRequestDTO.getMarriageDate());
        ChildMarriageCase savedCase = caseRepository.save(newCase);
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
        return new CaseResponseDTO(
                caseEntity.getId(),
                caseEntity.getComplainant(),
                caseEntity.getAddress(),
                caseEntity.getCaseDetails(),
                caseEntity.getMarriageDate()
        );
    }
}