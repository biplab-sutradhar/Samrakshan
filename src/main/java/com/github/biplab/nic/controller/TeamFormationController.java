package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.service.TeamFormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/team-formations")
public class TeamFormationController {

    @Autowired
    private TeamFormationService teamFormationService;

    @PostMapping
    public ResponseEntity<TeamFormationDTO> createTeamFormation(@RequestBody TeamFormationDTO teamFormationDTO) {
        TeamFormationDTO createdDTO = teamFormationService.createTeamFormation(teamFormationDTO);
        return ResponseEntity.ok(createdDTO);
    }

    @PostMapping("/initiate/{caseId}")
    public ResponseEntity<Void> initiateTeamFormation(@PathVariable UUID caseId) {
        // Assume district is fetched from case; adjust if needed
        teamFormationService.initiateTeamFormation(caseId, "Delhi"); // Hardcoded for now; fetch dynamically
        return ResponseEntity.ok().build();
    }

    @PostMapping("/response/{teamId}")
    public ResponseEntity<Void> handleResponse(
            @PathVariable UUID teamId,
            @RequestParam UUID personId,
            @RequestParam String department,
            @RequestParam String status) {
        teamFormationService.handleResponse(teamId, personId, department, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationByCaseId(@PathVariable UUID caseId) {
        TeamFormationDTO dto = teamFormationService.getTeamFormationByCaseId(caseId);
        return ResponseEntity.ok(dto);
    }
}