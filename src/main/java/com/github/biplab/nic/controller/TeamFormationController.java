package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.TeamDto.TeamResponseDTO;
import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.service.TeamFormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/team-formations")
public class TeamFormationController {

    @Autowired
    private TeamFormationService teamFormationService;

    @GetMapping("/{id}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationById(@PathVariable UUID id) {
        TeamFormationDTO dto = teamFormationService.getTeamFormationById(id);
        return ResponseEntity.ok(dto);  // Now returns dynamic maps
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationByCaseId(@PathVariable UUID caseId) {
        TeamFormationDTO dto = teamFormationService.getTeamFormationByCaseId(caseId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createTeamFormation(
            @RequestParam UUID caseId,
            @RequestParam String subdivision) {
        try {
            teamFormationService.initiateTeamFormation(caseId, subdivision);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Team formation initiated successfully for case: " + caseId);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to create team formation: " + e.getMessage());
        }
    }

    // 2. Team member accepts/rejects notification
    @PutMapping("/{teamId}/response")
    public ResponseEntity<String> handleTeamResponse(
            @PathVariable UUID teamId,
            @RequestParam UUID personId,
            @RequestParam String department,
            @RequestParam String status) {
        try {
            // Validate status
            if (!status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("REJECTED")) {
                return ResponseEntity.badRequest()
                        .body("Invalid status. Must be 'ACCEPTED' or 'REJECTED'");
            }

            teamFormationService.handleResponse(teamId, personId, department, status.toUpperCase());
            return ResponseEntity.ok("Response recorded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to handle response: " + e.getMessage());
        }
    }

    // 3. View pending responses from team members
    @GetMapping("/pending-responses")
    public ResponseEntity<List<TeamResponseDTO>> getPendingResponses() {
        try {
            List<TeamResponseDTO> pendingResponses = teamFormationService.getPendingResponses();
            return ResponseEntity.ok(pendingResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Optional: Get pending responses for a specific team
    @GetMapping("/{teamId}/pending-responses")
    public ResponseEntity<List<TeamResponseDTO>> getPendingResponsesByTeam(@PathVariable UUID teamId) {
        try {
            List<TeamResponseDTO> pendingResponses = teamFormationService.getPendingResponsesByTeam(teamId);
            return ResponseEntity.ok(pendingResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}