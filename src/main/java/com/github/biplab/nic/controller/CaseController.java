package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.CaseDto.CaseRequestDTO;
import com.github.biplab.nic.dto.CaseDto.CaseResponseDTO;
import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.service.CaseService;
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

    @PutMapping("/{id}")
    public ResponseEntity<CaseResponseDTO> updateCase(@PathVariable UUID id, @RequestBody CaseRequestDTO caseRequestDTO) {
        return ResponseEntity.ok(caseService.updateCase(id, caseRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable UUID id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{caseId}/team")
    public ResponseEntity<TeamFormationDTO> formTeam(@PathVariable UUID caseId, @RequestBody TeamFormationDTO teamFormationDTO) {
        teamFormationDTO.setCaseId(caseId);
        return ResponseEntity.ok(teamFormationService.createTeamFormation(teamFormationDTO));
    }
}