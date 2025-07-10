package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.service.TeamFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/team-formations")
@RequiredArgsConstructor
public class TeamFormationController {

    private final TeamFormationService teamFormationService;

    @GetMapping("/{id}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamFormationService.getTeamFormationById(id));
    }

    @PostMapping
    public ResponseEntity<TeamFormationDTO> createTeamFormation(@RequestBody TeamFormationDTO teamFormationDTO) {
        return ResponseEntity.ok(teamFormationService.createTeamFormation(teamFormationDTO));
    }

    @PutMapping("/{id}/response")
    public ResponseEntity<Void> handleResponse(@PathVariable UUID id, @RequestParam String department, @RequestParam String status) {
        teamFormationService.handleResponse(id, department, status);
        return ResponseEntity.ok().build();
    }

    // New endpoint
    @GetMapping("/case/{caseId}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationByCaseId(@PathVariable UUID caseId) {
        return ResponseEntity.ok(teamFormationService.getTeamFormationByCaseId(caseId));
    }
}